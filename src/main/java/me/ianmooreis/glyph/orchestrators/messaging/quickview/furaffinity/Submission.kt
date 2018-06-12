package me.ianmooreis.glyph.orchestrators.messaging.quickview.furaffinity

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.MessageEmbed
import java.net.URL
import java.util.*

class Submission(private val title: String, private val description: String, private val name: String, private val profile: URL, val link: URL,
                 val posted: String, private val posted_at: Date, private val download: URL, private val full: URL, private val thumbnail: URL,
                 private val category: String, private val theme: String, private val species: String?, private val gender: String?,
                 private val favorites: Int, private val comments: Int, private val views: Int, private val resolution: String?, val rating: SubmissionRating,
                 private val keywords: List<String>) {
    fun getEmbed(thumbnail: Boolean): MessageEmbed {
        val linkedKeywords = keywords.joinToString { "[$it](https://www.furaffinity.net/search/@keywords%20$it)" }
        val fancyKeywords = if (linkedKeywords.length < 1024) linkedKeywords else keywords.joinToString()
        val fileType = download.toString().substringAfterLast(".")
        return EmbedBuilder()
                .setTitle(title, link.toString())
                .setThumbnail(if (thumbnail) full.toString() else null)
                .setDescription(
                        "**Category** $category > $theme (${rating.name})\n" +
                                (if (species != null) "**Species** $species\n" else "") +
                                (if (gender != null) "**Gender** $gender\n" else "") +
                                "**Favorites** $favorites | **Comments** $comments | **Views** $views" +
                                if ((thumbnail && rating.nsfw) || !rating.nsfw) "\n**Download** [${resolution ?: fileType}]($download)" else "")
                .addField("Keywords", fancyKeywords, false)
                .setFooter("FurAffinity", null)
                .setColor(rating.color)
                .setAuthor(name, profile.toString())
                .setTimestamp(posted_at.toInstant())
                .build()
    }
}