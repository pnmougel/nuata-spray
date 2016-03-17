//package org.nuata.services
//
///**
// * Created by nico on 11/03/16.
// */
//import java.lang.management.ManagementFactory
//
//import com.typesafe.config.Config
//import kamon.metric.{ SingleInstrumentEntityRecorder, MetricKey, Entity }
//import kamon.statsd.MetricKeyGenerator
//
//class KamonKeyGenerator(config: Config) extends MetricKeyGenerator {
//  type Normalizer = String ⇒ String
//
//  val configSettings = config.getConfig("kamon.statsd.simple-metric-key-generator")
//  val application = configSettings.getString("application")
//  val includeHostname = configSettings.getBoolean("include-hostname")
//  val hostnameOverride = configSettings.getString("hostname-override")
//  val normalizer = createNormalizer(configSettings.getString("metric-name-normalization-strategy"))
//
//  val normalizedHostname =
//    if (hostnameOverride.equals("none")) normalizer(hostName)
//    else normalizer(hostnameOverride)
//
//  val baseName: String =
//    if (includeHostname) s"$application.$normalizedHostname"
//    else application
//
//  def generateKey(entity: Entity, metricKey: MetricKey): String = entity.category match {
//    case "trace-segment" ⇒
//      s"${baseName}.trace.${normalizer(entity.tags("trace"))}.segments.${normalizer(entity.name)}.${metricKey.name}"
//
//    case _ if SingleInstrumentEntityRecorder.AllCategories.contains(entity.category) ⇒
//      val tagValues = entity.tags.values
//      val tags = if(tagValues.isEmpty) {
//        ""
//      } else {
//        entity.tags.values.mkString(".", ".", "")
//      }
//      s"${baseName}.${entity.category}.${normalizer(entity.name)}${tags}"
//
//    case _ ⇒
//      val tagValues = entity.tags.values
//      val tags = if(tagValues.isEmpty) {
//        ""
//      } else {
//        entity.tags.values.mkString(".", ".", "")
//      }
//      s"${baseName}.${entity.category}.${normalizer(entity.name)}${tags}.${metricKey.name}"
//
//  }
//
//  def hostName: String = ManagementFactory.getRuntimeMXBean.getName.split('@')(1)
//
//  def createNormalizer(strategy: String): Normalizer = strategy match {
//    case "percent-encode" ⇒ PercentEncoder.encode
//    case "normalize"      ⇒ (s: String) ⇒ s.replace(": ", "-").replace(" ", "_").replace("/", "_").replace(".", "_")
//  }
//}
//
//object PercentEncoder {
//
//  def encode(originalString: String): String = {
//    val encodedString = new StringBuilder()
//
//    for (character ← originalString) {
//      if (shouldEncode(character)) {
//        encodedString.append('%')
//        val charHexValue = Integer.toHexString(character).toUpperCase
//        if (charHexValue.length < 2)
//          encodedString.append('0')
//
//        encodedString.append(charHexValue)
//
//      } else {
//        encodedString.append(character)
//      }
//    }
//    encodedString.toString()
//  }
//
//  def shouldEncode(ch: Char): Boolean = {
//    if (ch > 128 || ch < 0) true
//    else " %$&+,./:;=?@<>#%".indexOf(ch) >= 0;
//  }
//}