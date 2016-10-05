#!/bin/bash

source ./ci/common.sh

info "Generating guide docs..."
git submodule init
git submodule update
(cd docs && make allhtml &> $WORKDIR/target/sphinx.log) &
PID=$!
show_spinner "$PID"

wait $PID ## sets exit code from command
EXIT_CODE=$?

if [ "$EXIT_CODE" -ne 0 ]; then
  error "Error generating guides"
  cat $WORKDIR/target/sphinx.log
  exit $EXIT_CODE
fi

info "Generating JavaDocs..."
(mvn -q javadoc:aggregate -P travis-docs &> $WORKDIR/target/javadocs.log) &
PID=$!

show_spinner "$PID"

wait $PID ## sets exit code from command
EXIT_CODE=$?

if [ "$EXIT_CODE" -ne 0 ]; then
  error "Error generating JavaDocs"
  cat $WORKDIR/target/javadocs.log
  exit $EXIT_CODE
fi

