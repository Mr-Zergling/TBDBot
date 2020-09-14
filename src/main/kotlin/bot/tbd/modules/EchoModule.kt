@file:AutoWired

package bot.tbd.modules

import bot.tbd.util.Preconditions
import bot.tbd.scheduling.RemindMeTask
import bot.tbd.scheduling.scheduleTask
import bot.tbd.util.DateParsing
import bot.tbd.util.toSnowflake
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import mu.KotlinLogging
import java.awt.Color
import java.time.Instant

private val log = KotlinLogging.logger {}

object EchoModule {

    private val messageSpecifyingRegex = Regex("/(?<guild>\\d{16,})/(?<channel>\\d{16,})/(?<message>\\d{16,})")

    // magic number at least till I figure out how to let me get groups by name
    @Suppress("MagicNumber", "ReturnCount")
    suspend fun autoQuote(messageCreateEvent: MessageCreateEvent) {
        val client = messageCreateEvent.kord
        val triggerMessage = messageCreateEvent.message
        val groups = messageSpecifyingRegex.find(triggerMessage.content)?.groups ?: return
        val quotedGuild = groups[1]?.value?.let { client.getGuild(it.toSnowflake()) } ?: return
        val quotedChannel = groups[2]?.value?.let { quotedGuild.getChannel(it.toSnowflake()) } ?: return
        val quotedMessage =
            groups[3]?.value?.let { (quotedChannel as GuildMessageChannel).getMessage(it.toSnowflake()) } ?: return
        triggerMessage.channel.createEmbed {
            color = Color.GREEN
            description = quotedMessage.content
            author {
                name = "${quotedMessage.author?.username}#${quotedMessage.author?.discriminator}"
                icon = quotedMessage.author?.avatar?.url
            }
            footer {
                text = "${quotedMessage.timestamp}"
            }
        }
    }
}

val kordEchoModule = module("echo-module") {
    command("ping") {
        invoke {
            respond("Pong, ${author.tag}")
        }
    }

    command("dm_ping") {
        invoke {
            author.getDmChannel().createMessage("DM Pong, ${author.tag}")
        }
    }

    command("log") {
        preconditions.add(Preconditions.isBotOwner)
        invoke(StringArgument) {
            log.debug("${message.timestamp} ${author.tag} - ${message.content}")
        }
    }

    command("version") {
        invoke {
            respond("TBDBot-Kotlin ${this::class.java.`package`.implementationVersion}")
        }
    }

    command("remindme") {
        invoke(StringArgument) { str ->
            val split = str.split(",")
            if (split.isEmpty()) {
                respond("At least need something resembling a time")
                return@invoke
            }
            val parsedTime = DateParsing.natLanguageToInstant(split[0])
            if (parsedTime == null) {
                respond("Couldn't parse a time out of that")
                return@invoke
            }
            val task = RemindMeTask(
                parsedTime.toEpochMilli(),
                author.id.value,
                split.takeLast(split.size - 1).joinToString(",").trim()
            )
            scheduleTask(task)
            respond(
                "You will be reminded at ${Instant.ofEpochMilli(task.executionTime)} of ${task.reminderText}"
            )
        }
    }
}
