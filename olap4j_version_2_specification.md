# Specification: olap4j version 2.0

## Introduction

Goals of olap4j version 2.0 are:
* Support metadata and API changes in recent SSAS (Microsoft SQL Server Analysis Services) and XMLA (XML for Analysis)
* Support metadata and API changes in Mondrian version 4

We aim to be backwards compatible in the same way that each JDBC release is backwards compatible:
* Applications developed against earlier versions will work against 2
* Drivers supporting version 2 will also serve as 1.x drivers (JDBC version 4 broke this rule, and it was painful for driver developers)
* Some work will be required to convert a version 1.x driver to a version 2 driver

Timescale:
* Beta in Q3 2013
* Production in Q4 2013

## Features

Existing bugs, features and pull requests
* https://github.com/olap4j/olap4j/issues/4 Integrate hierarchy structure
* TODO: review feature requests at SF.net http://sourceforge.net/p/olap4j/feature-requests
* TODO: review bugs at SF.net http://sourceforge.net/p/olap4j/bugs
* Measure group names http://sourceforge.net/p/olap4j/discussion/577988/thread/781635f9

