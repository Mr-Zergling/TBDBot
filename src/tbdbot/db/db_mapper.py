from discord import channel as discord_channel
from tbdbot.db.db_model import *

def get_or_create_message(apimessage):
    content = apimessage.content
    clean_content = apimessage.clean_content
    id = apimessage.id
    message = Message.get(id=id)
    if not message:
        author = get_or_create_user(apimessage.author)
        timestamp = apimessage.created_at
        channel = get_or_create_channel(apimessage.channel)
        message = Message(id=apimessage.id,
                          content=content,
                          author=author,
                          channel=channel,
                          clean_content=clean_content,
                          server=channel.server,
                          timestamp=timestamp)
        flush()
        print(f"db: {message.timestamp} {message.author}: {message.content}")
    else:
        message.content = content
        message.clean_content = clean_content

    return message


def get_or_create_user(apiuser):
    user = User.get(id=apiuser.id)
    name = apiuser.name
    discriminator = apiuser.discriminator
    if not user:
        user = User(id=apiuser.id, name=name, discriminator=discriminator)
        flush()
    user.name = name
    user.discriminator = discriminator
    return user


def get_or_create_server(guild):
    server = Server.get(id=guild.id)
    name = guild.name
    if not server:
        server = Server(id=guild.id, name=name)
        flush()
    else:
        server.name = name
    return server


def get_or_create_channel(apichannel):
    channel = Channel.get(id=apichannel.id)
    if not channel:
        channel = Channel(id=apichannel.id)
        if isinstance(apichannel, discord_channel.DMChannel):
            channel.dm_user = get_or_create_user(apichannel.recipient)
        else:
            channel.server = get_or_create_server(apichannel.guild)
        flush()
    return channel


def get_or_create_custom_emoji_in_text(apimessage, use_count, *, api_emoji=None, id=None, name=None):
    message = get_or_create_message(apimessage)
    id = api_emoji.id if api_emoji else id
    name = api_emoji.name if api_emoji else name
    emoji = get_or_create_custom_emoji(api_emoji=api_emoji, id=id, name=name)
    use = InTextEmojiUse.get(message=message, custom_emoji=emoji)
    if not use:
        use = InTextEmojiUse(message=message, custom_emoji=emoji, count=use_count)
    else:
        use.count = use_count
    return use


def get_or_create_custom_emoji(*, api_emoji=None, id=None, name=None):
    id = api_emoji.id if api_emoji else id
    name = api_emoji.name if api_emoji else name
    emoji = CustomEmoji.get(id=id)
    if not emoji:
        emoji = CustomEmoji(id=id, name=name)
        flush()
    else:
        emoji.name = name
    if hasattr(api_emoji, "guild"):
        if api_emoji.guild:
            emoji.server = get_or_create_server(api_emoji.guild)
    if hasattr(api_emoji, "animated"):
        emoji.animated = api_emoji.animated
    return emoji

def get_or_create_unicode_emoji_in_text(apimessage, use_count, name, codepoint):
    message = get_or_create_message(apimessage)
    emoji = get_or_create_unicode_emoji(name, codepoint)
    use = InTextEmojiUse.get(message=message, unicode_emoji=emoji)
    if not use:
        use = InTextEmojiUse(message=message, unicode_emoji=emoji, count=use_count)
    return use

def get_or_create_unicode_emoji(name, codepoint):
    emoji = UnicodeEmoji.get(name=name)
    if not emoji:
        emoji = UnicodeEmoji(name=name, codepoint=codepoint)
        flush()
    return emoji

def get_or_create_reaction(api_emoji, apiuser, apimessage):
    pass