#!/bin/sh
set -eu
java -cp /opt/iemes ContainerSupport configure
exec "$@"
