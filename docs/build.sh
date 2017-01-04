#!/usr/bin/env bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CWD="$(pwd -P)"

if [ -z "$1" ]; then
  targets=( 'servlet' 'spring' 'springboot' 'sczuul' )
else
  targets=( "$1" )
fi

command -v scms >/dev/null 2>&1 || { echo >&2 "The docs/build.sh requires scms to be installed.  Aborting."; exit 1; }
command -v sphinx-build >/dev/null 2>&1 || { echo >&2 "The docs/build.sh requires sphinx to be installed.  Aborting."; exit 1; }

cd "$SCRIPT_DIR"

for target in "${targets[@]}"; do
  echo Working on building: $target
  scms -e "$target" "$SCRIPT_DIR/build/$target"
  cd "$SCRIPT_DIR/build/$target"
  sphinx-build -n -b html -E -d build/doctrees source -t "$target" build/html
  cd "$SCRIPT_DIR"
done

cd "$CWD"
