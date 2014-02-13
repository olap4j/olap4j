# Olap4j changes

###############################################################################

## Olap4j 1.2.0

This is a maintenance release of the olap4j specification. The changes are
constrained to the XMLA driver and maintains backward compatibility with
previous releases.

* Issues fixed in core olap4j
    - Small clarification on the javadocs of Level.getMembers regarding
      the presence of calculated members.

* Issues addressed in XMLA
    - Fixes for Member.getParentMember when dealing with calculated members.
    - Added support for GZIP compression in the XmlaOlap4jHttpProxy.
    - Fixed issue where an xsd:decimal element was materialized as
      java.lang.Integer.
    - Added support for xsd:byte, xsd:biginteger and xsd:bigdecimal

###############################################################################

## Olap4j 1.1.0

This release is a minor update to the olap4j specification. There have been
changes to the internal APIs which will affect developers and implementors
of the specification. This release is backwards compatible with applications
written against olap4j 1.0.

This release is also the first to introduce our new sister project:
olap4j-xmlaserver. It consists of a Java Servlet implementation which provides
XMLA services from an olap4j connection. For more details, consult our GitHub
page at github.com/olap4j

* New Features / Improvements
    - Switched to the JavaCC parser (was previously JavaCUP).
    - Added compatibility with Mondrian 4+.
    - Minor changes to the NamedList interface.

* Bug Fixes in Core
    - Fix bug 3526908. Incomplete LCID list.
    - Fix bug 3515404. Inconsistent behavior when parsing ".CHILDREN" and
     ".Children"

* Bug Fixes in XMLA
    - Fixes compatibility issues with Essbase.
    - Fixes compatibility issues with SAP BW.
    - Fixes a thread safety issue in DeferredNamedList.

###############################################################################

## Olap4j 1.0.1

This release is a maintenance release and maintains backwards compatibility
with version 1.0.0. The license under which olap4j is distributed has changed
starting with this release. Olap4j is now distributed in accordance to the
Apache License Version 2.0.

Among other notable changes, this version introduces support for JDBC 4.1.

* New Features / Improvements
    - Added support for Java 7 / JDBC 4.1.
    - XMLA now supports locales.
    - TCK now tests the roles and access controls.

* XMLA Bug Fixes
    - Fixed bug 848534. Virtual cubes are now returned as part of
      Schema.getCubes().
    - Fixed issues with the  experimental Query Model.
    - Fixes an issue with SAP BW where the XMLA driver would scan
      through all the available databases/catalogs/schemas, causing
      access control exceptions.
    - Added the ability to pass server specific properties as part
      of the XMLA's PropertyList payload.
    - The XmlaDriver now exposes a public constructor to prevent
      errors in Sun's JDBC implementation.

###############################################################################

## Olap4j 1.0.0

Although this file was not kept up to date since a while, we intend to change
this starting with version 1.0.0. :)

Olap4j 1.0.0 brings changes to the top of the metadata hierarchy,
changes in the driver specification, along with the removal of
all APIs which were marked for deprecation prior to 1.0.0.

* Metadata changes
    Version 1.0.0 introduces a new object at the top of the OlapDatabaseMetaData,
    namely the Database object. The database was an implicit member; we
    have decided to turn it into a first class object of the hierarchy. Catalogs
    have been modified to reflect this new parent. OlapConnections now have
    methods to explore the Databases, Catalogs and Schemas on a server.

* Driver specification changes
    OlapConnections must now be bound unto a database, a catalog and a schema
    before being able to resolve cubes and execute queries. Reference
    implementations have been modified to reflect the correct behavior, as was
    the TCK. New methods were added to OlapConnection in order to change the
    connection binding and obtain lists of available Databases, Catalogs
    and Schemas.

Olap4j is now split into five Java libraries.

* olap4j.jar
    The core API

* olap4j-xmla.jar
    The XML/A driver for olap4j.

* olap4j-tck.jar
    TCK classes.

* olap4j-jdk14.jar
    Java 1.4 compatible release. Contains the core API and XML/A driver.

* olap4j-sources.jar
    Java source files.

===============================================================================

## Changes to olap4j since olap4j-0.9.3.

Summary of changes between olap4j-0.9.3 and olap4j-0.9.4-svn072:
* olap4j now has an XMLA driver. Tested against mondrian's XMLA driver,
  it should also work against other XMLA drivers such as Microsoft
  Analysis Services.

* olap4j's mondrian driver is now part of the mondrian release.
  Look for it in mondrian-3.0 onwards.

* More tests, numerous bug-fixes.

API changes:
* Arguments to AxisNode constructor re-ordered.

* Hierarchy.getDefaultMEmber() now throws OlapException.

* Cube.getMeasures() returns members sorted by ordinal.

* Added constructor for IdentifierNode which takes a list
  (convenient for calling with the result from
  IdentifierNode.parseIdentifier).

## Commit history

[d01460d](../../commit/d01460d9408a6a8f7267d54172e5e276bbaf1924)
Mon, 10 Feb 2014 11:04:44 -0500 - __(Luc Boudreau)__
Fix for feature request #23. Adds support for GZIP compression in the HTTP proxy.

[e33d7d8](../../commit/e33d7d8e8f1d4f010a9cdf32f530f458d4f145d2)
Mon, 10 Feb 2014 11:03:56 -0500 - __(Luc Boudreau)__
Renames CHANGES.txt to HISTORY.md to make this file more Gitty.

[d92fcab](../../commit/d92fcab88ae4ffb88875bc27864b027c0be2a6d6)
Mon, 16 Dec 2013 14:03:14 -0800 - __(Julian Hyde)__
Enable oraclejdk8 in Travis CI.

[43e2371](../../commit/43e23719fa0f9999793ec20d587d3273e405f183)
Mon, 2 Dec 2013 12:10:39 -0500 - __(mkambol)__
[MONDRIAN-1770]  Disabling ConnectionTest.testParentChild, which fails due to MONDRIAN-1796, an issue with closure tables introduced with the QueryBuilder changes. Also adjusted the content of XmlaOlap4jCellSetTest.testTypedValues, which formerly referred to the [Employee] dimension and hit the same error.

[fc132e0](../../commit/fc132e0d4462be764bc7654e4892c998a50d54ea)
Thu, 8 Aug 2013 15:16:44 -0700 - __(Julian Hyde)__
Update travis link in README.

[5a301fe](../../commit/5a301feb5a3b07c7fb76fca75532285e72224f9e)
Thu, 8 Aug 2013 13:53:41 -0700 - __(Julian Hyde)__
Enable travis-ci.

[b49f30f](../../commit/b49f30f708ee26e007c8417b489a5c3d54d84545)
Tue, 30 Jul 2013 18:10:57 +0200 - __(pstoellberger)__
fix javadoc issue and improve comments

[3551c85](../../commit/3551c8531f75647c0866472daaa34b656466ddf4)
Wed, 26 Jun 2013 10:36:11 -0400 - __(mkambol)__
Corrected expected value for ConnectionTest.testVirtualCubeCmBug().  The test was expecting two measures to be present in [Warehouse and Sales] that are not present in the virtual cube's definition.

[35d9ba4](../../commit/35d9ba46d5939df0cc459f59436d6b6b05a5dcd7)
Wed, 26 Jun 2013 10:17:49 -0400 - __(mkambol)__
[MONDRIAN-1581] Adding support for .getParentMember() to XmlaOlap4jSurpriseMember.

[1728c11](../../commit/1728c118a9c7173308a586e64529dc05c1d47aa6)
Tue, 18 Jun 2013 17:03:46 -0400 - __(mkambol)__
[MONDRIAN-1581]  XmlaOlap4jCellSet was not handling certain datatypes correctly (e.g. xsd:decimal).

[f77ffa0](../../commit/f77ffa05a1bdb0d7407817bb98a2d36a3afaa027)
Mon, 3 Jun 2013 11:46:31 -0600 - __(Julian Hyde)__
Add goal to remove XmlaExtra.

[be152f0](../../commit/be152f0a5f3da6214a5a6fa8292c4ba38be6c75a)
Sun, 2 Jun 2013 16:33:25 -0600 - __(Julian Hyde)__
Add Julian's wishlist to 2.0 spec

[7487bb8](../../commit/7487bb8814b9d98828d485897afb4525a7b598fb)
Sun, 2 Jun 2013 15:13:36 -0700 - __(Julian Hyde)__
Start specification for olap4j version 2.0.

[af8c7b0](../../commit/af8c7b01adf7f847fb2dd09d209bc4e33d9bb046)
Sun, 2 Jun 2013 16:04:35 -0600 - __(Julian Hyde)__
Add details of olap4j version 2.0 to README.

[d242d19](../../commit/d242d19a7b0033911add4a06e67f98632c9a01be)
Wed, 1 May 2013 11:01:21 -0700 - __(Julian Hyde)__
Axis ordinal in error message is now 0-based; was 1-based.

[11012a6](../../commit/11012a6e92806ef0ea66be0cb557b040ce697823)
Mon, 4 Mar 2013 10:53:38 -0500 - __(Luc Boudreau)__
Small change in the javadoc of Level.getMembers

[59a4c89](../../commit/59a4c8965f97b43f1a94b3743039499839dd5c06)
Fri, 1 Mar 2013 20:50:29 +0000 - __(Matt Campbell)__
MONDRIAN-1438.  Disabled testDatabaseMetaDataGetMembers, which was failing due to member ordinal values varying.  MEMBER_ORDINAL is deprecated and will always return 0 once MONDRIAN-1378 is picked up, at which point this test can be re-enabled.

[5f1afbb](../../commit/5f1afbb1158a59530ed7b0e6abb4a15f67e36579)
Fri, 1 Mar 2013 15:42:04 -0500 - __(Luc Boudreau)__
Adds calculated members to Level.getMembers() along with a new test to verify and updated documentation.

[e3a4f3d](../../commit/e3a4f3da954defbbfa770b2c78c65105c99ce6aa)
Tue, 26 Feb 2013 17:00:14 -0500 - __(Matt Campbell)__
Correction to expected values in MetadataTest.testDatabaseMetaDataGetSets.  Formerly looking for the [Top Sellers] set to be in [Warehouse and Sales], which is not consistent with the Foodmart3.mondrian.xml schema.

[d533c9e](../../commit/d533c9e82ffdc0624c05e76e47fad934c8e419a9)
Fri, 1 Feb 2013 14:51:42 -0800 - __(Julian Hyde)__
Remove dependency on MySQL JDBC driver.

[453eb11](../../commit/453eb11924409a7c47dd62ce32f4ae6ced37f7ec)
Thu, 24 Jan 2013 10:37:18 -0500 - __(Luc Boudreau)__
Update build.properties

[e4150f9](../../commit/e4150f9c92e73fde6433515b617563747c7e5133)
Thu, 24 Jan 2013 10:37:01 -0500 - __(Luc Boudreau)__
Update build.properties

[7e31ed0](../../commit/7e31ed001f565caba65c3ee73e5754ed8d6622eb)
Thu, 24 Jan 2013 13:34:07 +0100 - __(pstoellberger)__
add Filter() for QueryAxis

[d07acc9](../../commit/d07acc9ad380717b4190dc813dd4ba80a4bada56)
Tue, 22 Jan 2013 15:05:16 +0100 - __(pstoellberger)__
implement limit function on axis (topcount, bottompercent, ...)

[1df1436](../../commit/1df1436d58fa7eebdc55fde099f058b4397f54f9)
Fri, 18 Jan 2013 10:40:14 -0500 - __(Luc Boudreau)__
Update build.properties

[5acc206](../../commit/5acc20601f24cbbbd08d0e65fb12efbd6c00d9e2)
Fri, 18 Jan 2013 10:36:08 -0500 - __(Luc Boudreau)__
Slight modification to the build script. The JAR file contained two nls.properties files.

[7b74403](../../commit/7b744033d34ee9cb772858382029d7516ef65f4e)
Wed, 16 Jan 2013 11:42:15 -0500 - __(Luc Boudreau)__
Removes some libraries from the binary distro. Updates the Eclipse classpath.

[3e0ec12](../../commit/3e0ec1271612d2be58d85958bc7fcb53ef38b0b5)
Wed, 16 Jan 2013 11:16:31 -0500 - __(Luc Boudreau)__
Update build.properties

[9fdcf8b](../../commit/9fdcf8bc4f684022e12b951ced434240b3002a0c)
Wed, 16 Jan 2013 11:13:47 -0500 - __(Luc Boudreau)__
Adds more information to the Version.txt file, namely a git commit ID and dates. Also sets the version to 1.1.0 for a numbered build.

[10a62e1](../../commit/10a62e1ebe7d6ef8a25107c59003fb6e4f856855)
Mon, 14 Jan 2013 16:07:51 -0500 - __(Luc Boudreau)__
Some work to get ready for the 1.1.0 release. Also fixes a problem with the ant script. The file nls.properties was not part of the classpath when running the tests form Ant.

[57c24f6](../../commit/57c24f6e267ae8758375998ae847f135432b028e)
Mon, 17 Dec 2012 11:57:22 -0500 - __(Luc Boudreau)__
Fixes a woopsie.

[0c6e986](../../commit/0c6e9862e80b2ddca461aa6f9e7ada780adff200)
Mon, 17 Dec 2012 11:52:30 -0500 - __(Luc Boudreau)__
Removes the Java RTs from the runtime dependencies.

[dd8aba4](../../commit/dd8aba4fc91a18bcf19b91b30c4dda555d5b3234)
Mon, 17 Dec 2012 11:37:18 -0500 - __(Luc Boudreau)__
Adds Profit last Period to Warehouse and Sales.

[8c36dc5](../../commit/8c36dc518699e70503a2bc942b390d215afa5d50)
Mon, 17 Dec 2012 10:51:08 -0500 - __(Luc Boudreau)__
Some more fixes for Mondrian 4.0 TCK.

[3eb8b41](../../commit/3eb8b4124123fc7cc7fde700967ccf5feba3ca07)
Fri, 7 Dec 2012 11:39:10 -0500 - __(Luc Boudreau)__
Small modification to the XMLA driver so that the format string of measures is available as a property.

[f1bd652](../../commit/f1bd652948db3fb64212098fa6ffffbc2ee21e5e)
Sun, 2 Dec 2012 20:44:10 -0800 - __(Julian Hyde)__
Web site tweaks.

[810401d](../../commit/810401dc00c67e920cd5f2f0e5b0c0f9a5769e23)
Sat, 1 Dec 2012 22:57:18 -0800 - __(Julian Hyde)__
Update web site, README and spec for new github location.

[01ed70f](../../commit/01ed70fc79a4626c823594c0b05a1b0a0f124c71)
Thu, 29 Nov 2012 11:55:45 -0800 - __(Julian Hyde)__
Initial commit

[33c415e](../../commit/33c415ea856d34502d37f0b2b86e55ec0f382506)
Thu, 29 Nov 2012 11:49:35 -0800 - __(Julian Hyde)__
Improve javadoc for XMLA constants.

[31cdbb5](../../commit/31cdbb5621d882370ea939f388a95ec191b372b5)
Thu, 29 Nov 2012 11:46:26 -0800 - __(Julian Hyde)__
Remove '$Id' tags; they are useless under git.

[b6801fd](../../commit/b6801fd6f458e8122e1ad144ed16a689c9938f36)
Thu, 29 Nov 2012 11:43:58 -0800 - __(Julian Hyde)__
Fix bug: Incorrect parsing of Identifier - "[Customers].[City].&[San Francisco]&[CA]&[USA].&[cust1234]". (Contributed by Antony.)

[4a56111](../../commit/4a561119a4e4e83f55f9d5b496ddf8b47d11efe4)
Wed, 28 Nov 2012 21:16:13 +0000 - __(Julian Hyde)__
Obsolete buildOnJdk.sh and buildOnJdk.bat. Ant now builds for multiple JDK versions using javac's bootclasspath option.

[f8ac181](../../commit/f8ac181c5b6b00727cef8cc5da28ab71493b5b57)
Tue, 27 Nov 2012 21:26:27 +0000 - __(Julian Hyde)__
Oops.

[39bd69b](../../commit/39bd69b226fc5922113fbe46f98aa1a138206222)
Tue, 27 Nov 2012 20:42:57 +0000 - __(Julian Hyde)__
Add Spacer, efficient concatenation of strings of spaces. (Previous technique, using String.substring to create strings with a shared backing array, does not work in JDK 1.7.0_u7 or later due to fix of Java bug 6924259, "Remove String.count/String.offset/String.hashcode", http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6924259.)

[432c4d1](../../commit/432c4d1f7450bfa518df61500af9a28efa016ba9)
Tue, 16 Oct 2012 20:00:43 +0000 - __(Julian Hyde)__
Tidy up LcidLocale.

[b77b2f1](../../commit/b77b2f18938a99c146287c3f39400ec184a57f3a)
Tue, 16 Oct 2012 18:42:18 +0000 - __(Julian Hyde)__
Fix bug olap4j-76, "Still missing codes from LCID-to-Locale mapping".

[7bc91ff](../../commit/7bc91ff4ad225cb0e66f1422a529f4343695c1a3)
Wed, 3 Oct 2012 00:59:15 +0000 - __(Julian Hyde)__
DRILLTHROUGH tests now close ResultSet

[c5f9dfc](../../commit/c5f9dfc3cfe0acc0b6b622110381db6e83149b51)
Fri, 28 Sep 2012 10:38:24 +0000 - __(Luc Boudreau)__
Makes DeferredNamedList thread safe to prevent concurrent modifications exceptions.

[5913e32](../../commit/5913e32cc43f1b398c3be3c0323045e2e19d0274)
Mon, 20 Aug 2012 17:30:53 +0000 - __(Luc Boudreau)__
Fixes a few minor memory leaks in the TCK.

[88108fb](../../commit/88108fbed8b00a2055f579a714aa211fd0da332e)
Fri, 10 Aug 2012 20:53:06 +0000 - __(Luc Boudreau)__
Adds a test for 1205. MONDRIAN-1205. It is fixed in Mondrian 4.0 but not in 3.4 so this test is expected to fail until we fix it in 3.4 as well.

[849b745](../../commit/849b74549c3c4977e53405b95306776ce0b2a27c)
Fri, 10 Aug 2012 16:12:09 +0000 - __(Luc Boudreau)__
Updates to latest Mondrian 4.0 drillthrough code.

[138579e](../../commit/138579ee0299e2a2b809232eab4c80afb43bdaab)
Wed, 8 Aug 2012 19:32:30 +0000 - __(Luc Boudreau)__
Fixes some failures introduced by the change to Mondrian 4.0.

[24cd3e6](../../commit/24cd3e6e81f38d15fc746f14175d952e4f287287)
Thu, 2 Aug 2012 17:39:40 +0000 - __(Luc Boudreau)__
Fixes some changes in naming conventions introduced by Mondrian 4.0.

[cceb717](../../commit/cceb717ff4a8fdfeaaabc664681774bd20389824)
Thu, 2 Aug 2012 15:19:25 +0000 - __(Luc Boudreau)__
Makes the TCK run against MySQL by default. Seems like all other DBs mess up the row ordering and make tests fail randomly.

[d5ff50d](../../commit/d5ff50dec8f16ff5952255b239adbd0f006fc4bb)
Wed, 1 Aug 2012 17:37:25 +0000 - __(Luc Boudreau)__
First round of fixes for Mondrian 4 compatibility.

[43d0836](../../commit/43d083649041dc65e878d143a4d2d7f230fe80bc)
Wed, 1 Aug 2012 16:25:05 +0000 - __(Luc Boudreau)__
Migrates olap4j to Mondrian 4.0. This will cause a bunch of failures, but we will address them in the coming days.

[ea07d97](../../commit/ea07d97cf73913cff40555a620a3e3ae60b2624b)
Wed, 1 Aug 2012 16:07:05 +0000 - __(Luc Boudreau)__
Setting back to SNAPSHOT mode.

[10eede4](../../commit/10eede4c630c8f6183c1a53aa7294e30e7e50d23)
Wed, 1 Aug 2012 16:00:57 +0000 - __(Luc Boudreau)__
Triggers a numbered build for pre-Mondrian 4 work.

[6469044](../../commit/646904474629d4d93e731e18cfa45d2c97419a7a)
Tue, 31 Jul 2012 14:04:13 +0000 - __(Luc Boudreau)__
Reverts a previous fix to this test. Will fix FoodMart instead.

[2464ceb](../../commit/2464cebf38fee4694e4cbfa158c52dd915289069)
Wed, 25 Jul 2012 16:58:01 +0000 - __(Luc Boudreau)__
Turns out the dataset in HSQLDB is different from my MySQL version. Fixing the difference in this commit so that it passes on the default dataset.

[6c6a10c](../../commit/6c6a10c327a5019b3cae10ac32b28fc86dc96049)
Wed, 25 Jul 2012 16:46:50 +0000 - __(Luc Boudreau)__
An attempt to fix a few tests which fail on some machines because of OS dependent ordering.

[07107aa](../../commit/07107aad85e5c5752c720f52dcab2cac2fb8c906)
Mon, 21 May 2012 18:49:27 +0000 - __(Julian Hyde)__
Code formatting: disallow multi-line C-style comments /* ... */.

[54664d0](../../commit/54664d0b907bdb02486d3f5ad2a8d3e01e0ec686)
Tue, 15 May 2012 22:43:10 +0000 - __(Julian Hyde)__
Fix bug 3526908, "Incomplete LCID list".

[a41091b](../../commit/a41091bc930b4ad658084f0e0e97044e29213ec5)
Tue, 15 May 2012 20:47:28 +0000 - __(Julian Hyde)__
Enhancements to checkFile; fix some formatting.

[1152987](../../commit/1152987ed3964e7b83306e1acecd0ce13b4c6977)
Wed, 25 Apr 2012 16:49:07 +0000 - __(Luc Boudreau)__
Fixes a memory leak while running the TCK. The connection reference was not getting cleared if the connection was closed.

[7771362](../../commit/7771362b000392e9ec1fe1875fa02ee17e3d5f3b)
Fri, 20 Apr 2012 14:46:42 +0000 - __(Luc Boudreau)__
Makes a few connection tests not run until we actually implement drillthrough over XMLA.

[6c836aa](../../commit/6c836aa7911badc33a1bd32ebd7be32775693bd1)
Mon, 16 Apr 2012 20:47:59 +0000 - __(Luc Boudreau)__
Cancel that. We must keep getName(Object).

[f7768c3](../../commit/f7768c32ad57eccabb6c2f49117f6b651aace19a)
Mon, 16 Apr 2012 20:17:41 +0000 - __(Luc Boudreau)__
Woops.

[610d34d](../../commit/610d34d32ebbe77e2eeaa25ea13cc578b1880316)
Mon, 16 Apr 2012 20:04:03 +0000 - __(Luc Boudreau)__
NamedList.getName must use generic type.

[8d0ca7b](../../commit/8d0ca7bef282358aadfd1986b5f22763435eca07)
Fri, 6 Apr 2012 22:24:30 +0000 - __(Julian Hyde)__
Fix bug 3515404, "Inconsistent parsing behavior('.CHILDREN' and '.Children')".

[0646d5f](../../commit/0646d5f0f3964aa4fca7c52d6347c6a828253054)
Mon, 2 Apr 2012 14:33:33 +0000 - __(Luc Boudreau)__
Fixes compatibility issues with Essbase. Essbase expects the DataSourceInfo element to be a tad different than other backends.

[08f2550](../../commit/08f255015f0025c67f277a32a8a22ab5215ec12d)
Wed, 21 Mar 2012 05:55:38 +0000 - __(Julian Hyde)__
XmlaOlap4jCellSet now treats a missing filter axis the same as a filter axis with one empty tuple. Both signify a missing WHERE clause; the former is expected by Excel 2007 and is now the behavior of Mondrian.

[5838d7c](../../commit/5838d7cbd0fc172940ff1273d1c888475b1e9702)
Mon, 12 Mar 2012 20:52:37 +0000 - __(Paul Stoellberger)__
fix hierarchyConsistent test (forgot to upgrade to new syntax)

[60b247d](../../commit/60b247d8d88b63d7fd223a5c654fb99865f6922f)
Thu, 8 Mar 2012 17:04:53 +0000 - __(Luc Boudreau)__
Fix for SAP BW. Some SAP servers seem to omit the SOAP header from their responses. Adds a check for null.

[57abe2c](../../commit/57abe2cac73187010d2fce0825ed003adb200def)
Wed, 7 Mar 2012 19:24:42 +0000 - __(Julian Hyde)__
Update buildOnJdk.sh similar to those on mondrian. No longer need to edit the script in a build environment, provided that you set environment variables JAVA_HOME_xx.

[e97625c](../../commit/e97625cb30ec8e9e0e8097bf2c4d5ce60aaabc7e)
Mon, 5 Mar 2012 18:03:49 +0000 - __(Luc Boudreau)__
Normalizes the drillthrough results for numeric values so that they produce a deterministic number format.

[5e6aa68](../../commit/5e6aa6855043567738048906ef8cea920068922d)
Mon, 5 Mar 2012 16:15:31 +0000 - __(Luc Boudreau)__
Fixes the trivial call on connect by populating lazily the list of database properties, only when it is needed.

[41885cc](../../commit/41885cc1d4c37fc786d6c5548747954a4001dc6f)
Wed, 29 Feb 2012 06:45:38 +0000 - __(Julian Hyde)__
Allow the target target, 'all', to run under any JDK. The 'dist' target, used to create an official release, requires JDK 1.5. We assume that anyone creating a release is following RELEASE_CHECKLIST.txt closely.

[f521228](../../commit/f521228de75f21738f326257c247e46060eba005)
Wed, 29 Feb 2012 06:37:31 +0000 - __(Julian Hyde)__
Modify the multi-JDK build mechanism so that, by default, olap4j builds on just the current JDK. Files intended for other JDK versions don't get built. That's what most developers want, because they're not building a release, they're building for themselves or their organization, and their organization uses just one JDK version.

[f0a1a2e](../../commit/f0a1a2ee0a43645762ef8f73d425c049073cfc5e)
Mon, 27 Feb 2012 21:43:58 +0000 - __(Paul Stoellberger)__
in query model use Exists(<set1>,<set2>).Count > 0 instead of mondrian-specific <set1> IN <set2>

[9f1c518](../../commit/9f1c5188dcb30534d6a79ef56c6acd47fab2671c)
Mon, 27 Feb 2012 21:38:16 +0000 - __(Paul Stoellberger)__
switch the TCK to mondrian 3.4.0-SNAPSHOT, new drillthrough stuff depends on it

[2845d0e](../../commit/2845d0ed78153e4c9a5d32c2afe6370e0fb77527)
Mon, 27 Feb 2012 08:48:32 +0000 - __(Julian Hyde)__
Clean wasn't.

[e765ea1](../../commit/e765ea1ff601d69968991f9117c2d09084ee40e8)
Mon, 27 Feb 2012 07:54:36 +0000 - __(Julian Hyde)__
Remove files relating to JavaCUP.

[ff570b4](../../commit/ff570b4e1316e2fe462404ff0529c3ec02a02dfd)
Mon, 27 Feb 2012 06:46:24 +0000 - __(Julian Hyde)__
Switch to JavaCC-based parser (was JavaCUP). Add DrillThroughNode. Plus a couple of cosmetic changes.

[cc74e15](../../commit/cc74e15912f53ca5516af91a7fcfffa6694be5ee)
Mon, 27 Feb 2012 06:28:51 +0000 - __(Julian Hyde)__
Rename NamedList.elementName(Object) to .getName(Object); plays better with existing code that implement AbstractNamedList.getName(Object).

[71d9fdf](../../commit/71d9fdf5ef766cf3a285c892982c3be2568cde12)
Fri, 24 Feb 2012 04:23:24 +0000 - __(Luc Boudreau)__
Shouldn't have done that.

[5e11a1f](../../commit/5e11a1f6166507c984a41b37b3c34ec6b814bb51)
Fri, 24 Feb 2012 01:15:54 +0000 - __(Julian Hyde)__
Add methods .elementName and .asMap() to NamedList.

[75f5545](../../commit/75f55457eccadde092e7a3f10c6479a7ef6edde2)
Fri, 24 Feb 2012 01:08:10 +0000 - __(Luc Boudreau)__
A bit of code to add the session ID into the server infos when parsing the SOAP headers.

[803e11a](../../commit/803e11a2409433759ae5f96a496f16b05ec00149)
Fri, 24 Feb 2012 01:07:37 +0000 - __(Luc Boudreau)__
Refactored the ConnectionTest for DRILLTHROUGH RETURN. Still not implemented in XMLA though.

[5437000](../../commit/543700079096c961f873ef63da2f3414dde64742)
Tue, 21 Feb 2012 17:42:57 +0000 - __(Luc Boudreau)__
Adds tests for DRILLTHROUGH with RETURN.

[1e15147](../../commit/1e151472aa0dd21eceef552e059a0eee5f6b171a)
Thu, 16 Feb 2012 23:17:37 +0000 - __(Paul Stoellberger)__
cleanup xmla driver, verify additional connection properties against DISCOVER_PROPERTIES

[047ad63](../../commit/047ad63f326fb3c35b488b6e40cb3c0a673f5031)
Wed, 8 Feb 2012 07:12:36 +0000 - __(Julian Hyde)__
Tweak website for olap4j-1.0.1 release. Obsolete deployDoc.sh.

[9d55f29](../../commit/9d55f2960997ce585df09fea0b152f082f75a30e)
Tue, 7 Feb 2012 21:35:50 +0000 - __(Luc Boudreau)__
An (even) better README.

[8aaba4a](../../commit/8aaba4a1d2c2d45ca38b147f2f13ace22d56bce2)
Tue, 7 Feb 2012 21:32:28 +0000 - __(Luc Boudreau)__
A better README.

[c448dcb](../../commit/c448dcb3913a5a9b64eb22aa1e74b2927913b29d)
Mon, 6 Feb 2012 20:30:42 +0000 - __(Luc Boudreau)__
Setting the build back to SNAPSHOT mode.

[c879232](../../commit/c8792323d40b4e6f1d38cf8f126642db0fedea1a)
Mon, 6 Feb 2012 20:13:55 +0000 - __(Luc Boudreau)__
Fixes code formatting issues. Sets the build number to 1.0.1.500 for a tentative release.

[af107f8](../../commit/af107f839e8853ccd62147ebfc16b230cf60761c)
Mon, 6 Feb 2012 16:06:29 +0000 - __(Luc Boudreau)__
Fixes an issue with cache pass-through properties. The properties that are used to configure the XMLA cache shouldn't be added to the PropertyList of the XMLA payload.

[d914472](../../commit/d914472345c579f1eeea05df79ef89d7775177af)
Mon, 30 Jan 2012 16:30:42 +0000 - __(Luc Boudreau)__
Different way to fix the memory leaks. When running the TCK in an embeded mode, the test context must be dereferenced or the configuration doesn't pass through.

[cbc6665](../../commit/cbc6665abf59a77184078b356612f70aaf1be61d)
Mon, 30 Jan 2012 16:14:48 +0000 - __(Luc Boudreau)__
Fixes memory leaks in the test suite.

[4180611](../../commit/41806112ad2a55d2d4fdf64d59c88916ebbf7b73)
Mon, 30 Jan 2012 13:33:27 +0000 - __(Paul Stoellberger)__
xmla driver: add connection properties and roles to statement call as well

[0c40d41](../../commit/0c40d41c30e4b2b42e27945081867d8b579511f5)
Tue, 24 Jan 2012 20:05:49 +0000 - __(Luc Boudreau)__
Setting the build back to SNAPSHOT mode.

[e01bc4d](../../commit/e01bc4d695f92f3e633fd41b19a46ee4d20e3101)
Tue, 24 Jan 2012 19:07:19 +0000 - __(Luc Boudreau)__
Fixes some missing files from the source distro.

[b622e30](../../commit/b622e30d0237a7e0ff828594584e0dfd2d72034b)
Tue, 24 Jan 2012 18:51:30 +0000 - __(Luc Boudreau)__
Fixes some last minute test failures. Also increments the build number for a second release attempt.

[9035400](../../commit/90354002b5d239e9c4a7a5691907b0c289de1ced)
Tue, 24 Jan 2012 17:32:44 +0000 - __(Luc Boudreau)__
Setting the build back to SNAPSHOT mode.

[59450d6](../../commit/59450d6032b9d13cfad8845f038d75aa8605ecd1)
Tue, 24 Jan 2012 17:02:14 +0000 - __(Luc Boudreau)__
Updates the documentation for the 1.0.1 release. Sets the build number to 1.0.1.491 for a deployment.

[c6c0808](../../commit/c6c080842257dd533fcc71e0b029ae4c44f9f66c)
Mon, 23 Jan 2012 22:35:25 +0000 - __(Luc Boudreau)__
Adds a test for the changes checked in previously by Paul. I've also refactored some string literals into enumeration members and I've documented the new XMLA driver properties that it introduces.

[bbe5864](../../commit/bbe5864314319555ac0d5bbebf9df8bd2e482258)
Mon, 23 Jan 2012 20:03:16 +0000 - __(Paul Stoellberger)__
cleanup properties support, make properties injection safe

[c75477a](../../commit/c75477aab6744fda6a9e056d2ef7a469e0ea2f85)
Wed, 18 Jan 2012 21:28:50 +0000 - __(Paul Stoellberger)__
some improvements to the last commit

[33206a9](../../commit/33206a955f3e9b9f4b4538b46e568a37b9defe9d)
Wed, 18 Jan 2012 20:42:45 +0000 - __(Paul Stoellberger)__
role support and use of additional xmla properties as part of the connection string in the XMLA driver

[21a1d43](../../commit/21a1d43d43391bc61f098fe7e878266f0a06f029)
Wed, 18 Jan 2012 14:02:52 +0000 - __(Paul Stoellberger)__
Fix bugs for XMLA compatibility with SSAS (unique members and measure groups) and PALO (avoid unnecessary metadata requests in dummy cube for shared dimensions)

[f5ce571](../../commit/f5ce571ccecd15a9ab1ea7d0299d9237144df96e)
Tue, 17 Jan 2012 06:57:57 +0000 - __(Julian Hyde)__
Fix javadoc on Level.getMembers (for MONDRIAN-1063); add 3 more constructors for OlapException, to match SQLException; add an enum to XmlaConstants; correct values for ActionType constants.

[03c5c60](../../commit/03c5c60a261d3f0f6323b995be40b7973e30cfdf)
Thu, 5 Jan 2012 23:55:56 +0000 - __(Julian Hyde)__
Enable svn keyword substitution on some more files.

[0f9cbf1](../../commit/0f9cbf1d07d3afc70372800d004152db6edd3d08)
Thu, 5 Jan 2012 23:43:18 +0000 - __(Julian Hyde)__
Enable svn keyword substitution on some files.

[5b31d8b](../../commit/5b31d8ba4a4ec7cd948e18f338d3fe4f2037aa90)
Thu, 5 Jan 2012 23:27:27 +0000 - __(Julian Hyde)__
Apache Software License, version 2.0.

[c3370ac](../../commit/c3370aca2ec0f2e5e06e6593cf561bff9849e362)
Thu, 10 Nov 2011 00:31:07 +0000 - __(Luc Boudreau)__
Fixes a few problems with the build script and JDK versions.

[4c0f311](../../commit/4c0f31130ca8138ff93c869dd84e5726e043d97e)
Tue, 8 Nov 2011 15:50:36 +0000 - __(Luc Boudreau)__
Fixes an issue with the XmlaTester where each and every request would create a new MondrianServer instance and wouldn't clean up after it is done running the test. The test case now uses the same proxy cookie across requests. This is the same strategy used in the MondrianOlap4jTester.

[a411fbc](../../commit/a411fbc2d1b39071a94827f3632ced701b9907fd)
Mon, 7 Nov 2011 17:48:00 +0000 - __(Julian Hyde)__
Enable build on JDK 1.7 (JDBC 4.1).

[ae37510](../../commit/ae37510bc5c681cc248bbca2cbef1997fdb1f25f)
Mon, 7 Nov 2011 17:47:23 +0000 - __(Julian Hyde)__
Enable build on JDK 1.7 (JDBC 4.1).

[624a2a8](../../commit/624a2a8324749e67230ffa5296006fe93d71c42e)
Mon, 7 Nov 2011 16:24:39 +0000 - __(Luc Boudreau)__
Fixes an issue with SAP BW and the XMLA driver. The XMLA driver was using all of the databases/catalogs/schemas to lookup cubes. This was required in the pre-1.0 code, but it is not necessary anymore because connections are now always implicitly bound to a specific schema.

[191b83a](../../commit/191b83aeb3902a2cd538e8ead882abf5a3aff58f)
Mon, 24 Oct 2011 17:54:40 +0000 - __(Julian Hyde)__
Optimize & normalize imports.

[0627ad7](../../commit/0627ad7ffb2694b0074c7baca9b73a0b5eef6465)
Thu, 29 Sep 2011 15:37:47 +0000 - __(Luc Boudreau)__
Fixes an issue with the XmlaOlap4jDriver and the DriverManager from Java. Every java.sql.Driver implementation needs a public empty constructor so it can be instantiated correctly by JDBC. It is not explicitly specified by the JDBC specs, but Sun's JVM attempts to call that constructor none the less. We have also received some bug reports from the community about olap4j's driver implementation showing the same problems with WebSphere and some other JDBC pools.

[71d43c2](../../commit/71d43c220b1bfff0eaccac677ea4ec626af0c635)
Tue, 13 Sep 2011 20:13:34 +0000 - __(Luc Boudreau)__
Sorts the lists evaluated in the access control tests so their order is deterministic.

[2081404](../../commit/20814043310a7727cca8b0886e58c633981591e1)
Tue, 13 Sep 2011 19:35:54 +0000 - __(Luc Boudreau)__
Disables the control access tests for pure XMLA connections. We need to find a way to pass an XmlaRequestCallback to the tested XMLA servlet before we enable these tests.

[20bed40](../../commit/20bed40c7905bd730a73557a1f592d81c3502c8d)
Sat, 10 Sep 2011 05:53:47 +0000 - __(Luc Boudreau)__
Adds tests for roles and access to schemas, catalogs, cubes, dimensions, hierarchies, members, and parent-children relationships.

[dbe4d05](../../commit/dbe4d0566f5bd20c32eb4e4d0206d81e058dc0dc)
Wed, 3 Aug 2011 21:46:56 +0000 - __(Julian Hyde)__
XMLA driver now converts locale (e.g. "en_US") to LCID (e.g. 1033). (Mondrian can handle either locale or LCID, but the XMLA spec requires LCID.)

[6179da6](../../commit/6179da6c87b40893a615b45e5cee7ed9c59f9d0c)
Tue, 2 Aug 2011 19:30:41 +0000 - __(Julian Hyde)__
XMLA driver now supports localized connections.

[3583e9f](../../commit/3583e9f5bec1105d1c134fb90af0246fc692165f)
Tue, 2 Aug 2011 17:11:04 +0000 - __(Paul Stoellberger)__
if a hierarchy is kept consistent in the query model only make necessary checks. no need to check if ancestor of current member is in ancestor_level.members, fix formatting

[f89c6fe](../../commit/f89c6fe7af3701222da58a11437786172fd7b933)
Tue, 2 Aug 2011 16:33:53 +0000 - __(Paul Stoellberger)__
Bug CalculatedMembers in virtual cube dont show up fixed - http://sourceforge.net/tracker/?func=detail&aid=3312701&group_id=168953&atid=848534. enabled test case from now on

[07a492e](../../commit/07a492e8f99c0c8ca41630b344c91bcb1ba6a34b)
Tue, 2 Aug 2011 16:21:51 +0000 - __(Luc Boudreau)__
Reverts a change to the test suite which should not have been checked in.

[4d81734](../../commit/4d81734b581dc39cc163c5408434f2ffcf6ac2b8)
Wed, 27 Jul 2011 20:09:40 +0000 - __(Luc Boudreau)__
Fixes two failing tests.

[015876e](../../commit/015876edacbd4c1b5f0d164e1e8472e7edda8339)
Tue, 26 Jul 2011 00:59:31 +0000 - __(Julian Hyde)__
Tweaks to getSharedDimensions fix, and more testing.

[26f6862](../../commit/26f68622a5d182dd41b7dba8bd7a51192adb1dbe)
Mon, 25 Jul 2011 21:42:07 +0000 - __(Julian Hyde)__
Fix bug 3375355, "getSharedDimensions returns empty result" (requires a fix to mondrian, approx change 14490). More constructors for NamedListImpl and ArrayNamedListImpl. Fix Pair.toString().

[6f3f308](../../commit/6f3f3087032e4ee29e2f3e960fc59b95db9ed4af)
Tue, 19 Jul 2011 14:19:38 +0000 - __(Luc Boudreau)__
Fixes an error with the last commit. Faulty copy-paste.

[9473742](../../commit/94737426365637457192d3a0203e6ecd6e0c1670)
Mon, 18 Jul 2011 21:07:16 +0000 - __(Luc Boudreau)__
Switches the build dependency of Mondrian to 3.3-SNAPSHOT following the added support for HIERARCHY_IS_VISIBLE in MDSCHEMA_HIERARCHIES. Updates the test suite accordingly.

[bed8eee](../../commit/bed8eee0fc0648076ca21b3b85b65c25170e5230)
Mon, 18 Jul 2011 21:05:28 +0000 - __(Luc Boudreau)__
Switches the build dependency of Mondrian to 3.3-SNAPSHOT following the added support for HIERARCHY_IS_VISIBLE in MDSCHEMA_HIERARCHIES.

[0eb19d9](../../commit/0eb19d93d9977f4dfe2be4e659062b7db135b5c4)
Wed, 6 Jul 2011 13:57:33 +0000 - __(Paul Stoellberger)__
make sure hierarchyConsistent uses the right hierarchy for .CurrentMember, added test case for that

[33d32f7](../../commit/33d32f75398d9430f961319cf5c0f77c546f6020)
Mon, 27 Jun 2011 23:12:16 +0000 - __(Julian Hyde)__
Copy latest checkFile.awk from mondrian. Fix one exception.

[317eaf8](../../commit/317eaf89cea519fa395244d981af372372478f76)
Mon, 27 Jun 2011 22:42:38 +0000 - __(Paul Stoellberger)__
fix formatting (indentation)

[6304f01](../../commit/6304f017200fea5990f7b7b91b99e9aff333f9a6)
Fri, 24 Jun 2011 01:08:46 +0000 - __(Paul Stoellberger)__
Test case for Bug 3312701. calculated measures in virtual cube ignored

[ce511f6](../../commit/ce511f6c986f2286c82bbbbf5f71c3fd9c9f8586)
Fri, 24 Jun 2011 00:36:53 +0000 - __(Paul Stoellberger)__
Implement hierarchy consistency check for QueryDimension inclusions in the Query Model. Useful especially in combination with Level selections

[57c37b8](../../commit/57c37b81aa871c7f17cb5e6a0224c839e82f3023)
Tue, 24 May 2011 10:01:26 +0000 - __(Julian Hyde)__
Enforce formatting for hanging arguments; update copyright notices. Cosmetic changes only.

[f15a3bb](../../commit/f15a3bb0caf59268f9507ee4d886813cf553d20d)
Sun, 15 May 2011 23:25:22 +0000 - __(Paul Stoellberger)__
temporary fix for MONDRIAN-929, needs to be removed once it is fixed

[d2ece8b](../../commit/d2ece8bb3d7ab4192ef423b56307a7fd67bb3b8a)
Wed, 11 May 2011 18:49:20 +0000 - __(Luc Boudreau)__
Adds logic to choose between DBSCHEMA_SCHEMATA and MDSCHEMA_CUBES depending on the provider name.

[b64181a](../../commit/b64181a4cf54eb4e0737f83a004c298996d578e5)
Tue, 19 Apr 2011 03:53:54 +0000 - __(Julian Hyde)__
Add Olap4jUtil.toMap(Properties) and .unmodifiableNamedList(NamedList).

[6e9b2b6](../../commit/6e9b2b67fea5c23e71bc34977140e165148199fa)
Mon, 18 Apr 2011 16:53:46 +0000 - __(Paul Stoellberger)__
fix xmla discovery bug: use catalogschemahandler for schema result of DBSCHEMA_SCHEMATA

[c2c29d3](../../commit/c2c29d30859ed8ec5996d3b1659191fd9b8adbb8)
Wed, 13 Apr 2011 01:57:39 +0000 - __(Luc Boudreau)__
Fixes faulty version number.

[95ed9bd](../../commit/95ed9bd219d9f90c21149a49dff6c08ec13901d0)
Wed, 13 Apr 2011 00:48:09 +0000 - __(Julian Hyde)__
Don't create javadoc with ydoc for a release distro.

[6fd03b4](../../commit/6fd03b42d41599d7125aaf4ce223878c83fcf894)
Wed, 13 Apr 2011 00:32:40 +0000 - __(Julian Hyde)__
In XMLA driver, populate Cube.getCaption() by reading CUBE_CAPTION attribute, if present. Restore javadoc-with-ydoc target. Tweaks to web page.

[083083d](../../commit/083083d697b2ce32682b6446348cef5789d636b9)
Wed, 13 Apr 2011 00:31:41 +0000 - __(Julian Hyde)__
In XMLA driver, populate Cube.getCaption() by reading CUBE_CAPTION attribute, if present. Restore javadoc-with-ydoc target. Tweaks to web page.

[4c743f0](../../commit/4c743f089d9c67dc5db1a904e6422b1c5b8ae04b)
Mon, 11 Apr 2011 17:24:18 +0000 - __(Luc Boudreau)__
Sets the build number back to SNAPSHOT.

[e0a33c9](../../commit/e0a33c91c697ebb839c1c8fc628ead5f32124546)
Mon, 11 Apr 2011 17:09:29 +0000 - __(Luc Boudreau)__
Adds some files missing from the source package.

[a739224](../../commit/a739224926b942c0e74b402d5e15a9c10a411d5f)
Mon, 11 Apr 2011 16:58:46 +0000 - __(Luc Boudreau)__
Triggers a numbered build / maven deploy for the release of olap4j 1.0.0.444.

[577329f](../../commit/577329fe6e5449d73fd52997e25b7c1b18d20408)
Mon, 11 Apr 2011 16:03:59 +0000 - __(Julian Hyde)__
Web site updates for 1.0. Convert spec to PDF.

[5a52a1e](../../commit/5a52a1ec5569a54981e5168157de7dd416e85890)
Mon, 11 Apr 2011 01:05:26 +0000 - __(Luc Boudreau)__
Changes the references to "mdx query model" to "mdx object model". This section refers to the object model. The query model is the org.olap4j.query package.

[bd99422](../../commit/bd99422f83ca86d333bc167090fb03de5092a708)
Sun, 10 Apr 2011 23:17:11 +0000 - __(Luc Boudreau)__
Updates the Database interface description.

[9bb25d4](../../commit/9bb25d4938167e0f70992a1af7c1baa8e8011235)
Sun, 10 Apr 2011 22:32:41 +0000 - __(Julian Hyde)__
Minor corrections and cosmetic changes to functional specification.

[6308b28](../../commit/6308b288845e0d64f76ed90f6eed4b2f84035799)
Sun, 10 Apr 2011 00:57:09 +0000 - __(Julian Hyde)__
Revise functional specification (including PDF version) for release 1.0. Add spacer image (website was broken without it). Minor javadoc tweaks, and remove an obsolete method.

[a25dece](../../commit/a25dece08c66acedfa6f06229131e245999d24b3)
Fri, 8 Apr 2011 21:15:08 +0000 - __(Luc Boudreau)__
Adds the xmla-cache folder to the distributed source zip.

[5ac9bc8](../../commit/5ac9bc855faaf1943534ab077a7701d38af01c5a)
Fri, 8 Apr 2011 18:47:13 +0000 - __(Luc Boudreau)__
Triggers a fixed build number to be deployed to the maven repository.

[3ee09ed](../../commit/3ee09ede278340587821ba7d1bd8fe2bec65169d)
Fri, 8 Apr 2011 17:49:49 +0000 - __(Luc Boudreau)__
Fixes javadoc error.

[ab39d44](../../commit/ab39d443fb17925e65d55d7cfe79d7e3add7a722)
Fri, 8 Apr 2011 17:47:30 +0000 - __(Luc Boudreau)__
Fixes javadoc error.

[df2f6db](../../commit/df2f6db8004cb9a2676d0a9f354611813734668d)
Wed, 30 Mar 2011 23:21:39 +0000 - __(Luc Boudreau)__
Dereferences the call to Collections.emptyMap() into a properly typed variable in order to prevent the method calling itself.

[d74b0bf](../../commit/d74b0bf803ba223da6d67cb9b310abeee1e1f7e3)
Wed, 30 Mar 2011 19:56:50 +0000 - __(Luc Boudreau)__
Fixes checkFile errors.

[48d4687](../../commit/48d46877cf9e95db7c00d33b76a56859d5fb0fa6)
Fri, 25 Mar 2011 03:20:55 +0000 - __(Luc Boudreau)__
Adds Cube.isDrillThroughEnabled, adds related tests and adds a mechanism to override values returned by an XMLA server for metadata calls.

[a640c01](../../commit/a640c012a9d87de96f2284f3757009515277e10f)
Thu, 24 Mar 2011 15:14:50 +0000 - __(Luc Boudreau)__
Fixes issues with the comparison of parameters and properties. Due to the case of the parameters, sometimes a comparison would fail. They are now all compared as upper case values across the board.

[9bb70cd](../../commit/9bb70cdbe92d4741aadce819daaeb5fb0d01940b)
Thu, 24 Mar 2011 15:01:27 +0000 - __(Luc Boudreau)__
Fixes javadoc errors.

[46a1c34](../../commit/46a1c34ca20b79e3afd543d2c04810c0dc2db9a5)
Thu, 24 Mar 2011 05:12:36 +0000 - __(Luc Boudreau)__
Fixes bug 3060643. Cache properties are not ignoring the case of the passed parameters. Everything is evaluated as upper case.

[47b89a4](../../commit/47b89a4566e73fbdbd4fd66896bfc221309f6bbf)
Thu, 24 Mar 2011 05:04:05 +0000 - __(Luc Boudreau)__
Fixes bug 3192302. Cookie manager was not tokenizing the session cookies properly.

[09ef658](../../commit/09ef6583a0ec5341ae0bc7dd0039d9f00b5f00d3)
Thu, 24 Mar 2011 04:53:53 +0000 - __(Luc Boudreau)__
Fixes bug 3233181 in the XMLA driver. Refactored the way username and password are passed to the url. Should allow us later to easily implement user sessions.

[0a2e5c6](../../commit/0a2e5c67d2289b7b7f95fe9c1fb766f05c5bfcac)
Wed, 23 Mar 2011 03:47:55 +0000 - __(Luc Boudreau)__
Cleanup of the XmlaProxyCache prior to 1.0.0. Was throwing a Runtime Exception for no good reason. Downgrading it to OlapException. They get handled anyways by the XmlaProxy contract.

[fd44edb](../../commit/fd44edb4609632d0a874ba9d332a9fa2e571133c)
Tue, 22 Mar 2011 20:48:15 +0000 - __(Luc Boudreau)__
Fixes the catalog XMLA object so that it first tries DBSCHEMA_SCHEMATA request to find schemas and falls back to MDSCHEMA_CUBES if the server rejected the first query.

[571c2f6](../../commit/571c2f6d0e74406456abe10d6564c37f9c1ef038)
Tue, 22 Mar 2011 18:29:50 +0000 - __(Luc Boudreau)__
Adds some files to be ignored by SVN.

[79dbd1d](../../commit/79dbd1d8efb6552a6ad6dc1c8f9fbaade59dc945)
Tue, 22 Mar 2011 18:19:44 +0000 - __(Luc Boudreau)__
Changes the database xmla cache to save entries as XML rather than a Base64 encoded value.

[0f53c0a](../../commit/0f53c0a2c3559eb7937df25443ee67c6d89d493e)
Tue, 22 Mar 2011 03:45:14 +0000 - __(Luc Boudreau)__
Changes the recording proxy cache to a more fancy one using hsqldb.

[7915295](../../commit/791529526f9a242ec1ddb6364ffaf41710dfe298)
Tue, 22 Mar 2011 01:20:03 +0000 - __(Luc Boudreau)__
Adds a proof-of-concept recording/playback cache for the XMLA driver. To use, uncomment the right lines in test.properties and unzip the xmla/cache/mondrian-xmla-[VERSION].zip file you want to test against. Make sure that the test.properties entry points to the proper file.

[aa89f47](../../commit/aa89f4774e46f40018d466a3bb86228a965a4e7a)
Mon, 21 Mar 2011 02:43:53 +0000 - __(Luc Boudreau)__
Changes the repository URL on the index page.

[52cc997](../../commit/52cc9978a8214f5f52094f13a24379230e418c60)
Mon, 21 Mar 2011 01:21:42 +0000 - __(Luc Boudreau)__
Build script now publishes the parsed Ivy descriptors rather than the templates :)

[335df3b](../../commit/335df3bf9380f5f4f1c6f8fa49accee084295bfa)
Sun, 20 Mar 2011 22:09:14 +0000 - __(Luc Boudreau)__
Pom generator was looking in the wrong directory for the ivy descriptor.

[2b5b762](../../commit/2b5b7625ac68b75d904710a78e4b1149cbce2e9a)
Sun, 20 Mar 2011 21:56:14 +0000 - __(Luc Boudreau)__
Distributable Ivy descriptor was not generated in the right folder.

[b33bf9f](../../commit/b33bf9f77da3a60578a093742e418cb8d2c7414d)
Sun, 20 Mar 2011 21:46:45 +0000 - __(Luc Boudreau)__
Generates cleaner and more standard Ivy descriptors to be deployed.

[7d0dd82](../../commit/7d0dd82348fb62957c4497bc7c1b520f603889d6)
Sun, 20 Mar 2011 16:55:53 +0000 - __(Luc Boudreau)__
Standardizes the jdk14 jar module name. Enables retroweaving of the xmla driver.

[38d2fff](../../commit/38d2fff3b19bdd663813735f053fff2a7c279291)
Sun, 20 Mar 2011 16:37:50 +0000 - __(Luc Boudreau)__
Splits the deployed artifacts into different modules, each with their own POM descriptor.

[bc518b9](../../commit/bc518b9cf378a49d6b2a3b32ca1ad043594c7e3b)
Sun, 20 Mar 2011 04:36:09 +0000 - __(Luc Boudreau)__
Overrides subfloor resolve task so we can resolve custom Ivy configurations.

[b1be7b4](../../commit/b1be7b470c2e5461c12025e2754d701606ccfbda)
Sun, 20 Mar 2011 04:10:55 +0000 - __(Luc Boudreau)__
Modifies the ivy file to split dependencies in a more manageable way.

[b96b3af](../../commit/b96b3af3368a8951932c6824d0b9ac1e3176b227)
Sun, 20 Mar 2011 04:08:27 +0000 - __(Luc Boudreau)__
Generates a custom pom file for the deployment of the XMLA module.

[537756f](../../commit/537756fe4c4512c26aeec200d2232d3e5340b08d)
Sun, 20 Mar 2011 02:08:14 +0000 - __(Luc Boudreau)__
Updates the CHANGES file.

[c65dcd7](../../commit/c65dcd7194c94d4cb162234bafc25dbdb1f78bab)
Sun, 20 Mar 2011 01:49:50 +0000 - __(Luc Boudreau)__
Adds Maven repository information to the website index.

[b693034](../../commit/b69303426283006af7a0397c80edaeacba6b02d9)
Sun, 20 Mar 2011 01:40:23 +0000 - __(Luc Boudreau)__
Updates the website index page.

[41faf45](../../commit/41faf45f9d9fbebeaa2610996bf9ea1d39f3be75)
Sun, 20 Mar 2011 00:44:11 +0000 - __(Luc Boudreau)__
Fixes javadoc warnings. Updates README file.

[d302c08](../../commit/d302c0889c0cbde3ef171929b47198148d5a0b75)
Sat, 19 Mar 2011 18:11:10 +0000 - __(Luc Boudreau)__
Fixes a property conflict with the XMLA jar publish target.

[c62104f](../../commit/c62104f25df88ebe47c50be7d70a958318f3ce4b)
Sat, 19 Mar 2011 17:07:10 +0000 - __(Luc Boudreau)__
Sets the version to 1.0.0-SNAPSHOT in preparation to olap4j 1.0. Also splits the main olap4j jar into two separate jars for the core API and the XMLA driver.

[a0119c9](../../commit/a0119c958ac5c3fbe3be6786a042be26719bf771)
Fri, 18 Mar 2011 21:54:56 +0000 - __(Luc Boudreau)__
First pass at olap4j 1.0.

[6987348](../../commit/69873480469fa1925608aab42268872f490180e7)
Fri, 11 Mar 2011 03:10:41 +0000 - __(Paul Stoellberger)__
fix bug (that was already fixed) for catalog restriction in XMLA requests (for ssas / palo)

[35f2e94](../../commit/35f2e94ea6bf42f277f8e24558ff218a6f6b31bd)
Mon, 7 Feb 2011 01:23:12 +0000 - __(Paul Stoellberger)__
small harmonization of including member vs. level

[828ad8f](../../commit/828ad8f69dc17bce45145422dde81f086182ca77)
Fri, 4 Feb 2011 00:24:40 +0000 - __(Luc Boudreau)__
Refactored the selection objects so that everything is statically typed rather than a conditional instanceof check. I used a pseudo visitor pattern approach. I also factored out the selection implementations into an abstract super class. I also simplified the selection objects and removed all unnecessary calls.

[633ca0a](../../commit/633ca0a55cc662d1a3f1754d4d1c4c79c2644bc3)
Thu, 3 Feb 2011 22:32:06 +0000 - __(Paul Stoellberger)__
Fix Bug 2850060 (QueryModel needs to perform selections on levels)

[99a5541](../../commit/99a5541d08003238a9503a9b0263c4cd65a27b90)
Mon, 10 Jan 2011 10:32:48 +0000 - __(Julian Hyde)__
Commit patch 3150694, "SSAS 2000 XML/A compatibility". (Patch contributed by sjoynt.)

[9a3b7d8](../../commit/9a3b7d8ba996c7239f06c79bbc738f27de89b41a)
Mon, 10 Jan 2011 10:13:12 +0000 - __(Julian Hyde)__
Fix bug 3142240, "Additional columns in cube discovery". (Patch contributed by sjoynt.) Also fix some checkFile exceptions.

[8ceb0e5](../../commit/8ceb0e56a3883ebd9430a55e2e26535edc34e773)
Mon, 10 Jan 2011 09:47:57 +0000 - __(Julian Hyde)__
Fix bug 3152775, "EmptyResultSet bugs". (Patch contributed by sjoynt.)

[f741893](../../commit/f741893b18f28b97f5c45afb4c71c3b453e4e05a)
Thu, 23 Dec 2010 16:48:41 +0000 - __(Luc Boudreau)__
Upgrades Mondrian to 3.2.1.14022.

[04f4a37](../../commit/04f4a37700e00f7bf2a714104f2c314baaeed6a6)
Thu, 23 Dec 2010 15:23:27 +0000 - __(Luc Boudreau)__
Upgrades Mondrian to 3.2.1.14020.

[9c081ae](../../commit/9c081aeb89079d60a515901c656b61a0c3d16d4c)
Thu, 23 Dec 2010 02:20:27 +0000 - __(Luc Boudreau)__
Reverting back to SNAPSHOT builds.

[4bcf735](../../commit/4bcf735c259eff84b4c2866c8480c401a45a2e3d)
Thu, 23 Dec 2010 01:23:04 +0000 - __(Luc Boudreau)__
Fixes more issues with the TCK auto deployment to maven.

[0686294](../../commit/0686294f7f2002382b3448066ab729d659911fd2)
Thu, 23 Dec 2010 01:13:46 +0000 - __(Luc Boudreau)__
Fixes more issues with the TCK auto deployment to maven.

[9ce53ca](../../commit/9ce53ca468b3a5120ea40111f0220f623386d8ce)
Thu, 23 Dec 2010 00:07:41 +0000 - __(Luc Boudreau)__
Reverting back to SNAPSHOT builds.

[4389388](../../commit/43893880deb2204ea69dc222ddf63bbf7116464b)
Wed, 22 Dec 2010 23:58:33 +0000 - __(Luc Boudreau)__
Fixes (yet again) another woopsie with the build.

[a13b7ec](../../commit/a13b7ec8109ba88d0fa0e586288c26d52fefff3b)
Wed, 22 Dec 2010 23:48:07 +0000 - __(Luc Boudreau)__
Fixes a missing jar in the build system.

[bc18f65](../../commit/bc18f655a5872d851af1c5e855044f771206bb22)
Wed, 22 Dec 2010 23:36:54 +0000 - __(Luc Boudreau)__
Overrides a target in subfloor.xml to publish the olap4j-tck artifact as well.

[dc59642](../../commit/dc59642bddb7c7d6fea76e2a224024ce843e8cf6)
Wed, 22 Dec 2010 23:13:01 +0000 - __(Luc Boudreau)__
Reverting back to SNAPSHOT builds.

[02813b3](../../commit/02813b34f007c678c2504a7e4503dd74f72c954f)
Wed, 22 Dec 2010 23:11:19 +0000 - __(Luc Boudreau)__
Triggering a numbered build to get back in sync with Mondrian.

[d7e4802](../../commit/d7e48027ec26a99fce052f713037b889dca1786a)
Wed, 22 Dec 2010 22:13:07 +0000 - __(Luc Boudreau)__
Removes unnecessary dependency on the Mondrian code.

[1643a1a](../../commit/1643a1acdeb2e9a774a4d47bc1e19ab5b5157641)
Wed, 22 Dec 2010 20:47:49 +0000 - __(Luc Boudreau)__
Removes the signature override from OlapDatabaseMetaData. It should not have been committed.

[3c125ae](../../commit/3c125aee059c31369230a7cdbe5495ee7aadd8d6)
Mon, 20 Dec 2010 21:21:45 +0000 - __(Luc Boudreau)__
First round (and hopefully last) of olap4j 0.9.9.

[3bbe4b1](../../commit/3bbe4b1e33e2ab6274a35b80e0f5b69d487a72bd)
Fri, 17 Dec 2010 07:49:03 +0000 - __(Julian Hyde)__
Use BigDecimal, not Double or Integer, to hold the value of numeric LiteralNode. Deprecate corresponding LiteralNode.create methods.

[ddaf934](../../commit/ddaf93473c9693a2d1069c38926aee83c0348104)
Fri, 17 Dec 2010 04:30:19 +0000 - __(Julian Hyde)__
Change how tests in TCK get their context. Delete directories for obsolete mondrian.olap4j package.

[6c1e790](../../commit/6c1e7909d47b912a316c8f399ec39992da708cad)
Fri, 17 Dec 2010 02:58:28 +0000 - __(Julian Hyde)__
Javadoc changes regarding locale and visibility.

[2d66d33](../../commit/2d66d337ed2a5e0cf499054a7e35cf3f2a9e67ec)
Fri, 3 Dec 2010 20:03:34 +0000 - __(Julian Hyde)__
Typo in ConnectionTest.

[a65b39e](../../commit/a65b39e64bf1032f280a7c423df5ec5d536ef8d4)
Fri, 3 Dec 2010 18:55:51 +0000 - __(Julian Hyde)__
Disable ConnectionTest.testCellSetBug. Bug 3126553 logged.

[8b73854](../../commit/8b738540748f3d67bec261df40b2724f806e55f5)
Fri, 3 Dec 2010 18:51:13 +0000 - __(Julian Hyde)__
Dummy change

[5291574](../../commit/52915742263148a76b10d4fa93ef7a41d7929ac0)
Fri, 3 Dec 2010 04:54:05 +0000 - __(Julian Hyde)__
The TCK allows the test suite to be run multiple times in the same process with different properties. So, TestContext is no longer a singleton; if a test needs an instance, save it when the TestCase is being constructed (when the correct TestContext is available in a thread-local).

[f2beff8](../../commit/f2beff8dfd484d54f307ff54638b0c5fe564bce6)
Fri, 3 Dec 2010 02:29:37 +0000 - __(Julian Hyde)__
Implement MetadataElement.isVisible in more classes.

[116ad0d](../../commit/116ad0d1720764b8a0fdb4d488342ea77c1c0893)
Thu, 2 Dec 2010 22:06:52 +0000 - __(Julian Hyde)__
Add MetadataElement.isVisible, and implement in XMLA driver.

[0c8a01f](../../commit/0c8a01f4ec8a1581f8623c1fd82f01dc6c040694)
Thu, 2 Dec 2010 20:24:17 +0000 - __(Julian Hyde)__
Formatting.

[2cac149](../../commit/2cac1496aa6cb2cce5a6ebefae193920cf47e632)
Tue, 23 Nov 2010 20:27:57 +0000 - __(Julian Hyde)__
Create TCK so that mondrian can run olap4j tests from its own suite.

[6fdb574](../../commit/6fdb5744f9e4f7df23960d52ab2e77332635b518)
Mon, 22 Nov 2010 07:32:50 +0000 - __(Julian Hyde)__
Add 'throws OlapException' to a few methods.

[cae3a13](../../commit/cae3a13f48cb06cdbb9f534fcc3daf21400db7d0)
Wed, 17 Nov 2010 08:48:31 +0000 - __(Julian Hyde)__
Upgrade mondrian to fix bug: require its XMLA server to return a filter axis in a query result even if filter axis has 0 tuples.

[0ef4414](../../commit/0ef441480c71c54981ee275b308c588a3b90d2f3)
Tue, 16 Nov 2010 21:37:12 +0000 - __(Julian Hyde)__
Upgrade to version of mondrian that implements latest API.

[ed8217c](../../commit/ed8217c7c95cc3a3206953998fa02b757560d5ad)
Tue, 16 Nov 2010 18:54:39 +0000 - __(Julian Hyde)__
More API changes before 1.0...

[1741dbc](../../commit/1741dbc04386a2ad74668cb63cc3007eadf0f6a7)
Tue, 9 Nov 2010 21:09:40 +0000 - __(Julian Hyde)__
Log bug 3106220 and disable test.

[b2c7edf](../../commit/b2c7edf3250b4732ef5675ef538e564d7a28ca97)
Tue, 9 Nov 2010 18:44:22 +0000 - __(Julian Hyde)__
Fix MONDRIAN-831, "Failure parsing queries with member identifiers beginning with '_' and not expressed between brackets".

[5929226](../../commit/5929226cf8c522e0cb219a536b441ba4ed56eb3a)
Fri, 5 Nov 2010 03:27:48 +0000 - __(Julian Hyde)__
Fix behavior and test exception relating to IdentifierNode.ofNames.

[8d179c1](../../commit/8d179c1de50e4544e533726673d59c192026a060)
Fri, 5 Nov 2010 01:22:14 +0000 - __(Julian Hyde)__
Upgrade mondrian to version compatible with latest changes. Should fix regression test errors.

[da25c97](../../commit/da25c9722dab7b5d1a89a3e0dc468d20fe09e50e)
Fri, 29 Oct 2010 22:17:07 +0000 - __(Julian Hyde)__
Enable svn keyword substitution. Fix javadoc in KeySegment.

[6b74aec](../../commit/6b74aececcaf755058a17592f6d88887a5255be1)
Fri, 29 Oct 2010 16:43:02 +0000 - __(Julian Hyde)__
Fix formatting.

[e479cf9](../../commit/e479cf97008710894e173f9bead7b8930b83dcde)
Thu, 28 Oct 2010 18:14:32 +0000 - __(Julian Hyde)__
Fix bug 3095309, "Lookup methods should use Segment, not name, before 1.0".

[4b0a021](../../commit/4b0a021257e6a2fb6d9634aebd345cee4b3c0118)
Thu, 14 Oct 2010 21:24:51 +0000 - __(Julian Hyde)__
Change IdentifierNode.parseIdentifier(String) to return an IdentifierNode; it previously returned List<IdentifierNode.Segment>.

[f8e0a0a](../../commit/f8e0a0a2773c9c6cc50e297d2f3d61825fc21bd2)
Thu, 14 Oct 2010 16:44:47 +0000 - __(Paul Stoellberger)__
add unit test for found bug in the olap4jNodeConverter regarding complex selections that contain a selectioncontext

[5732212](../../commit/573221275923b299710cfd3c6077190e8e357a05)
Tue, 12 Oct 2010 00:39:07 +0000 - __(Julian Hyde)__
Fixes for cellsets with no filter or compound filter. Fix formatting if filter is compound (by upgrading mondrian). Add tests now that mondrian correctly returns all positions of compound filter.

[b8cb21d](../../commit/b8cb21d23a78faf4649cdd431eb8ba2c87bce516)
Fri, 8 Oct 2010 15:12:15 +0000 - __(Paul Stoellberger)__
fix checkFile errors, add unit tests for previously commited bug fixes: crossjoin sort, except and compound filter

[3d67870](../../commit/3d678701c25a739c14829d4755a80967d4422041)
Fri, 8 Oct 2010 12:33:16 +0000 - __(Paul Stoellberger)__
adapt QM tests to new filter behaviour (set instead of tuple)

[633b604](../../commit/633b6042c192b77d66ad91970bc8e35d1c424171)
Thu, 7 Oct 2010 19:02:22 +0000 - __(Paul Stoellberger)__
treat filter axis like any other, fix sort order and exclusions for crossjoin queries on an axis

[a07775e](../../commit/a07775ef6d41c93b11f32f05685ca5836d34ddcd)
Sat, 14 Aug 2010 01:46:56 +0000 - __(Julian Hyde)__
Make classes that implement IdentifierParser.Builder public, so that mondrian can use them.

[a385d41](../../commit/a385d416e35185db54117df6a277b39911ff4cb0)
Sat, 14 Aug 2010 01:12:17 +0000 - __(Julian Hyde)__
Upgrade to mondrian-3.2.0.13809, which implements Connection.getTransactionIsolation as required by new test.

[9044e9a](../../commit/9044e9a40b617eb77a3961116d77c336a6f473ac)
Thu, 12 Aug 2010 21:12:54 +0000 - __(Julian Hyde)__
Oops. Fix tests.

[7e99a82](../../commit/7e99a82b7719da8fc4b9a96dddc5d0862d21ae9b)
Thu, 12 Aug 2010 19:50:12 +0000 - __(Luc Boudreau)__
Adds a test for transaction isolation of the driver implementations.

[d13bb5c](../../commit/d13bb5c65ef2a69b01f2bf2decb601e636696f82)
Thu, 12 Aug 2010 08:49:13 +0000 - __(Julian Hyde)__
Fix further issues in bug 3042937 by no longer enclosing formula of calculated member and set in single quotes. Also, indent formulas for readability.

[c6702a9](../../commit/c6702a977f0224aaf4910e4b1e20cd39f8843d6d)
Wed, 11 Aug 2010 20:37:47 +0000 - __(Luc Boudreau)__
Fixes bug 3042937. Escaped single quotes that were nested in a single quoted section were unparsed as unescaped single quotes.

[4114437](../../commit/41144370096499712f19e0665a37ac987c43fe3f)
Tue, 10 Aug 2010 18:36:00 +0000 - __(Luc Boudreau)__
Makes the XMLA driver return TRANSACTION_NONE to signal the connection pools that it doesn't support transaction isolation.

[4564357](../../commit/4564357a2ce6745580f205c17f5f53b073134921)
Fri, 6 Aug 2010 15:55:31 +0000 - __(Luc Boudreau)__
Sets CI build version to 0.9.9-SNAPSHOT

[ebff6bb](../../commit/ebff6bbc3c6c64d778bb465560e96c3a6a5014c7)
Fri, 6 Aug 2010 13:51:27 +0000 - __(Luc Boudreau)__
sets the project revision for final build.

[e64022e](../../commit/e64022ec9564f8ce9c3859a66e3219defe25b4e4)
Fri, 6 Aug 2010 13:35:10 +0000 - __(Luc Boudreau)__
Sets project.revision.minor so the correct value.

[57ad1b1](../../commit/57ad1b172934127a77eff48c5c57aaed5752586d)
Thu, 5 Aug 2010 16:28:11 +0000 - __(Luc Boudreau)__
Sets the version ID for the release.

[fdc9ff7](../../commit/fdc9ff7fb57ed8cd1c7d26c7018f84c1596f91c2)
Thu, 5 Aug 2010 16:16:00 +0000 - __(Luc Boudreau)__
Updated the README file with the proper infos.

[9ff66db](../../commit/9ff66db4a3ea422e70b335eed2d47072d3dbc4a5)
Thu, 5 Aug 2010 15:49:21 +0000 - __(Luc Boudreau)__
Updated index page. Now includes information about the project roadmap.

[2da80d9](../../commit/2da80d923f608de2723b40b409a07387deecbec7)
Thu, 5 Aug 2010 14:58:22 +0000 - __(Luc Boudreau)__
Documented the test.properties file a little more.

[3df55a7](../../commit/3df55a7adbec4d8d5332e81513adce5f6f3c599d)
Thu, 5 Aug 2010 14:45:34 +0000 - __(Luc Boudreau)__
Fixes all failing tests with a constructor for ParseTreeWriter that takes a PrintWriter for argument.

[b94e089](../../commit/b94e089862dc5f7844b344573f856682c86da6d4)
Tue, 3 Aug 2010 01:28:26 +0000 - __(Julian Hyde)__
Oops.

[1c7fd8f](../../commit/1c7fd8fdc0e11fa4fbcf52009b73260c46785fef)
Tue, 3 Aug 2010 01:07:46 +0000 - __(Julian Hyde)__
Fix bug 3036629, "Patch 328 breaks test", by moving IdentifierParser from mondrian to olap4j and extending support for compound keys (e.g. [Customer].[City].&[San Francisco]&CA&USA.)

[09ba75f](../../commit/09ba75fe788295c3c770bb6dcf5435a6c94f1f3c)
Fri, 30 Jul 2010 00:43:55 +0000 - __(Julian Hyde)__
Fix bug 3035910, "Cannot parse sub-query". (Fix contributed by Thomas Klute.) ParseTreeWriter can now generate indented output.

[d651165](../../commit/d6511655112d7411ffbbacb651b9927305fda99b)
Wed, 28 Jul 2010 19:56:14 +0000 - __(Luc Boudreau)__
Better usage of the formatter.

[b06bcd2](../../commit/b06bcd272be2e7c29889b40c313bd5f21badab9b)
Wed, 28 Jul 2010 17:04:56 +0000 - __(Luc Boudreau)__
Added some code to close the connection/statement on the PaloConnection example.

[49915ad](../../commit/49915ad03924aaf2a654e4d2c2436a49a1f98287)
Wed, 28 Jul 2010 16:59:09 +0000 - __(Luc Boudreau)__
Adds a sample class that demonstrates how to connect to Palo with the XML/A server. Original post is at http://sourceforge.net/projects/olap4j/forums/forum/577988/topic/3787499

[ffc916c](../../commit/ffc916c5038db89d35c6d6e870e71d241cc87fbd)
Tue, 27 Jul 2010 15:53:09 +0000 - __(Luc Boudreau)__
Fixes all failing tests introduced by the new tester.

[92fe96c](../../commit/92fe96c3277d5c2b3ca677415592917fd4057262)
Mon, 26 Jul 2010 15:43:18 +0000 - __(Paul Stoellberger)__
Improve the Identifier parser to support mixed Segment types (QUOTED, UNQUOTED, ..) in an MDX Identifier as used in PALO

[1521c92](../../commit/1521c92490497e2b7f3eac292af6041600c55a31)
Thu, 22 Jul 2010 19:45:26 +0000 - __(Luc Boudreau)__
Added a new tester flavor to allow remote xmla servers to be tested against the olap4j test suite. The remote server must expose the Foodmart test database.

[c4079d7](../../commit/c4079d7cd02baf2655c8617783227f5d6f1de33d)
Tue, 20 Jul 2010 19:56:08 +0000 - __(Luc Boudreau)__
Fixes to the cube discovery for SAP BW <-> XML/A.

[8f72d8b](../../commit/8f72d8b3dca09973ec569e071ff3e55a423d8e1e)
Sun, 18 Jul 2010 19:32:11 +0000 - __(Julian Hyde)__
Fix bug 3030772, "DrilldownLevelTop parser error" by adding support for empty expressions in argument list. Copied from mondrian change 10047.

[9f80214](../../commit/9f80214247d7959ab84aa54e280ff252b827a22e)
Tue, 29 Jun 2010 19:21:24 +0000 - __(Paul Stoellberger)__
disable accidental DEBUG on XmlaOlap4jConnection again

[e44cbc0](../../commit/e44cbc062c55ce70ddcf9e953f15e2e58e79dd5f)
Tue, 29 Jun 2010 07:43:29 +0000 - __(Julian Hyde)__
Latest checkFile.

[5a8e2d2](../../commit/5a8e2d2a97eccea5ec13e416a8dc99fc9f8c553f)
Tue, 29 Jun 2010 00:49:51 +0000 - __(Luc Boudreau)__
Fixes code style violation.

[5d83a7a](../../commit/5d83a7a4ed1a3825d9f2157db321d8f825da46f9)
Fri, 25 Jun 2010 15:03:20 +0000 - __(Luc Boudreau)__
Integrates Paul's modifications to support SAP BW. This work is probably not completed yet but I'm checking it in so we can test it in different environments. SSAS, Mondrian and SAP.

[1148625](../../commit/1148625bc645989b0a01b9f38c2a449aa2bca3ae)
Thu, 17 Jun 2010 03:39:04 +0000 - __(Julian Hyde)__
Add Olap4jUtil.parseFormattedCellValue.

[141d730](../../commit/141d7308a20379b1af6e7b472247ab5a60790ac7)
Wed, 16 Jun 2010 05:16:04 +0000 - __(Julian Hyde)__
Add experimental API for client to ask server to notify when the cell set changes. XMLA driver does not currently support the API.

[1a1d929](../../commit/1a1d92935579ee5d6ff68cd22c4745eda2898c80)
Thu, 10 Jun 2010 19:08:03 +0000 - __(Paul Stoellberger)__
Fix bug 3009981 : XmlaOlap4jCube shouldnt populate namedsets immediately > sets being populated when accessed for the first time

[24b7010](../../commit/24b70105665b6e705d3937783a6b12a5f0f47b97)
Sun, 30 May 2010 03:32:25 +0000 - __(Julian Hyde)__
Fix javadoc.

[d872dca](../../commit/d872dcaddf5dd46ad033fd93e4509335edb9f90d)
Sat, 29 May 2010 19:42:14 +0000 - __(Julian Hyde)__
Upgrade to mondrian-3.2.0.13651 (implements latest PreparedOlapStatement API, and Member.getAncestorMembers()).

[d3704eb](../../commit/d3704ebee62aa4857985fb9a5c871c8a09e095fb)
Sat, 29 May 2010 00:56:11 +0000 - __(Julian Hyde)__
Update copyright notices for files modified this year.

[24f806d](../../commit/24f806da28f14d9e571a1a5c241386d32f2f9cb0)
Sat, 29 May 2010 00:50:47 +0000 - __(Julian Hyde)__
Add PreparedOlapStatement.isSet(int) and .unset(int), and implement in XMLA driver. (These methods are implemented in the mondrian driver in eigenbase change 13650.)

[d91ccf4](../../commit/d91ccf405fca7aa9a12baad978ccd954780a6083)
Thu, 20 May 2010 20:53:50 +0000 - __(Paul Stoellberger)__
equals and hashcode for Selection

[01238b8](../../commit/01238b89df6052c8e05f61c17bc86a6850653f8f)
Wed, 5 May 2010 23:32:54 +0000 - __(Julian Hyde)__
Test Olap4jPreparedStatment.getCube().

[254931b](../../commit/254931bfa100bf8fdf843af8f95aa2556586754e)
Tue, 27 Apr 2010 18:06:15 +0000 - __(Julian Hyde)__
Fix bug 2992614, "XmlaOlap4jMember throws NPE on PARENT_ Properties".

[041d119](../../commit/041d119d70dc4bac51ca48acd9dec78ad686bebe)
Fri, 23 Apr 2010 19:57:41 +0000 - __(Julian Hyde)__
Update test ref logs for mondrian change which emits '[All Xxxx] in member unique names.

[73e4db4](../../commit/73e4db480ad9cf2ad74d2e73eee3223b6d3f8491)
Thu, 22 Apr 2010 04:17:24 +0000 - __(Julian Hyde)__
Add property org.olap4j.test.driverClasspath from test.properties. Makes it easier to run the test suite against a database whose JDBC driver jar lives outside the source tree (e.g. Oracle).

[41c74ea](../../commit/41c74ea85b6801f41da92a8e2483dcb87604e141)
Wed, 7 Apr 2010 20:44:09 +0000 - __(Luc Boudreau)__
Sets Eclipse JRE setting to an execution environment value rather than a full JRE. This should prevent us from using com.sun.* forbidden packages.

[cc9f9d1](../../commit/cc9f9d18eacbb0646471f10a372e653ea5eaa219)
Tue, 6 Apr 2010 22:46:45 +0000 - __(Luc Boudreau)__
Makes the DefaultMdxParserImpl class not depend on OlapConnection. The validator must still depend on it since it must resolve members.

[518c99b](../../commit/518c99b6c8584b8a2c808bb860e9a7644b7e2397)
Mon, 5 Apr 2010 01:34:29 +0000 - __(Julian Hyde)__
Cosmetic stuff. Mainly checkFile exceptions.

[764f203](../../commit/764f203fe60c7e3680c5111f446d5af4c17dc9f4)
Mon, 8 Mar 2010 05:27:54 +0000 - __(Will Gorman)__
removed accidental import

[a236eb0](../../commit/a236eb091f2081654c4287c6f9111dd2f1838635)
Mon, 8 Mar 2010 04:27:50 +0000 - __(Will Gorman)__
added selection context concept and a new union algorithm when selecting multiple dimensions on an axis to support drill down

[f110e66](../../commit/f110e66e004b532f50aa700c40c88548b8f4792f)
Mon, 22 Feb 2010 08:35:19 +0000 - __(Julian Hyde)__
Add test (disabled) for bug 2951656. Bug needs to be fixed in mondrian driver, so the test may be disabled for a while.

[97812f2](../../commit/97812f205e98456187eb1245a567ff5b9baeb28e)
Mon, 22 Feb 2010 07:23:28 +0000 - __(Julian Hyde)__
Fix JDK1.4 compatability. Upgrade version of mondrian (previous version did not implement latest API).

[43c9cab](../../commit/43c9cabfacc84c10e97360eda0bef9169b416fd2)
Tue, 9 Feb 2010 20:54:51 +0000 - __(Luc Boudreau)__
Fixes an issue with Palo servers where some xsd:schema elements are inlined. This fix makes sure that we only read elements of the rowset namespace and skips elements from other namespaces.

[2523f8c](../../commit/2523f8c71f42a4be141d62e1d2cb19f3a5168b42)
Tue, 9 Feb 2010 20:51:26 +0000 - __(Luc Boudreau)__
Coding standard.

[991f4af](../../commit/991f4aff5c1fbe553e4f7e92539989d5ff4863de)
Tue, 9 Feb 2010 17:10:45 +0000 - __(Luc Boudreau)__
Resets the list's state upon encountering an exception.

[2ab221b](../../commit/2ab221b40683cff00a5c5ed17fd6f4ae6bd9278e)
Mon, 25 Jan 2010 07:45:08 +0000 - __(Julian Hyde)__
Add enums and constants related to XMLA (formerly in Mondrian). Add space-efficient maps ArrayMap and UnmodifiableArrayMap.

[5218d69](../../commit/5218d6944d232f839b4119e07c1695364425a5f8)
Sat, 9 Jan 2010 04:17:06 +0000 - __(Julian Hyde)__
Cosmetic stuff.

[b0ef48c](../../commit/b0ef48cd4641db3d4a8b3c399944cd443ad83539)
Thu, 7 Jan 2010 09:14:19 +0000 - __(Julian Hyde)__
Add OlapConnection.getAvailableRoleNames().

[32990d3](../../commit/32990d3411407ecadc79ef8dc2b923e2da7fd066)
Fri, 18 Dec 2009 22:28:32 +0000 - __(Julian Hyde)__
Upgrade ivy. (Next time you update, please do 'rm -rf ~/.subfloor_build_cache'.)

[0f591e0](../../commit/0f591e0fcadd926b5d11d7d81e773a0367b97f0b)
Fri, 18 Dec 2009 20:02:26 +0000 - __(Julian Hyde)__
Fix checkFile exception.

[58727bb](../../commit/58727bbf10ab1570c2c9d4a5b35470e16ceeeb20)
Sat, 28 Nov 2009 22:10:49 +0000 - __(Julian Hyde)__
Check in Paul Stoellberger's cube-discovery patch.

[11743a0](../../commit/11743a0fcd49727e2c18790ef6536f2288fb803b)
Mon, 23 Nov 2009 00:58:06 +0000 - __(Julian Hyde)__
Set connection's catalog before executing query.

[2a2b8a5](../../commit/2a2b8a576ef4473ace3793627a6066e0bde82fe6)
Tue, 20 Oct 2009 17:56:29 +0000 - __(Julian Hyde)__
Trailing space

[39ac535](../../commit/39ac535cc25933e54be48ead5d1b57fc997fd9c0)
Sun, 18 Oct 2009 23:50:49 +0000 - __(Julian Hyde)__
From 4.0.0.13109 onwards, mondrian.jar contains MondrianInprocProxy so we can obsolete ours.

[f8b0f40](../../commit/f8b0f40dc1ca34488d0dfe60b4d24d1c01388e87)
Sun, 18 Oct 2009 06:35:47 +0000 - __(Julian Hyde)__
Upgrade to latest version of mondrian from main branch.

[31702dd](../../commit/31702dd106c4d38d22c2cdd1b7cb6cf66e2055c9)
Sat, 17 Oct 2009 19:53:39 +0000 - __(Julian Hyde)__
Rename Olap4jUtil.uniqueNameToStringArray, and add unit test.

[1676e91](../../commit/1676e91e5313596844c840bbb1e1151c8e531912)
Sat, 17 Oct 2009 09:06:18 +0000 - __(Julian Hyde)__
Supply CatalogName property in an XMLA request implicitly if the CATALOG_NAME restriction is specified. But don't specify catalog otherwise. People expect to be able to get, say, all cubes from all catalogs and schemas. Fixes bug 2874977, "XMLA cube discovery".

[82ee139](../../commit/82ee13984513bd57693dff879dd4112c934230f7)
Sun, 4 Oct 2009 23:01:13 +0000 - __(Julian Hyde)__
Formatting.

[4482b52](../../commit/4482b52cfdf8420109882518a50069a9d4421e12)
Sat, 3 Oct 2009 23:35:54 +0000 - __(Julian Hyde)__
It's much more efficient to call ArrayMap(Map) than ArrayMap() followed by put or putAll. Fix Pair so that its hashCode is consistent with Map.Entry.

[ab3e951](../../commit/ab3e95198250d2d9d965cdd0824aa9251117278f)
Fri, 2 Oct 2009 13:59:50 +0000 - __(Luc Boudreau)__
Adds a test for the query validation.

[f277db7](../../commit/f277db7b7022dbb96235308ab93f32ab3bdb5c52)
Fri, 2 Oct 2009 13:39:07 +0000 - __(Luc Boudreau)__
Using the more efficient ArrayMap to hold on to the member properties.

[24050d7](../../commit/24050d713fc5f49ffe1cee966316f43003b86d6e)
Thu, 1 Oct 2009 00:57:29 +0000 - __(Julian Hyde)__
Add writeback API (Scenario, AllocationPolicy). Fix some references broken by the CellSetFormatter move.

[8e541bb](../../commit/8e541bb5289b062043750456bb351f8f2943de76)
Wed, 30 Sep 2009 20:20:56 +0000 - __(Luc Boudreau)__
Fixes issue https://sourceforge.net/tracker/?func=detail&aid=2589809&group_id=168953&atid=848534

[d344db5](../../commit/d344db53fea2f09d4898eb9527ba9be9e594604f)
Wed, 30 Sep 2009 19:31:10 +0000 - __(Luc Boudreau)__
Fixes issue https://sourceforge.net/projects/olap4j/forums/forum/577988/topic/3416385

[2dc01de](../../commit/2dc01de3f833f0dfb987cf150a02a2a591cfc11c)
Wed, 30 Sep 2009 19:07:29 +0000 - __(Luc Boudreau)__
Fixes issue https://sourceforge.net/tracker/?func=detail&aid=2784554&group_id=168953&atid=848537

[a6297b9](../../commit/a6297b927ee91f66af088a72ac3dca532386c05c)
Wed, 30 Sep 2009 13:05:54 +0000 - __(Luc Boudreau)__
Fixes a faulty handling of potentially null objects returned by the JVM API.

[2293cc2](../../commit/2293cc2028a3c74183c32ef1454f1821293c0855)
Tue, 18 Aug 2009 18:50:30 +0000 - __(Luc Boudreau)__
# The QueryDimension.SortOrder and QueryAxis.SortOrder will both be factored out and a single enumeration org.olap4j.query.SortOrder will remain. All relevant method setters and getters will be changed accordingly. We can't use Java's regular deprecation mechanism because of Java's no support for enumeration class heritage.

[8492829](../../commit/849282955abc4d360542d0f6258152e3034ef508)
Tue, 4 Aug 2009 17:38:06 +0000 - __(Luc Boudreau)__
Added sugar method to sort an axis by the default measure.

[a4cef50](../../commit/a4cef5078c7c35547bddf0dbe46c1b8685d9b5a2)
Tue, 4 Aug 2009 16:01:05 +0000 - __(Luc Boudreau)__
Adds a sort function to a QueryAxis. Axis can now be sorted by any literal expression.

[38cc365](../../commit/38cc365b44e5ca8688d3457f405359460df18467)
Thu, 30 Jul 2009 14:33:06 +0000 - __(Luc Boudreau)__
Added support for sorting on a dimension inclusions with the "break hierarchy" functionnality.

[8974576](../../commit/8974576e7daff2d674f78794a9a160e8d00f07fc)
Tue, 28 Jul 2009 15:27:15 +0000 - __(Luc Boudreau)__
Fixes issue https://sourceforge.net/tracker/?func=detail&aid=2105859&group_id=168953&atid=848534

[ff13ba2](../../commit/ff13ba2020e2e183e4fe85a617e72b22df40e3c7)
Wed, 22 Jul 2009 00:09:22 +0000 - __(Julian Hyde)__
Fix last checkFile error.

[72cf6cc](../../commit/72cf6cc6b0714932fd4bfff2c94a541d11443846)
Tue, 21 Jul 2009 16:23:03 +0000 - __(Luc Boudreau)__
Added a function to QueryDimension to allow hierarchization of the included member selections. Also added a regression test to OlapTest.

[f7363a7](../../commit/f7363a7eb91f74e5bb4d2729ade64a6c7a6268a9)
Mon, 20 Jul 2009 17:47:11 +0000 - __(Luc Boudreau)__
In the XMLA driver, Dimensions are inserted according to their correct ordinal value, as specified by DIMENSION_ORDINAL. Also added a test for that.

[13155f6](../../commit/13155f6f18abf53d5580e195f66f27b4be0fc001)
Mon, 20 Jul 2009 17:09:18 +0000 - __(Luc Boudreau)__
Fixes coding issues.

[919835e](../../commit/919835e9ecaa917ce4aa6d1d6b55fe65813f0598)
Mon, 20 Jul 2009 14:08:20 +0000 - __(Luc Boudreau)__
Holding back on last commit. Seems I broke stuff.

[653e62d](../../commit/653e62d6defa9dcfa6ba76de5c56d853ee611672)
Mon, 20 Jul 2009 14:03:19 +0000 - __(Luc Boudreau)__
Dimensions are inserted according to their correct ordinal value, as specified by DIMENSION_ORDINAL.

[ccf727d](../../commit/ccf727d04b0124c4941b594571f47a1809851c3e)
Mon, 20 Jul 2009 13:51:51 +0000 - __(Luc Boudreau)__
Added new suger method to place dimension on axis at arbitrary position.

[b1cc206](../../commit/b1cc2061e3cac1a07e1a74fd2c4597559391d117)
Thu, 16 Jul 2009 17:34:16 +0000 - __(Luc Boudreau)__
This commit fixes many issues with the XMLA driver. It used to load explicitely everything upon a cube object initialisation. Now it uses lazy collections.

[aceb6c2](../../commit/aceb6c2b878e88be2d3e3bf768c66b815eef8d3e)
Fri, 10 Jul 2009 19:17:20 +0000 - __(Julian Hyde)__
Upgrade to mondrian-3.1.1.12929 and commons-collections 3.2.

[d8f7cb5](../../commit/d8f7cb5a0725051910e9c6cbba86c04cea803223)
Fri, 10 Jul 2009 19:16:24 +0000 - __(Luc Boudreau)__
Fixed a race condition where data would remain in the HTTP proxy buffer upon an HTTP exception, so connections would remain in WAIT mode at the OS level for quite a while. We now attenpt to empty the input stream buffer just in case there is still something stuck in it.

[19e1f76](../../commit/19e1f76408bde08698af0a7ae2d044d72aa1335a)
Fri, 10 Jul 2009 04:35:11 +0000 - __(Julian Hyde)__
Upgrade to mondrian-3.1.1.12926, to fix (I hope) the two test exceptions on Hudson.

[ddaa646](../../commit/ddaa64618934e2c7cde88f9aeec0ee48b10c1fe3)
Wed, 8 Jul 2009 14:47:12 +0000 - __(Luc Boudreau)__
First off I updated the checkFile utility with the most recent version. It now checks for line lenghts and function wrapping.

[5028c7c](../../commit/5028c7c9d6979691bcbf05990a01909ebf379cc6)
Tue, 7 Jul 2009 22:53:48 +0000 - __(Luc Boudreau)__
Sets the project line ending default to Unix style so Eclipse won't mix up our files anymore.

[9e039d4](../../commit/9e039d432e4c7637dd41bda6f3402d078c5b8d64)
Tue, 7 Jul 2009 14:21:23 +0000 - __(Luc Boudreau)__
Added a test to make sure that there are no inherent links between a query model object and it's generated MDX object trees.

[ec7511e](../../commit/ec7511ec4f2247423d75edb890c06bd8d9d6baec)
Mon, 6 Jul 2009 09:50:16 +0000 - __(Julian Hyde)__
Comply with new checkFile rules.

[54196f0](../../commit/54196f08b871996ea0ef748daacfef8fb6308cea)
Thu, 2 Jul 2009 15:41:26 +0000 - __(Luc Boudreau)__
Fixed typo and added test case.

[7ef5ea4](../../commit/7ef5ea45f54ef411e9ef75c40d6d624030b040b4)
Thu, 2 Jul 2009 14:29:39 +0000 - __(Luc Boudreau)__
Override of equals() so objects can be properly used in Collections API.

[c4940c5](../../commit/c4940c5310e503ee3016f49998a30678a2d49c6a)
Tue, 30 Jun 2009 17:58:43 +0000 - __(Julian Hyde)__
Javadoc.

[9d30869](../../commit/9d308699d30902e9887c772fef9559e74d9268b8)
Tue, 30 Jun 2009 16:34:06 +0000 - __(Luc Boudreau)__
Fixed javadoc warnings and added override of hashCode for base XMLA metadata object.

[e4b2fdb](../../commit/e4b2fdbadbd2354be5bbf9944b5572a729bb9ff2)
Tue, 30 Jun 2009 03:06:10 +0000 - __(Julian Hyde)__
Comply with new checkFile rules. Remove calls to fold, and instead call assertEqualsVerbose, which calls fold internally.

[bf0c6dc](../../commit/bf0c6dc790457307852ba8bbc74c20a95af6872f)
Mon, 29 Jun 2009 15:30:28 +0000 - __(Luc Boudreau)__
Removed useless properties that might confuse users.

[920f52c](../../commit/920f52cc0c1efce5f0707c7b2f012b42f674aae9)
Thu, 25 Jun 2009 21:53:45 +0000 - __(Julian Hyde)__
Update PDF version of functional spec to match latest HTML.

[1e28ab7](../../commit/1e28ab7b651171f12003f28bc11fe84192e54a59)
Thu, 25 Jun 2009 21:35:50 +0000 - __(Julian Hyde)__
Enable svn id tag.

[d08b61d](../../commit/d08b61df7eadd1e692f5d8b93eea76223ec06898)
Thu, 25 Jun 2009 21:29:25 +0000 - __(Julian Hyde)__
Update code samples in olap4j functional spec.

[67f8a38](../../commit/67f8a386b03957235edae923ff77ea425e8c294c)
Wed, 24 Jun 2009 19:53:46 +0000 - __(Luc Boudreau)__
Implemented the listener pattern for the Query Model. Also fixed many bugs introduced by the last commit; line endings were not properly replaced on Windows systems.

[2321361](../../commit/2321361dec040f32eaf38c85f536a199e8160072)
Sat, 20 Jun 2009 05:52:40 +0000 - __(Julian Hyde)__
Comply with new checkFile rules. Remove use of 'fold' function from most regression tests.

[1d41474](../../commit/1d414744a6f1b13a1a9e99d643df876eb09dc400)
Thu, 11 Jun 2009 00:35:21 +0000 - __(Julian Hyde)__
Code formatting standards, in argument/parameter lists split over several lines.

[e76ddcb](../../commit/e76ddcbdc27f2d7a098bfa78e40aa6982182c625)
Tue, 26 May 2009 21:14:13 +0000 - __(Sherman Wood)__
deepCopy fix

[de77341](../../commit/de77341088e19ce482eabe804a15a39914ec8b34)
Mon, 25 May 2009 17:30:13 +0000 - __(Luc Boudreau)__
Added two methods to move a dimension along an axis. Created test cases. Fixed issue where calling set() on the QueryAxis's dimensions was loosing one object of the collection due to an overwrite.

[40daa57](../../commit/40daa57b8f3b65a6e3d32c61a09b85746579a182)
Fri, 22 May 2009 07:21:37 +0000 - __(Julian Hyde)__
Code formatting standards, in particular line length.

[4bd09de](../../commit/4bd09deb6015e5636bed292b3619f120519772be)
Tue, 19 May 2009 23:18:41 +0000 - __(Julian Hyde)__
Change enumerated values of Level.Type to uppercase, and add new values such as TIME_HALF_YEAR, TIME_HOURS, TIME_UNDEFINED.

[5b2a39d](../../commit/5b2a39d86183d02438f317e35b55c965fa27220c)
Tue, 19 May 2009 22:57:22 +0000 - __(Julian Hyde)__
Tests for compound filters

[4d5decd](../../commit/4d5decd958cfbce6223d3afb075b84f0c87d27d8)
Wed, 13 May 2009 16:23:47 +0000 - __(Luc Boudreau)__
There was a missing dependency in the Eclipse setup target.

[58f55e1](../../commit/58f55e1864bacdfaa3c2b196d5e7d3d6538f234c)
Wed, 13 May 2009 15:13:49 +0000 - __(Luc Boudreau)__
Fixed coding standard issue and removed some useless calls that were costly in calculations and returned values that were not used.

[aff327d](../../commit/aff327d0cce69f863209a011d1858eb98e45d12a)
Wed, 13 May 2009 14:56:05 +0000 - __(Luc Boudreau)__
Fixed an issue where there are no available catalogs. There was an IndexOutOfBoundsException, which is a no-no.

[2511d96](../../commit/2511d96065c4589c5512d07a36474862dde8a8fd)
Tue, 12 May 2009 21:31:18 +0000 - __(Julian Hyde)__
Generate java class containing version information, and use it to populate XMLA driver's version information.

[a7031a9](../../commit/a7031a90d2fe92509b8153f5d7c77db56a585f28)
Tue, 12 May 2009 07:42:17 +0000 - __(Julian Hyde)__
Rename org.olap4j.OlapException to org.olap4j.driver.xmla.XmlaHelper (a utility class doesn't belong in the main package). Make its methods non-static, and create an instance in each connection; MondrianOlap4jConnection.Helper already works that way.

[7de4b27](../../commit/7de4b27f15435ad665ccf5925ccb89aca515c350)
Tue, 12 May 2009 06:33:40 +0000 - __(Julian Hyde)__
Oops\!

[3dacbf5](../../commit/3dacbf5025b95ab9f3ba1025b07cdf56508cf364)
Tue, 12 May 2009 06:06:13 +0000 - __(Julian Hyde)__
Clean before building a release

[a1810a4](../../commit/a1810a4bfe95ad35dd80053e934c502e2071eb93)
Tue, 12 May 2009 06:05:49 +0000 - __(Julian Hyde)__
Ensure that SelectNode's filter axis is always set. If the axis has a null expression, that means there is no WHERE clause. You can assign the expression after tha axis has been created, including to null. Fixes bug 2789893, "SelectNode should have method setFilterAxis".

[b41879b](../../commit/b41879ba186b371ed4892583543b3a4c76f5540a)
Sat, 9 May 2009 00:16:54 +0000 - __(Julian Hyde)__
Oops!

[8d210c4](../../commit/8d210c4205d03e25812c0c80ef69e1333bdb3987)
Fri, 8 May 2009 22:21:44 +0000 - __(Julian Hyde)__
Improve javadoc in IdentifierNode. Add optimized implementation of read-only lists backed by arrays, and use it in IdentifierNode and XmlaOlap4jConnection.MetadataRequest.

[9c7ceab](../../commit/9c7ceabdcdc6f1207a53ac3e3cf4d06d3bdd1102)
Fri, 8 May 2009 21:20:01 +0000 - __(Julian Hyde)__
Fix javadoc and doczip.

[377e6ba](../../commit/377e6ba606802201b9da5128319cd16609a90462)
Fri, 8 May 2009 19:11:29 +0000 - __(Julian Hyde)__
Change license from CPL to EPL. Update copyright notice for files modified in 2009.

[a40886c](../../commit/a40886cf200b8c6aa12fcc201a41ca67b46ca046)
Fri, 8 May 2009 09:31:37 +0000 - __(Julian Hyde)__
Parser and parse tree model can now handle key path expressions, including compound keys such as [Customers].[State].&[CA]&[USA]. Fixes bug 2782515, "Parser cannot handle &key in member names".

[39cd229](../../commit/39cd229b836c65d5ba43c381a821aff3d585a6f6)
Thu, 7 May 2009 20:20:07 +0000 - __(Luc Boudreau)__
Fixed overwrite of classes files by Retroweaver.

[ecd743d](../../commit/ecd743d79faf05e53948016530ce5b3ca2741ae5)
Thu, 7 May 2009 18:46:49 +0000 - __(Luc Boudreau)__
Woops. Mondrian was compiled with JDK 1.6 =)

[329b911](../../commit/329b911df029d26d4d2437a65e25e6e2466d35a0)
Thu, 7 May 2009 18:22:49 +0000 - __(Luc Boudreau)__
Forgot to comit the SVN:IGNORE changes

[8ffd1b5](../../commit/8ffd1b5a1d3b6b519c492a5f6f3842d35f00be41)
Thu, 7 May 2009 18:21:45 +0000 - __(Luc Boudreau)__
Since Mondrian doesn't use olap4j SNAPSHOT builds, all tests fail. I included a custom built Mondrian library and deactivated the current Ivy one until Mondrian gets modified accordingly.

[50010d9](../../commit/50010d9be9d5bfa9f5e76db091e0cc86ef0e0c72)
Wed, 6 May 2009 13:03:36 +0000 - __(Luc Boudreau)__
Removed Cobertura tests. Performance is terrible. Will optimize later.

[5ef55f4](../../commit/5ef55f4f1884b6395a9e8cbad8a6c0bbb77f74b9)
Wed, 6 May 2009 12:49:40 +0000 - __(Luc Boudreau)__
typo...

[b6d0a08](../../commit/b6d0a08b2d1bef12bae96a925c0be37a66c24f53)
Wed, 6 May 2009 12:48:10 +0000 - __(Luc Boudreau)__
git-svn-id: https://olap4j.svn.sourceforge.net/svnroot/olap4j/trunk@221 c6a108a4-781c-0410-a6c6-c2d559e19af0

[592e1d4](../../commit/592e1d49a934dfdaf0261ee483a436f5fc73cd80)
Wed, 6 May 2009 12:46:59 +0000 - __(Luc Boudreau)__
Fixed hsql init stuff.

[20f7b87](../../commit/20f7b879dae552ced917fe65f025e3fae7b36ff0)
Wed, 6 May 2009 12:43:24 +0000 - __(Luc Boudreau)__
Fixed some remaining issues with Javadoc generation.

[6395648](../../commit/639564816fd08b63b1838c95f21b53aa0b152dab)
Wed, 6 May 2009 12:25:23 +0000 - __(Luc Boudreau)__
Should not be committed.

[0293aca](../../commit/0293aca75fd60c7a0985656d088b8b70767a2749)
Tue, 5 May 2009 20:27:02 +0000 - __(Luc Boudreau)__
Fixed issue with HSQLDB + Cobertura.

[d7df62c](../../commit/d7df62c0925ec056096a3dab71d36fd31f9e462f)
Tue, 5 May 2009 20:14:08 +0000 - __(Luc Boudreau)__
Changed dependencies order and fixed faulty delete task.

[33a4979](../../commit/33a4979d352c4ae95e966be737af144c1a8f6059)
Tue, 5 May 2009 16:47:53 +0000 - __(Luc Boudreau)__
Ant targets dependencies cleanup.

[e90d0e6](../../commit/e90d0e6c3e470f6797fb5df120a98b42cbc35619)
Tue, 5 May 2009 16:31:04 +0000 - __(Luc Boudreau)__
Removed clean in dist target.

[a58576a](../../commit/a58576abb79371a2beb7ae211b90a62c70d3f625)
Tue, 5 May 2009 16:18:41 +0000 - __(Luc Boudreau)__
Faulty comment.

[fdf58a5](../../commit/fdf58a54694be11f2426836e4523d3da8ac3bd89)
Tue, 5 May 2009 16:12:15 +0000 - __(Luc Boudreau)__
Needs basedir prefix.

[c5b187f](../../commit/c5b187fd98244a5690d238229c673658e0ad69ea)
Tue, 5 May 2009 15:56:28 +0000 - __(Luc Boudreau)__
Created a goal for CI builds.

[62f7395](../../commit/62f7395b17f630b20354c1700daf325f7f1ce00d)
Tue, 5 May 2009 15:51:06 +0000 - __(Luc Boudreau)__
Need to add this to repo for CI builds to work properly.

[93b1243](../../commit/93b1243cfbb6604a381c7d9e7a500a5742d9c31d)
Tue, 5 May 2009 15:26:11 +0000 - __(Luc Boudreau)__
Updated comment in example test properties.

[ee4bae1](../../commit/ee4bae1cf94b12ea3876f1a60d9dc980751d4395)
Tue, 5 May 2009 15:00:04 +0000 - __(Luc Boudreau)__
Ignoring libraries in SVN.

[4504961](../../commit/45049613ba7e0907c13d8e919996357347e3dfc7)
Tue, 5 May 2009 14:58:00 +0000 - __(Luc Boudreau)__
Applying ivyfication to project. (fingers crossed...)

[e33489c](../../commit/e33489c40f068d1b03571065c53151ced32e723d)
Sat, 2 May 2009 07:28:35 +0000 - __(Julian Hyde)__
In SimpleQuerySample, use Axis.axisOrdinal not Axis.ordinal(); fix driver class name; add example of using a CellSetFormatter.

[baf3322](../../commit/baf33224359fe3110178bd20cb3528d882a89a37)
Mon, 27 Apr 2009 23:24:46 +0000 - __(Julian Hyde)__
Fix XMLA driver where dimension type=Geography. See issue https://sourceforge.net/forum/message.php?msg_id=7301948 "j.l.AIOOBE 17 with Geography Dimension".

[fd57914](../../commit/fd57914bc4c669d86f35c4a5ed12ec72c42cecd4)
Sun, 19 Apr 2009 01:33:03 +0000 - __(Julian Hyde)__
Tests for CoordinateIterator; fix coding style.

[93fc515](../../commit/93fc51567f9d20daef8b80e5e3a3351d67e71606)
Sun, 19 Apr 2009 00:43:03 +0000 - __(Julian Hyde)__
Add CellSetFormatter, a couple of implementations, and a few tests.

[93d335a](../../commit/93d335a3b17a41bcda449e1abab0f745f6f0c76c)
Sun, 19 Apr 2009 00:35:20 +0000 - __(Julian Hyde)__
Update tests to match mondrian's latest capabilities. All tests now pass clean.

[0f5b0e9](../../commit/0f5b0e98732237f4d24fd514a4bb3e5b09e7840d)
Thu, 26 Mar 2009 14:36:51 +0000 - __(Luc Boudreau)__
Added a sortOrder() method to QueryDimension to sort the dimension members either in ascending or descending order.

[156558b](../../commit/156558bfe98ca01584164c79e0a9448bc28994d1)
Mon, 23 Mar 2009 18:18:28 +0000 - __(Luc Boudreau)__
Javadoc was useless since this method should be restricted to package access only. Reduced visibility and removed useless javadoc links.

[c6e27b1](../../commit/c6e27b1e7d9cb94b1d45415edf6201ccb6b64629)
Mon, 23 Mar 2009 16:41:06 +0000 - __(Luc Boudreau)__
Fixed lack of comments.

[d3cda6c](../../commit/d3cda6c50387cff4deb1953e46091adf4f896bdc)
Mon, 23 Mar 2009 16:30:21 +0000 - __(Luc Boudreau)__
Corrected conditional pattern.

[fd200e4](../../commit/fd200e47b3b69d02bf2626c11c414efe2e9908dd)
Mon, 23 Mar 2009 16:05:58 +0000 - __(Luc Boudreau)__
Performed some cleanup and refactoring. I just want to tidy things up before we work more seriously in this package.

[c29d55e](../../commit/c29d55e07c6d30278ca036409fb966e3517824ec)
Thu, 5 Mar 2009 19:12:55 +0000 - __(Luc Boudreau)__
Fixed code formating.

[4485f7c](../../commit/4485f7c18043c32539e029ee37c654d45e213f7d)
Thu, 5 Mar 2009 18:21:16 +0000 - __(Luc Boudreau)__
Made the Axis enumeration serializable and all the components of the org.olap4j.mdx package too, thus allowing their use through RPC.

[2f880a9](../../commit/2f880a934c206df2da90e4f08ff899be37c6b125)
Mon, 23 Feb 2009 20:23:25 +0000 - __(Julian Hyde)__
olap4j build: don't include java.sql.Driver twice in olap4j.jar; generate javadoc for XMLA driver; rename olap4j-sources.jar to olap4j-source.jar

[d6e5991](../../commit/d6e59917d51f0d8746bfbdfaaa2af2b348ee6267)
Sat, 21 Feb 2009 03:27:20 +0000 - __(Julian Hyde)__
Fix javadoc.

[49d19e0](../../commit/49d19e0fe808b93da11fbe43197a09ee00646301)
Fri, 20 Feb 2009 22:49:42 +0000 - __(Julian Hyde)__
Remove restriction on number of query axes; Axis used to be an enum, is now an interface. Deprecated Axis.UNUSED and Axis.NONE.

[43546cc](../../commit/43546cc8cae99e8e8895e746106c8de66543a656)
Fri, 20 Feb 2009 20:21:23 +0000 - __(Julian Hyde)__
Cosmetic changes & review comments for Luc's cache code.

[858f916](../../commit/858f9161b1a2f1d663aaea55fd87acc2683c8a96)
Mon, 9 Feb 2009 17:12:05 +0000 - __(Luc Boudreau)__
Fixes bug 2582046. All tests passed.

[ce2aa44](../../commit/ce2aa44d17e8b905a1d57613c792dd88d1c642e7)
Thu, 22 Jan 2009 19:36:33 +0000 - __(Julian Hyde)__
Fix XMLA driver bug 2527862, "Failed to get Hierarchy list for particular dimension".

[1dd1911](../../commit/1dd19114fb1c5a410b407e8f72167c5fa76c003b)
Tue, 20 Jan 2009 23:43:19 +0000 - __(Julian Hyde)__
Create META-INF/services/java.sql.Driver under classes so that driver discovery works in dev environment. Fix ANT_HOME and comments in buildJdk16.bat.

[c67dc06](../../commit/c67dc0658ae37c8a835d641f656c23b2e66d8c71)
Wed, 14 Jan 2009 14:26:48 +0000 - __(Luc Boudreau)__
Javadoc error.

[ef4594d](../../commit/ef4594d75d2ff374f3e8ce5836da30593a809f94)
Tue, 6 Jan 2009 01:55:06 +0000 - __(Julian Hyde)__
Add targets to generate olap4j-sources.jar and olap4j-javadoc.jar, artifacts that can be deployed to a maven2 repository alongside olap4j.jar and downloaded automatically by ivy.

[62b605a](../../commit/62b605a9140f2c73e9f9888b8419cb50c61fce95)
Thu, 18 Dec 2008 22:00:13 +0000 - __(Julian Hyde)__
Restore UNIX line endings

[4cdcda3](../../commit/4cdcda3cbe45eaf481186fc9474e0baaadef3340)
Tue, 16 Dec 2008 21:42:35 +0000 - __(Luc Boudreau)__
git-svn-id: https://olap4j.svn.sourceforge.net/svnroot/olap4j/trunk@137 c6a108a4-781c-0410-a6c6-c2d559e19af0

[da546b6](../../commit/da546b60f965fc88441526b8fef74e3c2b985b3f)
Tue, 25 Nov 2008 14:53:35 +0000 - __(Will Gorman)__
Unit test addition for mondrian bug 2026930, MondrianOlap4jMember.getCaption() throws exception

[f35e504](../../commit/f35e504cde65bff4131949cdf132ab59b3bb494d)
Wed, 12 Nov 2008 07:39:40 +0000 - __(Julian Hyde)__
Mainly code formatting. Fix how ConnectionTest.testXmlaCatalogParameter unwraps statements. Clean up DoubleSubmissionTestProxy by factoring out base class DelegatingTestProxy.

[9c590aa](../../commit/9c590aac4c4b7c44ae0fb717d9df14ec05f9482b)
Tue, 4 Nov 2008 20:24:50 +0000 - __(Julian Hyde)__
Fix a few typos in javadoc. Add testcase for bug 1868075. Include README in release, and convert to DOS format.

[bc2f3a1](../../commit/bc2f3a14c2478027b48a72deec90bb3b6dd1948a)
Mon, 3 Nov 2008 20:30:17 +0000 - __(Luc Boudreau)__
Code formatting issues

[485ff4b](../../commit/485ff4b54bc6260412a40577a6ffe27c68c557ef)
Mon, 3 Nov 2008 19:43:35 +0000 - __(Luc Boudreau)__
Code formatting issues

[1f5271c](../../commit/1f5271c31780be4fd5c55a617ea5b5aaf07f6f03)
Mon, 3 Nov 2008 19:15:04 +0000 - __(Luc Boudreau)__
Added the dependencies schema for archives.

[94e5496](../../commit/94e549629aab931b19cfec41409debcaee9bc7b1)
Mon, 3 Nov 2008 18:40:41 +0000 - __(Julian Hyde)__
Fix formatting in parser.

[95940ce](../../commit/95940ce31ea736b5c8b8dc79f978bf8dec3321d0)
Mon, 3 Nov 2008 18:36:30 +0000 - __(Luc Boudreau)__
I have double checked the dependencies tree and made sure there was nothing circular. I have also uploaded a schema to help you understand better the current dependencies. It's available here :

[3ee359a](../../commit/3ee359aae9eaa8d6b36b91b061e31efffebc4fb5)
Mon, 3 Nov 2008 17:39:10 +0000 - __(Luc Boudreau)__
Fixed an issue with the LS serializer usage that triggered an AbstractMethodError at runtime under certain environments. The parsing is now done old-school.

[0919416](../../commit/091941618ea37b31fec486a5cd9269db0bfbfe34)
Mon, 3 Nov 2008 16:43:04 +0000 - __(Luc Boudreau)__
I was able to create a new test for that and i also corrected the defect. The test makes sure that a given statement object doesn't send twice the same query.

[dfcb158](../../commit/dfcb158623e62d4d66f06deb20863965458274b5)
Sun, 2 Nov 2008 07:54:05 +0000 - __(Julian Hyde)__
Ensure that all Java files comply with checkFile coding standard.

[610aa2a](../../commit/610aa2ad84b2fbd327b07b116d5018b7473826c0)
Sun, 2 Nov 2008 07:43:12 +0000 - __(Julian Hyde)__
Update copyright notices. Ensure all Java files have keyword replacement.

[0a6f2dc](../../commit/0a6f2dc035cbbfd78d6cc356c9ac8e2df6edaf59)
Sun, 2 Nov 2008 07:34:39 +0000 - __(Julian Hyde)__
Fix bug 2032449, 'User Defined Dimension Properties'. Add builtin properties MEMBER_KEY, IS_PLACEHOLDERMEMBER, IS_DATAMEMBER. Special treatment for DEPTH property and Member.getDepth() method. Add test for reading members of parent-child hierarchy. Fix test now that mondrian has more variants of the Descendants function. Introduce interface XmlaOlap4jMemberBase, to allow commonality between various implementations of Member in XMLA driver.

[dd190c9](../../commit/dd190c9f592aabcaac082e954128a129d6f407db)
Sat, 1 Nov 2008 21:49:35 +0000 - __(Julian Hyde)__
Add Query.setNonEmpty/isNonEmpty (bug 1994488).

[4692256](../../commit/46922562fc25ddb6217ae20f66d896cd852bdfc4)
Thu, 30 Oct 2008 19:30:54 +0000 - __(Luc Boudreau)__
I reduced the visibility on a few classes of the XMLA driver because it's namespace got polluted by my careless declarations... :D

[7b3729c](../../commit/7b3729c89c54db56da6c225ecc53db2d42ef354a)
Wed, 29 Oct 2008 19:16:57 +0000 - __(Luc Boudreau)__
Made the fault string parsing more efficient and standard with the CellSet implementation way of doing it.

[7f766f2](../../commit/7f766f2b2e9ca92b3a37b102d514b374e272de4c)
Tue, 21 Oct 2008 13:34:39 +0000 - __(Luc Boudreau)__
Forgot to commit these in last commit.

[19f9d9d](../../commit/19f9d9de883823582a154da04880d81733715144)
Mon, 6 Oct 2008 16:46:14 +0000 - __(Luc Boudreau)__
Fixed issue 2018878. If a SOAP error is received from the server, it will be included in the exception that is thrown back at the end-user.

[3b1e745](../../commit/3b1e7456121a50750cf992a41932e86bff8aca7e)
Wed, 17 Sep 2008 23:52:40 +0000 - __(Luc Boudreau)__
Fixed missing conversion. The element absence returns null, but the correct value to send back is an empty string. It was fixed in CatalogSchemaHandler but not here.

[c8f266e](../../commit/c8f266e926901d0eb8a3f50290052233d13d45bb)
Mon, 15 Sep 2008 19:02:30 +0000 - __(Luc Boudreau)__
Fixed a faulty race condition and updated tests to detect it.

[81d65b0](../../commit/81d65b0b16e67bcf5ffb22987f0c36dc0f333ff9)
Mon, 15 Sep 2008 18:25:38 +0000 - __(Luc Boudreau)__
Fixed some comments.

[3c39d58](../../commit/3c39d584ffc638913ff5844ccd1601d67c8f20f0)
Mon, 15 Sep 2008 17:17:52 +0000 - __(Luc Boudreau)__
Fixed https://sourceforge.net/forum/forum.php?thread_id=2248227&forum_id=577988

[82777df](../../commit/82777df1a2a63016a06bdf3f8d5743d76f961c47)
Mon, 25 Aug 2008 01:18:27 +0000 - __(Julian Hyde)__
Back out revisions 110-113.

[bfe29fc](../../commit/bfe29fcad456cb9ee98b542358eefca659faf66d)
Thu, 21 Aug 2008 16:21:25 +0000 - __(Luc Boudreau)__
Fixed a faulty condition.

[edbb9f4](../../commit/edbb9f45ddf4d99590a718ccfdc6fe37a5751a45)
Thu, 21 Aug 2008 15:30:07 +0000 - __(Luc Boudreau)__
An invalid UTF character was present in the file. Fixed.

[b67641a](../../commit/b67641a05df566af333ac8bbe3b822581f4e32d8)
Tue, 19 Aug 2008 17:44:39 +0000 - __(Luc Boudreau)__
Created a messaging framework for Olap4j so that all messages and OlapExceptions can be easily localized, parametrized and build correctly by both Olap4j itself and implementing drivers.

[3108dd2](../../commit/3108dd2f1a7817da5019eee5b8b82c228aac6dbe)
Fri, 15 Aug 2008 15:44:32 +0000 - __(Luc Boudreau)__
First round of exceptions paths cleanup. This commit introduces an OlapExceptionHandler which was factored out of the XmlaOlap4jConnection class. I made sure that all OlapException get thrown through the helper in the xmla driver. Eventually, we could do the same with the rest of Olap4j.

[1e25fee](../../commit/1e25fee24d25bedad3b3bb552a4ee4f58f6bbb5b)
Thu, 14 Aug 2008 22:22:01 +0000 - __(Julian Hyde)__
Update source code to enforce coding standards (spaces, tabs, braces, etc.). No functionality changes.

[9c0fa92](../../commit/9c0fa92bd26fd0f8cafc28a22b2023cfb12ccf73)
Thu, 14 Aug 2008 18:42:30 +0000 - __(Julian Hyde)__
Oops

[7846791](../../commit/78467918711bce0dd94572e94aafcec4d613ef79)
Thu, 14 Aug 2008 18:38:06 +0000 - __(Julian Hyde)__
Add experimental org.olap4j.transform package, and method ParseTreeNode.deepCopy() (contributed by Etienne Dube)

[daeb88e](../../commit/daeb88edc8fc4efb5f35c65fee63be2bdd4d3623)
Mon, 11 Aug 2008 16:23:27 +0000 - __(Julian Hyde)__
Test case for mondrian bug 2046318, 'olap4j driver should throw OlapException on validate error'

[88e52dd](../../commit/88e52dd98ef4179832d0e7affd1709305fc98899)
Wed, 30 Jul 2008 13:21:20 +0000 - __(Luc Boudreau)__
Modified the access mechanism to the underlying connection object for member lookup optimization.

[2a32251](../../commit/2a3225116c7f6ab0990f41a5880361cb13d00597)
Tue, 29 Jul 2008 14:44:48 +0000 - __(Luc Boudreau)__
Added an optimization mechanism for the members lookup and a new signature to the Handler interface which receives a reference to the parent connection object.

[e8d3567](../../commit/e8d3567bc0c948a0bc2ff4bebe6a14f5146332a3)
Mon, 28 Jul 2008 20:10:36 +0000 - __(Luc Boudreau)__
Temporary fix for 2027796. This makes sure that XmlaOlap4jMembers are created because SSAS doesn't support multiple restraints at once on a members meta query. The solution has much space for optimization. See the bug thread for discussion at :

[bc26a47](../../commit/bc26a4766607188e356abc0e08c129afddd6e086)
Fri, 25 Jul 2008 18:25:13 +0000 - __(Will Gorman)__
fixed deadlock bug in Xmla Driver, added unit test to verify fix.  Also added generated java files and testclasses directory to svn:ignore.

[e1280ce](../../commit/e1280ce03a2931b068019bcc4071cf3324ebe38e)
Wed, 23 Jul 2008 18:25:06 +0000 - __(Julian Hyde)__
In XMLA driver, handle empty string value for PROPERTY_CONTENT_TYPE attribute (bug 2025638).

[f806186](../../commit/f806186f362fbb7fcfbb36903da19ff75f63ace1)
Wed, 23 Jul 2008 18:20:12 +0000 - __(Julian Hyde)__
In XMLA driver, treat empty strings for integer attributes as nulls.

[8f0c269](../../commit/8f0c269ae04fe23ce0d45a73136f152b09a01c1b)
Mon, 21 Jul 2008 13:46:46 +0000 - __(Luc Boudreau)__
Added back the Proxy static interface to maintain retro-compatibility but marked it as deprecated. This proxy only extends the new one.

[1d0e3a2](../../commit/1d0e3a25e776475337e7e729a840c4aa295b7e68)
Fri, 18 Jul 2008 17:59:55 +0000 - __(Luc Boudreau)__
Tons of performance enhancing modifications for the XML/A driver.

[5abb967](../../commit/5abb967d91f580bf053341b04ec418024b747ec8)
Mon, 9 Jun 2008 11:33:50 +0000 - __(Julian Hyde)__
Fix examples referring to driver names and connect strings.

[e4e7fbe](../../commit/e4e7fbe39dc05cb610bc9ef9ce029fcd33e53f31)
Sat, 31 May 2008 02:39:40 +0000 - __(Julian Hyde)__
Upgrade to xerces 2.6 (2.3 doesn't play well with jdk 1.6); update names of mondrian jars (since upgrade to ivy they no longer contain version numbers); OlapTest was wrongly assuming that Schema.getCubes() produces a list in a particular order.

[e201c97](../../commit/e201c97bcba0892cc4883733a0e89c5ef19f026b)
Tue, 27 May 2008 19:21:25 +0000 - __(Julian Hyde)__
Implement clearWarnings() as no-op (needed by apache-commons-dbcp); Make sure Level.getMembers() returns objects that implement Measure if applied to level in [Measures] dimension; Minor changes to web home page.

[babc6b2](../../commit/babc6b276301131f05b1d8e1f4ccddef6db7c3ca)
Mon, 21 Apr 2008 20:52:59 +0000 - __(Julian Hyde)__
Declare XMLA driver so that it can we loaded implicitly, without using 'Class.forName'. Requires JDBC 4.0 (JDK 1.6) and later.

[46213b1](../../commit/46213b1c79c9dd940d67388093d78b2746d5fb97)
Mon, 21 Apr 2008 20:23:46 +0000 - __(Julian Hyde)__
Fix bug in conversion of query model to olap4j parse tree; the bug meant that queries would lose their slicer.

[ecad30c](../../commit/ecad30cfaa63ebc1a0c511846207c4efc8b0ae29)
Thu, 17 Apr 2008 07:41:10 +0000 - __(Julian Hyde)__
Ensure that members of the Measures dimension implement Measure interface, and that list inclues calculated members defined against the cube.

[d7bfc03](../../commit/d7bfc03f85ee27307506c014c8f49ceb1e7f8852)
Sun, 13 Apr 2008 16:56:45 +0000 - __(Luc Boudreau)__
Fixed a missing exception handling.

[c0fa333](../../commit/c0fa33370887b9925797dbd893c7000aa05c1206)
Sun, 13 Apr 2008 08:52:20 +0000 - __(Julian Hyde)__
Change code to comply with code-formatting standard.

[8a3e97d](../../commit/8a3e97d68bd38138a11b72cf496874a0441188dd)
Sun, 13 Apr 2008 07:14:33 +0000 - __(Julian Hyde)__
Fix XMLA teste and XMLA driver for DBCP.

[22e6f32](../../commit/22e6f32716cd385ce2611349d1b359f1d7e8f599)
Sun, 13 Apr 2008 07:06:03 +0000 - __(Julian Hyde)__
Fix URL of Sourceforge logo in javadoc pages

[b9756d6](../../commit/b9756d6fca0104fb05bc1f2011abd892c32b9951)
Fri, 11 Apr 2008 15:04:20 +0000 - __(Luc Boudreau)__
Fixed a few code style issues, a result set which wasn't closed and some missing blocks.

[dc08e7c](../../commit/dc08e7ce3904fa2a528b4cdebd4bbf57b2e26089)
Wed, 9 Apr 2008 05:51:52 +0000 - __(Julian Hyde)__
Fix bug in connect-string parser (see mondrian bug 1938151)

[e2863ed](../../commit/e2863ed172385e9be9157b5ede1eec62b8aa41d9)
Thu, 3 Apr 2008 14:26:37 +0000 - __(Luc Boudreau)__
The following data types are now casted to their respective class :

[f537684](../../commit/f5376842ef112ffbc6d076719306ca9242a1b283)
Wed, 2 Apr 2008 09:55:08 +0000 - __(Julian Hyde)__
Fix more tests for dbcp.

[2496cad](../../commit/2496cad17a31bac3cf9205bf998f26043f43cd9d)
Wed, 2 Apr 2008 08:03:19 +0000 - __(Julian Hyde)__
Add test mode that wraps connections in a connection-pool (currently just apache commons-dbcp); fix xmla driver for JDK 1.4; fix connection-leak in test framework; make test framework work with commons-dbcp (but still some bugs with XMLA driver and commons-dbcp); collect into an enum all properties used by the test framework; more javadoc.

[e1b7a59](../../commit/e1b7a59f83d56e4b30153698be6f6dfb429b9e0b)
Tue, 1 Apr 2008 14:51:07 +0000 - __(Luc Boudreau)__
Added two parameters to the XMLA driver url.

[6a62a7d](../../commit/6a62a7deb043a20c444261276298bcd7bb240cce)
Wed, 26 Mar 2008 00:11:06 +0000 - __(Julian Hyde)__
Add OlapDataSource interface.

[af33fc9](../../commit/af33fc904a4efc56ab4a64001523edd3ce975069)
Fri, 14 Mar 2008 08:09:19 +0000 - __(Julian Hyde)__
Various fixes contributed by Tomek Grabon. Include DefaultMdxParser.cup in source distro; add xercesImpl.jar to path; in XMLA driver, explicitly pass <Catalog>; encode characters <, >, single and double quote in XML strings.

[567c871](../../commit/567c871a38da6e3fe4581c7e404e6c3c85dd9d17)
Mon, 10 Mar 2008 20:45:29 +0000 - __(Julian Hyde)__
Apply hierarchy lookup logic in one other place.

[667e875](../../commit/667e875ff4974c4542173a7ba6799663ce0d5cba)
Wed, 5 Mar 2008 22:03:00 +0000 - __(Julian Hyde)__
Oops. Didn't intend to check those in.

[94d2d1f](../../commit/94d2d1fb1f2250c7fb3500eca4f3afe4f12830e8)
Wed, 5 Mar 2008 21:36:54 +0000 - __(Julian Hyde)__
Enhance XMLA proxy used by testing infrastructure to include charset. XMLA driver tolerates if HierarchyInfo contains hierarchy's unique name rather than name.

[8b4b2e3](../../commit/8b4b2e38bdcde8935184f361d47808f3096eaf33)
Wed, 5 Mar 2008 20:42:11 +0000 - __(Julian Hyde)__
Add OlapConnection.setRoleName and .getRoleName. Dummy implementation in XMLA driver.

[dd4e9e6](../../commit/dd4e9e657c57b2974d126a04b9d209de15e31734)
Fri, 29 Feb 2008 22:56:28 +0000 - __(Julian Hyde)__
Set encoding for XMLA requests to 'UTF-8'; contributed by lucboudreau.

[81a26e5](../../commit/81a26e588a27a3ecdc70eaeb520b7193844a23c0)
Wed, 13 Feb 2008 20:36:10 +0000 - __(Will Gorman)__
Completed the implementation of the org.olap4j.query API

[1b5c317](../../commit/1b5c3171f208b958b9492fea003db8ca1c3e952d)
Thu, 7 Feb 2008 04:15:15 +0000 - __(Julian Hyde)__
Update spec and change log for release 0.9.4-svn72.

[9043264](../../commit/9043264da3c5d3f6f889087a75a5551e5d75a670)
Tue, 5 Feb 2008 19:46:03 +0000 - __(Julian Hyde)__
Up olap4j version; remove jars required by mondrian driver (now that it lives under mondrian).

[82b4b82](../../commit/82b4b82861a666a60a6828f5879612df689e53d0)
Tue, 5 Feb 2008 19:37:23 +0000 - __(Julian Hyde)__
XMLA driver now reads member corresponding to each measure, so that it can sort by ordinal.

[0582677](../../commit/0582677ed53295dbfeb9a9fe63ff8d209a969c72)
Tue, 5 Feb 2008 00:22:15 +0000 - __(Julian Hyde)__
Cube.getMeasures() returns members sorted by ordinal.

[091e638](../../commit/091e6380fbcf4a579aeaac3866be08fa5ddba374)
Sat, 2 Feb 2008 23:28:59 +0000 - __(Julian Hyde)__
Switch order of parameters to AxisNode (it's easier to write code if the bulky expression comes last). Fix 'jar' build on windows. Push up memory for unit test (in-process XMLA test is hungry).

[3e562cf](../../commit/3e562cf44900a2b8c20bf1a471d0cf89a2f3f942)
Fri, 1 Feb 2008 07:07:41 +0000 - __(Julian Hyde)__
XMLA driver: add member cache; call MDSCHEMA_MEMBERS with multiple unique-names. Hierarchy.getDefaultMember() now throws OlapException. XmlaTester caches connections.

[14ca2de](../../commit/14ca2de31e64b09c731b26bf53dec6e54c5d7c2a)
Thu, 24 Jan 2008 07:42:35 +0000 - __(Julian Hyde)__
XMLA driver: when constructing metadata request XML, encode restriction values

[afa7d80](../../commit/afa7d801f8467676f86eff85cd942a1417c96933)
Thu, 24 Jan 2008 07:21:25 +0000 - __(Julian Hyde)__
Add constructor for IdentifierNode which takes a list (convenient for calling with the result from IdentifierNode.parseIdentifier); Test case for building MDX parse tree programmatically and executing as query.

[aeffdfb](../../commit/aeffdfbadb59f0500c668b1e360bdd87503c58e0)
Thu, 24 Jan 2008 05:14:58 +0000 - __(Julian Hyde)__
Add support for Basic Authentication to XMLA driver (contributed by Luc Boudreau); add Base64 utilities (needed for Basic Authentication); fix MetadataTest for additional functions just added to mondrian.

[16524c8](../../commit/16524c875ec5a27813d4d0eab9702297879faac3)
Mon, 21 Jan 2008 14:13:36 +0000 - __(Julian Hyde)__
Oops

[21877d2](../../commit/21877d26f43b987063a913d0710993d5b8612aaf)
Mon, 21 Jan 2008 14:12:55 +0000 - __(Julian Hyde)__
Remove unused mondrian import

[dd62506](../../commit/dd625061904ecd17854a9db66248fdb8c63804c9)
Sun, 13 Jan 2008 02:18:47 +0000 - __(Julian Hyde)__
Fix XMLA driver for calculated members defined in query; and make hierarchies and members returned for the slicer consistent between XMLA and mondrian drivers.

[0428f8e](../../commit/0428f8e3fb941773e6b22d9d93a0a04723e982bf)
Thu, 10 Jan 2008 19:20:01 +0000 - __(Julian Hyde)__
Fix typo in javadoc

[c257ba6](../../commit/c257ba67f1f2ca31d10898e056d941f74ec967aa)
Thu, 10 Jan 2008 09:25:49 +0000 - __(Julian Hyde)__
Clarify what the slicer axis contains if the query has no WHERE clause; clarify Cell.getFormattedValue() if the value is NULL; and add testcase for query with no slicer.

[cbbba63](../../commit/cbbba63dd5d2be9226192675918ef4348ac7e985)
Thu, 10 Jan 2008 05:35:55 +0000 - __(Julian Hyde)__
Move olap4j driver for mondrian to mondrian code base.

[467484c](../../commit/467484c3e974f9ff6a05d4a44bddd1ebe62b8bb9)
Wed, 9 Jan 2008 08:34:03 +0000 - __(Julian Hyde)__
Enable keyword substitution for some source files.

[9bcadc8](../../commit/9bcadc87cbe084168d8f7d3ed3468d6f2ce700a3)
Wed, 9 Jan 2008 08:31:56 +0000 - __(Julian Hyde)__
XMLA driver now uses HTTP POST (some server's don't support GET)

[728d79c](../../commit/728d79cd0f0dccea4ab49595672b7dbe25dce801)
Sun, 6 Jan 2008 20:34:57 +0000 - __(Julian Hyde)__
Fix metadata test now mondrian has more functions

[ea8b51d](../../commit/ea8b51d3f173cff8e7974fba3d886f13f62b80b4)
Fri, 21 Dec 2007 23:55:30 +0000 - __(Julian Hyde)__
Update MetadataTest now mondrian has 2 extra functions; fix drill-through test for Derby's JDBC driver weirdness.

[6d9c74d](../../commit/6d9c74da00a59c52cc7fccab0e1a194f2a6bc9ac)
Tue, 18 Dec 2007 22:11:57 +0000 - __(Julian Hyde)__
Fix code examples in functional spec, and improve a few javadoc comments. ResultSet returned from Cell.drillThrough() now closes its connection and statement on close, thereby fixing a connection leak.

[84e5841](../../commit/84e5841c10f209c237b1bd930110120c3eb4ccf7)
Fri, 14 Dec 2007 01:07:28 +0000 - __(Julian Hyde)__
Oops: fix for JDK 1.5, and fix typo in ant classpath; update web home page for 0.9.3.

[179dd06](../../commit/179dd0692b632d6dbb71d8bc04d8d5ea18dcf0d1)
Thu, 13 Dec 2007 23:15:53 +0000 - __(Julian Hyde)__
Major progress on XMLA driver. All tests pass for XMLA driver (running against mondrian in-process, but nevertheless sending and receiving SOAP requests) and still pass for mondrian driver.

[5abb015](../../commit/5abb01590f58387bd6a728342e3d58e98bd26874)
Mon, 10 Dec 2007 21:43:24 +0000 - __(Julian Hyde)__
Regenerate PDF specification

[88d7976](../../commit/88d7976b021b83ae6a74897cb8fc8b8f6110cf5a)
Mon, 10 Dec 2007 21:38:03 +0000 - __(Julian Hyde)__
Make Property.Datatype a top-level enum; a few methods now throw OlapException; add methods to metadata classes.

[863a7c4](../../commit/863a7c42bee5309cc0a4470fc1b8f3869366da4f)
Sun, 2 Dec 2007 23:53:46 +0000 - __(Julian Hyde)__
Release 0.9.2.

[3df918e](../../commit/3df918eba5debb8376aca77211889214ecf6d19a)
Sun, 2 Dec 2007 10:42:19 +0000 - __(Julian Hyde)__
Fix release issues noted by John Sichi.

[74efed7](../../commit/74efed7ccd60528ae90439a4e025a2a67eb0511b)
Wed, 28 Nov 2007 04:03:57 +0000 - __(Julian Hyde)__
Website re-design.

[df5d08b](../../commit/df5d08bc3162a856c769cbcb9bda4c17138691b7)
Thu, 22 Nov 2007 03:41:45 +0000 - __(Julian Hyde)__
Fix build for JDK 1.6; check in mondrian jars built under JDK 1.5 (not JDK 1.6); add script to recursively switch to JDK 1.6 and invoke ant to build a couple of classes.

[640a790](../../commit/640a790e44153050330a7f3b37b6b4fb9b71e087)
Thu, 22 Nov 2007 03:31:13 +0000 - __(Julian Hyde)__
Latest spec in PDF format. Obsolete SpringSample.

[1ed37d9](../../commit/1ed37d9297c6e6f11a8682ed5c73ae3ebcf82d19)
Wed, 21 Nov 2007 12:09:46 +0000 - __(Julian Hyde)__
Check in necessary libraries. Mondrian as of change 10210. Remove dependency on spring. Revise README and up version number.

[dcd6e4d](../../commit/dcd6e4d7a18f03a3a1048acdac60624f00fcce7c)
Wed, 21 Nov 2007 01:57:47 +0000 - __(Julian Hyde)__
Enable keyword expansion.

[ba29a42](../../commit/ba29a42943c8c82232c3eb28772e8db7fe6882c2)
Wed, 21 Nov 2007 01:39:37 +0000 - __(Julian Hyde)__
Move test parameters out of source code and into a file. 'test.properties' should be the only file you need to edit. Provided 'test.properties.example'. First cut README.txt.

[6909980](../../commit/69099807ad07d28cc2d621ae97179a235893b0e3)
Sun, 18 Nov 2007 01:33:12 +0000 - __(Julian Hyde)__
Trip down which packages/classes appear in public API javadoc.

[d65a89e](../../commit/d65a89ed9e3ec3dfe265e068a14dd00aacdf0353)
Sun, 18 Nov 2007 01:13:26 +0000 - __(Julian Hyde)__
Specify thread-safety, timeout, statement cancel; Specify compliance levels; Specify access control; Specify internationalization; add OlapConnection.get/setLocale. Add diagram of object model to spec; Ensure that every public class and method has a full javadoc description. Implement timeout and cancel in mondrian driver; and test. Implement drill-through in mondrian driver; and test. Make CellSetAxis implement Iterable<Position>. Remove some unneeded methods from org.olap4j.type package. Rename SLICER to FILTER.

[ad23a24](../../commit/ad23a24885dfd583b002cf9bac5eb848a9fe379c)
Tue, 13 Nov 2007 09:46:46 +0000 - __(Julian Hyde)__
Oops, fix for JDK 1.5.

[96d3da3](../../commit/96d3da3816e8c32d6238612d09f84f28f8e72b81)
Tue, 13 Nov 2007 09:37:16 +0000 - __(Julian Hyde)__
Lots of tests for metadata classes, and implement corresponding functionality in mondrian driver. Review org.olap4j.type package, and remove/hide several methods. Add API for creating validator and validating an MDX parse tree. Test that non-unique axis names cause a validation error.

[fb680a8](../../commit/fb680a8ee4cd620171ae0eb0c55ecd5ca5af6e35)
Mon, 12 Nov 2007 04:14:39 +0000 - __(Julian Hyde)__
Javadoc fixes. One javadoc target documents the public API, another to check all javadoc including against private methods and test classes.

[b066d25](../../commit/b066d259357da518825e4050460a91d90204c9da)
Mon, 5 Nov 2007 09:06:12 +0000 - __(John V. Sichi)__
Add unit test for construction of an MDX statement via language object model.

[f689ac9](../../commit/f689ac98e539162455c8f0b61e9693643bc95041)
Tue, 23 Oct 2007 21:42:48 +0000 - __(Julian Hyde)__
Document and revise OlapDatabaseMetaData methods; write unit tests for all methods; implement methods in mondrian's olap4j driver.

[6ff349d](../../commit/6ff349d5fe1dc44a6eed62bae66d502d76a1f658)
Sun, 7 Oct 2007 03:06:58 +0000 - __(John V. Sichi)__
Delete "ant test" task, add one for removing site-specifics from unit test code.

[10d9791](../../commit/10d9791d3105149dc45540ea61cee83b0ffce0f7)
Thu, 27 Sep 2007 05:11:22 +0000 - __(John V. Sichi)__
Implement ant "test" target.

[c1f9f16](../../commit/c1f9f168d873293500c552ae28929c8913b1dee9)
Thu, 27 Sep 2007 00:54:27 +0000 - __(John V. Sichi)__
Just testing commit privileges.

[b200add](../../commit/b200add749650c261fa7067b1cc91cd59eaa4bda)
Sat, 22 Sep 2007 02:29:21 +0000 - __(Julian Hyde)__
Correct typos in spec; eliminate QueryAxis.xxxDimension and QueryDimension.xxxSelection methods; property methods now take a Property, not the property name

[b07c768](../../commit/b07c768da02466fd2c9e0a3cdacfdd6dedcd0d0c)
Fri, 21 Sep 2007 09:14:34 +0000 - __(Julian Hyde)__
A few more parameters to OlapDatabaseMetaData.getXxx methods

[9c858b6](../../commit/9c858b635fc592f71aac3559161b878d0ff94ea9)
Fri, 21 Sep 2007 07:13:14 +0000 - __(Julian Hyde)__
Add parameters to OlapDatabaseMetaData.getXxx methods

[a3d5480](../../commit/a3d548027c78571e918adc9a7c909d8ffd9139a3)
Thu, 20 Sep 2007 10:46:43 +0000 - __(Julian Hyde)__
Javadoc and spec tasks, mostly adding descriptions of metadata elements.

[190887c](../../commit/190887c180b600bcb7d181860d2c3c9bc199080a)
Tue, 18 Sep 2007 07:34:57 +0000 - __(Julian Hyde)__
Add task list, and minor spec/javadoc edits

[b072f2e](../../commit/b072f2e55b8f971def6a7696d5c82531c26b4e79)
Wed, 15 Aug 2007 06:55:30 +0000 - __(Julian Hyde)__
Fix build under JDK 1.5

[12500d1](../../commit/12500d1e9c8997b368753956e68faa1e790a4f56)
Thu, 9 Aug 2007 07:50:03 +0000 - __(Julian Hyde)__
Add skeleton olap4j driver for XMLA

[da3fcfa](../../commit/da3fcfae4be396573f8016ac781b804401400368)
Wed, 25 Jul 2007 05:56:33 +0000 - __(Julian Hyde)__
Add junit.jar

[b099238](../../commit/b099238f227a1094c1f0fdbcba90bb03de33a716)
Fri, 15 Jun 2007 03:53:14 +0000 - __(Julian Hyde)__
Add notion of ParseRegion to parser and parse tree nodes.

[7a691f0](../../commit/7a691f0c3956051c013f39acf6eedc4661d8dfc8)
Fri, 15 Jun 2007 02:23:07 +0000 - __(Julian Hyde)__
In mondrian driver, create dual implementations of key JDBC objects to allow running against both JDBC 3.0 (JDK 1.5) and JDBC 4.0 (JDK 1.6); add OlapWrapper for JDBC 3.0 users, who have no access to java.sql.Wrapper methods; implement OlapPreparedStatement in mondrian driver.

[bddae98](../../commit/bddae982a65dda0d5bcdb39f973f8db820e47a98)
Thu, 14 Jun 2007 20:37:17 +0000 - __(Julian Hyde)__
Get mondrian classes from parallel source directory, not log file (for now); fix 'ant clean' mayhem; add 'javadoc-with-ydoc' ant target.

[5a0bb18](../../commit/5a0bb18b9bfd245da914fc4a6545c12e907d0cb9)
Sun, 10 Jun 2007 23:09:28 +0000 - __(Julian Hyde)__
Enable keyword substitution

[33a2089](../../commit/33a20898025144a4a5df32b3d6d688010a7a4129)
Sun, 10 Jun 2007 22:53:45 +0000 - __(Julian Hyde)__
Obsolete Database and Olap4j; update spec with parse tree model changes; enable keyword substitutions

[5783fb2](../../commit/5783fb25911d5ebae77590869a1d8e157c1ca6c5)
Sun, 10 Jun 2007 18:31:17 +0000 - __(Julian Hyde)__
Add query model (from James Dixon), parser and parser object model.

[5d7b66c](../../commit/5d7b66c95c87a10f25cd9d4ffeaa21735d8cbd3a)
Fri, 25 May 2007 22:34:38 +0000 - __(Julian Hyde)__
Rename OlapResultSet to CellSet, OlapResultSetMetaData to CellSetMetaData, OlapResultAxis to CellSetAxis, ResultCell to Cell, ResultSetPosition to Position; add implementation of olap4j against mondrian, and a unit test

[b456d38](../../commit/b456d3899dd539206ca978124fb9e5272098dec5)
Tue, 24 Oct 2006 22:48:56 +0000 - __(Julian Hyde)__
Add NamedSet and Database; clean up schema result set defns in spec; switch some uses of SQLException to OlapException

[6c87da1](../../commit/6c87da115ff5b223b5a81dd298b2ace45fa221fb)
Tue, 24 Oct 2006 08:41:02 +0000 - __(Julian Hyde)__
Changes to org.olap4j package regarding result sets, and org.olap4j.metadata package regarding Database, Catalog, Schema

[a6a9c85](../../commit/a6a9c858404a95cad6d2d3895971053535cb1c83)
Sat, 21 Oct 2006 00:35:30 +0000 - __(Julian Hyde)__
Add items from 2006/10/20 progress meeting to spec.

[37c47b3](../../commit/37c47b340d419b8d28c6459db77f96dac9f600ab)
Thu, 19 Oct 2006 20:06:48 +0000 - __(Julian Hyde)__
Barry Klawans's changes to Transform component of olap4j specification

[b4ed0f5](../../commit/b4ed0f52b18016fcc6422daaab4ee13062ea3853)
Wed, 18 Oct 2006 00:32:56 +0000 - __(Julian Hyde)__
Move JDK topic from 'open issues' to body of spec; clean up some links.

[2e2dae3](../../commit/2e2dae31fbd19cdd0c230d5f6de74cef40669385)
Wed, 18 Oct 2006 00:31:42 +0000 - __(Julian Hyde)__
Include PNG images in doczip.

[511578c](../../commit/511578ced21791dd12e14bb0c8060ce58cfce667)
Mon, 16 Oct 2006 05:37:08 +0000 - __(Julian Hyde)__
Update specification and java code, especially metadata (org.olap4j.metadata) and type (org.olap4j.type) sections, and description of how to interoperate with connection pools.

[90f80a3](../../commit/90f80a3c762c05ddf75f99ff2363c7a0bbe7f322)
Thu, 12 Oct 2006 10:07:40 +0000 - __(Julian Hyde)__
Add table of contents functional spec, and some content regarding drivers, connections and statements; Add ant target to generate javadoc in PDF format; Rename OlapResult to OlapResultSet.

[7c0072c](../../commit/7c0072c587a1bc995c6523cd2ff4974e486a1380)
Mon, 4 Sep 2006 01:57:41 +0000 - __(Julian Hyde)__
Tweak stylesheet.

[4572039](../../commit/45720399a72a1ef5d3bcb69c1c4de9608dc96e00)
Sun, 3 Sep 2006 09:23:08 +0000 - __(Julian Hyde)__
Release 0.5 (continued).

[f2da514](../../commit/f2da51497ee28d9390fa1d2dad0d2636b7d63690)
Sun, 3 Sep 2006 08:57:45 +0000 - __(Julian Hyde)__
Release 0.5.

[ab979cd](../../commit/ab979cd3552894245b5bfb1932fee5d4ff0d2114)
Sun, 3 Sep 2006 03:15:36 +0000 - __(Julian Hyde)__
Add script to deploy doc to website; add retroweaver jars.

[ce4de02](../../commit/ce4de0291f4068792c718d5e537b4f86f18f0a09)
Sat, 2 Sep 2006 14:43:48 +0000 - __(Sherman Wood)__
Ignore generated olap4j.lib

[efacbb5](../../commit/efacbb5666927bd3926968c93927127243547a96)
Sat, 2 Sep 2006 14:42:11 +0000 - __(Sherman Wood)__
Allow Ant binzip to work: commented out retroweaver and javadoc targets

[d69ef67](../../commit/d69ef67a944e7a24fa868e67d5da18d87db872f1)
Sat, 2 Sep 2006 14:12:52 +0000 - __(Sherman Wood)__
Eclipse JDK 1.5 and build path settings

[0f8fa3b](../../commit/0f8fa3bc5667f139b7235c3e3da791ac743d14d2)
Sat, 2 Sep 2006 14:08:27 +0000 - __(Sherman Wood)__
Initial checkin from Julian.
