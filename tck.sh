#! /bin/bash
#
# Copyright 2016 Stormpath, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
OPTION=${1:-NO_OPTION}
OPTION_ARGUMENT01=${2:-NOT_SET}
OPTION_ARGUMENT02=${3:-NOT_SET}

AVAILABLE_PROFILES="java, laravel, express"

if [ "$OPTION" = "NO_OPTION" ] ; then
    echo "usage: tck [<option>]"
    echo "Option"
    echo "    clone <dir>            clones stormpath-framework-tck locally under directory <dir>."
    echo "                           If no <dir>, then current dir."
    echo "    run <profile> <dir>    runs actual TCK tests using profile <profile>. Valid profiles are $AVAILABLE_PROFILES."
    echo "                           TCK code will be sought under <dir>. If no directory is specified then current dir will be used."
    echo ""
    exit
fi

case "$OPTION" in
    clone)
        DIR=${OPTION_ARGUMENT01}
        if [ "${DIR}" = "NOT_SET" ] ; then DIR="stormpath-framework-tck"; fi
        echo "Checking out TCK"
        git clone git@github.com:stormpath/stormpath-framework-tck.git ${DIR}
        cd ${DIR}
        git fetch
        git checkout master
        echo "TCK cloned"
        ;;
    run)
        SCRIPT_DIR=$(cd "$(dirname "$0")"; pwd)
        if [ -e "$SCRIPT_DIR/ci/stormpath_env.sh" ]; then
          source ${SCRIPT_DIR}/ci/stormpath_env.sh
          export STORMPATH_APPLICATION_HREF=$STORMPATH_TEST_APPLICATION_HREF
        fi
        PROFILE=${OPTION_ARGUMENT01}
        DIR=${OPTION_ARGUMENT02}
        #Let's read the name of the directory where tck was cloned
        if [ "$PROFILE" = "NOT_SET" ] ; then echo "Missing profile. Valid profiles are $AVAILABLE_PROFILES"; exit; fi
        if [ "${DIR}" = "NOT_SET" ] ; then DIR="stormpath-framework-tck"; fi
        echo "Setting TCK properties"
        echo "Using profile: ${PROFILE}"
        cd ${DIR}
        echo "Running TCK now!"
        mvn -Prun-ITs -P$PROFILE clean verify
        EXIT_STATUS="$?"
        if [ "$EXIT_STATUS" -ne 0 ]; then
            echo "TCK found errors! :^(. Exit status was $EXIT_STATUS"
            exit $EXIT_STATUS
        fi
        echo "TCK ran successfully, no errors found!"
        ;;
esac
