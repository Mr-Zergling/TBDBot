from discord.ext import commands
import psutil
import platform

def is_bot_bullshit(ctx):
    return ctx.message.channel.id == 647172167284555779 or ctx.message.channel.id == 646039898381615115

class Hello(commands.Cog):
    def __init__(self, bot):
        self.bot = bot


    @commands.command(name="version")
    @commands.check(is_bot_bullshit)
    async def version(self, ctx):
        await ctx.message.channel.send("TBD Bot V0.00001 PrePrePrePre...-Alpha")

    @commands.command(name="ping")
    @commands.check(is_bot_bullshit)
    async def ping(self, ctx):
        await ctx.message.channel.send(f"Pong {str(ctx.message.author)}, you're welcome")

    @commands.command(name="sysinfo")
    @commands.check(is_bot_bullshit)
    async def sysinfo(self, ctx):
        memory = psutil.virtual_memory()
        info = f"""```
{psutil.cpu_count()} CPUs @ {psutil.cpu_freq().current} Mhz
CPU %: {psutil.cpu_percent(interval=1)}
Total Memory (MB): {int(memory.total/1024/1024)}
Available Memory (MB): {int(memory.available /1024/1024)}
Plaform: {platform.platform()}```"""
        await ctx.message.channel.send(info)

    @commands.group(pass_context=True)
    @commands.is_owner()
    async def hello(self, ctx):
        pass
        

    @hello.command()
    async def son(self, ctx):
        await ctx.message.channel.send('HI DAD!1!1!!1ONE!1!ONE1!')


