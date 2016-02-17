package org.nuata.mock.generators

import com.typesafe.config.Config
import org.nuata.mock.generators.exceptions.MissingPropertyException
import org.nuata.shared.Settings

import scala.util.Random

/**
 * Created by nico on 17/02/16.
 */
abstract class Generator[T](name: String) {

  def generate: T

  def generateSeq[T](property: String, f: => T): Seq[T] = {
    val n = getInt(property)
    for(_ <- 0 to n) yield {
      f
    }
  }

  def getString(property: String): String = {
    val config = getConfig(property)
    val values = config.getStringList("values")
    values.get(Random.nextInt(values.size()))
  }

  def getInt(property: String): Int = {
    val config = getConfig(property)
    val propertyType = config.getString("type")
    val generatorType = config.getString("method")
    if (propertyType == "int") {
      generateInt(generatorType, config)
    } else {
      0
    }
  }

  def getConfig(property: String): Config = {
    val propertyPath = s"default.${name}.${property}"
    if(!Settings.hasPath(propertyPath)) {
      throw new MissingPropertyException(s"path ${propertyPath} not found")
    } else {
      Settings.getConfig(propertyPath)
    }
  }

  def getBoolean(property: String): Boolean = {
    val config = getConfig(property)
    generateBoolean("", config)
  }

  def generateInt(generatorType: String, config: Config) : Int = {
    generatorType match {
      case "uniform" => {
        val from = config.getInt("from")
        val to = config.getInt("to")
        Random.nextInt(to - from) + from
      }
      case _ => {
        Random.nextInt()
      }
    }
  }

  def generateBoolean(generatorType: String, config: Config) : Boolean = {
    config.getDouble("p") >= Random.nextDouble()
  }
}
