#! /bin/bash
#
# Copyright 2016 Stormpath, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
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

KEY_ID=${STORMPATH_API_KEY_ID?"STORMPATH_API_KEY_ID needs to be set for this script to work"}
KEY_SECRET=${STORMPATH_API_KEY_SECRET?"STORMPATH_API_KEY_SECRET needs to be set for this script to work"}

echo 'connecting with' $KEY_ID

LOCATION_HEADER=$(curl --request GET -v --silent --user $KEY_ID:$KEY_SECRET --url "https://api.stormpath.com/v1/tenants/current" --stderr - | grep Location)
TENANT_URL=${LOCATION_HEADER#\< Location: }

# remove \r at end of line
TENANT_URL=${TENANT_URL%$'\r'}

curl --request GET --silent --user $KEY_ID:$KEY_SECRET --url $TENANT_URL | python -mjson.tool

#TENANT_INFO=$(curl --request GET --user $KEY_ID:$KEY_SECRET --url $TENANT_URL | python -mjson.tool)

#echo $TENANT_INFO