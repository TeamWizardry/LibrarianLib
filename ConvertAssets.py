import os
import sys
import re

matcher = re.compile("[A-Z]{2,}")


def to_snake_case(string):
    """
    :type string: str
    """
    if string.isupper():
        return string.lower()
    elif string.islower():
        return string
    elif matcher.search(string):
        return string.lower()

    split = list()
    last_index = 0
    for i in range(len(string)):
        if string[i].isupper():
            split.append(string[last_index:i].lower())
            last_index = i
    split.append(string[last_index:len(string)].lower())
    return "_".join(split)


allConverted = list()

homeDir = os.path.expanduser("~")

directories = os.walk("." if len(sys.argv) < 2 else sys.argv[1])

for (path, dirs, files) in directories:
    for f in files:
        if not f.islower():
            path_to_file = os.path.join(path, f)
            path_to_new = os.path.join(path, to_snake_case(f))
            allConverted.append((path_to_file + " -> " + path_to_new).replace(homeDir, "~"))

            loaded = open(path_to_file)
            data = loaded.read()
            if not data.endswith("\n"):
                data += "\n"
            loaded.close()
            os.remove(path_to_file)

            new = open(path_to_new, "w")
            new.write(data)
            new.close()

for converted in allConverted:
    print "Converted file", converted
