#!/bin/bash

source ./ci/common.sh

echo 'Formatting results...'
FILES=$(find "$WORKDIR" -path '*/target/*-reports/junitreports/*.xml' | xargs --no-run-if-empty grep -L "failures=\"0\"")
FILES="$FILES $(find "$WORKDIR" -path '*/target/*-reports/junitreports/*.xml' | xargs --no-run-if-empty grep -L "errors=\"0\"")"
if [ -n "$FILES" ]; then
  for file in $FILES; do
    if [ -f "$file" ]; then
      echo -e "\n\n====================================================="
      xsltproc "./ci/junit-xml-format-errors.xsl" "$file" | grep --color -E "at com\.stormpath.*$|$"
    fi
  done
  echo -e "\n=====================================================\n\n"
fi
