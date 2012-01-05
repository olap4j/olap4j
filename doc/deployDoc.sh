#!/bin/sh
# $Id$
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
# This is a script to deploy olap4j's website.
# Only the release manager (jhyde) should run this script.

set -e
set -v

generate=true
if [ "$1" == --nogen ]; then
  shift
  generate=
fi

# Prefix is usually "release" or "head"
prefix="$1"
# Directory at sf.net
docdir=
case "$prefix" in
release) export docdir=olap4j;;
head) export docdir=olap4j.head;;
*) echo "Bad prefix '$prefix'"; exit 1;;
esac

cd $(dirname $0)/..
if [ "$generate" ]; then
  ant doczip
else
  echo Skipping generation...
fi


if false; then

  scp dist/doc.tar.gz jhyde@shell.sf.net:
  GROUP_DIR=/home/groups/o/ol/olap4j

  ssh -T jhyde@shell.sf.net <<EOF
set -e
set -v
rm -f $GROUP_DIR/doc.tar.gz
mv doc.tar.gz $GROUP_DIR
cd $GROUP_DIR
tar xzf doc.tar.gz
rm -rf old
if [ -d $docdir ]; then mv $docdir old; fi
mv doc $docdir
rm -rf old
rm -f doc.tar.gz
./makeLinks
EOF

else

  scp -oPort=7022 dist/doc.tar.gz jhyde@olap4j.org:/home/jhyde
  GROUP_DIR=/home/jhyde/olap4j

  ssh -oPort=7022 -T jhyde@olap4j.org <<EOF
set -e
set -v
cd /home/jhyde
tar xzf doc.tar.gz
rm -rf olap4j.old
if [ -d $docdir ]; then mv $docdir $docdir.old; fi
mv doc $docdir
rm -rf $docdir.old
rm -f doc.tar.gz
case $docdir in
olap4j) (cd olap4j; rm -rf head; ln -s . head) ;;
olap4j.head) (cd olap4j; rm -rf head; mv ../olap4j.head ./head) ;;
esac
./makeLinks $docdir
EOF

fi

# End deployDoc.sh
