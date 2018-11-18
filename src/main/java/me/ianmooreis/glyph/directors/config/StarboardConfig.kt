/*
 * StarboardConfig.kt
 *
 * Glyph, a Discord bot that uses natural language instead of commands
 * powered by DialogFlow and Kotlin
 *
 * Copyright (C) 2017-2018 by Ian Moore
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

package me.ianmooreis.glyph.directors.config

import net.dv8tion.jda.core.entities.Guild

/**
 * A configuration for starboarding
 */
data class StarboardConfig(
    /**
     * Whether or not the starboard is enabled
     */
    val enabled: Boolean = false,
    /**
     * The webhook to send starred messages to
     */
    val webhook: String? = null,
    /**
     * The emoji to check for when starboarding
     */
    val emoji: String = "star",
    /**
     * The minimum number of reactions of the check emoji needed before the message is sent to the starboard
     */
    val threshold: Int = 1,
    /**
     * Whether or not members can star their own messages
     */
    val allowSelfStarring: Boolean = false
) : Config() {
    override fun getMicroConfig(guild: Guild): MicroConfig {
        return MicroConfigBuilder()
            .addValue(enabled)
            .addValue(threshold)
            .addValue(allowSelfStarring)
            .addValue(webhook, emoji)
            .build()
    }
}