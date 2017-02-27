#!/usr/bin/env bash
# Select nodes, info in ~info/machines
# jupiter
messaging_nodes="earth jupiter mars mercury neptune saturn uranus venus raleigh boston columbia olympia topeka cooper loveland keystone annapolis albany pierre monarch cairo damascus dhaka hanoi hong-kong riyadh pyongyang berlin bentley"


# Login and kick up all messaging nodes
for host in $messaging_nodes; do
  tmux splitw "ssh cdedward@${host}.cs.colostate.edu 'cs455hw2client honolulu.cs.colostate.edu 8001 10'"
  tmux select-layout tiled
done

# Makes all the terminals share the same input
tmux set-window-option synchronize-panes on

# Otherwise the last pane will still be local
ssh cdedward@honolulu.cs.colostate.edu
