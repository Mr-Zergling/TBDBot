package bot.tbd.util

import com.gitlab.kordlib.common.entity.Snowflake

fun String.toSnowflake() = Snowflake(this)
