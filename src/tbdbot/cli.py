from discord.ext import commands
from tbdbot.emoji_counter import backfill_emoji_count
from tbdbot.cogs.echo import Hello
import argparse
import json

BOT_PREFIX = "$"
bot = commands.Bot(BOT_PREFIX)

@bot.event
async def on_ready():
    print('We have logged in as {0.user}'.format(bot))

async def on_message_bak(message):
    if message.author == client.user:
        return

    if message.content.startswith('count emoji please') and await client.is_owner(message.author):
        async with message.channel.typing():
            await message.channel.send('counting emoji in messages and reactions for last 20000 messages in each channel')
            await backfill_emoji_count(message)

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--apikey')
    args = parser.parse_args()
    bot.add_cog(Hello(bot))
    bot.run(args.apikey)