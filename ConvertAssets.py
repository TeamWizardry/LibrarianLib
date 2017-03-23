#!/usr/bin/python
import os
import sys
import re


def to_snake_case(string):
    # type: (str) -> str
    string = string.replace("-", "_")
    if string.isupper():
        return string.lower()
    elif string.islower():
        return string
    split = list()
    last_index = 0
    last_token_upper = True
    for i in range(len(string)):
        if string[i].isupper():
            if not last_token_upper:
                split.append(string[last_index:i].lower())
                last_index = i
            last_token_upper = True
        else:
            last_token_upper = string[i] == "_"
    split.append(string[last_index:len(string)].lower())
    return "_".join(split)


def break_up_lang(string):
    # type: (str) -> str
    if string.strip().startswith("#"):
        return string
    broken = string.split("=", 1)
    if len(broken) != 2:
        return string
    lang_parts = broken[0].replace(":", ".:").split(".")
    corrected = ""
    for part in lang_parts:
        snake = to_snake_case(part)
        if part.startswith(":"):
            corrected += snake
        else:
            corrected += "." + snake

    return corrected[1:] + "=" + broken[1]


resource_location_pattern = re.compile(r"\w+:[\w/.]+")


def break_up_resource_location(string):
    # type: (str) -> str
    if not resource_location_pattern.match(string):
        return string
    elif string.isupper():
        return string.lower()
    elif string.islower():
        return string

    broken = string.split(":", 1)
    if len(broken) != 2:
        return string
    resource_domain = broken[0]
    resource_path = broken[1].split("/")
    corrected = to_snake_case(resource_domain) + ":"
    corrected += "/".join(map(to_snake_case, resource_path))
    return corrected


def parse_json_line(string):
    # type: (str) -> str
    for i in resource_location_pattern.findall(string):
        string = string.replace(i, break_up_resource_location(i))
    return string


allConverted = list()

homeDir = os.path.expanduser("~")

walk_path = "." if len(sys.argv) < 2 else sys.argv[1]

possible_path = walk_path + os.path.sep + "src" + os.path.sep + "main" + os.path.sep + "resources"

if os.path.exists(possible_path):
    walk_path = possible_path

directories = os.walk(walk_path)

for (path, dirs, files) in directories:
    for f in files:
        changed = False

        path_to_file = os.path.join(path, f)
        if not f.islower():
            path_to_new = os.path.join(path, to_snake_case(f))
            changed = True
        else:
            path_to_new = path_to_file

        loaded = open(path_to_file)
        data = loaded.read()
        loaded.close()
        if not data.endswith("\n") and not f.endswith(".png"):
            data += "\n"
            changed = True

        # noinspection SpellCheckingInspection
        if f.endswith(".lang"):
            lines = data.splitlines(True)
            lines = map(break_up_lang, lines)
            data = "".join(lines)
            changed = True
        elif f.endswith(".json") or f.endswith(".mcmeta"):
            lines = data.splitlines(True)
            lines = map(parse_json_line, lines)
            data = "".join(lines)
            changed = True

        if changed:
            os.remove(path_to_file)
            new = open(path_to_new, "w")
            new.write(data)
            new.close()
            if path_to_file == path_to_new:
                allConverted.append(path_to_file)
            else:
                allConverted.append(path_to_file + " -> " + path_to_new)

for converted in allConverted:
    print "Converted file", converted
