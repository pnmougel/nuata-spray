package org.nuata.core.json

import org.json4s.FieldSerializer
import org.json4s.JsonAST.JField

/**
 * Created by nico on 22/03/16.
 */
object SnakizeKeys {
  def toCamelCase: PartialFunction[JField, JField] = {
    case JField(name, x) => JField(toCamelCase(name), x)
  }

  def toSnakeCase: PartialFunction[(String, Any), Option[(String, Any)]] = {
    case (name, x) => Some(toSnakeCase(name), x)
  }

  def toSnakeCase(name: String) = {
    val builder = new StringBuilder()
    for((c, i) <- name.zipWithIndex) {
      if(c.isUpper) {
        if(i != 0) {
          builder.append('_')
        }
        builder.append(c.toLower)
      } else {
        builder.append(c)
      }
    }
    builder.toString()
  }

  def toCamelCase(name: String) = {
    var isPrevUnderscore = false
    val builder = new StringBuilder()
    for((c, i) <- name.zipWithIndex) {
      if(c == '_' && i != 0) {
        isPrevUnderscore = true
      } else {
        if(isPrevUnderscore) {
          builder.append(c.toUpper)
        } else {
          builder.append(c)
        }
        isPrevUnderscore = false
      }
    }
    builder.toString()
  }


  val serializer = FieldSerializer[Any](
    toSnakeCase,
    toCamelCase
  )
}
