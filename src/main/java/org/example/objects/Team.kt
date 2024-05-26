package it.unilix.easyteam.models

import it.unilix.easyteam.EasyTeam
import it.unilix.easyteam.utils.msg
import it.unilix.json.JsonExclude
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.UUID.*
import java.util.concurrent.CopyOnWriteArrayList

class Team {
    @JsonExclude
    val plugin = JavaPlugin.getPlugin(EasyTeam::class.java)

    val members = CopyOnWriteArrayList<TeamMember>()
    val banList = CopyOnWriteArrayList<UUID>()
    var friendlyFire = false
    var name = "N/A"
    var color = "#4287f5" // Hex color
    var discord = "N/A"
    var uuid: UUID = randomUUID()
    var owner: TeamMember = TeamMember()
    var timeCreation: Long = System.currentTimeMillis()
    private var roles = mutableListOf<PermissionRole>(
        PermissionRole().apply {
            role = "member"
            permissionNode = PermissionNode().apply {
                permissions.add("easyteam.teamchat")
            }
            priority = 0
        }
    )

    fun addMember(member: TeamMember) {
        members.add(member)
    }

    fun assignRole(member: TeamMember, name: String) {
        val role = roles.find { it.role == name } ?: PermissionRole().apply {
            role = name
            roles.add(this)
        }

        member.role = role.role
        plugin.jsonLoader.saveMember(member)
    }

    fun removeRole(member: TeamMember) {
        member.role = "member"
        plugin.jsonLoader.saveMember(member)
    }

    fun getRole(member: TeamMember): PermissionRole {
        return roles.find { it.role == member.role } ?: PermissionRole().apply { role = member.role }
    }

    fun getRole(name: String): PermissionRole {
        return roles.find { it.role == name } ?: PermissionRole().apply { role = name }
    }

    fun addMember(uuid: UUID) {
        val member = plugin.jsonLoader.getMember(uuid) ?: return
        member.role = "member"
        members.add(member)
    }

    fun removeMember(member: TeamMember) {
        members.remove(member)
    }

    fun clearMembers() {
        members.clear()
    }

    fun getMembers(): List<TeamMember> {
        return members
    }

    fun hasMember(member: TeamMember): Boolean {
        return members.contains(member)
    }

    fun hasMember(uuid: String): Boolean {
        return members.any { it.uuid.toString() == uuid }
    }

    fun ban(uuid: UUID) {
        banList.add(uuid)
    }

    fun isBannded(uuid: UUID): Boolean {
        return banList.contains(uuid)
    }

    fun unban(uuid: UUID) {
        banList.remove(uuid)
    }

    fun msg(message: String) {
        members.forEach { it.getPlayer()?.msg(message) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as Team

        return uuid == other.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString(): String {
        return "Team(friendlyFire=$friendlyFire, name='$name', color='$color', discord='$discord', uuid=$uuid, owner=${owner.uuid}, timeCreation=$timeCreation)"
    }

    fun getMember(uniqueId: UUID): TeamMember? {
        return members.find { it.uuid == uniqueId }
    }

    fun isOwner(uniqueId: UUID): Boolean {
        return owner.uuid == uniqueId
    }

    fun getMemberByName(target: String): TeamMember? {
        return members.find { it.name == target }
    }
}