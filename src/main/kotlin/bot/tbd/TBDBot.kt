@file:com.gitlab.kordlib.kordx.commands.annotation.AutoWired
package bot.tbd

import bot.tbd.config.Config
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.kord.bot
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke

suspend fun main() = bot(Config.global.apiToken) {

}

fun testModule() = module("test-module") {
    command("add") {
        invoke(IntArgument, IntArgument) { a, b ->
            respond("${a + b}")
        }
    }
}
