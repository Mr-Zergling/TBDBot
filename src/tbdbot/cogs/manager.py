from collections import defaultdict

from discord.ext import commands


def qualify_name(name):
    if not name.starts_with("tbdbot.cogs."):
        return "tbdbot.cogs." + name
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
            ext_list = result[cog.__class__.__module__]
            ext_list.append(name)
        return result

    @commands.group(pass_context=True)
    async def ext(self, ctx):
        pass

    @ext.command(name="list")
    async def ext_list(self, ctx):
        await self.bot.reply(ctx, self.list_loaded_cogs())


def setup(bot):
    bot.add_cog(ExtensionManagerCog(bot))


def teardown(bot):
    bot.remove_cog(ExtensionManagerCog(bot))
