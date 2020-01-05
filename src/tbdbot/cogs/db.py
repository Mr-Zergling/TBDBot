from discord.ext import commands
from tbdbot.db.db_interface import *
import traceback
from datetime import datetime, timezone, timedelta


class MessageListenerCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.version = "0.1"

    async def fresh_start(self):
        await consistency_check(self.bot, timedelta(days=2))

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
            messages = []
            reactions = []
            async for message in channel.history(limit=None):
                if message.created_at < message_cutoff:
                    break
                messages.append(message)
                reactions.extend(message.reactions)
                if len(messages) > 1000:
                    await bulk_handle_messages_and_reactions(messages, reactions, bot)
                    messages = []
                    reactions = []
            await bulk_handle_messages_and_reactions(messages, reactions, bot)
        except Exception as e:
            print(f"Cannot Access history for: {channel}")
            print(e)
            traceback.print_exc()


async def bulk_handle_messages_and_reactions(messages, reactions, bot):
    await bulk_upsert_messages(messages, bot)
    for reaction in reactions:
        reaction_user_pairs = []
        async for user in reaction.users():
            reaction_user_pairs.append((reaction, user))
        await bulk_upsert_reactions(reaction_user_pairs)


async def handle_message(message, bot):
    try:
        await upsert_message(message, bot)
        for reaction in message.reactions:
            async for user in reaction.users():
                await handle_reaction(reaction, user)
    except Exception as e:
        print(f"Error Handing Message {message}")
        print(e)
        traceback.print_exc()


async def handle_reaction(reaction, user):
    await upsert_reaction(reaction.emoji, user, reaction.message)


def setup(bot):
    bot.add_cog(MessageListenerCog(bot))


def teardown(bot):
    bot.remove_cog(MessageListenerCog(bot))
