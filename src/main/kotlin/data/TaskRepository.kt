package data

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/**
 * NOTE FOR NON-INTELLIJ IDEs (VSCode, Eclipse, etc.):
 * IntelliJ IDEA automatically adds imports as you type. If using a different IDE,
 * you may need to manually add imports. The commented imports below show what you'll need
 * for future weeks. Uncomment them as needed when following the lab instructions.
 *
 * When using IntelliJ: You can ignore the commented imports below - your IDE will handle them.
 */

// Week 7+ imports (no new imports needed for find/update methods)

// Week 8+ imports (search functionality added, no new imports needed)

// Week 10+ evolution note:
// In the solution repo, this file is split into two separate files:
//
// 1. model/Task.kt (data class with validation):
//    import java.time.LocalDateTime
//    import java.time.format.DateTimeFormatter
//    import java.util.UUID
//
// 2. storage/TaskStore.kt (CSV persistence using Apache Commons CSV):
//    import model.Task
//    import org.apache.commons.csv.CSVFormat
//    import org.apache.commons.csv.CSVParser
//    import org.apache.commons.csv.CSVPrinter
//    import java.io.FileReader
//    import java.io.FileWriter
//    import java.time.format.DateTimeParseException

/**
 * Simple task data model for Week 6.
 *
 * **Week 7 evolution**: Add `completed: Boolean` field
 * **Week 8 evolution**: Add `createdAt` timestamp for sorting
 */
data class Task(
    val id: Int,
    var title: String,
)

/**
 * In-memory repository with CSV persistence.
 *
 * **Simple approach for Week 6**: Singleton object with integer IDs
 * **Week 10 evolution**: Refactor to class with UUID for production-readiness
 */
object TaskRepository {
    private val file = File("data/tasks.csv")
    private val tasks = mutableListOf<Task>()
    private val idCounter = AtomicInteger(1)

    init {
        file.parentFile?.mkdirs()
        if (!file.exists()) {
            file.writeText("id,title\n")
        } else {
            file.readLines().drop(1).forEach { line ->
                val parts = line.split(",", limit = 2)
                if (parts.size == 2) {
                    val id = parts[0].toIntOrNull() ?: return@forEach
                    tasks.add(Task(id, parts[1]))
                    idCounter.set(maxOf(idCounter.get(), id + 1))
                }
            }
        }
    }

    fun all(): List<Task> = tasks.toList()

    fun add(title: String): Task {
        val task = Task(idCounter.getAndIncrement(), title)
        tasks.add(task)
        persist()
        return task
    }

    fun delete(id: Int): Boolean {
        val removed = tasks.removeIf { it.id == id }
        if (removed) persist()
        return removed
    }

    // TODO: Week 7 Lab 1 Activity 2 Step 6
    // Add find() and update() methods here
    // Follow instructions in mdbook to implement:
    // - fun find(id: Int): Task?
    // - fun update(task: Task)
    fun get(id: Int): Task? {
        return tasks.find { it.id == id }
    }

    fun update(id: Int, newTitle: String): Task? {
        val task = tasks.find { it.id == id } ?: return null
        task.title = newTitle
        persist()
        return task
    }

    private fun persist() {
        file.writeText("id,title\n" + tasks.joinToString("\n") { "${it.id},${it.title}" })
    }
}
