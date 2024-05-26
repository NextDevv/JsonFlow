package it.unilix.easyteam.models

import com.google.gson.annotations.Expose
import it.unilix.json.JsonExclude
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class TeamMember {
    var uuid: UUID? = null
    var name: String = "N/A"
    @JsonExclude var team: Team? = null
    var teamChat: Boolean = false
    var teamUUID: UUID? = null
    var permissionNode = PermissionNode()
    var role: String = "member"

    fun getPlayer(): Player? {
        return Bukkit.getPlayer(uuid ?: return null)
    }

    fun hasPermission(permission: String?): Boolean {
        return permissionNode.hasPermission(permission) || team?.getRole(role)?.hasPermission(permission) == true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as TeamMember

        return uuid == other.uuid
    }

    override fun hashCode(): Int {
        return uuid?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "TeamMember(uuid=$uuid, name='$name', team=${team?.uuid}, teamChat=$teamChat)"
    }
}