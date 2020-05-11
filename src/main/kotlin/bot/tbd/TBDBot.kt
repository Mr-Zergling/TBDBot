package bot.tbd

import bot.tbd.command.About
import bot.tbd.command.Hello
import bot.tbd.config.Config
import bot.tbd.module.CommandModule
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.gateway.ReadyEvent
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.on
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

@Suppress("UtilityClassWithPublicConstructor")
class TBDBotRunner {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking { TBDBot.run() }
        }
    }
}

object TBDCommandModule : CommandModule(
    listOf(
        Hello,
        About
    )
)

object TBDBot {
    suspend fun run() {
        log.info("Logging in....")
        val client = Kord(Config.global.apiToken)
        listOf(
            TBDCommandModule
        ).forEach { it.initialize(client) }
        client.on<ReadyEvent> {
            val self = client.getSelf()
            log.info(
                "Logged in as ${self.username}#${self.discriminator}"
            )
        }

        client.on<MessageCreateEvent> {
            log.info("${message.author?.username}#${message.author?.discriminator}: ${message.content}")
        }

        client.login()
    }
}
