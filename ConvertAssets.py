#!/usr/bin/python
"""
Run this in a minecraft mod project root to convert all its assets to snake case. This changes three things at the moment:
- Keys in lang files will be collapsed to snake case.
- Resource locations in any .json or .mcmeta file will be snake-cased.
- All file names not beginning with . (for mac) will be snake-cased.
If you run it on a folder not containing src/main/resources, and your current folder's path doesn't include src/main/resources, it'll warn you.
"""
import os
import sys
import re

try:
    agnostic_input = raw_input
except NameError:
    agnostic_input = input


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
    split.append(string[last_index:].lower())
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


src_main_resources = "src" + os.path.sep + "main" + os.path.sep + "resources"

if __name__ == "__main__":
    allConverted = list()

    homeDir = os.path.expanduser("~")

    walk_path = "." if len(sys.argv) < 2 else sys.argv[1]

    should_continue = False

    expanded_path = os.path.normpath(walk_path)
    if src_main_resources in expanded_path:
        should_continue = True

    if not should_continue:
        possible_path = walk_path + src_main_resources

        if os.path.exists(possible_path):
            walk_path = possible_path
            should_continue = True

    if not should_continue:
        forge_forward = agnostic_input("This path (" + expanded_path + ") doesn't contain " + src_main_resources + ".\n"
                                       + "Are you sure you want to continue? Anything other than a blank will be "
                                       + "interpreted as \"go ahead\". ")
        if not forge_forward.isspace():
            exit()

    directories = os.walk(walk_path)

    for (path, dirs, files) in directories:
        for f in files:
            if f.startswith("."):
                continue

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
        print("Converted file " + converted)
