#!/bin/bash

# TODO: Move top-level docs folder into extensions/servlet-plugin

source ./ci/common.sh

# servlet-plugin docs
info "Generating servlet plugin docs..."
(cd docs && make html &> $WORKDIR/target/servlet-plugin-docs.log) &
PID=$!
show_spinner "$PID"

wait $PID ## sets exit code from command
EXIT_CODE=$?

if [ "$EXIT_CODE" -ne 0 ]; then
  error "Error generating servlet plugin docs"
  cat $WORKDIR/target/servlet-plugin-docs.log
  exit $EXIT_CODE
fi

# spring boot docs
info "Generating SpringBoot extension docs..."
(cd extensions/spring/boot/docs && make html &> $WORKDIR/spring-boot-docs.log) &
PID=$!
show_spinner "$PID"

wait $PID ## sets exit code from command
EXIT_CODE=$?

if [ "$EXIT_CODE" -ne 0 ]; then
  error "Error generating SpringBoot plugin docs"
  cat $WORKDIR/target/spring-boot-docs.log
  exit $EXIT_CODE
fi

info "Generating JavaDocs..."
# javadocs
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

