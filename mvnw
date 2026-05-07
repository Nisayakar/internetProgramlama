#!/bin/sh

if [ -n "$MVNW_VERBOSE" ]; then
  set -x
fi

BASEDIR=$(dirname "$0")
if [ -z "$BASEDIR" ]; then
  BASEDIR=.
fi

if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi

echo "Error: Maven executable 'mvn' was not found on PATH." >&2
echo "Install Maven or restore the full Maven Wrapper files to use ./mvnw." >&2
exit 1
