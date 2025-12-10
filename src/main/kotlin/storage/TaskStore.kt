package storage

import model.Task
import java.io.File

/**
 * Simple in-memory task storage with CSV persistence.
 * Week 8 uses String UUID IDs.
 */
class `TaskStore.kt`(private val csvFile: File = File("data/tasks.csv")) {
    private val tasks = mutableListOf<Task>()

    init {
        if (csvFile.exists()) {
            // Load tasks from CSV (implement if needed)
        }
    }

    fun getAll(): List<Task> = tasks.toList()

    fun getById(id: String): Task? = tasks.find { it.id == id }

    fun add(task: Task) {
        tasks.add(task)
    }

    fun update(task: Task): Boolean {
        val index = tasks.indexOfFirst { it.id == task.id }
        return if (index != -1) {
            tasks[index] = task
            true
        } else false
    }

    fun delete(id: String): Boolean = tasks.removeIf { it.id == id }

    /**
     * Search tasks by title (returns all matching tasks).
     * Pagination happens in the routes layer, not here.
     */
    fun search(query: String): List<Task> {
        if (query.isBlank()) return getAll()

        val normalizedQuery = query.trim().lowercase()
        return getAll().filter { task ->
            task.title.lowercase().contains(normalizedQuery)
        }
    }

}
