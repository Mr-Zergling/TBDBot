package bot.tbd.util

import dev.kord.common.entity.Snowflake


fun String.toSnowflake() = Snowflake(this)
