#!/bin/sh
# $Id: //open/mondrian/doc/deployDoc.sh#3 $
# This software is subject to the terms of the Common Public License
# Agreement, available at the following URL:
# http://www.opensource.org/licenses/cpl.html.
# Copyright (C) 2005-2005 Julian Hyde
# All Rights Reserved.
# You must accept the terms of that agreement to use this software.
#
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
0.5) export docdir=htdocs;;
head) export docdir=head;;
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

  scp -oPort=8022 dist/doc.tar.gz jhyde@olap4j.org:/home/jhyde
  GROUP_DIR=/home/jhyde/olap4j

  ssh -p 8022 -T jhyde@olap4j.org <<EOF
set -e
set -v
cd /home/jhyde
tar xzf doc.tar.gz
rm -rf olap4j.old
if [ -d olap4j ]; then mv olap4j olap4j.old; fi
mv doc olap4j
rm -rf olap4j.old
rm -f doc.tar.gz
./makeLinks
EOF

fi

# End deployDoc.sh
