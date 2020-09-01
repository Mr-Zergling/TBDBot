package bot.tbd.scheduling

import com.beust.klaxon.Klaxon
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class ScheduledTaskQueue(private val storagePath: String) {
    val mutex = Mutex()
    val queue = PriorityQueue<ScheduledTaskData> { data1: ScheduledTaskData, data2: ScheduledTaskData ->
        data1.executionTime.compareTo(data2.executionTime)
    }
    val map = HashMap<String, ScheduledTaskData>()

    suspend fun insert(taskData: ScheduledTaskData) =
        mutex.withLock {
            unsafeInsert(taskData)
            saveToDisk(taskData)
        }

    suspend fun peek(): ScheduledTaskData? =
        mutex.withLock {
            return queue.peek()
        }

    suspend fun poll(): ScheduledTaskData? =
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
                    val taskData = Klaxon().parse<ScheduledTaskData>(it)
                    taskData?.let { data -> unsafeInsert(data) }
                }
            }
        }
    }

    private fun unsafeInsert(taskData: ScheduledTaskData) {
        map[taskData.id] = taskData
        queue.add(taskData)
    }

    private fun saveToDisk(data: ScheduledTaskData) {
        File(makeFilePath(data)).writeText(Klaxon().toJsonString(data))
    }

    private fun removeFromDiskIfExists(data: ScheduledTaskData) {
        val file = File(makeFilePath(data))
        if (file.exists()) {
            file.delete()
        }
    }

    private fun makeFilePath(data: ScheduledTaskData) = "$storagePath${data.id}.json"
}
