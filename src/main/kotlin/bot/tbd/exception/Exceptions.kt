package bot.tbd.exception

import bot.tbd.config.GuildConfigKey
import dev.kord.common.entity.Snowflake
import java.lang.RuntimeException

class NoGuildConfigValueFound(guildId: String, key: GuildConfigKey) :
    RuntimeException("No guild-specific config value found for guildId $guildId and key ${key.name}")

class NoGuildConfigurationFound(guild: Snowflake) :
    RuntimeException("No guild-specific config found for guildId ${guild.value}")
