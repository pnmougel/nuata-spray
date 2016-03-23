package org.nuata.users

import org.nuata.authentication.Role.Role
import org.nuata.models.EsModel

/**
 * Created by nico on 29/12/15.
 */
case class User(_id: Option[String],
                _score: Option[Double],
                email: String,
                name: Option[String],
                roles: Role,
                password: Option[String] = None,
                token: Option[String] = None
                ) extends EsModel[User] {
  def withId(_id: String) = this.copy(_id = Some(_id))
}
