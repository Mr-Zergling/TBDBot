from asgiref.sync import sync_to_async
from tbdbot.db.db_model import db_session
from tbdbot.db import db_mapper as mapper
from emoji import demojize, emoji_lis
import re


@sync_to_async
def upsert_message(apimessage):
    with db_session:
        mapper.get_or_create_message(apimessage)

@sync_to_async
def upsert_guild(guild):
    with db_session:
        mapper.get_or_create_server(guild)

@sync_to_async
def upsert_custom_emoji_in_text(message, use_count, emoji=None, id=None, name=None):
    with db_session:
        mapper.get_or_create_custom_emoji_in_text(message, use_count, api_emoji=emoji, id=id, name=name)

@sync_to_async
def upsert_custom_emoji(emoji):
    with db_session:
        mapper.get_or_create_custom_emoji(api_emoji=emoji)

@sync_to_async
def bulk_upsert_custom_emoji(emoji):
    with db_session:
        for e in emoji:
            mapper.get_or_create_custom_emoji(api_emoji=e)

@sync_to_async
def upsert_user(user):
    with db_session:
        mapper.get_or_create_user(user)

@sync_to_async
def upsert_unicode_emoji_in_text(message, use_count, name, codepoint):
    with db_session:
        mapper.get_or_create_unicode_emoji_in_text(message, use_count, name=name, codepoint=codepoint)

@sync_to_async
def upsert_reaction(emoji, user, message):
    with db_session:
        mapper.get_or_create_reaction(emoji, user, message)
