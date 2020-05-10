package bot.tbd.config

import com.beust.klaxon.Klaxon
import java.io.File

object Config{
    private const val BOT_CONFIG_PATH = "bot_config.json"

    val global: BotConfig

    init{
        global = Klaxon().parse<BotConfig>(File(BOT_CONFIG_PATH))!!
    }
}

data class BotConfig(val apiToken: String)
