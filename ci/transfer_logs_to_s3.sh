#!/bin/bash

source ./ci/common.sh

S3_BASE_BUCKET=s3://jsdk-travis-ci-build-logs
TSTAMP=`date "+%Y-%m-%d_%H-%M-%S"`
S3_BUCKET="$S3_BUCKET/$TSTAMP-$TRAVIS_JOB_ID"

echo "writing logs to: $S3_BUCKET/tests.log"

aws s3 mb $S3_BUCKET
aws s3 cp "$WORKDIR/target/tests.log" $S3_BUCKET
