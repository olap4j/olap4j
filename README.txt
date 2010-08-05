# $Id$

olap4j, release 0.9.8 (beta)

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
* lib/asm.jar
* lib/asm-commons.jar

If you use the JDK 1.4 compatible jar, you will also need:

* lib/retroweaver.jar
* lib/retroweaver-rt.jar

If you are using the XML/A driver, you will also need:

* lib/xercesImpl.jar

Building olap4j from source
---------------------------

1. Unzip olap4j-<version>-src.zip.
2. Set JAVA_HOME to a JDK version 1.5 home, and put $JAVA_HOME/bin
   (%JAVA_HOME%\bin for Windows) on your path.
3. Edit buildJdk16.bat and buildJdk16.sh and set the JAVA_HOME variable
   to a valid home directory of a Java 6 installation.
4. In the root of the source tree, run 'ant'.

Writing a simple program
------------------------

You can now write and run a simple program against olap4j. For example, under Java 6,

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
