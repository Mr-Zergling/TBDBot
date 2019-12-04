from tortoise.models import Model
from tortoise import fields


class Server(Model):
    id = fields.BigIntField(pk=True, generated=False)
    server_name = fields.TextField()


class User(Model):
    id = fields.BigIntField(pk=True, generated=False)
    user_name = fields.TextField()


class Channel(Model):
    id = fields.BigIntField(pk=True, generated=False)
    server_id = fields.BigIntField(index=True)
    channel_name = fields.TextField()

    class Meta:
        indexes = (("server_id", "id"), )


class Message(Model):
    id = fields.BigIntField(pk=True, generated=False)
    channel = fields.ForeignKeyField("tbdbot.Channel")
    author = fields.ForeignKeyField("tbdbot.User")


class Emoji(Model):
    id = fields.TextField(pk=True)
    server_id = fields.BigIntField(index=True)
    image_hash = fields.CharField(index=True, max_length=1024, null=True)

    class Meta:
        index = (("server_id", "id"), ("server_id", "image_hash"), )


class Reaction(Model):
    id = fields.BigIntField(pk=True, generated=True)
    message = fields.ForeignKeyField("tbdbot.Message", index=True)
    reacting_user = fields.ForeignKeyField("tbdbot.User", index=True)


class EmojiUse(Model):
    id = fields.BigIntField(pk=True, generated=True)
    emoji = fields.ForeignKeyField("tbdbot.Emoji", index=True)
    message_id = fields.BigIntField(index=True, null=True)
    reaction_id = fields.BigIntField(Index=True, null=True)

    class Meta:
        index = (("emoji_id", "message_id"),("emoji_id", "reaction_id"), )