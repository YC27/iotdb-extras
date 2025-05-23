#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

source "$(dirname "$0")/common.sh"
COLLECTOR_CONF="`dirname "$0"`/../conf"

if [ -f "${COLLECTOR_CONF}/application.properties" ]; then
    api_service_port=`sed '/^api_service_port=/!d;s/.*=//' ${COLLECTOR_CONF}/application.properties | tr -d '\r'`
else
    api_service_port=`sed '/^api_service_port=/!d;s/.*=//' ${COLLECTOR_CONF}/application.properties | tr -d '\r'`
fi

if [ -z "$api_service_port" ]; then
    echo "WARNING: api_service_port not found in the configuration file. Using default value api_service_port=17070"
    api_service_port=17070
fi

check_config_unique "api_service_port" "$api_service_port"

force=""

while true; do
    case "$1" in
        -f)
            force="yes"
            break
        ;;
        "")
            #if we do not use getopt, we then have to process the case that there is no argument.
            #in some systems, when there is no argument, shift command may throw error, so we skip directly
            #all others are args to the program
            PARAMS=$*
            break
        ;;
    esac
done

echo "Check whether the api_service_port is used..., port is" $api_service_port

if  type lsof > /dev/null 2>&1 ; then
  PID=$(lsof -t -i:"${api_service_port}" -sTCP:LISTEN)
elif type netstat > /dev/null 2>&1 ; then
  PID=$(netstat -anp 2>/dev/null | grep ":${api_service_port} " | grep ' LISTEN ' | awk '{print $NF}' | sed "s|/.*||g" )
else
  echo ""
  echo " Error: No necessary tool."
  echo " Please install 'lsof' or 'netstat'."
  exit 1
fi

PID_VERIFY=$(ps ax | grep -i 'Application' | grep java | grep -v grep | awk '{print $1}')
if [ -z "$PID" ]; then
  echo "No Application to stop"
  if [ "$(id -u)" -ne 0 ]; then
    echo "Maybe you can try to run in sudo mode to detect the process."
  fi
  exit 1
elif [[ "${PID_VERIFY}" =~ ${PID} ]]; then
  if [[ "${force}" == "yes" ]]; then
    kill -9 "$PID"
    echo "Force to stop Application, PID:" "$PID"
  else
    kill -s TERM "$PID"
    echo "Stop Application, PID:" "$PID"
  fi
else
  echo "No Application to stop"
  exit 1
fi