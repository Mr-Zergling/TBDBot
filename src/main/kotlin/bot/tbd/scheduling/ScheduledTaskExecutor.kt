package bot.tbd.scheduling

import com.gitlab.kordlib.core.Kord
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ScheduledTaskExecutor(storagePath: String, private val kord: Kord) {
    private val ses = Executors.newScheduledThreadPool(1)
    private val taskQueue: ScheduledTaskQueue = ScheduledTaskQueue(storagePath)

    companion object {
        lateinit var instance: ScheduledTaskExecutor
    }

    init {
        val storage = File(storagePath)
        if (!storage.exists()) {
            storage.mkdirs()
        }
        GlobalScope.launch {
            taskQueue.loadFromDisk()
            pollAndLaunch()
        }
        instance = this
    }

    suspend fun addTask(taskData: ScheduledTaskData) {
        taskQueue.insert(taskData)
        GlobalScope.launch { pollAndLaunch() }
    }

    private suspend fun pollAndLaunch() {
        while (true) {
            val peeked = taskQueue.peek()
            if (peeked != null && Instant.ofEpochMilli(peeked.executionTime) <= Instant.now()) {
                taskQueue.poll()?.let { GlobalScope.launch { runAppropriateTask(it) } }
            } else {
                break
            }
        }
        taskQueue.peek()?.executionTime?.let {
            val delay = it - Instant.now().toEpochMilli()
            if (delay > 0) {
                ses.schedule({ GlobalScope.launch { pollAndLaunch() } }, delay, TimeUnit.MILLISECONDS)
            }
        }
    }

    private suspend fun runAppropriateTask(taskData: ScheduledTaskData) =
        when (taskData) {
            is RemoveMemberRoleData -> RemoveMemberRoleTask.execute(taskData, kord)
            is RemindMeData -> RemindMeTask.execute(taskData, kord)
            else -> throw IllegalArgumentException("Unknown Task Data Type: ${taskData::class}")
        }
}
