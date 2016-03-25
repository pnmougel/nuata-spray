package org.nuata.core.utils

import com.optimaize.langdetect.LanguageDetectorBuilder
import com.optimaize.langdetect.ngram.NgramExtractors
import com.optimaize.langdetect.profiles.LanguageProfileReader
import com.optimaize.langdetect.text.CommonTextObjectFactories


object LanguageDetector {
  val languageProfiles = new LanguageProfileReader().readAllBuiltIn()
  val languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
    .withProfiles(languageProfiles)
    .build()
  val textObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText()

  def getLanguage(text: String) : Option[String] = {
    val textObject = LanguageDetector.textObjectFactory.forText(text)
    val languageDetector = LanguageDetector.languageDetector.detect(textObject)
      if(languageDetector.isPresent) Some(languageDetector.get().getLanguage) else None
  }
}