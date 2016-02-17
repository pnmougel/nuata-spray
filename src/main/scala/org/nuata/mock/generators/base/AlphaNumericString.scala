package org.nuata.mock.generators.base

import org.nuata.mock.generators.Generator

import scala.util.Random

/**
 * Created by nico on 16/02/16.
 */
object AlphaNumericString extends Generator[String]("string") {
  def generate() = {
    Random.alphanumeric.take(getInt("length")).mkString
  }
}
