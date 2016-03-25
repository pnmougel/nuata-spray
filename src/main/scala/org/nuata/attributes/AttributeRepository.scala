package org.nuata.attributes

import org.nuata.core.NodeRepository
import org.nuata.core.json.ESJackson._
import org.nuata.models.Attribute

/**
 * Created by nico on 24/02/16.
 */
object AttributeRepository extends NodeRepository[Attribute]("attributes")