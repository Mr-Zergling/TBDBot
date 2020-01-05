from discord.ext import commands
from tbdbot.db.db_interface import *
from tbdbot.util.emoji_util import get_custom_emoji_count_from_string, get_unicode_emoji_count_from_string

from datetime import datetime, timezone, timedelta


class MessageListenerCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.version = "0.1"

    async def fresh_start(self):
        await consistency_check(self.bot, timedelta(days=1))

    @commands.Cog.listener()
    async def on_message(self, message):
        await handle_message(message, self.bot)

    @commands.Cog.listener()
    async def on_reaction_add(self, reaction, user):
        await handle_reaction(reaction, user)


class DBManagementCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.version = "0.1"


async def consistency_check(bot, timedelta=None):
    print("starting consistency check")
    async for guild in bot.fetch_guilds():
        print(f"updated guild {guild}")
        await bulk_upsert_custom_emoji(await guild.fetch_emojis())
        #async for user in guild.fetch_members(): needs discord.py 1.3.0a apparently?
        #    await upsert_user(user)
    print("done updating guilds")
    message_cutoff = (datetime.utcnow() - timedelta) if timedelta else None
    for channel in bot.get_all_channels():
        if not hasattr(channel, "history"):
            continue
        print(f"{channel}")
        try:
            async for message in channel.history():
                if message.created_at < message_cutoff:
                    break
                await handle_message(message, bot)
        except Exception as e:
            print(f"Cannot Access history for: {channel}")
            print(e)


async def handle_message(message, bot):
    try:
        await upsert_message(message)
        for emoji_tuple, use_count in get_unicode_emoji_count_from_string(message.content).items():
            await upsert_unicode_emoji_in_text(message, use_count, name=emoji_tuple[0], codepoint=emoji_tuple[1])
        for emoji_tuple, use_count in get_custom_emoji_count_from_string(message.content).items():
            name = emoji_tuple[0]
            id = emoji_tuple[1]
            api_emoji = bot.get_emoji(id)
            await upsert_custom_emoji_in_text(message, use_count, emoji=api_emoji, id=id, name=name)
        for reaction in message.reactions:
            async for user in reaction.users():
                await handle_reaction(reaction, user)
    except Exception as e:
        print(f"Error Handing Message {message}")
        print(e)


async def handle_reaction(reaction, user):
    await upsert_reaction(reaction.emoji, reaction.message, user)


def setup(bot):
    bot.add_cog(MessageListenerCog(bot))


def teardown(bot):
    bot.remove_cog(MessageListenerCog(bot))
