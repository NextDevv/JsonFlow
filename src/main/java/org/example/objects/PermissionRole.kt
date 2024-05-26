package it.unilix.easyteam.models

class PermissionRole {
    var permissionNode: PermissionNode = PermissionNode()
    var role: String = ""
    var priority: Int = 0
    var color: String = "#ffffff"

    fun hasPermission(permission: String?): Boolean {
        return permissionNode.hasPermission(permission)
    }
}