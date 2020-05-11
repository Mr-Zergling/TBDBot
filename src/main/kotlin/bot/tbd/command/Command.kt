package bot.tbd.command

import com.gitlab.kordlib.core.entity.Message

abstract class Command(val prefix: String, vararg val childCommands: Command) {
    abstract suspend fun execute(ctx: CommandContext)
    abstract val help: String
    val children = childCommands.toList()
}

data class CommandContext(val message: Message, val trigger: String, val args: String)
