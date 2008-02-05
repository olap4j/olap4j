# $Id$

olap4j, release 0.9.4 (beta)

Contents of this distribution
-----------------------------

This is a distribution of olap4j, the open Java API for accessing OLAP data.
For version information, see VERSION.txt.
For licensing, see LICENSE.html.

This release of olap4j contains a driver implementation based on the XML for
Analysis (XML/A) protocol. There is also an olap4j driver for mondrian; this
is part of the mondrian distribution from mondrian-3.0 onwards.

olap4j requires the following files at run time:

* lib/olap4j.jar (or lib/olap4j-jdk14.jar if you are running JDK 1.4)
* lib/javacup.jar
* lib/log4j-1.2.9.jar

Building olap4j from source
---------------------------

1. Unzip olap4j-<version>-src.zip.
2. Create a lib directory in the root directory of the source tree, and copy
   all JAR files from olap4j-<version>/lib into it.
3. Create a testlib directory in the root directory of the source tree, and
   copy all JAR files from olap4j-<version>/testlib into it.
4. Set JAVA_HOME to a JDK version 1.5 or 1.6 home, and put $JAVA_HOME/bin
   (%JAVA_HOME%\bin for Windows) on your path.
5. In the root of the source tree, run 'ant'.

Building a jar file
-------------------

The above process is sufficient for running tests; however, due to differences
between the versions of JDBC included with JDK 1.5 and JDK 1.6, it produces a
set of class files which will only run on the JDK under which they were built.

To produce a jar file which will work on both versions, you need to use a more
complicated process. The steps are as follows:

1. Install both JDK 1.5 and JDK 1.6.
2. Set JAVA_HOME to the JDK 1.5 home, and put $JAVA_HOME/bin (or %JAVA_HOME%\bin
   for Windows) on your path.
3. If you are running UNIX or Linux, edit buildJdk16.sh and change the line
   'export JAVA_HOME=...' to point to your JDK 1.6 installation.
4. If you are running Windows, edit buildJdk16.bat and change the line 'set
   JAVA_HOME=...' to point to your JDK 1.6 installation.
5. In the root of the source tree, run 'ant clean' then 'ant jar'.

Running the test suite
----------------------

Follow the instructions for 'Building olap4j from source'. Then:

1. Download the latest mondrian release and use the
   MondrianFoodMartLoader utility to load the sample database into the DBMS of
   your choice.
2. In the root of the source tree, create a file called "test.properties"
   describing your environment. The easiest way to this is to copy
   "test.properties.example" and customize it. Note that you will need to
   specify the location of the FoodMart.xml in your mondrian distribution
   and the URL of your database.
3. If your database requires a JDBC driver, edit build.xml and add the location
   of the driver to the "project.classpath" path.
4. In the root of the source tree, run 'ant test'.

Writing a simple program
------------------------

You can now write and run a simple program against olap4j. For example,

        import org.olap4j.*;
        import org.olap4j.metadata.Member;
        import java.sql.*;

        Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
        Connection connection =
            DriverManager.getConnection(
                "jdbc:xmla:"
                    + "Server=http://deedub:8080/mondrian/xmla;"
                    + "Catalog=FoodMart");
        OlapConnection olapConnection = connection.unwrap(OlapConnection.class);
        OlapStatement statement = olapConnection.createStatement();
        CellSet cellSet =
            statement.executeOlapQuery(
                "SELECT {[Measures].[Unit Sales]} ON 0,\n"
                    + "{[Product].Children} ON 1\n"
                    + "FROM [Sales]");
        for (Position row : cellSet.getAxes().get(1)) {
            for (Position column : cellSet.getAxes().get(0)) {
                for (Member member : row.getMembers()) {
                    System.out.println(member.getUniqueName());
                }
                for (Member member : column.getMembers()) {
                    System.out.println(member.getUniqueName());
                }
                final Cell cell = cellSet.getCell(column, row);
                System.out.println(cell.getFormattedValue());
                System.out.println();
            }
        }


Or if you are using the in-process mondrian driver, include mondrian.jar
and its dependencies in your classpath, and change the
appropriate lines in the above code to the following:

        Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
        Connection connection =
            DriverManager.getConnection(
                "jdbc:mondrian:"
                    + "Jdbc='jdbc:odbc:MondrianFoodMart';"
                    + "Catalog='file://c:/open/mondrian/demo/FoodMart.xml';"
                    + "JdbcDrivers=sun.jdbc.odbc.JdbcOdbcDriver;");

JDK 1.4
-------

This distribution includes a JDK 1.4-compatible library for olap4j.
If you are using JDK version 1.4, replace olap4j.jar in your classpath
with olap4j-jdk14.jar.

# End README.txt
