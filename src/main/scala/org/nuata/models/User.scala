package org.nuata.models

import com.github.t3hnar.bcrypt._
import org.mindrot.jbcrypt.BCrypt
import org.nuata.authentication.Role.Role

/**
 * Created by nico on 29/12/15.
 */
case class User(var _id: Option[String],
                _score: Option[Double],
                login: String,
                hashedPassword: Option[String] = None,
                mail: String,
                roles: List[Role]) {
  def withPassword(password: String) = copy (hashedPassword = Some(password.bcrypt(generateSalt)))

  def passwordMatches(password: String): Boolean = hashedPassword.exists(hp => BCrypt.checkpw(password, hp))
}
