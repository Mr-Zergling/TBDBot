from discord.ext.commands import ExtensionError

from ..checks import is_bot_channel
from collections import defaultdict

from discord.ext import commands

TBD_BOT_COG_PREFIX = "tbdbot.cogs."

TBD_BOT_STARTUP_EXTENSIONS = [
    "echo",
    "emoji",
]


def qualify_name(name):
    if not name.startswith(TBD_BOT_COG_PREFIX):
        return TBD_BOT_COG_PREFIX + name
    return name


def unqualify_name(name):
    if name.startswith(TBD_BOT_COG_PREFIX):
        return name.lstrip(TBD_BOT_COG_PREFIX)
    return name


class ExtensionManagerCog(commands.Cog):

    def __init__(self, bot):
        self.bot = bot

    def first_start(self):
        for extension in TBD_BOT_STARTUP_EXTENSIONS:
            self.load_extension(extension)
        for name, cog in self.bot.cogs.items():
            fresh_start = getattr(cog, "fresh_start", None)
            if callable(fresh_start):
                fresh_start()

    def load_extension(self, name):
        name = qualify_name(name)
        self.bot.load_extension(name)

    def unload_extension(self, name):
        name = qualify_name(name)
        self.bot.unload_extension(name)

    def reload_extension(self, name):
        name = qualify_name(name)
        self.bot.reload_extension(name)

    def list_loaded_cogs(self):
        result = defaultdict(list)
        for name, cog in self.bot.cogs.items():
            ext_list = result[unqualify_name(cog.__class__.__module__)]
            ext_list.append(name)
        return result

    @commands.group(pass_context=True)
    async def ext(self, ctx):
        pass

    @commands.check(is_bot_channel)
    @ext.command(name="list")
    async def ext_list(self, ctx):
        cogs_dict = self.list_loaded_cogs()
        result = "```\n"
        for ext_name, cog_list in cogs_dict.items():
            result += f"{ext_name}\n"
            for cog in cog_list:
                result += f"|-- {cog} - v{getattr(cog, 'version', 'ersion undefined')}\n"
        result += "```"
        await self.bot.reply(ctx, result)

    @commands.is_owner()
    @ext.command(name="load")
    async def ext_load(self, ctx, name):
        self.load_extension(name)
        try:
            await self.bot.reply(ctx, f"loaded extension {name}")
            await ctx.invoke(self.ext_list)
        except ExtensionError:
            await self.bot.reply(ctx, f"error loading {name}")

    @commands.is_owner()
    @ext.command(name="unload")
    async def ext_unload(self, ctx, name):
        self.unload_extension(name)
        try:
            await self.bot.reply(ctx, f"unloaded extension {name}")
            await ctx.invoke(self.ext_list)
        except ExtensionError:
            await self.bot.reply(ctx, f"error unloading {name}")

    @commands.is_owner()
    @ext.command(name="reload")
    async def ext_reload(self, ctx, name):
        self.reload_extension(name)
        try:
            await self.bot.reply(ctx, f"reloaded extension {name}")
            await ctx.invoke(self.ext_list)
        except ExtensionError:
            await self.bot.reply(ctx, f"error reloading {name}")


def setup(bot):
    bot.add_cog(ExtensionManagerCog(bot))


def teardown(bot):
    bot.remove_cog(ExtensionManagerCog(bot))
