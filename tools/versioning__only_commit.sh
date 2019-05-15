#!/bin/bash

CURRENT_BRANCH=$(git branch -l | grep "*" | cut -d" " -f2)

git commit -a && git push gitlab $CURRENT_BRANCH
