@file:AutoWired

package bot.tbd.modules

import bot.tbd.util.Preconditions
import bot.tbd.scheduling.RemindMeTask
import bot.tbd.scheduling.scheduleTask
import bot.tbd.util.DateParsing
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import mu.KotlinLogging
import java.time.Instant

private val log = KotlinLogging.logger {}

val echoModule = module("echo-module") {
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
