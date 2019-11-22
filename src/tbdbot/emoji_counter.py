from emoji import demojize, emoji_lis
import re
import pandas as pd
import io
import discord


async def backfill_emoji_count(message):
    emoji_count = {}
    def add_count(date, channel, name, count=1):
        emoji_count[(date, channel, name)] = emoji_count.get((date, channel, name), 0) + count
    if isinstance(message.channel, discord.DMChannel):
        await process_channel(message.channel, add_count)
    else:
        await process_guild(message.channel.guild, add_count)
        
    df = pd.Series(emoji_count).rename_axis(['date', 'channel', 'emoji_name']).reset_index(name='count')
    df = df.sort_values(["date", "channel", "emoji_name"], ascending=[False, False, True])
    date_and_channel = io.StringIO()
    df.to_csv(date_and_channel, index = False)
    date_and_channel.seek(0)

    df = df.groupby(["emoji_name"]).sum()
    df = df.sort_values("count", ascending=False)
    overall = io.StringIO()
    df.to_csv(overall, index = True)
    overall.seek(0)
    await message.channel.send("Here are your counts by date and channel!", file=discord.File(date_and_channel,"emojis_by_date_and_channel.csv"))
    await message.channel.send("And your overall counts", file=discord.File(overall,"overall.csv"))

async def process_guild(guild, add_count_fn):
    for channel in guild.channels:
        try:
            await process_channel(channel, add_count_fn)
        except:
            print(f"exception processing channel {str(channel)}")



async def process_channel(channel, add_count_fn):
    if isinstance(channel, discord.TextChannel) or isinstance(channel, discord.DMChannel):
        async for historical_message in channel.history(limit=20000):
            await process_message(historical_message, add_count_fn)

async def process_message(message, add_count_fn):
    message_date = date_to_string(message.created_at)
    channel = str(message.channel)
    custom_emojis_in_message = re.findall(r'<:\w*:\d*>', message.content)
    custom_emojis_in_message = [e.split(':')[1].replace('>', '') for e in custom_emojis_in_message]
    for custom_emoji in custom_emojis_in_message:
        add_count_fn(message_date, channel, custom_emoji)
    for standard_emoji in emoji_lis(message.content):
        add_count_fn(message_date, channel, demojize(standard_emoji["emoji"],delimiters=("","")))
    for react in message.reactions:
        e = react.emoji
        if(not isinstance(e, str)):
            e = e.name
        else:
            e = demojize(e,delimiters=("",""))
        add_count_fn(message_date, channel, e, react.count)

def date_to_string(date):
    return date.strftime("%Y-%m-%d")