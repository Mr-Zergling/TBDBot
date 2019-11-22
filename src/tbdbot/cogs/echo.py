from discord.ext import commands

class Hello(commands.Cog):
    def __init__(self, bot):
        self.bot = bot


    @commands.group(pass_context=True)
    @commands.is_owner()
    async def hello(self, ctx):
        pass
        

    @hello.command()
    async def son(self, ctx):
        await ctx.message.channel.send('HI DAD!1!1!!1ONEONE!1!1!')