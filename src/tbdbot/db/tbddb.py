from tortoise import Tortoise
from tbdbot.db.models import Server, Message

db_ready = False

async def _init():
    await Tortoise.init(
        db_url='sqlite://tbddb.sqlite3',
        modules={'tbdbot': ['tbdbot.db.models']}
    )
    # Generate the schema
    await Tortoise.generate_schemas()


async def add_message(message):
    await _init()
