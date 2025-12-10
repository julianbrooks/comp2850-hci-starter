import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.sessions.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.pebbletemplates.pebble.PebbleEngine
import routes.taskRoutes
import routes.configureHealthCheck
import utils.SessionData
import java.io.StringWriter
import io.ktor.util.*
import io.ktor.util.AttributeKey

/**
 * NOTE FOR NON-INTELLIJ IDEs (VSCode, Eclipse, etc.):
 * IntelliJ IDEA automatically adds imports as you type. If using a different IDE,
 * you may need to manually add imports. The commented imports below show what you'll need
 * for future weeks. Uncomment them as needed when following the lab instructions.
 *
 * When using IntelliJ: You can ignore the commented imports below - your IDE will handle them.
 */

// Week 7+ imports (inline edit routes):
// import routes.configureEditRoutes  // Separate route file for edit functionality

// Week 8+ imports (status pages, error handling):
// import io.ktor.http.ContentType     // For custom error page content types
// import io.ktor.http.HttpStatusCode  // For status codes in error handlers
// import io.ktor.server.plugins.statuspages.*  // Status pages plugin (StatusPages, status)

// Week 9+ imports (request tracking, metrics):
// import utils.ReqIdKey                // AttributeKey for request ID tracking
// import utils.generateRequestId       // Generate unique request IDs for logging
// Note: ApplicationCallPipeline is covered by io.ktor.server.application.*

// Week 9 also adds ApplicationCallPipeline.Setup intercept in configureRouting() for session/reqId

/**
 * Main entry point for COMP2850 HCI server-first application.
 *
 * **Architecture**:
 * - Server-side rendering with Pebble templates
 * - Progressive enhancement via HTMX
 * - No-JS parity required for all features
 * - Privacy-by-design: anonymous session IDs only
 *
 * **Key Principles**:
 * - WCAG 2.2 AA compliance mandatory
 * - Semantic HTML baseline
 * - ARIA live regions for dynamic updates
 * - Keyboard navigation support
 *
 * @see <a href="https://htmx.org/docs/">HTMX Documentation</a>
 * @see <a href="https://www.w3.org/WAI/WCAG22/quickref/">WCAG 2.2 Quick Reference</a>
 */
fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val host = "0.0.0.0" // Required for Codespaces

    embeddedServer(Netty, port = port, host = host) {
        configureLogging()
        configureTemplating()
        configureSessions()
        configureRouting()
    }.start(wait = true)
}

/**
 * Configure request logging for development and debugging.
 */
fun Application.configureLogging() {
    install(CallLogging) {
        // Log format: METHOD /path - status (duration ms)
        format { call ->
            val status = call.response.status()
            val method = call.request.httpMethod.value
            val path = call.request.path()
            "$method $path - $status"
        }
    }
}

/**
 * Configure Pebble templating engine.
 * Templates are in src/main/resources/templates/
 *
 * **Template conventions**:
 * - Partials start with underscore: `_list.peb`, `_item.peb`
 * - Layouts in `_layout/` subdirectory
 * - Full pages in root or feature subdirectories
 */
fun Application.configureTemplating() {
//    val pebbleEngine =
//        PebbleEngine
//            .Builder()
//            .loader(
//                io.pebbletemplates.pebble.loader.ClasspathLoader().apply {
//                    prefix = "templates/"
//                },
//            ).autoEscaping(true) // XSS protection via auto-escaping
//            .cacheActive(false) // Disable cache in dev for hot reload
//            .strictVariables(false) // Allow undefined variables (fail gracefully)
//            .build()
//
//    environment.monitor.subscribe(ApplicationStarted) {
//        log.info("✓ Pebble templates loaded from resources/templates/")
//        log.info("✓ Server running on configured port")
//    }
//
//    // Make Pebble available to all routes
//    attributes.put(PebbleEngineKey, pebbleEngine)
    val engine = PebbleEngine.Builder()
        .loader(ClasspathLoader().apply { prefix = "templates/" })
        .build()

    // Store in attributes so renderTemplate() can access it
    attributes.put(PebbleEngineKey, engine)
}



/**
 * AttributeKey for storing Pebble engine instance.
 */
val PebbleEngineKey = AttributeKey<PebbleEngine>("PebbleEngine")

/**
 * Render Pebble template to HTML string.
 * Extension function for cleaner template rendering in routes.
 */
suspend fun ApplicationCall.renderTemplate(
    templateName: String,
    context: Map<String, Any> = emptyMap()
): String {
    val engine = application.attributes[PebbleEngineKey]
    val writer = StringWriter()
    val template = engine.getTemplate(templateName)

    // Auto-add session info to all templates
    val sessionData = sessions.get<SessionData>()
    val enrichedContext = context + mapOf(
        "sessionId" to (sessionData?.id ?: "anonymous"),
        "isHtmx" to isHtmxRequest()
    )

    template.evaluate(writer, enrichedContext)
    return writer.toString()
}

/**
 * Render a Pebble template to HTML string.
 *
 * **Usage**:
 * ```kotlin
 * val html = call.renderTemplate("tasks/index.peb", mapOf("tasks" to taskList))
 * call.respondText(html, ContentType.Text.Html)
 * ```
 *
 * **Context enrichment**:
 * - Automatically adds `sessionId` from session
 * - Automatically adds `isHtmx` flag (true if HX-Request header present)
 *
 * @param templateName Template path relative to resources/templates/
 * @param context Data to pass to template (map of variable names to values)
 * @return Rendered HTML string
 */
suspend fun ApplicationCall.renderTemplate(
    templateName: String,
    context: Map<String, Any> = emptyMap(),
): String {
    val engine = application.attributes[PebbleEngineKey]
    val writer = StringWriter()
    val template = engine.getTemplate(templateName)

    // Add global context available to all templates
    val sessionData = sessions.get<SessionData>()
    val enrichedContext =
        context +
            mapOf(
                "sessionId" to (sessionData?.id ?: "anonymous"),
                "isHtmx" to isHtmxRequest(),
            )

    template.evaluate(writer, enrichedContext)
    return writer.toString()
}

/**
 * Check if request is from HTMX (progressive enhancement mode).
 *
 * **HTMX detection**:
 * - HTMX adds `HX-Request: true` header to all AJAX requests
 * - Use this to return fragments vs full pages
 *
 * **Pattern**:
 * ```kotlin
 * if (call.isHtmxRequest()) {
 *     // Return partial HTML fragment
 *     call.respondText(render("tasks/_list.peb"))
 * } else {
 *     // Traditional redirect (POST-Redirect-GET)
 *     call.respondRedirect("/tasks")
 * }
 * ```
 */
fun ApplicationCall.isHtmxRequest(): Boolean = request.headers["HX-Request"] == "true"

/**
 * Configure session handling (privacy-safe anonymous IDs).
 *
 * **Privacy notes**:
 * - Session IDs are random, anonymous (no PII)
 * - Used for metrics correlation only
 * - Cookie is HttpOnly, SameSite=Strict
 * - No tracking across devices/browsers
 */
fun Application.configureSessions() {
    install(Sessions) {
        cookie<SessionData>("COMP2850_SESSION") {
            cookie.path = "/"
            cookie.httpOnly = true
            cookie.extensions["SameSite"] = "Strict"
            // No maxAge = session cookie (deleted when browser closes)
        }
    }
}

/**
 * Configure application routing.
 *
 * **Route organization**:
 * - Static files: `/static/...` (CSS, JS, HTMX)
 * - Health check: `/health`
 * - Task CRUD: `/tasks`, `/tasks/{id}`, etc.
 */
fun Application.configureRouting() {
    val store = TaskStore()  // Create instance
    routing {
        configureTaskRoutes(store)  // Pass to routes
    }
}
}
