#! /bin/bash

echo "Publishing coverage results to S3"
cd clover/target/site/clover
aws s3 sync --quiet  . s3://jsdk-clover-results
