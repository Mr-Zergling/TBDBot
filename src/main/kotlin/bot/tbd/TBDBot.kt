package bot.tbd

import bot.tbd.config.Config
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.gateway.ReadyEvent
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.on
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.time.Instant

private val log = KotlinLogging.logger {}

class TBDBotRunner {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runBlocking { TBDBot.run() }
        }
    }
}

object TBDBot {
    suspend fun run() {
        log.info("Logging in....")
        val client = Kord(Config.global.apiToken)
        client.on<ReadyEvent> {
            val self = client.getSelf()
            log.info(
                "Logged in as ${self.username}#${self.discriminator}"
            )
        }
        client.on<MessageCreateEvent> {
            val message = this.message
            log.info("${message.author?.username}#${message.author?.discriminator}: ${message.content}")
        }
        client.login()
    }
}
