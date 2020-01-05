from datetime import datetime
from pony.orm import *


db = Database()


class Message(db.Entity):
    id = PrimaryKey(int, size=64)
    reactions = Set('Reaction')
    content = Optional(LongStr)
    clean_content = Optional(LongStr)
    author = Required('User')
    timestamp = Required(datetime, precision=0, index=True)
    channel = Required('Channel')
    server = Optional('Server')
    in_text_emoji_uses = Set('InTextEmojiUse')


class Server(db.Entity):
    id = PrimaryKey(int, size=64)
    name = Required(str)
    messages = Set(Message)
    channels = Set('Channel')
    settings = Required(Json, default="{}")
    custom_emojis = Set('CustomEmoji')


class Channel(db.Entity):
    id = PrimaryKey(int, size=64)
    messages = Set(Message)
    server = Optional(Server)
    dm_user = Optional('User')


class CustomEmoji(db.Entity):
    id = PrimaryKey(int, size=64)
    name = Required(str, index=True)
    reactions = Set('Reaction')
    in_text_emoji_uses = Set('InTextEmojiUse')
    server = Optional(Server)
    animated = Required(bool, default=False)
    dhash = Optional(int, size=64)


class Reaction(db.Entity):
    id = PrimaryKey(int, size=64, auto=True)
    message = Required(Message)
    custom_emoji = Optional(CustomEmoji)
    unicode_emoji = Optional('UnicodeEmoji')
    user = Required('User')


class InTextEmojiUse(db.Entity):
    id = PrimaryKey(int, size=64, auto=True)
    message = Required(Message)
    custom_emoji = Optional(CustomEmoji)
    unicode_emoji = Optional('UnicodeEmoji')
    count = Required(int)


class User(db.Entity):
    id = PrimaryKey(int, size=64)
    messages = Set(Message)
    dm_channel = Optional(Channel)
    reactions = Set(Reaction)
    name = Required(str)
    discriminator = Required(str)


class UnicodeEmoji(db.Entity):
    name = PrimaryKey(str)
    codepoint = Optional(str, index=True)
    reactions = Set(Reaction)
    in_text_emoji_uses = Set(InTextEmojiUse)


db.bind(provider='sqlite', filename='database.sqlite3', create_db=True)
db.generate_mapping(create_tables=True)
