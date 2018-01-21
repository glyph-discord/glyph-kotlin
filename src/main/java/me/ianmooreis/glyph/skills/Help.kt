package me.ianmooreis.glyph.skills

import ai.api.model.AIResponse
import me.ianmooreis.glyph.orchestrators.Skill
import me.ianmooreis.glyph.orchestrators.reply
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.awt.Color

object HelpSkill : Skill("skill.help") {
    override fun onTrigger(event: MessageReceivedEvent, ai: AIResponse) {
        val creator: User = event.jda.getUserById(System.getenv("CREATOR_ID"))
        val name = event.jda.selfUser.name
        val embed = EmbedBuilder()
                .setTitle("$name Help")
                .setDescription("Hi, I'm **$name**!\n\n" +
                        "A **constantly evolving** and **learning** Discord bot created by ${creator.name}#${creator.discriminator}.\n\n" +
                        "I use **machine learning** to process **natural language** requests you give to me to the best of my current trained ability.\n\n" +
                        "To see what I can do, be sure to check out my **full skills list** and **suggest new ones** you'd like to see, in the official server.\n\n" +
                        "[Full Skills List](https://glyph-discord.readthedocs.io/en/latest/skills.html) - " +
                        "[Official Glyph Server](https://discord.me/glyph-discord) - " +
                        "[Add Me To Your Server](http://glyph-discord.rtfd.io/invite)")
                .setColor(Color.getHSBColor(0.6f, 0.89f, 0.61f))
                .build()
        event.message.reply(embed = embed)
    }
}