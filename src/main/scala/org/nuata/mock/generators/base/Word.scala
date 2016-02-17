package org.nuata.mock.generators.base

import org.nuata.mock.generators.Generator
import scala.util.Random

/**
 * Created by nico on 16/02/16.
 */
object Word extends Generator[String]("word") {
  def generate() = {
    val length = getInt("length")
    Random.alphanumeric.filter(_.isLetter).take(length).mkString.toLowerCase
  }
}
