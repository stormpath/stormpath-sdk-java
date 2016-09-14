#!/bin/bash

source ./ci/common.sh

if [ -n "$BUILD_DOCS" ]; then
  echo "-------> Installing Sphinx..."
  pip -q install --user sphinx &> $WORKDIR/target/pip.log
  if [ $? -ne 0 ]; then
    error "-------> Error installing Sphinx"
    cat $WORKDIR/target/pip.log
    exit $?
  fi
fi

if [ -n "RUN_ITS" ]; then
  echo "-------> Installing AWS CLI..."
  ./ci/install_aws_cli.sh $> $WORKDIR/target/aws-cli.log
  if [ $? -ne 0 ]; then
    error "-------> Error installing AWS CLI"
    cat $WORKDIR/target/aws-cli.log
    exit $?
  fi
fi

