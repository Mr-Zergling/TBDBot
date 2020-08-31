@file:AutoWired

package bot.tbd

import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import mu.KotlinLogging

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
}
