package org.nuata.core.json

import org.json4s.DefaultFormats
import spray.httpx.Json4sJacksonSupport

/**
 * Created by nico on 23/12/15.
 */


trait Json4sProtocol extends Json4sJacksonSupport {

  implicit def json4sJacksonFormats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all + SnakizeKeys.serializer
}
