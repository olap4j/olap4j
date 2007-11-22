# $Id: $
# Called recursively from 'ant release' to build the files which can only be
# built under JDK 1.6.

export JAVA_HOME=/usr/local/jdk1.6.0_01
export PATH=$JAVA_HOME/bin:$PATH
ant compile.java

# End buildJdk16.sh

