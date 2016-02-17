package org.nuata.mock.generators.exceptions

/**
 * Created by nico on 17/02/16.
 */
class MissingPropertyException(message: String, cause: Throwable) extends RuntimeException(message) {
  def this(message: String) = this(message, null)
}

class InvalidParameterException(message: String, cause: Throwable) extends RuntimeException(message) {
  def this(message: String) = this(message, null)
}