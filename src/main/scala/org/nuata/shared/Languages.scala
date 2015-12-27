package org.nuata.shared

import com.sksamuel.elastic4s.{EnglishLanguageAnalyzer, FrenchLanguageAnalyzer}

/**
 * Created by nico on 02/11/15.
 */
object Languages {
  val available = List("en", "fr")

  val analyzers = Map(
    "en" -> EnglishLanguageAnalyzer,
    "fr" -> FrenchLanguageAnalyzer)
}
