package it.unilix.easyteam.models

class PermissionNode {
    val permissions = mutableListOf("easyteam.create", "easyteam.info", "easyteam.list", "easyteam.help")
    private var allowAll = false
    private var admin = false // for op and authorized users

    fun addPermission(permission: String) {
        permissions.add(permission)
    }

    fun removePermission(permission: String) {
        permissions.remove(permission)
    }

    fun hasPermission(permission: String?): Boolean {
        if (permission == null) return true
        if(permission == "easyteam.admin") return admin

        return allowAll || permissions.contains(permission) || admin
    }

    fun allowAll() {
        allowAll = true
    }

    fun disallowAll() {
        allowAll = false
    }

    fun isAdmin(): Boolean {
        return admin
    }

    fun setAdmin(admin: Boolean) {
        this.admin = admin
    }

    @JvmName("getPermissions_")
    fun getPermissions(): List<String> {
        return permissions
    }

    fun toDefault() {
        permissions.clear()
        permissions.addAll(listOf("easyteam.create", "easyteam.info", "easyteam.list", "easyteam.help"))
        allowAll = false
        admin = false
    }
}