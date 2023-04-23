#!/bin/bash

# ----------------------- DOCUMENTATION -----------------------
# game modes: -game [story, training, speedrun, battle, versus]
#      stage: -stage {story=[original, beginner, veteran], training=[original-X, beginner-X, veteran-X], speedrun=[1], battle=[1], versus=[1]}
# difficulty: -difficulty [beginner, normal, hard, lionhard]
#    players: -player {story=[1], training=[1], speedrun=[1, 2, 3, 4], battle=[1, 2, 3, 4], versus=[2, 3, 4]}
# -------------------------------------------------------------

# ------------------------- EXAMPLES --------------------------
#  default: set ARGS=
#    story: set ARGS=-game story -stage beginner -difficulty beginner
# training: set ARGS=-game training -stage veteran-6 -difficulty lionhard
# speedrun: set ARGS=-game speedrun -stage 1 -players 2
#   battle: set ARGS=-game battle -stage 1 -players 3
#   versus: set ARGS=-game versus -stage 1 -players 4
# -------------------------------------------------------------



export ARGS="-game story -stage original"



# ----------------------- DO NOT CHANGE -----------------------
export VERSION=%%INPUT_APPV%%
export PARAM="-server -splash:splash.png -jar lionheart-pc-"$VERSION".jar "

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_path"

cd data
jre_linux-x86_64/bin/java $PARAM $ARGS
# -------------------------------------------------------------
