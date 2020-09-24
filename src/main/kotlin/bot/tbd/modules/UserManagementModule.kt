@file:AutoWired

package bot.tbd.modules

import bot.tbd.config.Config
import bot.tbd.config.GuildConfigKey
import bot.tbd.scheduling.RemoveMemberRoleTask
import bot.tbd.scheduling.ScheduledTaskExecutor
import bot.tbd.util.DateParsing
import bot.tbd.util.toSnowflake
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.module.module
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

val userManagementModule = module("user-management") {

    command("cwbanme") {
        invoke(StringArgument) { str ->
            val user = message.author!!
            val until = DateParsing.natLanguageToInstant(str)
            until?.let {
                ScheduledTaskExecutor.instance.addTask(
                    RemoveMemberRoleTask(
                        it.toEpochMilli(),
                        Config.getGuildConfig(guild!!.id, GuildConfigKey.CW_BAN_ROLE),
                        user.id.value,
                        guild!!.id.value
                    )
                )
            } ?: run {
                channel.createMessage("Couldn't parse a date from $str")
                return@invoke
            }
            author.asMember(guild!!.id)
                .addRole(Config.getGuildConfig(guild!!.id, GuildConfigKey.CW_BAN_ROLE).toSnowflake())
            respond("${user.username}#${user.discriminator} CWBanned until $until")
        }
    }
}
