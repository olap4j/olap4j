[![Build Status](https://travis-ci.org/olap4j/olap4j.png)](https://travis-ci.org/olap4j/olap4j)

# olap4j #

Olap4j is an open Java API for accessing OLAP data.

It is an extension to JDBC. For example, its
<a href="http://www.olap4j.org/api/org/olap4j/OlapConnection.html">OlapConnection</a>
class extends
<a href="http://docs.oracle.com/javase/7/docs/api/java/sql/Connection.html">java.sql.Connection</a>,
from which you can create an
<a href="http://www.olap4j.org/api/org/olap4j/OlapStatement.html">OlapStatement</a>,
and execute to create a
<a href="http://www.olap4j.org/api/org/olap4j/CellSet.html">CellSet</a>
(analogous to a
<a href="http://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html">java.sql.ResultSet</a>).
There are also similar mechanisms for browsing metadata.
As a result, olap4j is easy to learn if you have JDBC
experience and know a little about OLAP.

## Prerequisites ##

Olap4j requires ant (version 1.7 or later) and JDK 1.7 to build. (Once built, it also runs under JDK 1.5 and 1.6.)

## Download and build ##

```bash
$ git clone git://github.com/olap4j/olap4j.git
$ cd olap4j
$ ant
```

## Writing a simple program ##

You can now write and run a simple program against olap4j. For
example, you can write:

```java
import org.olap4j.*;
import org.olap4j.metadata.Member;
import java.sql.*;

Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
Connection connection =
    DriverManager.getConnection(
        "jdbc:xmla:Server=http://example.com:8080/mondrian/xmla");
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
```

Or, if you are using the in-process mondrian driver, include mondrian.jar
and its dependencies in your classpath, and change the
appropriate lines in the above code to the following:

```java
Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
Connection connection =
    DriverManager.getConnection(
        "jdbc:mondrian:"
        + "Jdbc='jdbc:odbc:MondrianFoodMart';"
        + "Catalog='file://c:/open/mondrian/demo/FoodMart.xml';"
        + "JdbcDrivers=sun.jdbc.odbc.JdbcOdbcDriver;");
```

## Packages and Roadmap ##

The core API of olap4j version 1.0 is a Long Term Support (LTS) release,
but some parts of the olap4j project will remain considered as experimental,
thus subject to change in future releases. 

Core packages are as follows:
* <a href="http://www.olap4j.org/api/org/olap4j/driver/xmla/package-summary.html">org.olap4j.driver.xmla</a> - Generic XML/A driver.
* <a href="http://www.olap4j.org/api/org/olap4j/mdx/package-summary.html">org.olap4j.mdx</a> - Core objects of the MDX model.
* <a href="http://www.olap4j.org/api/org/olap4j/mdx/parser/package-summary.html">org.olap4j.mdx.parser</a> - Parser for the MDX query language.
* <a href="http://www.olap4j.org/api/org/olap4j/metadata/package-summary.html">org.olap4j.metadata</a> - Discovery of an OLAP server's metadata.
* <a href="http://www.olap4j.org/api/org/olap4j/type/package-summary.html">org.olap4j.type</a> - System for the core MDX object model and the metadata package.

The following packages are considered experimental and are subject to change:
* <a href="http://www.olap4j.org/api/org/olap4j/query/package-summary.html">org.olap4j.query</a> - Programmatic Query Model.
* <a href="http://www.olap4j.org/api/org/olap4j/transform/package-summary.html">org.olap4j.transform</a> - Core MDX object model transformation utilities.
* <a href="http://www.olap4j.org/api/org/olap4j/layout/package-summary.html">org.olap4j.layout</a> - Utility classes to display CellSets.
* <a href="http://www.olap4j.org/api/org/olap4j/CellSetListener.html">org.olap4j.CellSetListener</a> and all associated classes - Event-based system for real time updates of CellSet objects.
* <a href="http://www.olap4j.org/api/org/olap4j/Scenario.html">org.olap4j.Scenario</a> and all associated classes - Statistical simulations module.

### Version 2.0

Olap4j version 2.0 is currently under development. Goals are:
* Support metadata and API changes in recent SSAS (Microsoft SQL Server Analysis Services) and XMLA (XML for Analysis)
* Support metadata and API changes in Mondrian version 4

We aim to be backwards compatible in the same way that each JDBC release is backwards compatible:
* Applications developed against earlier versions will work against 2
* Drivers supporting version 2 will also serve as 1.x drivers (JDBC version 4 broke this rule, and it was painful for driver developers)
* Some work will be required to convert a version 1.x driver to a version 2 driver

Version 2 specification is <a href="olap4j_version_2_specification.md">here</a>.

## More information ##

General project information:
* License: <a href="NOTICE">Apache License, Version 2.0</a>.
* Lead developers: <a href="https://github.com/julianhyde">Julian Hyde</a>, <a href="https://github.com/lucboudreau">Luc Boudreau</a>.
* Project page: http://www.olap4j.org
* Specification: <a href="http://www.olap4j.org/olap4j_fs.html">HTML</a>, <a href="http://www.olap4j.org/olap4j_fs.pdf">PDF</a>, <a href="https://github.com/olap4j/olap4j/commits/master/doc/olap4j_fs.html">history</a>.
* Javadoc: http://www.olap4j.org/api
* Source code: http://github.com/julianhyde/olap4j
* Developers list: https://lists.sourceforge.net/lists/listinfo/olap4j-devel
* Forum: http://sourceforge.net/p/olap4j/discussion/577988/

Related projects:
* <a href="https://github.com/pentaho/mondrian">Mondrian</a>
* <a href="https://github.com/olap4j/olap4j-xmlaserver">olap4j-xmlaserver</a>

If you have downloaded a release:
* <a href="README.txt">README.txt</a> describes the release structure.
* <a href="CHANGES.txt">CHANGES.txt</a> describes what has changed in the release.
* The VERSION.txt file holds the version number of the release.
