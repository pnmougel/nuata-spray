package org.nuata.attributes

import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.nuata.core.NodeRepository
import org.nuata.models.Attribute

/**
 * Created by nico on 24/02/16.
 */
object AttributeRepository extends NodeRepository[Attribute]("attributes")