package org.nuata.models

import com.github.t3hnar.bcrypt._
import org.mindrot.jbcrypt.BCrypt
import org.nuata.authentication.Role.Role

/**
 * Created by nico on 29/12/15.
 */
case class User(_id: Option[String],
                _score: Option[Double],
                email: String,
                name: Option[String],
                roles: Role) {
}
