package routes

import data.TaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.pebbletemplates.pebble.PebbleEngine
import java.io.StringWriter

/**
 * NOTE FOR NON-INTELLIJ IDEs (VSCode, Eclipse, etc.):
 * IntelliJ IDEA automatically adds imports as you type. If using a different IDE,
 * you may need to manually add imports. The commented imports below show what you'll need
 * for future weeks. Uncomment them as needed when following the lab instructions.
 *
 * When using IntelliJ: You can ignore the commented imports below - your IDE will handle them.
 */

// Week 7+ imports (inline edit, toggle completion):
// import model.Task               // When Task becomes separate model class
// import model.ValidationResult   // For validation errors
// import renderTemplate            // Extension function from Main.kt
// import isHtmxRequest             // Extension function from Main.kt

// Week 8+ imports (pagination, search, URL encoding):
// import io.ktor.http.encodeURLParameter  // For query parameter encoding
// import utils.Page                       // Pagination helper class

// Week 9+ imports (metrics logging, instrumentation):
// import utils.jsMode              // Detect JS mode (htmx/nojs)
// import utils.logValidationError  // Log validation failures
// import utils.timed               // Measure request timing

// Note: Solution repo uses storage.TaskStore instead of data.TaskRepository
// You may refactor to this in Week 10 for production readiness

/**
 * Week 6 Lab 1: Simple task routes with HTMX progressive enhancement.
 *
 * **Teaching approach**: Start simple, evolve incrementally
 * - Week 6: Basic CRUD with Int IDs
 * - Week 7: Add toggle, inline edit
 * - Week 8: Add pagination, search
 */

fun Routing.configureTaskRoutes(store: TaskStore = TaskStore()) {
    val pebble =
        PebbleEngine
            .Builder()
            .loader(
                io.pebbletemplates.pebble.loader.ClasspathLoader().apply {
                    prefix = "templates/"
                },
            ).build()

    /**
     * Helper: Check if request is from HTMX
     */
    fun ApplicationCall.isHtmx(): Boolean = request.headers["HX-Request"]?.equals("true", ignoreCase = true) == true

    // Fragment endpoint for HTMX updates
    get("/tasks/fragment") {
        val q = call.request.queryParameters["q"]?.trim().orEmpty()
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val tasks = store.search(q).map { it.toPebbleContext() }
        val pageData = Page.paginate(tasks, currentPage = page, pageSize = 10)

        val list = call.renderTemplate("tasks/_list.peb", mapOf("page" to pageData, "q" to q))
        val pager = call.renderTemplate("tasks/_pager.peb", mapOf("page" to pageData, "q" to q))
        val status = """<div id="status" hx-swap-oob="true">Updated: showing ${pageData.items.size} of ${pageData.totalItems} tasks</div>"""

        call.respondText(list + pager + status, ContentType.Text.Html)
    }

    /**
     * GET /tasks - List all tasks
     * Returns full page (no HTMX differentiation in Week 6)
     */
    get("/tasks") {
        val q = call.request.queryParameters["q"]?.trim().orEmpty()
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val tasks = store.search(q).map { it.toPebbleContext() }
        val pageData = Page.paginate(tasks, currentPage = page, pageSize = 10)

        val html = call.renderTemplate("tasks/index.peb", mapOf(
            "page" to pageData,
            "q" to q,
            "title" to "Tasks"
        ))
        call.respondText(html, ContentType.Text.Html)
    }

    /**
     * POST /tasks - Add new task
     * Dual-mode: HTMX fragment or PRG redirect
     */
    post("/tasks") {
        val title = call.receiveParameters()["title"].orEmpty().trim()

        if (title.isBlank()) {
            // Validation error handling
            if (call.isHtmx()) {
                val error = """<div id="status" hx-swap-oob="true" role="alert" aria-live="assertive">
                    Title is required. Please enter at least one character.
                </div>"""
                return@post call.respondText(error, ContentType.Text.Html)
            } else {
                // No-JS: redirect back (could add error query param)
                call.response.headers.append("Location", "/tasks")
                return@post call.respond(HttpStatusCode.SeeOther)
            }
        }

        val task = TaskRepository.add(title)

        if (call.isHtmx()) {
            // Return HTML fragment for new task
            val fragment = """<li id="task-${task.id}">
                <span>${task.title}</span>
                <form action="/tasks/${task.id}/delete" method="post" style="display: inline;"
                      hx-post="/tasks/${task.id}/delete"
                      hx-target="#task-${task.id}"
                      hx-swap="outerHTML">
                  <button type="submit" aria-label="Delete task: ${task.title}">Delete</button>
                </form>
            </li>"""

            val status = """<div id="status" hx-swap-oob="true">Task "${task.title}" added successfully.</div>"""

            return@post call.respondText(fragment + status, ContentType.Text.Html, HttpStatusCode.Created)
        }

        // No-JS: POST-Redirect-GET pattern (303 See Other)
        call.response.headers.append("Location", "/tasks")
        call.respond(HttpStatusCode.SeeOther)
    }

    /**
     * POST /tasks/{id}/delete - Delete task
     * Dual-mode: HTMX empty response or PRG redirect
     */
    post("/tasks/{id}/delete") {
        val id = call.parameters["id"]?.toIntOrNull()
        val removed = id?.let { TaskRepository.delete(it) } ?: false

        if (call.isHtmx()) {
            val message = if (removed) "Task deleted." else "Could not delete task."
            val status = """<div id="status" hx-swap-oob="true">$message</div>"""
            // Return empty content to trigger outerHTML swap (removes the <li>)
            return@post call.respondText(status, ContentType.Text.Html)
        }

        // No-JS: POST-Redirect-GET pattern (303 See Other)
        call.response.headers.append("Location", "/tasks")
        call.respond(HttpStatusCode.SeeOther)
    }

    // TODO: Week 7 Lab 1 Activity 2 Steps 2-5
    // Add inline edit routes here
    // Follow instructions in mdbook to implement:
    // - GET /tasks/{id}/edit - Show edit form (dual-mode)
    // - POST /tasks/{id}/edit - Save edits with validation (dual-mode)
    // - GET /tasks/{id}/view - Cancel edit (HTMX only)
    get("/tasks/{id}/edit") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        val task = TaskRepository.get(id)
        if (task == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        val errorParam = call.request.queryParameters["error"]
        val errorMessage = if (errorParam == "blank") "Title is required. Please enter at least one character." else null

        if (call.isHtmx()) {
            val writer = StringWriter()
            pebble.getTemplate("tasks/_edit.peb").evaluate(writer, mapOf(
                "task" to task,
                "error" to errorMessage
            ))
            call.respondText(writer.toString(), ContentType.Text.Html)
        } else {
            val writer = StringWriter()
            pebble.getTemplate("tasks/index.peb").evaluate(writer, mapOf(
                "title" to "Tasks",
                "tasks" to TaskRepository.all(),
                "editingId" to id,
                "errorMessage" to errorMessage
            ))
            call.respondText(writer.toString(), ContentType.Text.Html)
        }
    }

    post("/tasks/{id}/edit") {
        val id = call.parameters["id"]?.toIntOrNull()
        val newTitle = call.receiveParameters()["title"]?.trim()

        if (id == null || newTitle == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        if (newTitle.isBlank()) {
            if (call.isHtmx()) {
                val task = TaskRepository.get(id)
                val writer = StringWriter()
                pebble.getTemplate("tasks/_edit.peb").evaluate(writer, mapOf(
                    "task" to task,
                    "error" to "Title is required. Please enter at least one character."
                ))
                call.respondText(writer.toString(), ContentType.Text.Html)
            } else {
                call.respondRedirect("/tasks/$id/edit?error=blank")
            }
            return@post
        }

        val updatedTask = TaskRepository.update(id, newTitle)

        if (updatedTask != null) {
            if (call.isHtmx()) {
                val writer = StringWriter()
                pebble.getTemplate("tasks/_item.peb").evaluate(writer, mapOf("task" to updatedTask))
                val status = """<div id="status" hx-swap-oob="true">Task updated.</div>"""
                call.respondText(writer.toString() + status, ContentType.Text.Html)
            } else {
                call.respondRedirect("/tasks")
            }
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    get("/tasks/{id}/view") {
        val id = call.parameters["id"]?.toIntOrNull()
        val task = id?.let { TaskRepository.get(it) }

        if (task != null) {
            val writer = StringWriter()
            pebble.getTemplate("tasks/_item.peb").evaluate(writer, mapOf("task" to task))
            call.respondText(writer.toString(), ContentType.Text.Html)
        } else {
            call.respondRedirect("/tasks")
        }
    }
}
