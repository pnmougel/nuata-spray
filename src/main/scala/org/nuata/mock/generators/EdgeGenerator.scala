package org.nuata.mock.generators

import java.util.UUID

import org.nuata.models.Edge
import org.nuata.models.datavalues.DataValue

import scala.util.Random

/**
 * Created by nico on 17/02/16.
 */
object EdgeGenerator extends Generator[Edge]("edge") {
  def generate(): Edge = {
    val attributeId = UUID.randomUUID().toString
    val value = DataValueGenerator.generate()
    val qualifiers = generateSeq("nbQualifiers", EdgeValueOnlyGenerator.generate()).toArray
    val sources = generateSeq("nbSources", {
      generateSeq("nbEdgeBySource", EdgeValueOnlyGenerator.generate()).toArray
    }).toArray

    Edge(attributeId, value, qualifiers, sources)
  }
}
