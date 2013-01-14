olap4j, release 1.1.0

Contents of this distribution
-----------------------------

This is a distribution of olap4j, the open Java API for accessing OLAP data.
For version information, see VERSION.txt. For licensing, see LICENSE/NOTICE.
For details regarding this release and the changes it introduces, see
CHANGES.txt.

Olap4j is distributed as four Java JAR libraries.

    * olap4j-[version].jar
        Core API, Query Model, Transformation and other auxiliary packages,
        along with the driver specification.

    * olap4j-xmla-[version].jar
        Driver implementation of olap4j for XML/A data sources. It is
        compatible with Mondrian, Palo, SAP BW and SQL Server 2005+.

    * olap4j-tck-[version].jar
        Technology compatibility kit. Consists mostly of JUnit tests.

    * olap4j-jdk14-[version].jar
        Olap4j distribution compatible with Java 1.4. Includes the core
        API and the XML/A driver.

Packages and Roadmap
-----------------------------

The core API of olap4j version 1.0 is a Long Term Support (LTS) release,
but some parts of the olap4j project will remain considered as experimental,
thus subject to change in future releases. 

Core packages
-----------------------------

The core packages in olap4j-1.0 are as follows.

    * org.olap4j.driver.xmla
        Generic XML/A driver.

    * org.olap4j.mdx
        Core objects of the MDX model.

    * org.olap4j.mdx.parser
        Parser for the MDX query language.

    * org.olap4j.metadata
        Discovery of OLAP servers metadata.

    * org.olap4j.type
        System for the core MDX object model and the metadata package.

Experimental packages
-----------------------------

The following packages are considered experimental and are subject to change.

    * org.olap4j.query
        Programmatic Query Model.

    * org.olap4j.transform
        Core MDX object model transformation utilities.

    * org.olap4j.layout
        Utility classes to display CellSets.

    * org.olap4j.CellSetListener and all associated classes
        Event-based system for real time updates of CellSet objects.

    * org.olap4j.Scenario and all associated classes
        Statistical simulations module.

Dependencies
-----------------------------

olap4j requires the following libraries at run time:

* lib/olap4j.jar (or lib/olap4j-jdk14.jar if you are running JDK 1.4)

If you use the JDK 1.4 compatible jar, you will also need:

* lib/retroweaver.jar
* lib/retroweaver-rt.jar
* lib/asm.jar
* lib/asm-commons.jar

If you are using the XML/A driver, you will also need:

* lib/xercesImpl.jar

The TCK requires:

* lib/commons-dbcp.jar

Building olap4j from source
---------------------------

1. Unzip olap4j-<version>-src.zip.

2. Make sure that you are running JDK 1.7, the JAVA_HOME variable is
   set, and $JAVA_HOME/bin is on your path.

3. Run a test build: $ ant clean-all dist

Writing a simple program
------------------------

You can now write and run a simple program against olap4j. For example, under
Java 1.6 or later,

        import org.olap4j.*;
        import org.olap4j.metadata.Member;
        import java.sql.*;

        Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
        Connection connection =
            DriverManager.getConnection(
                "jdbc:xmla:"
                    + "Server=http://example.com:8080/mondrian/xmla");
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

Or, if you are using the in-process mondrian driver, include mondrian.jar
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
