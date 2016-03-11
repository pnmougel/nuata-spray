package org.nuata.models

import org.nuata.authentication.Role.Role

/**
 * Created by nico on 29/12/15.
 */
case class User(_id: Option[String],
                _score: Option[Double],
                email: String,
                name: Option[String],
                roles: Role) extends EsModel[User] {
  def withId(_id: String) = this.copy(_id = Some(_id))
}
