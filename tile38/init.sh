#!/bin/sh

echo "initializing tile38..."
echo "with a kafka webhook"

/usr/local/bin/tile38-cli -h tile38 SETHOOK trains_at_stations kafka://host.docker.internal:9092/I_HAVE_ARRIVED NEARBY trains FENCE ROAM stations '*' 50

echo "finished setup"
