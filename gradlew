#!/usr/bin/env sh
#
# Gradle start up script for UNIX
#

# Attempt to set APP_HOME
# Resolve links: %PROG% is the new stance, but it may be an absolute or relative path
APP_NAME="Gradle"
# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-XX:MaxMetaspaceSize=64m"'
# Use the maximum code cache size (-XX:MaxCodeCacheSize) and the heap growth limit if specified.
MAX_CODE_CACHE_SIZE="-XX:MaxCodeCacheSize=64m"
# Determine JVM settings
JAVA_HOME="${JAVA_HOME:-$(command -v java)}"
# Check if JAVA_HOME is set
# Try to get the canonical path to the Java executable
JAVA_BIN="$JAVA_HOME/bin/java"
if [ ! -x "$JAVA_BIN" ]; then
    echo "Error: JAVA_HOME is not set properly." >&2
    exit 1
fi
