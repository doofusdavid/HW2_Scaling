#!/usr/bin/env bash
# Select nodes, info in ~info/machines
# jupiter
messaging_nodes="earth jupiter mars mercury neptune saturn uranus venus raleigh"


# Login and kick up all messaging nodes
for host in $messaging_nodes; do
  tmux splitw "ssh cdedward@${host}.cs.colostate.edu 'killall -u cdedward java'"
  tmux select-layout even-vertical
done

# Makes all the terminals share the same input
tmux set-window-option synchronize-panes on

# Otherwise the last pane will still be local
ssh cdedward@honolulu.cs.colostate.edu
