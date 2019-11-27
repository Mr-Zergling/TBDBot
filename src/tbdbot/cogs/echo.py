from ..checks import is_bot_channel
from discord.ext import commands

import psutil
import platform


class HelloCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.version = "0.1"

    @commands.command(name="ping")
    @commands.check(is_bot_channel)
    async def ping(self, ctx):
        await self.bot.reply(ctx, f"Pong {str(ctx.message.author)}, you're welcome")

    @commands.command(name="dm_ping")
    async def dm_ping(self, ctx):
        await self.bot.reply(ctx, f"DM Pong {str(ctx.message.author)}, you're welcome", dm_reply=True)

    @commands.group(pass_context=True)
    @commands.is_owner()
    async def hello(self, ctx):
        pass

    @hello.command()
    async def son(self, ctx):
        await self.bot.reply(ctx, 'HI DAD!1!1!!1ONE!1!ONE1!')


class InfoCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.version = "0.1"

    @commands.command(name="version")
    @commands.check(is_bot_channel)
    async def version(self, ctx):
        await self.bot.reply(ctx, f"TBD Bot {self.bot.version}")

    @commands.command(name="sysinfo")
    @commands.check(is_bot_channel)
    async def sysinfo(self, ctx):
        memory = psutil.virtual_memory()
        info = f"""```
{psutil.cpu_count()} CPUs @ {psutil.cpu_freq().current} Mhz
CPU %: {psutil.cpu_percent(interval=1)}
Total Memory (MB): {int(memory.total/1024/1024)}
Available Memory (MB): {int(memory.available /1024/1024)}
Plaform: {platform.platform()}```"""
        await self.bot.reply(ctx, info)


def setup(bot):
    bot.add_cog(HelloCog(bot))
    bot.add_cog(InfoCog(bot))


def teardown(bot):
    bot.remove_cog(HelloCog(bot))
    bot.remove_cog(InfoCog(bot))
