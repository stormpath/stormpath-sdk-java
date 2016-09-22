#!/bin/bash

source ./ci/common.sh

if [ -n "$RUN_ITS" ]; then
  info "Running unit and IT tests..."
  (mvn -Pclover.all -DskipITs=false -q install &> $WORKDIR/target/tests.log) &
  PID=$!
  show_spinner "$PID"

  wait $PID ## sets exit code from command

  EXIT_CODE=$?
  if [ "$EXIT_CODE" -ne 0 ]; then
    error "Tests failed"
    ./ci/junit-errors-to-stdout.sh
    exit $EXIT_CODE
  fi
fi

if [ -z "$RUN_ITS" ]; then
  info "Running unit tests..."
  (mvn -q install &> $WORKDIR/target/tests.log) &
  PID=$!
  show_spinner "$PID"

  wait $PID ## sets exit code from command

  EXIT_CODE=$?
  if [ "$EXIT_CODE" -ne 0 ]; then
    error "Tests failed"
    ./ci/junit-errors-to-stdout.sh
    exit $EXIT_CODE
  fi
fi

if [ -n "$BUILD_DOCS" ]; then
  ./ci/build_docs.sh
  EXIT_CODE=$?
  if [ "$EXIT_CODE"-ne 0 ]; then
    # Only exit since build_docs.sh would handle its own error messages
    exit $EXIT_CODE
  fi
fi

