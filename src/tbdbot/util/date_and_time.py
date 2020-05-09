import dateparser


def parse_time_period(string_period):
    return dateparser.parse(string_period, settings={'PREFER_DATES_FROM': 'future', 'TO_TIMEZONE': 'UTC'})
