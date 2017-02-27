#!/usr/bin/env bash
# Select nodes, info in ~info/machines
# jupiter
messaging_nodes="earth jupiter mars mercury neptune saturn uranus venus raleigh boston columbia olympia topeka cooper loveland keystone annapolis albany"


# Login and kill all clients
for host in $messaging_nodes; do
  ssh cdedward@${host}.cs.colostate.edu 'killall -u cdedward java'
done

