from emoji import demojize, emoji_lis
import re

def get_unicode_emoji_count_from_string(content):
    count_dict = {}
    for standard_emoji in emoji_lis(content):
        codepoint = standard_emoji["emoji"]
        name = codepoint_to_name(codepoint)
        count_dict[(name, codepoint)] = count_dict.get((name, codepoint), 0) + 1
    return count_dict

def get_custom_emoji_count_from_string(content):
    count_dict = {}
    custom_emojis_in_message = re.findall(r'<:\w*:\d*>', content)
    for custom_emoji in custom_emojis_in_message:
        split = custom_emoji.replace('>', '').replace('<',"").split(':')
        id = split[2]
        name = split[1]
        count_dict[(name, id)] = count_dict.get((name, id), 0) + 1
    return count_dict

def codepoint_to_name(codepoint):
    return demojize(codepoint, delimiters=("", ""))
