#!/bin/sh
set -eu
java -cp '/opt/iemes:/usr/local/tomcat/webapps/ROOT/WEB-INF/lib/*' ContainerSupport configure
exec "$@"
