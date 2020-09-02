package bot.tbd.scheduling

import com.beust.klaxon.Klaxon
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class ScheduledTaskQueue(private val storagePath: String) {
    val mutex = Mutex()
    val queue = PriorityQueue<ScheduledTask> { task1: ScheduledTask, task2: ScheduledTask ->
        task1.executionTime.compareTo(task2.executionTime)
    }
    val map = HashMap<String, ScheduledTask>()

    suspend fun insert(task: ScheduledTask) =
        mutex.withLock {
            unsafeInsert(task)
            saveToDisk(task)
        }

    suspend fun peek(): ScheduledTask? =
        mutex.withLock {
            return queue.peek()
        }

    suspend fun poll(): ScheduledTask? =
        mutex.withLock {
            val popped = queue.poll()
            popped?.id?.let {
                map.remove(it)
                removeFromDiskIfExists(popped)
            }
            return popped
        }

    suspend fun size(): Int =
        mutex.withLock {
            return queue.size
        }

    suspend fun loadFromDisk() {
        mutex.withLock {
            File(storagePath).walk().forEach {
                if (it.isFile && it.extension == "json") {
                    val task = Klaxon().parse<ScheduledTask>(it)
                    task?.let { task -> unsafeInsert(task) }
                }
            }
        }
    }

    private fun unsafeInsert(task: ScheduledTask) {
        map[task.id] = task
        queue.add(task)
    }

    private fun saveToDisk(task: ScheduledTask) {
        File(makeFilePath(task)).writeText(Klaxon().toJsonString(task))
    }

    private fun removeFromDiskIfExists(task: ScheduledTask) {
        val file = File(makeFilePath(task))
        if (file.exists()) {
            file.delete()
        }
    }

    private fun makeFilePath(task: ScheduledTask) = "$storagePath${task.id}.json"
}
