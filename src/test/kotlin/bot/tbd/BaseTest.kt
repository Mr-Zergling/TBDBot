package bot.tbd

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.DmChannel
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import org.testng.annotations.BeforeMethod

abstract class BaseTest {

    val mockGuild = mockk<Guild>() {
        coEvery { id } answers { Snowflake(4444444) }
    }

    val mockDMChannel = mockk<DmChannel>()

    val mockGuildChannel = mockk<MessageChannel>()

    val mockGuildMember = mockk<Member>() {}

    val mockGuildUser = mockk<User>() {
        coEvery { getDmChannel() } answers { mockDMChannel }
    }

    val mockDMUser = mockk<User>() {
        coEvery { getDmChannel() } answers { mockDMChannel }
    }

    val mockGuildChannelMessage = mockk<Message>() {
        coEvery { getGuild() } returns mockGuild
        coEvery { channel } returns mockGuildChannel
        coEvery { author } returns mockGuildUser
        coEvery { getAuthorAsMember() } returns mockGuildMember
    }

    val mockDMMessage = mockk<Message>() {
        coEvery { getGuild() } returns null
        coEvery { author } returns mockDMUser
        coEvery { getAuthorAsMember() } returns null
    }

    @BeforeMethod
    fun reset() {
        clearAllMocks(answers = false)
    }
}