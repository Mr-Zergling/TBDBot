package bot.tbd.module

import bot.tbd.command.Command
import bot.tbd.command.CommandContext
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.on
import com.google.re2j.Matcher
import com.google.re2j.Pattern
import io.ktor.util.error
import mu.KotlinLogging
import org.apache.commons.lang3.StringUtils

private val log = KotlinLogging.logger {}

abstract class CommandModule(val topLevelCommands: List<Command>) : Module {

    companion object {
        private const val COMMAND_PREFIX = "(\\%|\\$)"
        private const val TRIGGER_GROUP_NAME = "trigger"
        private const val ARGS_GROUP_NAME = "args"
    }

    private var selfId: Snowflake? = null

    val commandMap = generateCommandMap(topLevelCommands)

    val fullCommandPattern = generateCommandPattern()

    private fun generateCommandMap(commands: List<Command>, prefix: String = ""): Map<String, Command> {
        val map = HashMap(commands.map { prefix + it.prefix to it }.toMap())
        for (command in commands)
            map.putAll(generateCommandMap(command.children, "$prefix ${command.prefix} "))
        return map
    }

    private fun generateCommandPattern(): Pattern {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\\A$COMMAND_PREFIX(?P<$TRIGGER_GROUP_NAME>(")
        commandMap.keys.sortedWith(NumSpacesComparator).forEach {
            stringBuilder.append(it)
            stringBuilder.append('|')
        }
        stringBuilder.removeSuffix("|")
        stringBuilder.append("))\\s*(?P<$ARGS_GROUP_NAME>.*)\\z")
        log.info(stringBuilder.toString())
        return Pattern.compile(stringBuilder.toString(), Pattern.CASE_INSENSITIVE)
    }

    suspend fun handleMessage(message: Message) {
        if (message.author?.id == selfId) return
        val matcher = fullCommandPattern.matcher(message.content)
        if (!matcher.matches()) return
        try {
            val context = createContext(message, matcher)
            commandMap[context.trigger]?.execute(context)
        } catch (e: IllegalStateException) {
            log.error(e)
        }
    }

    private fun createContext(message: Message, matcher: Matcher): CommandContext {
        val trigger = matcher.group(TRIGGER_GROUP_NAME)
        var args = ""
        try {
            args = matcher.group(ARGS_GROUP_NAME)
        } catch (e: IllegalStateException) {
            args = ""
        }
        return CommandContext(message, trigger, args)
    }

    override fun initialize(client: Kord) {
        selfId = client.selfId
        client.on<MessageCreateEvent> {
            handleMessage(message)
        }
    }
}

object NumSpacesComparator : Comparator<String> {
    override fun compare(o1: String?, o2: String?): Int {
        return StringUtils.countMatches(o2, " ") - StringUtils.countMatches(o1, " ")
    }
}
