package org.nuata.services

import com.optimaize.langdetect.LanguageDetectorBuilder
import com.optimaize.langdetect.ngram.NgramExtractors
import com.optimaize.langdetect.profiles.LanguageProfileReader
import com.optimaize.langdetect.text.CommonTextObjectFactories
import spray.routing.HttpService

import scala.concurrent.Future

/**
 * Created by nico on 28/12/15.
 */
trait LanguageDetectorService extends HttpService {
  val languageDetectorRoute = path("language" / Segment) { text =>
    get {
      val textObject = LanguageDetectorConfig.textObjectFactory.forText(text)
      val languageDetector = LanguageDetectorConfig.languageDetector.detect(textObject)
      complete {
        if(languageDetector.isPresent) languageDetector.get().getLanguage else ""
      }
    }
  }
}

object LanguageDetectorConfig {
  val languageProfiles = new LanguageProfileReader().readAllBuiltIn()
  val languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
    .withProfiles(languageProfiles)
    .build()
  val textObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText()
}