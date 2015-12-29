package org.nuata.services

import org.json4s.Extraction
import org.json4s.JsonAST.JObject
import org.nuata.repositories._
import spray.routing._
import org.nuata.models.Dimension
import org.nuata.repositories.{OoiRepository, DimensionRepository}
import org.nuata.shared._
import spray.http.StatusCodes
import spray.httpx.unmarshalling._
import org.json4s.{Extraction, DefaultFormats}
import spray.httpx.{Json4sSupport, Json4sJacksonSupport}
import spray.routing._
import spray.http._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.json4s.JsonDSL._

import scala.reflect.runtime._

case class Foo(name: String = "test", age: Int = 42)

/**
 * Created by nico on 27/12/15.
 */
trait SearchService extends HttpService with Json4sProtocol {
  implicit val formats = DefaultFormats

  val items = Map(
    "dimension" -> DimensionRepository,
    "ooi" -> OoiRepository,
    "category" -> CategoryRepository,
    "fact" -> FactRepository,
    "source" -> SourceRepository,
    "unit" -> UnitRepository
  )
  import scala.util.Try

  def getPositiveIntFromParam(key: String, params : Map[String, List[String]]): Option[Int] = {
    val param = params.getOrElse(key, List("")).head
    Try(param.toInt).toOption.filter(_ >= 0)
  }

  import scala.reflect.runtime._
  import reflect._
  import scala.reflect.runtime.{ currentMirror => cm }
  import scala.reflect.runtime.universe._

//  def newDefault[A](implicit t: reflect.ClassTag[A]): mutable.HashMap[String, Any] = {
//    import reflect.runtime.{universe => ru, currentMirror => cm}
//
//    val default = mutable.HashMap[String, Any]()
//
//    val clazz  = cm.classSymbol(t.runtimeClass)
//    val mod    = clazz.companion.asModule
//    val im     = cm.reflect(cm.reflectModule(mod).instance)
//    val ts     = im.symbol.typeSignature
//    val mApply = ts.member(ru.TermName("apply")).asMethod
//    mApply.paramLists.flatten.zipWithIndex.map { case (p, i) =>
//      val defMethod = ts.member(ru.TermName(s"apply$$default$$${i+1}"))
//      if(defMethod.isMethod) {
//        val mDef = defMethod.asMethod
//        val value = im.reflectMethod(mDef)()
//        default(p.name.toString) = value
//        println(p.name)
//        println(value)
//      }
//
////      val mDef = ts.member(ru.TermName(s"apply$$default$$${i+1}")).asMethod
////      im.reflectMethod(mDef)()
//    }
//    default
////    im.reflectMethod(mApply)(args: _*).asInstanceOf[A]
//  }


//  def newDefault2[A](t: reflect.ClassTag[A]): mutable.HashMap[String, Any] = {
//    import reflect.runtime.{universe => ru, currentMirror => cm}
//
//    val default = mutable.HashMap[String, Any]()
//
//    val clazz  = cm.classSymbol(t.runtimeClass)
//    val mod    = clazz.companion.asModule
//    val im     = cm.reflect(cm.reflectModule(mod).instance)
//    val ts     = im.symbol.typeSignature
//    val mApply = ts.member(ru.TermName("apply")).asMethod
//    mApply.paramLists.flatten.zipWithIndex.map { case (p, i) =>
//      val defMethod = ts.member(ru.TermName(s"apply$$default$$${i+1}"))
//      if(defMethod.isMethod) {
//        val mDef = defMethod.asMethod
//        val value = im.reflectMethod(mDef)()
//        default(p.name.toString) = value
//        println(p.name)
//        println(value)
//      }
//
//      //      val mDef = ts.member(ru.TermName(s"apply$$default$$${i+1}")).asMethod
//      //      im.reflectMethod(mDef)()
//    }
//    default
//    //    im.reflectMethod(mApply)(args: _*).asInstanceOf[A]
//  }

  def queryParamsToCaseClass[T](params: Map[String, List[String]])(implicit tag: TypeTag[T]) = {
    var args = Vector[Any]()

//    val default = mutable.HashMap[String, Any]()

    val classTag = ClassTag[T]( typeTag[T].mirror.runtimeClass( typeTag[T].tpe ) )
    val mod    = cm.classSymbol(classTag.runtimeClass).companion.asModule
    val im     = cm.reflect(cm.reflectModule(mod).instance)
    val ts     = im.symbol.typeSignature
    val mApply = ts.member(universe.TermName("apply")).asMethod

    val defaultParams = Map((mApply.paramLists.flatten.zipWithIndex.map { case (p, i) =>
      (p.name.toString, ts.member(universe.TermName(s"apply$$default$$${i+1}")))
    }.filter( _._2.isMethod ).map( m => {
      (m._1, im.reflectMethod(m._2.asMethod)())
    })) :_*)

//    mApply.paramLists.flatten.zipWithIndex.map { case (p, i) =>
//      val defMethod = ts.member(universe.TermName(s"apply$$default$$${i+1}"))
//      if(defMethod.isMethod) {
//        val mDef = defMethod.asMethod
//        val value = im.reflectMethod(mDef)()
//        default(p.name.toString) = value
//        println(p.name)
//        println(value)
//      }
//    }

    println(defaultParams)
//    val f = newDefault[Foo]
//    val g = newDefault2[T](classTag)
//    println(g)

//    val im = currentMirror.reflect(tag.tpe.typeSymbol.asModule)
//    val ts = im.symbol.typeSignature
//    val name = "default"
//    val defarg = ts member TermName(s"$name$$default$$${2}")
//    val foo = (im.reflectMethod(defarg.asMethod))()
//    println(foo)

//    for(m <- tag.tpe.members; if(m.isMethod)) {
//      println(m.fullName)
//      val foo = (im.reflectMethod(m.asMethod))()
//      println(foo)
//    }

    currentMirror.reflectClass(tag.tpe.typeSymbol.asClass).reflectConstructor(tag.tpe.members.filter { m =>
      m.isMethod && m.asMethod.isConstructor
    }.map { m =>
      for(p <- m.asMethod.paramLists.head) {
        val name = p.name.toString
        val pType = p.typeSignature

//        val defarg = pType member TermName(s"$name$$default$$${1}")


        val value = if(params.contains(name)) {
          val values = params(name)
          val first = values.head
          pType match {
            case t if t <:< typeOf[String] => first
            case t if t <:< typeOf[Option[String]] => Some(first)
            case t if t <:< typeOf[List[String]] => values

            case t if t <:< typeOf[Int] => first.toInt
            case t if t <:< typeOf[Option[Int]] => Try(first.toInt).toOption
            case t if t <:< typeOf[List[Int]] => values.map( v => Try(first.toInt).toOption ).filter(_.isDefined).map(_.get)

            case t if t <:< typeOf[Double] => first.toDouble
            case t if t <:< typeOf[Option[Double]] => Try(first.toDouble).toOption
            case t if t <:< typeOf[List[Double]] => values.map( v => Try(first.toDouble).toOption ).filter(_.isDefined).map(_.get)
          }
//          if(pType <:< typeOf[String]) {
//            first
//          } else if(pType <:< typeOf[Option[String]]) {
//            Some(first)
//          } else if(pType <:< typeOf[List[String]]) {
//            values
//          } else if(pType <:< typeOf[Int]) {
//            first.toInt
//          } else if(pType <:< typeOf[Option[Int]]) {
//            Try(first.toInt).toOption
//          } else if(pType <:< typeOf[List[Int]]) {
//            values.map( v => Try(first.toInt).toOption ).filter(_.isDefined).map(_.get)
//          } else if(pType <:< typeOf[Double]) {
//            first.toDouble
//          } else if(pType <:< typeOf[Option[Double]]) {
//            Try(first.toDouble).toOption
//          } else if(pType <:< typeOf[List[Double]]) {
//            values.map( v => Try(first.toDouble).toOption ).filter(_.isDefined).map(_.get)
//          }
        } else if(pType <:< typeOf[Option[_]]) {
          None
        } else if(pType <:< typeOf[List[_]]) {
          List()
        }
        args :+= value
      }
//      println(m.asMethod.paramLists)
//      println(m.asMethod.paramLists.head)
//      println(m.asMethod.typeParams)
//      println(m.name)
//      println(m.fullName)
//      println(m.info.termSymbol.name)
      m
    }.iterator.toSeq.head.asMethod)(args: _*).asInstanceOf[T]

  }

  def newCase[A]()(implicit t: ClassTag[A]): A = {
    val module = (cm classSymbol t.runtimeClass).companion.asModule
    val im = cm reflect (cm reflectModule module).instance
    defaut[A](im, "apply")
  }

  def defaut[A](im: InstanceMirror, name: String): A = {
    val at = TermName(name)
    val ts = im.symbol.typeSignature
    val method = (ts member at).asMethod

    // either defarg or default val for type of p
    def valueFor(p: Symbol, i: Int): Any = {
      val defarg = ts member TermName(s"$name$$default$$${i+1}")
      if (defarg != NoSymbol) {
        println(s"default $defarg")
        (im reflectMethod defarg.asMethod)()
      } else {
        println(s"def val for $p")
        p.typeSignature match {
          case t if t =:= typeOf[String] => null
          case t if t =:= typeOf[Int]    => 0
          case x                         => throw new IllegalArgumentException(x.toString)
        }
      }
    }
    val args = (for (ps <- method.paramLists; p <- ps) yield p).zipWithIndex map (p => valueFor(p._1,p._2))
    (im reflectMethod method)(args: _*).asInstanceOf[A]
  }

  val searchRoutes = path("search") {
    get {
      parameterMultiMap { params =>

        val foo = queryParamsToCaseClass[Foo](params)
        println(foo)

        val name = params.getOrElse("name", List("")).head
        val start = getPositiveIntFromParam("start", params).getOrElse(0)
        val limit = getPositiveIntFromParam("limit", params).getOrElse(10)
        val expand = getPositiveIntFromParam("expand", params).getOrElse(0)
        val repositories = params.getOrElse("from", List("")).map(items)
//        val filters = Map(
//          "categoryIds" -> params.getOrElse("categoryIds", List()),
//          "dimensionIds" -> params.getOrElse("dimensionIds", List()),
//          "sourceIds" -> params.getOrElse("sourceIds", List())
//        )

        val searchOptions = SearchOptions(name, NameOperations.StartsWith, start, limit,
          categoryId = List(),
          parentId = List(),
          sourceId = List(),
          expand = expand)
        complete( {
          Future.sequence(repositories.map(repository => {
            repository.searchAndExpand(searchOptions).map( jsonValues => {
              (repository.`type`, Extraction.decompose(jsonValues))
            } )
          })).map( seqOfJson => {
            Extraction.decompose(seqOfJson.foldLeft(JObject()) { (value, acc) => value ~ acc })
          })
        })
      }
    }
  }

  val searchRoutesOld = path("search") {
    get {
      parameterMultiMap { params =>
        val name = params.getOrElse("name", List("")).head
        val start = getPositiveIntFromParam("start", params).getOrElse(0)
        val limit = getPositiveIntFromParam("limit", params).getOrElse(10)
        val expand = getPositiveIntFromParam("expand", params).getOrElse(0)
        val repositories = params.getOrElse("from", List("")).map(items)
//        val filters = Map(
//          "categoryIds" -> params.getOrElse("categoryIds", List()),
//          "dimensionIds" -> params.getOrElse("dimensionIds", List()),
//          "sourceIds" -> params.getOrElse("sourceIds", List())
//        )

        val searchOptions = SearchOptions(name, NameOperations.StartsWith, start, limit,
          categoryId = params("categoryId"),
          parentId = params("categoryId"),
          sourceId = params("categoryId"),
          expand = expand)
        complete( {
          Future.sequence(repositories.map(repository => {
            repository.searchAndExpand(searchOptions).map( jsonValues => {
              (repository.`type`, Extraction.decompose(jsonValues))
            } )
          })).map( seqOfJson => {
            Extraction.decompose(seqOfJson.foldLeft(JObject()) { (value, acc) => value ~ acc })
          })
        })
      }
    }
  }

  /*
  val searchRoutes = (for((itemName, repository) <- items.toList) yield {
    path(itemName / "search") {
      get {
        parameterMultiMap { params =>
          val name = params.getOrElse("name", List("")).head
          val start = getPositiveIntFromParam("start", params).getOrElse(0)
          val limit = getPositiveIntFromParam("limit", params).getOrElse(10)
          val expand = getPositiveIntFromParam("expand", params).getOrElse(0)
          val repositories = params.getOrElse("from", List("")).map(items)
          val filters = Map(
            "categoryIds" -> params.getOrElse("categoryIds", List()),
            "dimensionIds" -> params.getOrElse("dimensionIds", List()),
            "sourceIds" -> params.getOrElse("sourceIds", List())
          )

          val searchOptions = SearchOptions(name, NameOperations.StartsWith, start, limit, filters, expand = expand)
          println(searchOptions)
          complete( {
            Future.sequence(repositories.map(repository => {
              repository.searchAndExpand(searchOptions).map( jsonValues => {
                (repository.`type`, Extraction.decompose(jsonValues))
              } )
            })).map( seqOfJson => {
              Extraction.decompose(seqOfJson.foldLeft(JObject()) { (value, acc) => value ~ acc })
            })
            //            repository.searchAndExpand(searchOptions).map( item => {
            //              Extraction.decompose(item)
            //            })
          })
        }
      }
    }
  }).reduce((a, b) => { a ~ b })
  */
}
