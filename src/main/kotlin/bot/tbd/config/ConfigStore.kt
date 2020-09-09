package bot.tbd.config

import bot.tbd.exception.NoGuildConfigurationFound
import com.beust.klaxon.Klaxon
import com.gitlab.kordlib.common.entity.Snowflake
import java.io.File
import java.nio.file.Paths

typealias GuildConfigMap = MutableMap<String, GuildConfig>

object Config {

    lateinit var global: BotConfig
    val guildConfigs: GuildConfigMap = HashMap()

    fun init(botConfigPath: String) {
        global = Klaxon().parse<BotConfig>(File(botConfigPath))!!
        loadGuildConfigs("${global.storageDir}/guildConfigs")
    }

    fun getGuildConfig(guild: Snowflake, key: GuildConfigKey): String =
        guildConfigs[guild.value]?.getValue(key) ?: throw NoGuildConfigurationFound(guild)

    private fun loadGuildConfigs(guildConfigBasePath: String) {
        val storage = File(guildConfigBasePath)
        if (!storage.exists()) {
            storage.mkdirs()
        }
        storage.walk().forEach {
            if (it.isFile && it.extension == "json") {
                val conf = Klaxon().parse<GuildConfig>(it)
                conf?.let { c -> guildConfigs[c.guildId] = c }
            }
        }
    }
}

data class BotConfig(
    val apiToken: String,
    val storageDir: String = Paths.get(".").toAbsolutePath().normalize().toString()
)
