/*
 * DiscordUser.kt
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

package me.ianmooreis.glyph.config.discord

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header

/**
 * Represents a Discord user
 */
class User(
    /**
     * The user snowflake id
     */
    val id: Long,
    /**
     * The user's name
     */
    val username: String,
    /**
     * Guilds the user belongs to
     */
    val guilds: List<UserGuild>
) {
    companion object {
        private val client: HttpClient = HttpClient {
            install(JsonFeature)
        }

        /**
         * The base url for the user API endpoints
         */
        private const val USER_API_BASE: String = "https://discord.com/api/users/@me"

        /**
         * Get a user, based on
         */
        suspend fun getUser(token: String): User {
            val user: User = client.get(USER_API_BASE) {
                header("Authorization", "Bearer $token")
            }
            val guilds: List<UserGuild> = client.get("$USER_API_BASE/guilds") {
                header("Authorization", "Bearer $token")
            }

            // TODO: Something that feels less "hacky"
            return User(user.id, user.username, guilds)
        }
    }
}