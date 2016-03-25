package org.nuata.authentication

import org.nuata.authentication.Permission.Permission
import org.nuata.authentication.Role.Role
import org.nuata.users.User

/**
 * Created by nico on 29/12/15.
 */
class AuthInfo(val user: User) {
  def hasPermission(permission: Permission) = {
//     user.roles.exists(role => roleHasPermission(role, permission))
    roleHasPermission(user.roles, permission)
//    true
  }

  def roleHasPermission(role: Role, permission: Permission): Boolean = {
    if (role == Role.Admin) {
      true
    } else if (role == Role.User) {
      permission != Permission.Admin
    } else {
      false
    }
  }
}
