from discord.ext import commands


class PersistanceCog(commands.cog):
    def __init__(self, bot):
        self.bot = bot


def setup(bot):
    bot.add_cog(PersistanceCog(bot))


def teardown(bot):
    bot.remove_cog(PersistanceCog(bot))
