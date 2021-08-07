package bot.tbd.scheduling

import dev.kord.core.Kord
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

    suspend fun addTask(task: ScheduledTask) {
        taskQueue.insert(task)
        GlobalScope.launch { pollAndLaunch() }
    }

    private suspend fun pollAndLaunch() {
        while (true) {
            val firstTask = taskQueue.poll() ?: break
            if (Instant.ofEpochMilli(firstTask.executionTime) <= Instant.now()) {
                GlobalScope.launch { firstTask.execute(kord) }
            } else {
                taskQueue.insert(firstTask)
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
}
