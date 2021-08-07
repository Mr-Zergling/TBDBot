package bot.tbd.extensions

import bot.tbd.scheduling.RemindMeTask
import bot.tbd.scheduling.scheduleTask
import bot.tbd.util.DateParsing
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.utils.respond
import java.time.Instant

class EchoExtension: Extension() {

    override val name: String = "echo"

    inner class RemindMeArgs : Arguments() {
        val str by coalescedString("reminder arguments", description = "<time>, <message>")
    }

    override suspend fun setup() {
        command{
            name = "ping"
            description = "tis a ping"

            action{
                message.respond("pong, ${message.author?.tag}")
            }
        }

        command {
            name = "dm_ping"
            description = "ping in dms"

            action{
                message.author?.getDmChannelOrNull()?.createMessage("DM Pong, ${message.author?.tag}")
            }
        }

        command {
            name = "version"
            description = "version"

            action{
                message.respond("TBDBot-Kotlin ${this::class.java.`package`.implementationVersion}")
            }
        }

        command(::RemindMeArgs){
            name = "remindme"
            description = "remind you of something"
            action {
                val split = arguments.str.split(",")
                if (split.isEmpty()) {
                    message.respond("At least need something resembling a time")
                    return@action
                }
                val parsedTime = DateParsing.natLanguageToInstant(split[0])
                if (parsedTime == null) {
                    message.respond("Couldn't parse a time out of that")
                    return@action
                }

                val author = message.author ?: let {
                    message.respond("Couldn't get message author, wtf...")
                    return@action
                }

                val task = RemindMeTask(
                    parsedTime.toEpochMilli(),
                    author.id.asString,
                    split.takeLast(split.size - 1).joinToString(",").trim()
                )
                scheduleTask(task)
                message.respond(
                    "You will be reminded at ${Instant.ofEpochMilli(task.executionTime)} of ${task.reminderText}"
                )
            }
        }
    }
}