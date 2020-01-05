from discord import channel as discord_channel
from tbdbot.db.db_model import *


def get_or_create_message(apimessage):
    content = apimessage.content
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
                          server=channel.server,
                          timestamp=timestamp)
        flush()
        print(f"{message.timestamp} {message.author}: {message.content}")
    else:
        message.content = content
    return message

def get_or_create_user(apiuser):
    user = User.get(id=apiuser.id)
    if not user:
        user = User(id=apiuser.id)
        flush()
    return user

def get_or_create_server(guild):
    server = Server.get(id=guild.id)
    if not server:
        server = Server(id=guild.id)
        flush()
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
