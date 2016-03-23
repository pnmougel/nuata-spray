package org.nuata.core

import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import org.nuata.core.settings.Settings


/**
 * Created by nico on 14/10/15.
 */
object ElasticSearch {
   val client = ElasticClient.transport(ElasticsearchClientUri(
      Settings.getString("elasticsearch.interface"),
      Settings.getInt("elasticsearch.port")))
}
