package org.nuata.core.utils

import java.security.MessageDigest

/**
 * Created by nico on 25/02/16.
 */
object StringHash {
  def getHash(string: String) : String = {
    val m = MessageDigest.getInstance("SHA1")
    m.update(string.getBytes)
    m.digest().map("%02X" format _).mkString
  }
}
