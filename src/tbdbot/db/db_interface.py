from asgiref.sync import sync_to_async
from tbdbot.db.db_model import db_session
from tbdbot.db import db_mapper as mapper
from tbdbot.util.emoji_util import get_custom_emoji_count_from_string, get_unicode_emoji_count_from_string
import re


@sync_to_async
@db_session
def upsert_message(apimessage, bot):
    _upsert_single_message(apimessage, bot)


@sync_to_async
@db_session
def bulk_upsert_messages(messages, bot):
    for m in messages:
        _upsert_single_message(m, bot)


def _upsert_single_message(apimessage, bot):
    mapper.get_or_create_message(apimessage)
    for emoji_tuple, use_count in get_unicode_emoji_count_from_string(apimessage.content).items():
        mapper.get_or_create_unicode_emoji_in_text(apimessage, use_count, name=emoji_tuple[0], codepoint=emoji_tuple[1])
    for emoji_tuple, use_count in get_custom_emoji_count_from_string(apimessage.content).items():
        name = emoji_tuple[0]
        id = emoji_tuple[1]
        api_emoji = bot.get_emoji(id)
        mapper.get_or_create_custom_emoji_in_text(apimessage, use_count, api_emoji=api_emoji, id=id, name=name)


@sync_to_async
def upsert_guild(guild):
    mapper.get_or_create_server(guild)


@sync_to_async
def upsert_custom_emoji(emoji):
    mapper.get_or_create_custom_emoji(api_emoji=emoji)


@sync_to_async
@db_session
def bulk_upsert_custom_emoji(emoji):
    for e in emoji:
        mapper.get_or_create_custom_emoji(api_emoji=e)


@sync_to_async
def upsert_user(user):
    mapper.get_or_create_user(user)


@sync_to_async
@db_session
def upsert_reaction(emoji, user, message):
    _upsert_single_reaction(emoji, user, message)


@sync_to_async
@db_session
def bulk_upsert_reactions(reaction_user_pairs):
    for reaction, user in reaction_user_pairs:
        _upsert_single_reaction(reaction.emoji, user, reaction.message)


@sync_to_async
@db_session
def remove_reaction(emoji_id, user_id, message_id):


def _upsert_single_reaction(emoji, user, message):
    mapper.get_or_create_reaction(emoji, user, message)
