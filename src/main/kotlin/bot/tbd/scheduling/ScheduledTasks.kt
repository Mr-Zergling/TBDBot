@file:Suppress("UnnecessaryAbstractClass")

package bot.tbd.scheduling

import bot.tbd.util.toSnowflake
import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import com.kotlindiscord.kord.extensions.commands.MessageCommand
import dev.kord.core.Kord
import mu.KotlinLogging
import java.util.*
import kotlin.reflect.KClass

private val log = KotlinLogging.logger {}

@TypeFor(field = "type", adapter = ScheduledTaskTypeAdapter::class)
abstract class ScheduledTask(
    val id: String = UUID.randomUUID().toString(),
    val executionTime: Long,
    val type: String
) {
    abstract suspend fun execute(kord: Kord): Any
}

class RemoveMemberRoleTask(
    executionTime: Long,
    val roleId: String,
    val memberId: String,
    val guildId: String
) : ScheduledTask(executionTime = executionTime, type = RemoveMemberRoleTask::class.simpleNameOrEmpty()) {
    override suspend fun execute(kord: Kord) =
        kord.getGuild(guildId.toSnowflake())
            ?.getMember(memberId.toSnowflake())
            ?.removeRole(roleId.toSnowflake())
            ?: log.error("Guild with id $guildId was null")
}

class RemindMeTask(
    executionTime: Long,
    val userId: String,
    val reminderText: String
) : ScheduledTask(executionTime = executionTime, type = RemindMeTask::class.simpleNameOrEmpty()) {
    override suspend fun execute(kord: Kord) =
        kord.getUser(userId.toSnowflake())
            ?.getDmChannel()
            ?.createMessage("Reminder: $reminderText")
            ?: log.error("User for reminder task $this was null")
}

class ScheduledTaskTypeAdapter : TypeAdapter<ScheduledTask> {
    override fun classFor(type: Any): KClass<out ScheduledTask> =
        when (type as String) {
            RemoveMemberRoleTask::class.simpleNameOrEmpty() -> RemoveMemberRoleTask::class
            RemindMeTask::class.simpleNameOrEmpty() -> RemindMeTask::class
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
}

fun KClass<*>.simpleNameOrEmpty() = this.simpleName.orEmpty()

suspend fun scheduleTask(taskData: ScheduledTask) =
    ScheduledTaskExecutor.instance.addTask(taskData)
