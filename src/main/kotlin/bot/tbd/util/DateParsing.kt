package bot.tbd.util

import com.joestelmach.natty.Parser
import mu.KotlinLogging
import java.lang.Exception
import java.time.Instant

private val log = KotlinLogging.logger {}

@Suppress("TooGenericExceptionCaught")
object DateParsing {
    fun natLanguageToInstant(str: String): Instant? =
        try {
            val parser = Parser()
            val groups = parser.parse(str)
            groups?.first()?.dates?.first()?.toInstant()
        } catch (e: Exception) {
            log.warn("Date parsing error", e)
            null
        }
}
