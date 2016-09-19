#! /bin/bash

source ./ci/common.sh

export PATH=$PATH:~/usr/local/bin

info "Publishing coverage results to S3"
aws s3 sync --quiet clover/target/site/clover s3://jsdk-clover-results
