#!/bin/bash

source ./ci/common.sh

./ci/travis_bootstrap.sh

# if openjdk7 then hackup the hostname to prevent a buffer overflow travis-ci/travis-ci#5227
if [ $TRAVIS_JDK_VERSION == 'openjdk7' ]
then
    echo "\nOriginal /etc/hosts:"
    cat /etc/hosts
    sudo hostname "$(hostname | cut -c1-63)"
    sed -e "s/^\\(127\\.0\\.0\\.1.*\\)/\\1 $(hostname | cut -c1-63)/" /etc/hosts | sudo tee /etc/hosts
    echo "\nNew /etc/hosts:"
    cat /etc/hosts
    echo ""
fi

cp id_rsa_stormpath.github.io "$HOME/.ssh/id_rsa"
chmod 600 "$HOME/.ssh/id_rsa"
mkdir -p "$HOME/.stormpath/clover"
cp clover.license "$HOME/.stormpath/clover"
#Using xmllint is faster than invoking maven
export RELEASE_VERSION="$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)"
export IS_RELEASE="$([ ${RELEASE_VERSION/SNAPSHOT} == $RELEASE_VERSION ] && [ $TRAVIS_BRANCH == 'master' ] && echo 'true')"
export BUILD_DOCS="$([ $TRAVIS_JDK_VERSION == 'oraclejdk8' ] && echo 'true')"
export RUN_ITS="$([ $TRAVIS_JDK_VERSION == 'openjdk7' ] && echo 'true')"
#Install Maven 3.3.9 since Travis uses 3.2 by default
wget http://mirror.cc.columbia.edu/pub/software/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
unzip -qq apache-maven-3.3.9-bin.zip
export M2_HOME=$PWD/apache-maven-3.3.9
export PATH=$M2_HOME/bin:$PATH

info "Build configuration:"
echo "Version:             $RELEASE_VERSION"
echo "Is release:          ${IS_RELEASE:-false}"
echo "Build documentation: ${BUILD_DOCS:-false}"
echo "Running IT tests:    ${RUN_ITS:-false}"
echo "which java:          $(which java)"
echo "mvn --version:"
mvn --version

