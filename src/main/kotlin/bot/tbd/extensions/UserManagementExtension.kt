package bot.tbd.extensions

import bot.tbd.config.Config
import bot.tbd.config.GuildConfigKey
import bot.tbd.scheduling.RemoveMemberRoleTask
import bot.tbd.scheduling.ScheduledTaskExecutor
import bot.tbd.util.DateParsing
import bot.tbd.util.toSnowflake
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescedString
import com.kotlindiscord.kord.extensions.commands.parser.Arguments
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.core.any

class UserManagementExtension: Extension() {
    override val name: String = "User Management"

    inner class CWBanMeArguments: Arguments(){
        val date by coalescedString(displayName = "date", "Something Resembling a date or duration, how long you wanna be CW Banned for")
    }

    override suspend fun setup() {
        command(::CWBanMeArguments){
            name = "cwbanme"
            description = "CW Ban yourself for a period of time"
            action{
                val cwBanRole = Config.getGuildConfig(guild!!.id, GuildConfigKey.CW_BAN_ROLE).toSnowflake()
                val user = message.getAuthorAsMember()!!
                if(user.roles.any { it.id == cwBanRole}){
                    message.respond("You're already CW Banned, nerd")
                    return@action
                }
                val until = DateParsing.natLanguageToInstant(arguments.date)
                until?.let {
                    ScheduledTaskExecutor.instance.addTask(
                        RemoveMemberRoleTask(
                            it.toEpochMilli(),
                            Config.getGuildConfig(guild!!.id, GuildConfigKey.CW_BAN_ROLE),
                            user.id.asString,
                            guild!!.id.asString
                        )
                    )
                } ?: run {
                    channel.createMessage("Couldn't parse a date from ${arguments.date}")
                    return@action
                }
                user.addRole(cwBanRole)
                message.respond("${user.username}#${user.discriminator} CWBanned until $until")
            }
        }
    }
}