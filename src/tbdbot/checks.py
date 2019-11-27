from discord import channel

def is_bot_channel(ctx):
    return ctx.message.channel.id == 647172167284555779 or \
           ctx.message.channel.id == 646039898381615115 or \
           isinstance(ctx.message.channel, channel.DMChannel)
