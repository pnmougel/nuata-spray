package org.nuata.authentication

import org.nuata.authentication.Permission._

/**
 * Created by nico on 29/12/15.
 */
object Role extends Enumeration {
  type Role = Value
  val Admin, User = Value
}
