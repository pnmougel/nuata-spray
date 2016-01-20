package org.nuata.services

import com.optimaize.langdetect.LanguageDetectorBuilder
import com.optimaize.langdetect.ngram.NgramExtractors
import com.optimaize.langdetect.profiles.LanguageProfileReader
import com.optimaize.langdetect.text.CommonTextObjectFactories
import org.nuata.authentication.Authenticator
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.Settings
import spray.routing.HttpService

import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by nico on 28/12/15.
 */
trait LanguageDetectorService extends HttpService with Authenticator with RouteRegistration {
  registerRoute { path("language" / Segment) { text =>
    get {
      //      authenticate(basicUserAuthenticator) { authInfo =>
      //        authorize(authInfo.hasPermission("test")) {
      val textObject = LanguageDetectorConfig.textObjectFactory.forText(text)
      val languageDetector = LanguageDetectorConfig.languageDetector.detect(textObject)
      complete {
        if(languageDetector.isPresent) languageDetector.get().getLanguage else ""
      }
    }
  }}
}

object LanguageDetectorConfig {
  val languageProfiles = new LanguageProfileReader().readAllBuiltIn()
  val languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
    .withProfiles(languageProfiles)
    .build()
  val textObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText()
}