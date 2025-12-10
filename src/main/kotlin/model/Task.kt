package model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Task model for Week 8.
 * Uses UUID strings (not Int IDs) for production architecture.
 */
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val completed: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        const val MIN_TITLE_LENGTH = 3
        const val MAX_TITLE_LENGTH = 100

        fun validate(title: String): ValidationResult = when {
            title.isBlank() -> ValidationResult.Error("Title is required. Please enter a task description.")
            title.length < MIN_TITLE_LENGTH -> ValidationResult.Error("Title must be at least $MIN_TITLE_LENGTH characters.")
            title.length > MAX_TITLE_LENGTH -> ValidationResult.Error("Title must be less than $MAX_TITLE_LENGTH characters.")
            else -> ValidationResult.Success
        }
    }

    /**
     * Convert task to template-friendly map.
     * REQUIRED for rendering tasks in Pebble templates.
     * Used in routes: tasks.map { it.toPebbleContext() }
     */
    fun toPebbleContext(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "completed" to completed,
        "createdAt" to createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
        "createdAtISO" to createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}

/**
 * Validation result for form processing.
 */
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
