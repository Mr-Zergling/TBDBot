from discord.ext import commands
from discord.utils import get

from tbdbot.cogs.scheduled_tasks import add_task
from tbdbot.util.date_and_time import parse_time_period


class CWBanCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot

    @commands.command(name="cwbanme")
    async def self_cw_ban(self, ctx, *, time="1hr"):
        ban_end_date = parse_time_period(time)
        if not ctx.guild:
            await self.bot.reply(ctx, "Must be used in a server!", dm_reply=True)
            return
        user = ctx.message.author
        await user.add_roles(get(ctx.guild.roles, name="CW Ban"))
        add_task(ban_end_date, self.remove_cw_ban, {"user": user, "ctx": ctx})
        await self.bot.reply(ctx, f"CW Banned until {ban_end_date} UTC", dm_reply=True)
        await self.bot.log(f"{user.name} CW Banned until {ban_end_date} UTC")
        await ctx.message.delete()

    async def remove_cw_ban(self, user, ctx):
        await user.remove_roles(get(ctx.guild.roles, name="CW Ban"))
        await self.bot.reply(ctx, f"CW Ban Removed!", dm_reply=True)



def setup(bot):
    bot.add_cog(CWBanCog(bot))


def teardown(bot):
    bot.remove_cog(CWBanCog(bot))
