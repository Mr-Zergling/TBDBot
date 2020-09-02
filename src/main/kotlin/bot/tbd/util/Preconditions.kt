package bot.tbd.util

import com.gitlab.kordlib.kordx.commands.kord.model.precondition.precondition

object Preconditions {
    val isBotOwner = precondition {
        if (kord.getApplicationInfo().ownerId == author.id) {
            true
        } else {
            respond("Command ${command.name} only available to owner")
            false
        }
    }
}
