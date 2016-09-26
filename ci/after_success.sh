#!/bin/bash

if [ -n "$BUILD_DOCS" ]  && [ -n "$IS_RELEASE" ]; then
  ./ci/publish_docs.sh
fi

if [ -n "$RUN_ITS" ]; then
  ./ci/publish_coverage_report.sh
fi
