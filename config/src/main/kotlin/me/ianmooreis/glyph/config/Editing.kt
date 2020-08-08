/*
 * Editing.kt
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

package me.ianmooreis.glyph.config

import arrow.core.Either
import arrow.core.Option
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.mustache.MustacheContent
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.sessions
import me.ianmooreis.glyph.config.discord.User
import me.ianmooreis.glyph.config.session.ConfigSession
import me.ianmooreis.glyph.shared.pubsub.PubSub
import me.ianmooreis.glyph.shared.pubsub.PubSubChannel
import me.ianmooreis.glyph.shared.pubsub.PubSubException

/**
 * Endpoints for editing the config
 */
fun Route.editing(pubSub: PubSub) {
    route("/{guildId}") {
        get {
            val guildId = call.parameters["guildId"] ?: error("No guild id given")
            call.respond(MustacheContent("edit.hbs", mapOf("guildId" to guildId)))
        }

        route("/data") {
            get {
                val guildId = call.parameters["guildId"] ?: error("No guild id given")
                val session = call.sessions.getOption<ConfigSession>()

                if (session.canManageGuild(guildId)) {
                    val response = when (val config = pubSub.ask(guildId, PubSubChannel.CONFIG_PREFIX)) {
                        is Either.Left -> HttpStatusCode.InternalServerError to when (config.a) {
                            is PubSubException.Deaf -> "Bot is completely offline, try again later."
                            is PubSubException.Ignored -> "Bot could not find the requested guild. Is it a member?"
                            else -> "Unknown error"
                        }
                        is Either.Right -> HttpStatusCode.OK to config.b
                    }
                    call.respond(response.first, response.second)
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }

            post {
                val guildId = call.parameters["guildId"] ?: error("No guild id given")
//            val config = call.receiveText()
                val session = call.sessions.getOption<ConfigSession>()

                if (session.canManageGuild(guildId)) {
                    pubSub.publish(PubSubChannel.CONFIG_REFRESH, guildId)
                    call.respond(HttpStatusCode.Accepted)
                } else {
                    call.respond(HttpStatusCode.Forbidden, "You do not have permission to do that!")
                }
            }
        }
    }
}

private fun User.canManageGuild(guildId: String): Boolean = this.guilds.any { it.id == guildId && it.hasManageGuild }

private suspend fun Option<ConfigSession>.canManageGuild(guildId: String): Boolean =
    when (val user = User.getUser(this.map { it.accessToken })) {
        is Either.Left -> false
        is Either.Right -> user.b.canManageGuild(guildId)
    }
