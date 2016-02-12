package org.nuata.shared

import com.sksamuel.elastic4s.analyzers.{FrenchLanguageAnalyzer, EnglishLanguageAnalyzer}

/**
 * Created by nico on 02/11/15.
 */
object Languages {
  val available = List("en", "fr")

  val analyzers = Map(
    "en" -> EnglishLanguageAnalyzer,
    "fr" -> FrenchLanguageAnalyzer)
}
