from collections import defaultdict

from discord.ext import commands

TBD_BOT_COG_PREFIX = "tbdbot.cogs."


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

    @ext.command(name="list")
    async def ext_list(self, ctx):
        cogs_dict = self.list_loaded_cogs()
        result = ""
        for ext_name, cog_list in cogs_dict.items():
            result += f"Extension {ext_name}"
            for cog in cog_list:
                result += f"|-- {cog} - v{getattr(cog, 'version', 'ersion undefined')}"
        await self.bot.reply(ctx, result)


def setup(bot):
    bot.add_cog(ExtensionManagerCog(bot))


def teardown(bot):
    bot.remove_cog(ExtensionManagerCog(bot))
