package bot.tbd.config

import com.beust.klaxon.Klaxon
import java.io.File

object Config {

    lateinit var global: BotConfig

    fun init(botConfigPath: String) {
        global = Klaxon().parse<BotConfig>(File(botConfigPath))!!
    }
}

data class BotConfig(val apiToken: String)
