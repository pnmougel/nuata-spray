package org.nuata.models.edges

/**
 * Created by nico on 04/03/16.
 */
case class Edges(itemRef: Option[Array[ItemEdge]] = None,
                 attributeRef: Option[Array[AttributeEdge]] = None,
                 externalId: Option[Array[ExternalIdEdge]] = None,
                 url: Option[Array[UrlEdge]] = None,
                 math: Option[Array[MathEdge]] = None,
                 text: Option[Array[TextEdge]] = None,
                 localizedText: Option[Array[LocalizedTextEdge]] = None,
                 quantity: Option[Array[QuantityEdge]] = None,
                 time: Option[Array[TimeEdge]] = None,
                 coordinate: Option[Array[CoordinateEdge]] = None,
                 commonsMedia: Option[Array[CommonsMediaEdge]] = None) {
}
