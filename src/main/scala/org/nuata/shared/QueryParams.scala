package org.nuata.shared

import java.util.Date

import org.joda.time.format.ISODateTimeFormat

import scala.reflect.ClassTag
import scala.reflect.runtime._
import scala.reflect.runtime.universe._
import scala.util.Try
import org.joda.time.DateTime

/**
 * Created by nico on 29/12/15.
 */
object QueryParams {
  val dateParser = ISODateTimeFormat.dateTime()

  def check[T](t: Type)(implicit tag: TypeTag[T]): Boolean = {
    t <:< typeOf[T]
  }

  def toIntOpt(v: String) = Try(v.toInt).toOption
  def toDoubleOpt(v: String) = Try(v.toDouble).toOption
  def toDateOpt(v: String) = Try(dateParser.parseDateTime(v).toDate).toOption
  def toDateTimeOpt(v: String) = Try(dateParser.parseDateTime(v)).toOption

  def as[T](params: Map[String, List[String]])(implicit tag: TypeTag[T]) = {
    var args = Vector[Any]()

    var errors = Vector[(String, String)]()

    def handleOpt(opt: Option[_], name: String, message: String): Any = {
      if(opt.isDefined) {
        opt.get
      } else {
        errors :+= (name, message)
      }
    }

    // Get the default parameters
    val classTag = ClassTag[T]( typeTag[T].mirror.runtimeClass( typeTag[T].tpe ) )
    val mod    = currentMirror.classSymbol(classTag.runtimeClass).companion.asModule
    val im     = currentMirror.reflect(currentMirror.reflectModule(mod).instance)
    val ts     = im.symbol.typeSignature
    val mApply = ts.member(universe.TermName("apply")).asMethod
    val defaultParams = Map(mApply.paramLists.flatten.zipWithIndex.map { case (p, i) =>
      (p.name.toString, ts.member(universe.TermName(s"apply$$default$$${i+1}")))
    }.filter( _._2.isMethod ).map( m => {
      (m._1, im.reflectMethod(m._2.asMethod)())
    }) :_*)

    // Build the instance
    val constructor = currentMirror.reflectClass(tag.tpe.typeSymbol.asClass).reflectConstructor(tag.tpe.members.filter { m =>
      m.isMethod && m.asMethod.isConstructor
    }.map { m =>
      for(p <- m.asMethod.paramLists.head) {
        val name = p.name.toString

        val value = if(params.contains(name)) {
          val values = params(name)
          val first = values.head
          p.typeSignature match {
            // String
            case t if check[String](t) => first
            case t if t <:< typeOf[Option[String]] => Some(first)
            case t if t <:< typeOf[List[String]] => values

            // Int
            case t if t <:< typeOf[Int] => handleOpt(toIntOpt(first), name, s"integer value required")
            case t if t <:< typeOf[Option[Int]] => toIntOpt(first)
            case t if t <:< typeOf[List[Int]] => values.flatMap(toIntOpt)

            // Double
            case t if t <:< typeOf[Double] => handleOpt(toDoubleOpt(first), name, s"float value required")
            case t if t <:< typeOf[Option[Double]] => toDoubleOpt(first)
            case t if t <:< typeOf[List[Double]] => values.flatMap(toDoubleOpt)

            // Date
            case t if t <:< typeOf[Date] => handleOpt(toDateOpt(first), name, s"date required")
            case t if t <:< typeOf[Option[Date]] => toDateOpt(first)
            case t if t <:< typeOf[List[Date]] => values.flatMap(toDateOpt)

            // DateTime
            case t if t <:< typeOf[DateTime] => handleOpt(toDateTimeOpt(first), name, s"date required")
            case t if t <:< typeOf[Option[DateTime]] => toDateTimeOpt(first)
            case t if t <:< typeOf[List[DateTime]] => values.flatMap(toDateTimeOpt)

            // Enumeration
            case t if isEnumeration(t) => {
              val enumValues = getEnumerationValues(t)
              if(enumValues.contains(first)) {
                enumValues(first)
              } else {
                errors :+= (name, s"value must be either ${enumValues.keys.mkString(", ")}")
              }
            }

            // Field mapping error
            case _ => errors :+= (name, "unable to build the field")
          }
        } else if(defaultParams.contains(name)) {
          defaultParams(name)
        } else {
          p.typeSignature match {
            case t if t <:< typeOf[Option[_]] => None
            case t if t <:< typeOf[List[_]] => List()
            case _ => errors :+= (name, s"field required")
          }
        }
        args :+= value
      }
      m
    }.iterator.toSeq.head.asMethod)

    if(errors.nonEmpty) {
      Left(errors.head)
    } else {
      Right(constructor(args: _*).asInstanceOf[T])
    }
  }

  def isEnumeration(t: Type): Boolean = {
    val parent = t.asInstanceOf[TypeRef].pre
    parent <:< typeOf[Enumeration]
  }

  def getEnumerationValues(t: Type) : Map[String, Any] = {
    val parent = t.asInstanceOf[TypeRef].pre

    val module = currentMirror.staticModule(parent.typeSymbol.fullName)
    val obj = currentMirror.reflectModule(module)
    val enum = obj.instance.asInstanceOf[Enumeration]
    Map(enum.values.toList.map( value => {
      value.toString -> value
    }) :_*)
  }
}
