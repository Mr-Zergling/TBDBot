package bot.tbd.extensions

import bot.tbd.util.ResourceUrls
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.common.kColor
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.builder.message.modify.embed
import java.awt.Color

class PortalExtension : Extension() {
    override val name: String = "Portal"

    inner class PortalArgs : Arguments() {
        val destination by channel(displayName = "Destination Channel", description = "The channel to open a portal to")
        val portalMessage by defaultingString(displayName = "Portal Message", description = "The message on the portal", defaultValue = "Unnamed Portal")
    }

    override suspend fun setup() {
        command(::PortalArgs) {
            name = "portal"
            description = "Portal to another channel"

            check {
                failIf(message = "Can only be used in a server channel") {
                    event.getGuild() == null
                }
            }

            action {
                val srcChannel = message.channel.asChannel()
                when (val destChannel = arguments.destination) {
                    is GuildMessageChannel -> {
                        val destMessage = destChannel.createMessage("Incoming portal....")
                        val destUrl = createMessageLink(destMessage)
                        val sourceMessage = srcChannel.createMessage {
                            content = destUrl
                            embed {
                                title = "Portal to ${destChannel.name}"
                                description = "[${arguments.portalMessage}]($destUrl)"
                                color = Color.BLUE.kColor
                                thumbnail {
                                    url = ResourceUrls.bluePortal
                                }
                                author {
                                    name = "${message.getAuthorAsMember()?.displayName}"
                                    icon = message.author?.avatar?.url
                                }
                            }
                        }
                        val sourceUrl = createMessageLink(sourceMessage)
                        destMessage.edit {
                            content = sourceUrl
                            embed {
                                title = "Portal from ${(srcChannel as GuildMessageChannel).name}"
                                description = "[${arguments.portalMessage}]($sourceUrl)"
                                color = Color.ORANGE.kColor
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
                    else -> message.respond("Destination channel must be a server channel")
                }
            }
        }
    }
}

suspend fun createMessageLink(message: Message): String =
    "https://discordapp.com/channels/${message.getGuild().id.value}/" +
            "${message.getChannel().id.value}/" + message.id.value