@file:AutoWired

package bot.tbd.modules

import bot.tbd.util.ResourceUrls
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.argument.ChannelArgument
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import java.awt.Color

val portalModule = module("portal") {

    command("portal") {
        invoke(ChannelArgument, StringArgument) { channel, msg ->
            when (channel) {
                is GuildMessageChannel -> {
                    val destMessage = channel.createMessage("Incoming portal....")
                    val destUrl = createMessageLink(destMessage)
                    val sourceMessage = message.channel.createEmbed {
                        title = "Portal to ${channel.name}"
                        description = "[$msg]($destUrl)"
                        color = Color.BLUE
                        thumbnail {
                            url = ResourceUrls.bluePortal
                        }
                        author {
                            name = "${message.getAuthorAsMember()?.displayName}"
                            icon = message.author?.avatar?.url
                        }
                    }
                    val sourceUrl = createMessageLink(sourceMessage)
                    destMessage.edit {
                        content = ""
                        embed {
                            title = "Portal from ${(message.getChannel() as GuildMessageChannel).name}"
                            description = "[$msg]($sourceUrl)"
                            color = Color.ORANGE
                            thumbnail {
                                url = ResourceUrls.orangePortal
                            }
                            author {
                                name = "${message.getAuthorAsMember()?.displayName}"
                                icon = message.author?.avatar?.url
                            }
                        }
                    }
                    message.delete()
                }
                else -> respond("Invalid Channel Argument")
            }
        }
    }
}

suspend fun createMessageLink(message: Message): String =
    "https://discordapp.com/channels/${message.getGuild().id.value}/" +
            "${message.getChannel().id.value}/" + message.id.value
