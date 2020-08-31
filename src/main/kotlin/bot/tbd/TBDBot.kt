@file:Suppress("MatchingDeclarationName")
@file:AutoWired

package bot.tbd

import bot.tbd.config.Config
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.kord.bot
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.kord
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.mention
import com.gitlab.kordlib.kordx.commands.model.prefix.literal
import com.gitlab.kordlib.kordx.commands.model.prefix.or
import com.xenomachina.argparser.ArgParser
import kapt.kotlin.generated.configure

class TBDBotArgs(parser: ArgParser) {
    val configFilePath by parser.storing(
        "-C", "--config", help = "Config File Path"
    ) { toString() }
}

suspend fun main(args: Array<String>) {
    val parsedArgs = ArgParser(args).parseInto { TBDBotArgs(it) }

    Config.init(parsedArgs.configFilePath)
    bot(Config.global.apiToken) {
        prefix {
            kord { literal("$") or mention() }
        }
        configure()
    }
}
