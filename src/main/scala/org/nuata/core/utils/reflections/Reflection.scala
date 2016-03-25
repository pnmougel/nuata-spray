package org.nuata.core.utils.reflections

import org.reflections.Reflections

import scala.collection.JavaConversions._
import scala.reflect.ClassTag


/**
 * Created by nico on 17/03/16.
 */
object Reflection {
  val reflections = new Reflections("org.nuata")

  def newInstanceOf[T](classOf: Class[_ <: T]): Option[T] = {
    (for(constructor <- classOf.getConstructors
        if constructor.getParameterCount == 0) yield {
      constructor.setAccessible(true)
      constructor.newInstance().asInstanceOf[T]
    }).headOption
  }

  def getInstancesOf[T](implicit tag: ClassTag[T]): Array[T] = {
    val subTypes = reflections.getSubTypesOf(tag.runtimeClass)
    (for (subType <- subTypes.toList;
         constructor <- subType.getDeclaredConstructors
         if constructor.getParameterCount == 0
    ) yield {
      constructor.setAccessible(true)
      constructor.newInstance().asInstanceOf[T]
    }).toArray
  }
}
