from asgiref.sync import async_to_sync
from discord.ext import commands
from tbdbot.db.db_interface import upsert_message
import json

from datetime import datetime, timezone, timedelta


class MessageListenerCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.version = "0.1"

    async def fresh_start(self):
        await consistencyCheck(self.bot, timedelta(days=3))


class DBManagementCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.version = "0.1"


async def consistencyCheck(bot, timedelta=None):
    print("starting consistency check")
    message_cutoff = (datetime.utcnow() - timedelta) if timedelta else None
    for channel in bot.get_all_channels():
        if not hasattr(channel, "history"):
            continue
        print(f"{channel}")
        try:
            async for message in channel.history(after=message_cutoff):
                await upsert_message(message)
        except Exception as e:
            print(f"Cannot Access history for: {channel}")
            print(e)


def setup(bot):
    bot.add_cog(MessageListenerCog(bot))


def teardown(bot):
    bot.remove_cog(MessageListenerCog(bot))
