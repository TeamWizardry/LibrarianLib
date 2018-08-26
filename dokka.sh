#!/bin/bash

# Run this instead of `./gradlew dokka`, as it filters a metric ton of of error spam out of the log.

./gradlew --console plain dokka | grep --line-buffered -v "Can't find node" | grep --line-buffered -v "null:-1:-1" | grep --line-buffered -v "gui.provided"  | tee dokka.txt
