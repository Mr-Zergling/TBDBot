import discord
from discord.ext import commands
from tbdbot.cogs.emoji import EmojiCog
from tbdbot.cogs.echo import HelloCog
import argparse


class TBDBot(commands.Bot):

    def __init__(self, command_prefix):
        super().__init__(command_prefix)
        self.development_mode = False

    def set_development_mode(self, development_mode):
        self.development_mode = development_mode

    async def reply(self, ctx, content=None, *, tts=False, embed=None, file=None, files=None, delete_after=None,
                    nonce=None, dm_reply=False):
        if dm_reply or self.development_mode:
            user = bot.owner_id if self.development_mode else ctx.author
            channel = await self.get_user_dm_channel(user)
        else:
            channel = ctx.message.channel
        await channel.send(content=content,
                            tts=tts,
                            embed=embed,
                           file=file,
                           files=files,
                           delete_after=delete_after,
                           nonce=nonce)

    async def get_user_dm_channel(self, user):
        target = user
        if isinstance(target, int):
            target = self.get_user(target)
            if not target:
                target = await self.fetch_user(target)
        channel = target.dm_channel
        if not channel:
            channel = await target.create_dm()
        return channel

BOT_PREFIX = "$"
bot = TBDBot(BOT_PREFIX)


@bot.event
async def on_ready():
    print(f'We have logged in as {bot.user}')
    app_info = await bot.application_info()
    bot.owner_id = app_info.owner.id
    print(f"Owner id set to: {bot.owner_id}")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--apikey')
    parser.add_argument('--development', action='store_true')
    args = parser.parse_args()
    bot.add_cog(HelloCog(bot))
    bot.add_cog(EmojiCog(bot))
    bot.set_development_mode(args.development)
    bot.run(args.apikey)
