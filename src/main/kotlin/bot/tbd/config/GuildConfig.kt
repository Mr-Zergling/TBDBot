package bot.tbd.config

import bot.tbd.exception.NoGuildConfigValueFound

class GuildConfig(val guildId: String, val configMap: MutableMap<String, String> = HashMap()) {
    fun getValue(key: GuildConfigKey): String = configMap[key.name] ?: throw NoGuildConfigValueFound(guildId, key)
    fun setValue(key: GuildConfigKey, value: String) {
        configMap[key.name] = value
    }
}

enum class GuildConfigKey {
    CW_CHANNEL,
    CW_BAN_ROLE,
}
