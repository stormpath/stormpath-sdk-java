#!/bin/bash

source ./ci/common.sh

./ci/travis_bootstrap.sh

cp id_rsa_stormpath.github.io ~/.ssh/id_rsa
chmod 600 ~/.ssh/id_rsa
mkdir -p ~/.stormpath/clover
cp clover.license ~/.stormpath/clover
#Using xmllint is faster than invoking maven
export RELEASE_VERSION="$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)"
export IS_RELEASE="$([ ${RELEASE_VERSION/SNAPSHOT} == $RELEASE_VERSION ] && [ $TRAVIS_BRANCH == 'master' ] && echo 'true')"
export BUILD_DOCS="$([ $TRAVIS_JDK_VERSION == 'oraclejdk8' ] && echo 'true')"
export RUN_ITS="$([ $TRAVIS_JDK_VERSION == 'openjdk7' ] && echo 'true')"

info "Build configuration:"
echo "Version:             $RELEASE_VERSION"
echo "Is release:          ${IS_RELEASE:-false}"
echo "Build documentation: ${BUILD_DOCS:-false}"
echo "Running IT tests:    ${RUN_ITS:-false}"

