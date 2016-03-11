package org.nuata.models

case class SearchSuggestion(id: String, score: Double, name: Option[String], description: Option[String], `type`: String)


