package org.nuata.mock.generators

import org.nuata.models.Item

/**
 * Created by nico on 17/02/16.
 */
object ItemGenerator extends Generator[Item]("item") {
  def generate() : Item = {
    val labels = Map(generateSeq("nbLabels", {
      val label = LabelGenerator.generate()
      (label.lang, label)
    }) : _*)
    val edges = generateSeq("nbEdges", EdgeGenerator.generate()).toArray.groupBy(_.attributeId)

    Item(None, None, labels, edges)
  }
}
