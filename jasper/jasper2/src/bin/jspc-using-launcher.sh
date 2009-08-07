#!/bin/sh

# -----------------------------------------------------------------------------
#
# Script for running JSPC compiler using the Launcher
#
# -----------------------------------------------------------------------------

# Resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`
if [ -r "$PRGDIR"/setenv.sh ]; then
  . "$PRGDIR"/setenv.sh
fi

# Execute the Launcher using the "jspc" target
exec "$JAVA_HOME"/bin/java -classpath "$PRGDIR" LauncherBootstrap -launchfile jasper.xml -verbose jspc "$@"
