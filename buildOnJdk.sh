#
# $Id: //open/mondrian/buildOnJdk.sh#2 $
#
# Licensed to Julian Hyde under one or more contributor license
# agreements. See the NOTICE file distributed with this work for
# additional information regarding copyright ownership.
#
# Julian Hyde licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at:
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ==============================================================================
# Called recursively from 'ant release' to build the files which can only be
# built under a particular JDK version.
#
# Usage:
#   buildOnJdk.sh <jdk version> <ant args>
#
# For example:
#   buildOnJdk.sh jdk1.6 compile.java

jdkVersion=$1
shift

# Change the following line to point each JDK's home.
case "$jdkVersion" in
(*) export JAVA_HOME=/usr/lib/jvm/${jdkVersion};;
esac

if [ ! -d "$JAVA_HOME" ]; then
    echo "$0: Invalid JAVA_HOME $JAVA_HOME; skipping compile."
    exit 1
fi

export PATH=$JAVA_HOME/bin:$PATH

echo Using JAVA_HOME: $JAVA_HOME
echo Using Ant arguments: $@

ant "$@"

# End buildOnJdk.sh
