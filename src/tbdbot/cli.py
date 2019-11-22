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

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--apikey')
    args = parser.parse_args()
    bot.add_cog(Hello(bot))
    bot.add_cog(EmojiCog(bot))
    bot.run(args.apikey)