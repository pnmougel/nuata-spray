package org.nuata.core.json

import com.sksamuel.elastic4s.{RichSearchHit, HitAs, Reader}
import com.sksamuel.elastic4s.source.Indexable
import org.json4s.{DefaultFormats, Formats, Extraction}
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.Serialization._

/**
 * Created by nico on 22/03/16.
 */
object ESJackson {

  implicit val formats = DefaultFormats + SnakizeKeys.serializer

  implicit def JacksonJsonIndexable[T]: Indexable[T] = new Indexable[T] {
    override def json(t: T): String = {
      JsonMethods.mapper.writeValueAsString(Extraction.decompose(t)(formats))
    }
  }

  implicit def JacksonJsonHitAs[T: Manifest]: HitAs[T] = new HitAs[T] {
    override def as(hit: RichSearchHit): T = {
      val timestampField = hit.fieldOpt("_timestamp").map { f =>
        s""" "_timestamp": "${f.getValue.toString}", """
      }.getOrElse("")
      val scoreField = if(java.lang.Float.isNaN(hit.score)) {
        ""
      } else {
        s""","_score": ${hit.score}"""
      }
      val metaFields = s"""{"_id": "${hit.id}","_type": "${hit.`type`}","_index": "${hit.index}" ${scoreField} ,"_version": "${hit.version}",${timestampField} """
      val source = metaFields + hit.sourceAsString.substring(1)
      read[T](source)
    }
  }
}