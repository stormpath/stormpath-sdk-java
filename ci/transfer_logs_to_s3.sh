#! /bin/bash

source ./ci/common.sh

source ./env.sh

export PATH=$PATH:~/usr/local/bin

S3_BASE_BUCKET=s3://jsdk-travis-ci-build-logs
TSTAMP=`date "+%Y-%m-%d_%H-%M-%S"`
S3_BUCKET="$S3_BASE_BUCKET/$TSTAMP-$TRAVIS_JOB_ID"

echo "writing logs to: $S3_BUCKET/tests.log"
echo "you can access the log at: http://jsdk-travis-ci-build-logs.s3-website-us-east-1.amazonaws.com/$TSTAMP-$TRAVIS_JOB_ID/tests.log"

aws s3 mb $S3_BUCKET
aws s3 cp "$WORKDIR/target/tests.log" $S3_BUCKET/tests.log
