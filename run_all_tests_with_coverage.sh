#! /bin/bash

## Internal Stormpath: Refer to https://stormpath.atlassian.net/wiki/display/SDKS/Testing+Environments
## for the environment variable settings below

[ -z "$STORMPATH_BASE_URL" ] && echo "Need to set STORMPATH_BASE_URL" && exit 1
[ -z "$STORMPATH_TEST_APPLICATION_HREF" ] && echo "Need to set STORMPATH_TEST_APPLICATION_HREF" && exit 1
[ -z "$STORMPATH_API_KEY_ID" ] && echo "Need to set STORMPATH_API_KEY_ID" && exit 1
[ -z "$STORMPATH_API_KEY_SECRET" ] && echo "Need to set STORMPATH_API_KEY_SECRET" && exit 1
[ -z "$STORMPATH_API_KEY_ID_TWO_APP" ] && echo "Need to set STORMPATH_API_KEY_ID_TWO_APP" && exit 1
[ -z "$STORMPATH_API_KEY_SECRET_TWO_APP" ] && echo "Need to set STORMPATH_API_KEY_SECRET_TWO_APP" && exit 1

mvn --fail-never -DskipITs=false -Dreport-phase=post-integration-test clean verify && echo "All tests passed. Hooray!" || echo "Some tests failed. Boo."