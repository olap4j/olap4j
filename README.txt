Contents of this distribution
-----------------------------

This is a binary distribution of olap4j.
For version information, see VERSION.txt.
For licensing, see LICENSE.html.

It contains the following external libraries:

lib/asm-2.2.3.jar                Part of retroweaver.
lib/asm-commons-2.2.3.jar        Part of retroweaver.
lib/javacup.jar                  Javacup 0.10k
lib/log4j-1.2.9.jar
lib/mondrian-2.1.1.jar           From mondrian project, change xxxx. (Not an
                                 official release of mondrian.)
lib/pdfdoclet-1.0.2-all.jar      PDF doclet is a utility to generate javadoc
                                 in Adobe Acrobat format.
lib/retroweaver-1.2.4.jar        Retroweaver 1.2.4, compile-time library
lib/retroweaver-rt-1.2.4.jar     Retroweaver 1.2.4, runtime library
lib/spring.jar                   Spring. Used for code samples only
testlib/junit.jar                Junit. Used for test
testlib/servlet.jar              Servlet. Used for code samples only

mondrian.jar


Running the test suite
----------------------

To run the test suite:

1. create a test.properties file describing your environment. The easiest way
   to this is to copy test.properties.example and customize it.
2. execute 'ant test'

JDK 1.4
-------

This distribution does not include JDK 1.4-compatible binaries. To create them,
run 'ant retroweave'.

End README.txt
