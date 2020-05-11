package bot.tbd.extensions

import bot.tbd.BaseTest
import io.mockk.coVerify
import org.testng.annotations.Test

class MessageExtensionsTest : BaseTest() {

    @Test
    suspend fun testReplyToGuildMessage() {
        mockGuildChannelMessage.reply("test")
        coVerify { mockGuildChannel.createMessage("test") }
    }

    @Test
    suspend fun testReplyToDMMessage() {
        mockDMMessage.reply("test")
        coVerify { mockDMChannel.createMessage("test") }
    }

    @Test
    suspend fun testReplyDMToGuildMessage() {
        mockGuildChannelMessage.replyDM("test")
        coVerify { mockDMChannel.createMessage("test") }
    }

    @Test
    suspend fun testReplyDMToDM() {
        mockDMMessage.replyDM("test")
        coVerify { mockDMChannel.createMessage("test") }
    }

    @Test
    suspend fun testGetAuthorNickWithGuildMessage() {

    }

    @Test
    suspend fun testGetAuthorNickWithDMMessage() {

    }
}