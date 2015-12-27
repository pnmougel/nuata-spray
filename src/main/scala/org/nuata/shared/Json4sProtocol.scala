package org.nuata.shared

import org.json4s.DefaultFormats
import org.nuata.repositories.NameOperations
import org.nuata.repositories.NameOperations._
import spray.httpx.Json4sJacksonSupport
import org.json4s.ext.{EnumSerializer, EnumNameSerializer}

/**
 * Created by nico on 23/12/15.
 */

trait Json4sProtocol extends Json4sJacksonSupport {

  implicit def json4sJacksonFormats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all + new EnumNameSerializer(NameOperations)
}
