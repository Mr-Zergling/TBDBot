from peewee import *

db = SqliteDatabase("tbdbot.sqlite3")

class Server(Model):
    id = BigIntegerField(primary_key=True, auto_increment)
    server_name = CharField(max_length=128, index=True)

    class Meta:
        database = db


class User(Model):
    id = BigIntegerField(primary_key=True)
    user_name = CharField(max_length=64)
    discriminator = CharField(max_length=8)


    class Meta:
        database = db
        indexes = (
            (("user_name", "discriminator"), True)
        )

class Nick(Model):
    server = ForeignKeyField(Server, backref="users")
    user = ForeignKeyField(User, backref="nicks")
    name = CharField(max_length=64)
    last_join_date = DateTimeField()


class Channel(Model):
    id = BigIntegerField(primary_key=True)
    server_id = ForeignKeyField(Server, backref="channels")
    channel_name = CharField(max_length=128)

    class Meta:
        database = db


class Message(Model):
    id = BigIntegerField(primary_key=True)
    channel = ForeignKeyField(Channel, backref="messages")
    author = ForeignKeyField(User, backref="messages")

    class Meta:
        database = db
        indexes = (
            (("author", "channel"), False),
        )


class Emoji(Model):
    id = fields.TextField(pk=True)
    server_id = fields.BigIntField(index=True)
    image_hash = fields.CharField(index=True, max_length=1024, null=True)

    class Meta:
        database = db


class Reaction(Model):
    id = fields.BigIntField(pk=True, generated=True)
    message = fields.ForeignKeyField("tbdbot.Message", index=True)
    reacting_user = fields.ForeignKeyField("tbdbot.User", index=True)

    class Meta:
        database = db


class EmojiUse(Model):
    id = fields.BigIntField(pk=True, generated=True)
    emoji = fields.ForeignKeyField("tbdbot.Emoji", index=True)
    message_id = fields.BigIntField(index=True, null=True)
    reaction_id = fields.BigIntField(Index=True, null=True)

    class Meta:
        database = db
