#!/bin/bash

# TODO: Move top-level docs folder into extensions/servlet-plugin

source ./ci/common.sh

# servlet-plugin docs
echo "-------> Generating servlet plugin docs..."
$(cd docs && make html &> $WORKDIR/target/servlet-plugin-docs.log)
if [ $? -ne 0 ]; then
  error "Error generating servlet plugin docs"
  cat $WORKDIR/target/servlet-plugin-docs.log
  exit $?
fi

# spring boot docs
echo "-------> Generating SpringBoot extension docs..."
$(cd extensions/spring/boot/docs && make html &> $WORKDIR/spring-boot-docs.log)
if [ $? -ne 0 ]; then
  error "Error generating SpringBoot plugin docs"
  cat $WORKDIR/target/spring-boot-docs.log
  exit $?
fi

echo "-------> Generating JavaDocs..."
# javadocs
mvn -q javadoc:aggregate -P travis-docs &> $WORKDIR/target/javadocs.log
if [ $? -ne 0 ]; then
  error "Error generating JavaDocs"
  cat $WORKDIR/target/javadocs.log
  exit $?
fi

