@file:AutoWired

package bot.tbd

import bot.tbd.scheduling.RemindMeTask
import bot.tbd.scheduling.scheduleTask
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import com.mdimension.jchronic.Chronic
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
            val parsedSpan = Chronic.parse(split[0])
            if (parsedSpan == null) {
                respond("Couldn't parse a time out of that")
                return@invoke
            }
            val task = RemindMeTask(
                parsedSpan.beginCalendar.timeInMillis,
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
