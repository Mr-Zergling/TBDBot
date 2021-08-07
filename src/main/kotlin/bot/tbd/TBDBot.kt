@file:Suppress("MatchingDeclarationName")

package bot.tbd

import bot.tbd.config.Config
import bot.tbd.extensions.EchoExtension
import bot.tbd.extensions.PortalExtension
import bot.tbd.extensions.UserManagementExtension
import bot.tbd.scheduling.ScheduledTaskExecutor
import com.kotlindiscord.kord.extensions.DISCORD_BLURPLE
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.getKoin
import com.xenomachina.argparser.ArgParser
import dev.kord.core.Kord
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class TBDBotArgs(parser: ArgParser) {
    val configFilePath by parser.storing(
        "-C", "--config", help = "Config File Path"
    ) { toString() }
}

suspend fun main(args: Array<String>) {
    val parsedArgs = ArgParser(args).parseInto { TBDBotArgs(it) }
    Config.init(parsedArgs.configFilePath)
    log.info("Parsed Config File ${parsedArgs.configFilePath}")
    log.info("Storage Dir Set to ${Config.global.storageDir}")
    val bot = ExtensibleBot(Config.global.apiToken){
        messageCommands {
            defaultPrefix = "!"
        }
        extensions {
            add(::EchoExtension)
            add(::PortalExtension)
            add(::UserManagementExtension)
            help {
                pingInReply = true
                color { DISCORD_BLURPLE }
            }
        }
    }
    ScheduledTaskExecutor("${Config.global.storageDir}/tasks/", getKoin().get<Kord>())
    bot.start()
}
