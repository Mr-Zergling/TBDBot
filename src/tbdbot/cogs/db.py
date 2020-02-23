from discord.ext import commands


class MessageListenerCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.version = "0.1"

    async def fresh_start(self):
        pass

    @commands.Cog.listener()
    async def on_message(self, message):
        pass

    @commands.Cog.listener()
    async def on_reaction_add(self, reaction, user):
        pass

    @commands.Cog.listener()
    async def on_raw_reaction_remove(self, payload):
        pass


class PersistanceCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.version = "0.1"


async def consistency_check(bot, timedelta=None, target_guild=None):
    pass


def setup(bot):
    bot.add_cog(MessageListenerCog(bot))
    bot.add_cog(PersistanceCog(bot))


def teardown(bot):
    bot.remove_cog(MessageListenerCog(bot))
    bot.remove_cog(PersistanceCog(bot))
