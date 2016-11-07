#! /bin/bash

source ./ci/common.sh

source ./env.sh

export PATH="$HOME/usr/local/bin:$PATH"

info "Publishing coverage results to S3"
aws s3 sync --quiet clover/target/site/clover s3://jsdk-clover-results
