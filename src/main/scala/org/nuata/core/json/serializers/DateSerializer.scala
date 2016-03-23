package org.nuata.core.json.serializers

import java.util.Date

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JInt, JNull}

/**
 * Created by nico on 17/03/16.
 */
class DateSerializer {

}

case object DateSerializer extends CustomSerializer[Date](format => (
  {
    case JInt(s) => new Date(s.toLong)
    case JNull => null
  },
  {
    case x: Date => JInt(x.getTime)
  }
  )
)
