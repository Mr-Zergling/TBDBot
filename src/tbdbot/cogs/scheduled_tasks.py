import asyncio
from datetime import datetime, timezone

from discord.ext import commands

class ScheduledTask:
    def __init__(self, trigger_time, action, args):
        self.trigger_time = trigger_time
        self.action = action
        self.args = args


scheduled_tasks = []


def add_task(trigger_time, action, args):
    scheduled_tasks.append(ScheduledTask(trigger_time, action, args))


class ExecutorCog(commands.Cog):
    def __init__(self, bot):
        self.bot = bot
        self.version = "0.1"
        self.loop = asyncio.get_event_loop()
        self.task = self.loop.create_task(self.periodic_task_check())

    async def periodic_task_check(self):
        while True:
            tasks_to_execute = []
            for task in scheduled_tasks:
                if task.trigger_time < datetime.utcnow():
                    tasks_to_execute.append(task)
            for task in tasks_to_execute:
                await task.action(**task.args)
                scheduled_tasks.remove(task)
            await asyncio.sleep(15)

    def cog_unload(self):
        self.task.cancel()


def setup(bot):
    bot.add_cog(ExecutorCog(bot))


def teardown(bot):
    bot.remove_cog(ExecutorCog(bot))
