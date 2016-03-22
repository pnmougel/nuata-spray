package org.nuata.attributes

import org.nuata.core.NodeRepository
import org.nuata.models.Attribute
import org.nuata.shared.json.ESJackson._

/**
 * Created by nico on 24/02/16.
 */
object AttributeRepository extends NodeRepository[Attribute]("attributes")