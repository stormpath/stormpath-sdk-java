#!/bin/bash

source ./ci/common.sh

if [ -n "$BUILD_DOCS" ]; then
  info "Installing Sphinx..."
  pip -q install --user sphinx &> $WORKDIR/target/pip.log
  EXIT_CODE=$?
  if [ "$EXIT_CODE" -ne 0 ]; then
    error "Error installing Sphinx"
    cat $WORKDIR/target/pip.log
    exit $EXIT_CODE
  fi
fi

if [ -n "RUN_ITS" ]; then
  info "Installing AWS CLI..."
  ./ci/install_aws_cli.sh $> $WORKDIR/target/aws-cli.log
  EXIT_CODE=$?
  if [ "$EXIT_CODE" -ne 0 ]; then
    error "Error installing AWS CLI"
    cat $WORKDIR/target/aws-cli.log
    exit $EXIT_CODE
  fi
fi

