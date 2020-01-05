from asgiref.sync import sync_to_async
from tbdbot.db.db_model import db_session
from tbdbot.db.db_mapper import *


@sync_to_async
def upsert_message(message):
    with db_session:
        get_or_create_message(message)

