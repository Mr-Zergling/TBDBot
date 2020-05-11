package bot.tbd.extensions

import com.gitlab.kordlib.core.entity.Message

suspend fun Message.reply(content: String) {
    this.channel.createMessage(content)
}

suspend fun Message.replyDM(content: String) {
    this.author?.getDmChannel()?.createMessage(content)
}

suspend fun Message.getAuthorNick(): String {
    val author = this.author ?: return "system"
    return this.getAuthorAsMember()?.displayName ?: author.username
}
