from discord.ext import commands
from tbdbot.cogs.emoji import EmojiCog
from tbdbot.cogs.echo import Hello
import argparse
import json

BOT_PREFIX = "$"
bot = commands.Bot(BOT_PREFIX)

@bot.event
async def on_ready():
    print('We have logged in as {0.user}'.format(bot))

@bot.check
async def global_check_for_politeness(ctx):
    if(await bot.is_owner(ctx.message.author)):
        return True
    text = ctx.message.content.lower()
    if(text.endswith('please') or text.endswith('pls') or text.endswith('plz')):
        return True
    await ctx.message.channel.send("AH-AH-AH, you didn't say the magic word!")
    return False


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--apikey')
    args = parser.parse_args()
    bot.add_cog(Hello(bot))
    bot.add_cog(EmojiCog(bot))
    bot.run(args.apikey)