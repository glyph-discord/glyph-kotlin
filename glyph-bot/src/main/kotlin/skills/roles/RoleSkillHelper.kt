/*
 * RoleSkillHelper.kt
 *
 * Glyph, a Discord bot that uses natural language instead of commands
 * powered by DialogFlow and Kotlin
 *
 * Copyright (C) 2017-2020 by Ian Moore
 *
 * This file is part of Glyph.
 *
 * Glyph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.yttr.glyph.bot.skills.roles

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.yttr.glyph.bot.ai.AIResponse
import org.yttr.glyph.bot.extensions.cleanMentionedMembers
import org.yttr.glyph.bot.messaging.Response
import org.yttr.glyph.shared.config.server.SelectableRolesConfig

/**
 * Helps find a selectable role a member is asking about
 */
object RoleSkillHelper {
    /**
     * Attempt to figure out what selectable role a member is asking about or tell them what went wrong
     *
     * @param event the message event
     * @param ai the ai response
     * @param success the callback to run if the desired role exists and is found
     */
    fun getInstance(
        event: MessageReceivedEvent,
        ai: AIResponse,
        selectableRolesConfig: SelectableRolesConfig,
        success: (desiredRole: Role, selectableRoles: List<Role>, targets: List<Member>) -> Response
    ): Response {
        //Get the list of target(s) based on the mentions in the messages
        val targets: List<Member> = when {
            event.message.mentionsEveryone() -> event.guild.members
            //@here -> event.guild.members.filter { it.onlineStatus == OnlineStatus.ONLINE }
            event.message.cleanMentionedMembers.isNotEmpty() -> event.message.cleanMentionedMembers
            else -> listOf(event.member!!)
        }

        //Extract the desired role name and make a list of all available selectable roles
        val desiredRoleName: String = ai.result.getStringParameter("role")?.removeSurrounding("\"") ?: ""
        if (desiredRoleName.isEmpty()) {
            return Response.Volatile("I could not find a role name in your message!")
        }

        val desiredRole = event.guild.getRolesByName(desiredRoleName, true).firstOrNull()
            ?: return Response.Volatile("Role `$desiredRoleName` does not exist!")
        val selectableRoles = selectableRolesConfig.roles.mapNotNull { event.guild.getRoleById(it) }
        if (selectableRoles.isEmpty()) {
            return Response.Volatile("There are no selectable roles configured for this server!")
        }

        return if (selectableRoles.contains(desiredRole)) {
            success(desiredRole, selectableRoles, targets)
        } else {
            Response.Volatile("Sorry, `$desiredRoleName` is not a selectable role!")
        }
    }
}
