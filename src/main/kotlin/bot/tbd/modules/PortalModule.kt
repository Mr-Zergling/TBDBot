@file:AutoWired

package bot.tbd.modules

import bot.tbd.util.ResourceUrls
import com.gitlab.kordlib.core.behavior.channel.createEmbed
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
                    val sourceUrl = createMessageLink(message)
                    val destMessage = channel.createEmbed {
                        title = "Portal from ${(message.getChannel() as GuildMessageChannel).name}"
                        description = "[${message.getAuthorAsMember()?.displayName}: $msg]($sourceUrl)"
                        color = Color.ORANGE
                        thumbnail {
                            url = ResourceUrls.orangePortal
                        }
                    }
                    val destUrl = createMessageLink((destMessage))
                    message.channel.createEmbed {
                        title = "Portal to ${channel.name}"
                        description = "[${message.getAuthorAsMember()?.displayName}: $msg]($destUrl)"
                        color = Color.BLUE
                        thumbnail {
                            url = ResourceUrls.bluePortal
                        }
                    }
                }
                else -> respond("Invalid Channel Argument")
            }
        }
    }
}

suspend fun createMessageLink(message: Message): String =
    "https://discordapp.com/channels/${message.getGuild().id.value}/" +
            "${message.getChannel().id.value}/" + message.id.value
