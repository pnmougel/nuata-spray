package org.nuata.shared

import com.sksamuel.elastic4s.{ElasticsearchClientUri, ElasticClient}


/**
 * Created by nico on 14/10/15.
 */
object ElasticSearch {
   val client = ElasticClient.transport(ElasticsearchClientUri(
      Settings.getString("elasticsearch.interface"),
      Settings.getInt("elasticsearch.port")))
}
