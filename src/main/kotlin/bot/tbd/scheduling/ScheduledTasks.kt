@file:Suppress("UnnecessaryAbstractClass")

package bot.tbd.scheduling

import bot.tbd.util.toSnowflake
import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.kordx.commands.kord.model.context.KordCommandEvent
import mu.KotlinLogging
import org.koin.core.get
import java.util.*
import kotlin.reflect.KClass

private val log = KotlinLogging.logger {}

@TypeFor(field = "type", adapter = ScheduledTaskDataTypeAdapter::class)
abstract class ScheduledTaskData(
    val id: String = UUID.randomUUID().toString(),
    open val executionTime: Long,
    val type: String
)

abstract class ScheduledTask<D : ScheduledTaskData> {
    abstract suspend fun execute(taskData: D, kord: Kord): Any
}

data class RemoveMemberRoleData(
    override val executionTime: Long,
    val roleId: String,
    val memberId: String,
    val guildId: String
) : ScheduledTaskData(executionTime = executionTime, type = RemoveMemberRoleData::class.simpleNameOrEmpty())

object RemoveMemberRoleTask : ScheduledTask<RemoveMemberRoleData>() {
    override suspend fun execute(taskData: RemoveMemberRoleData, kord: Kord) =
        kord.getGuild(taskData.guildId.toSnowflake())
            ?.getMember(taskData.memberId.toSnowflake())
            ?.removeRole(taskData.roleId.toSnowflake())
            ?: log.error("Guild with id ${taskData.guildId} was null")
}

data class RemindMeData(
    override val executionTime: Long,
    val userId: String,
    val reminderText: String
) : ScheduledTaskData(executionTime = executionTime, type = RemindMeData::class.simpleNameOrEmpty())

object RemindMeTask : ScheduledTask<RemindMeData>() {
    override suspend fun execute(taskData: RemindMeData, kord: Kord) =
        kord.getUser(taskData.userId.toSnowflake())
            ?.getDmChannel()
            ?.createMessage("Reminder: ${taskData.reminderText}")
            ?: log.error("User for reminder task $taskData was null")
}

class ScheduledTaskDataTypeAdapter : TypeAdapter<ScheduledTaskData> {
    override fun classFor(type: Any): KClass<out ScheduledTaskData> =
        when (type as String) {
            RemoveMemberRoleData::class.simpleNameOrEmpty() -> RemoveMemberRoleData::class
            RemindMeData::class.simpleNameOrEmpty() -> RemindMeData::class
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
}

fun KClass<*>.simpleNameOrEmpty() = this.simpleName.orEmpty()

suspend fun KordCommandEvent.scheduleTask(taskData: ScheduledTaskData) =
    ScheduledTaskExecutor.instance.addTask(taskData)
