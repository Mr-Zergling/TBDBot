@file:Suppress("MatchingDeclarationName")
@file:AutoWired

package bot.tbd

import bot.tbd.config.Config
import bot.tbd.modules.EchoModule
import bot.tbd.scheduling.ScheduledTaskExecutor
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.on
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.kord.bot
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.kord
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.mention
import com.gitlab.kordlib.kordx.commands.model.prefix.literal
import com.gitlab.kordlib.kordx.commands.model.prefix.or
import com.xenomachina.argparser.ArgParser
import kapt.kotlin.generated.configure
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
    val kord = Kord(Config.global.apiToken)
    kord.on(consumer = EchoModule::autoQuote)
    ScheduledTaskExecutor("${Config.global.storageDir}/tasks/", kord)
    bot(kord) {
        prefix {
            kord { literal("$") or literal("!") or mention() }
        }
        configure()
    }
}
