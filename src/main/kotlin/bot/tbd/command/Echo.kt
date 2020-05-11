package bot.tbd.command

import bot.tbd.extensions.getAuthorNick
import bot.tbd.extensions.reply

object Hello : Command("hello") {
    override suspend fun execute(ctx: CommandContext) {
        ctx.message.reply("Hello ${ctx.message.getAuthorNick()}")
    }

    override val help: String = "```hello - says hello```"
}

object About : Command("about") {
    override suspend fun execute(ctx: CommandContext) {
        ctx.message.reply("TBDBot Kotlin v0.0.1 - https://github.com/Mr-Zergling/TBDBot")
    }

    override val help: String = "```version - displays version```"
}
