package org.nuata.mock.generators

import scala.util.Random

/**
 * Created by nico on 16/02/16.
 */
object Lang extends Generator[String]("lang") {
  val langCodes = Array("hy", "se", "ku", "ss", "st", "bs", "ae", "ba", "lb", "hr", "ta", "ka", "ar", "pi", "ty", "fr", "is", "ho", "ng", "ug", "lv", "eu", "kl", "rn", "am", "mt", "bn", "rw", "av", "uz", "uk", "co", "fj", "nr", "kr", "si", "li", "ky", "pa", "ga", "br", "tt", "so", "pt", "cs", "nd", "kv", "zu", "lo", "gl", "gn", "ny", "cu", "om", "sr", "ts", "el", "it", "sc", "su", "ca", "os", "vi", "la", "ab", "tg", "mg", "as", "yo", "dv", "tl", "nl", "bg", "gv", "bi", "ko", "rm", "or", "eo", "tk", "mk", "dz", "oc", "et", "af", "de", "bm", "xh", "ps", "cr", "ch", "yi", "ha", "cy", "nb", "ki", "sn", "to", "ig", "iu", "cv", "ur", "oj", "fy", "ln", "jv", "ru", "ht", "kw", "ml", "th", "id", "ce", "sq", "ia", "sv", "tr", "da", "my", "sg", "wo", "lg", "mh", "ve", "kj", "sm", "en", "gu", "tn", "he", "bh", "kn", "gd", "sk", "na", "az", "wa", "es", "ak", "fo", "hi", "vo", "tw", "te", "mr", "ie", "an", "nn", "io", "sw", "be", "qu", "hz", "sd", "lu", "mi", "ja", "aa", "sa", "za", "fi", "bo", "ro", "kg", "ne", "ee", "ff", "lt", "no", "km", "kk", "sl", "ik", "ay", "ti", "ii", "fa", "mn", "zh", "ms", "hu", "nv", "pl", "ks")

//  def generate() = langCodes(Random.nextInt(langCodes.length))
  def generate() = "en"
}
