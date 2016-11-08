#!/usr/bin/env bash

SCMS_VERSION="0.3.0"

mkdir -p "$HOME/usr/local/scms"
curl -s "http://repo.maven.apache.org/maven2/com/leshazlewood/scms/scms/$SCMS_VERSION/scms-$SCMS_VERSION.zip" -o scms.zip
unzip scms.zip -d "$HOME/usr/local/scms"
ln -s "$HOME/usr/local/scms/scms-$SCMS_VERSION" "$HOME/usr/local/scms/current"
export PATH="$HOME/usr/local/scms/current/bin:$PATH"
