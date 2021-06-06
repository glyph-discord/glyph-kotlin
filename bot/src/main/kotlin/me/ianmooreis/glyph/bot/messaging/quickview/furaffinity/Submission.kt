/*
 * Submission.kt
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

package me.ianmooreis.glyph.bot.messaging.quickview.furaffinity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.ianmooreis.glyph.bot.directors.messaging.SimpleDescriptionBuilder
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.time.Instant

/**
 * A FurAffinity submission
 */
@Serializable
data class Submission(
    /**
     * Title of the submission
     */
    val title: String,
    /**
     * Name of the poster
     */
    val name: String,
    /**
     * URL of the poster's profile
     */
    val profile: String,
    /**
     * URL of the poster's avatar
     */
    val avatar: String,
    /**
     * Direct link to the submission
     */
    val link: String,
    /**
     * Date the submission was posted
     */
    @SerialName("posted_at")
    val postedAt: String,
    /**
     * Download URL of the content
     */
    val download: String,
    /**
     * URL of the full resolution content
     */
    val full: String,
    /**
     * Category the submission is placed under
     */
    val category: String,
    /**
     * Theme of the submission
     */
    val theme: String,
    /**
     * Species specified in the submission
     */
    val species: String?,
    /**
     * Gender specified in the submission
     */
    val gender: String?,
    /**
     * Number of favorites the submission has received
     */
    val favorites: Int,
    /**
     * Number of comments the submission has received
     */
    val comments: Int,
    /**
     * Number of views the submission has received
     */
    val views: Int,
    /**
     * Resolution of the submission content is an image
     */
    val resolution: String?,
    /**
     * Submission rating (maturity level) of the submission
     */
    val rating: SubmissionRating,
    /**
     * Keywords assigned to the submission
     */
    val keywords: List<String>
) {

    /**
     * Creates an embed with the submission's info and a thumbnail if desired
     */
    fun getEmbed(nsfwAllowed: Boolean, thumbnailAllowed: Boolean): MessageEmbed {
        val embed = EmbedBuilder()
            .setFooter("FurAffinity")
            .setColor(rating.color)
            .setAuthor(name, profile, avatar)
            .setTimestamp(Instant.parse(postedAt))

        if (rating.nsfw && !nsfwAllowed) {
            embed.setDescription("Submissions with a rating of $rating cannot be previewed outside of a NSFW channel!")
        } else {
            val linkedKeywords = keywords.joinToString { "[$it](https://www.furaffinity.net/search/@keywords%20$it)" }
            val fancyKeywords = if (linkedKeywords.length < MessageEmbed.VALUE_MAX_LENGTH) {
                linkedKeywords
            } else {
                keywords.joinToString(limit = MessageEmbed.VALUE_MAX_LENGTH)
            }
            val fileType = download.substringAfterLast(".")
            val description = SimpleDescriptionBuilder()

            // Add the different fields to the quickview embed description
            description.addField("Category", "$category - $theme (${rating.name})")
            species?.let { description.addField("Species", it) }
            gender?.let { description.addField("Gender", it) }
            description.addField(null, "**Favorites** $favorites | **Comments** $comments | **Views** $views")
            description.addField("Download", "[${resolution ?: fileType}]($download)")

            if (thumbnailAllowed) {
                embed.setImage(full)
            }

            embed
                .setTitle(title, link)
                .setDescription(description.build())
                .addField("Keywords", fancyKeywords, false)
                .build()
        }

        return embed.build()
    }
}
