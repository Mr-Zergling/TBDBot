package bot.tbd.module

import com.gitlab.kordlib.core.Kord

interface Module {
    fun initialize(client: Kord)
}
