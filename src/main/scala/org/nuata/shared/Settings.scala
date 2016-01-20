package org.nuata.shared

import com.typesafe.config.ConfigFactory

/**
 * Created by nico on 31/12/15.
 */
object Settings {
  val conf = ConfigFactory.load()
}
