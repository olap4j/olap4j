/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.*;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.impl.Olap4jUtil;
import static org.olap4j.driver.xmla.XmlaOlap4jUtil.*;
import org.olap4j.metadata.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Implementation of {@link org.olap4j.CellSet}
 * for XML/A providers.
 *
 * <p>This class has sub-classes which implement JDBC 3.0 and JDBC 4.0 APIs;
 * it is instantiated using {@link Factory#newCellSet}.</p>
 *
 * @author jhyde
 * @version $Id$
 * @since May 24, 2007
 */
abstract class XmlaOlap4jCellSet implements CellSet {
    final XmlaOlap4jStatement olap4jStatement;
    protected boolean closed;
    private XmlaOlap4jCellSetMetaData metaData;
    private final Map<Integer, Cell> cellMap =
        new HashMap<Integer, Cell>();
    private final List<XmlaOlap4jCellSetAxis> axisList =
        new ArrayList<XmlaOlap4jCellSetAxis>();
    private final List<CellSetAxis> immutableAxisList =
        Olap4jUtil.cast(Collections.unmodifiableList(axisList));
    private XmlaOlap4jCellSetAxis filterAxis;
    private static final boolean DEBUG = false;

    private static final List<String> standardProperties = Arrays.asList(
        "UName", "Caption", "LName", "LNum", "DisplayInfo");

    /**
     * Creates an XmlaOlap4jCellSet.
     *
     * @param olap4jStatement Statement
     */
    XmlaOlap4jCellSet(
        XmlaOlap4jStatement olap4jStatement)
    {
        assert olap4jStatement != null;
        this.olap4jStatement = olap4jStatement;
        this.closed = false;
    }

    /**
     * Returns the error-handler.
     *
     * @return Error handler
     */
    private XmlaHelper getHelper() {
        return olap4jStatement.olap4jConnection.helper;
    }

    /**
     * Gets response from the XMLA request and populates cell set axes and cells
     * with it.
     *
     * @throws OlapException on error
     */
    void populate() throws OlapException {
        byte[] bytes = olap4jStatement.getBytes();

        Document doc;
        try {
            doc = parse(bytes);
        } catch (IOException e) {
            throw getHelper().createException(
                "error creating CellSet", e);
        } catch (SAXException e) {
            throw getHelper().createException(
                "error creating CellSet", e);
        }
        // <SOAP-ENV:Envelope>
        //   <SOAP-ENV:Header/>
        //   <SOAP-ENV:Body>
        //     <xmla:ExecuteResponse>
        //       <xmla:return>
        //         <root>
        //           (see below)
        //         </root>
        //       <xmla:return>
        //     </xmla:ExecuteResponse>
        //   </SOAP-ENV:Body>
        // </SOAP-ENV:Envelope>
        final Element envelope = doc.getDocumentElement();
        if (DEBUG) {
            System.out.println(XmlaOlap4jUtil.toString(doc, true));
        }
        assert envelope.getLocalName().equals("Envelope");
        assert envelope.getNamespaceURI().equals(SOAP_NS);
        Element body =
            findChild(envelope, SOAP_NS, "Body");
        Element fault =
            findChild(body, SOAP_NS, "Fault");
        if (fault != null) {
            /*
        <SOAP-ENV:Fault>
            <faultcode>SOAP-ENV:Client.00HSBC01</faultcode>
            <faultstring>XMLA connection datasource not found</faultstring>
            <faultactor>Mondrian</faultactor>
            <detail>
                <XA:error xmlns:XA="http://mondrian.sourceforge.net">
                    <code>00HSBC01</code>
                    <desc>The Mondrian XML: Mondrian Error:Internal
                        error: no catalog named 'LOCALDB'</desc>
                </XA:error>
            </detail>
        </SOAP-ENV:Fault>
             */
            // TODO: log doc to logfile
            throw getHelper().createException(
                "XMLA provider gave exception: "
                + XmlaOlap4jUtil.prettyPrint(fault));
        }
        Element executeResponse =
            findChild(body, XMLA_NS, "ExecuteResponse");
        Element returnElement =
            findChild(executeResponse, XMLA_NS, "return");
        // <root> has children
        //   <xsd:schema/>
        //   <OlapInfo>
        //     <CubeInfo>
        //       <Cube>
        //         <CubeName>FOO</CubeName>
        //       </Cube>
        //     </CubeInfo>
        //     <AxesInfo>
        //       <AxisInfo/> ...
        //     </AxesInfo>
        //   </OlapInfo>
        //   <Axes>
        //      <Axis>
        //        <Tuples>
        //      </Axis>
        //      ...
        //   </Axes>
        //   <CellData>
        //      <Cell/>
        //      ...
        //   </CellData>
        final Element root =
            findChild(returnElement, MDDATASET_NS, "root");

        if (olap4jStatement instanceof XmlaOlap4jPreparedStatement) {
            this.metaData =
                ((XmlaOlap4jPreparedStatement) olap4jStatement)
                    .cellSetMetaData;
        } else {
            this.metaData = createMetaData(root);
        }

        // todo: use CellInfo element to determine mapping of cell properties
        // to XML tags
        /*
                        <CellInfo>
                            <Value name="VALUE"/>
                            <FmtValue name="FORMATTED_VALUE"/>
                            <FormatString name="FORMAT_STRING"/>
                        </CellInfo>
         */

        final Element axesNode = findChild(root, MDDATASET_NS, "Axes");

        // First pass, gather up a list of member unique names to fetch
        // all at once.
        //
        // NOTE: This approach allows the driver to fetch a large number
        // of members in one round trip, which is much more efficient.
        // However, if the axis has a very large number of members, the map
        // may use too much memory. This is an unresolved issue.
        final MetadataReader metadataReader =
            metaData.cube.getMetadataReader();
        final Map<String, XmlaOlap4jMember> memberMap =
            new HashMap<String, XmlaOlap4jMember>();
        List<String> uniqueNames = new ArrayList<String>();
        for (Element axisNode : findChildren(axesNode, MDDATASET_NS, "Axis")) {
            final Element tuplesNode =
                findChild(axisNode, MDDATASET_NS, "Tuples");

            for (Element tupleNode
                : findChildren(tuplesNode, MDDATASET_NS, "Tuple"))
            {
                for (Element memberNode
                    : findChildren(tupleNode, MDDATASET_NS, "Member"))
                {
                    final String uname = stringElement(memberNode, "UName");
                    uniqueNames.add(uname);
                }
            }
        }

        // Fetch all members on all axes. Hopefully it can all be done in one
        // round trip, or they are in cache already.
        metadataReader.lookupMembersByUniqueName(uniqueNames, memberMap);

        // Second pass, populate the axis.
        final Map<Property, Object> propertyValues =
            new HashMap<Property, Object>();
        for (Element axisNode : findChildren(axesNode, MDDATASET_NS, "Axis")) {
            final String axisName = axisNode.getAttribute("name");
            final Axis axis = lookupAxis(axisName);
            final ArrayList<Position> positions = new ArrayList<Position>();
            final XmlaOlap4jCellSetAxis cellSetAxis =
                new XmlaOlap4jCellSetAxis(
                    this, axis, Collections.unmodifiableList(positions));
            if (axis.isFilter()) {
                filterAxis = cellSetAxis;
            } else {
                axisList.add(cellSetAxis);
            }
            final Element tuplesNode =
                findChild(axisNode, MDDATASET_NS, "Tuples");
            for (Element tupleNode
                : findChildren(tuplesNode, MDDATASET_NS, "Tuple"))
            {
                final List<Member> members = new ArrayList<Member>();
                for (Element memberNode
                    : findChildren(tupleNode, MDDATASET_NS, "Member"))
                {
                    String hierarchyName =
                        memberNode.getAttribute("Hierarchy");
                    final String uname = stringElement(memberNode, "UName");
                    XmlaOlap4jMemberBase member = memberMap.get(uname);
                    if (member == null) {
                        final String caption =
                            stringElement(memberNode, "Caption");
                        final int lnum = integerElement(memberNode, "LNum");
                        final Hierarchy hierarchy =
                            lookupHierarchy(metaData.cube, hierarchyName);
                        final Level level = hierarchy.getLevels().get(lnum);
                        member = new XmlaOlap4jSurpriseMember(
                            this, level, hierarchy, lnum, caption, uname);
                    }
                    propertyValues.clear();
                    for (Element childNode : childElements(memberNode)) {
                        XmlaOlap4jCellSetMemberProperty property =
                            ((XmlaOlap4jCellSetAxisMetaData)
                                cellSetAxis.getAxisMetaData()).lookupProperty(
                                hierarchyName,
                                childNode.getLocalName());
                        if (property != null) {
                            String value = childNode.getTextContent();
                            propertyValues.put(property, value);
                        }
                    }
                    if (!propertyValues.isEmpty()) {
                        member =
                            new XmlaOlap4jPositionMember(
                                member, propertyValues);
                    }
                    members.add(member);
                }
                positions.add(
                    new XmlaOlap4jPosition(
                        members, positions.size()));
            }
        }

        // olap4j requires a filter axis even if XMLA does not return one. If
        // XMLA does not return one, presumably there was no WHERE clause and
        // therefore the filter axis has a single position containing 0 members
        if (filterAxis == null) {
            filterAxis =
                new XmlaOlap4jCellSetAxis(
                    this,
                    Axis.FILTER,
                    Collections.<Position>singletonList(
                        new XmlaOlap4jPosition(
                            Collections.<Member>emptyList(), 0)));
        }

        final Element cellDataNode = findChild(root, MDDATASET_NS, "CellData");
        for (Element cell : findChildren(cellDataNode, MDDATASET_NS, "Cell")) {
            propertyValues.clear();
            final int cellOrdinal =
                Integer.valueOf(cell.getAttribute("CellOrdinal"));
            final Object value = getTypedValue(cell);
            final String formattedValue = stringElement(cell, "FmtValue");
            final String formatString = stringElement(cell, "FormatString");
            Olap4jUtil.discard(formatString);
            for (Element element : childElements(cell)) {
                String tag = element.getLocalName();
                final Property property =
                    metaData.propertiesByTag.get(tag);
                if (property != null) {
                    propertyValues.put(property, element.getTextContent());
                }
            }
            cellMap.put(
                cellOrdinal,
                new XmlaOlap4jCell(
                    this,
                    cellOrdinal,
                    value,
                    formattedValue,
                    propertyValues));
        }
    }

    /**
     * Returns the value of a cell, cast to the appropriate Java object type
     * corresponding to the XML schema (XSD) type of the value.
     *
     * <p>The value type must conform to XSD definitions of the XML element. See
     * <a href="http://books.xmlschemata.org/relaxng/relax-CHP-19.html">RELAX
     * NG, Chapter 19</a> for a full list of possible data types.
     *
     * <p>This method does not currently support all types; must numeric types
     * are supported, but no dates are yet supported. Those not supported
     * fall back to Strings.
     *
     * @param cell The cell of which we want the casted object.
     * @return The object with a correct value.
     * @throws OlapException if any error is encountered while casting the cell
     * value
     */
    private Object getTypedValue(Element cell) throws OlapException {
        Element elm = findChild(cell, MDDATASET_NS, "Value");
        if (elm == null) {
            // Cell is null.
            return null;
        }

        // The object type is contained in xsi:type attribute.
        String type = elm.getAttribute("xsi:type");
        try {
            if (type.equals("xsd:int")) {
                return XmlaOlap4jUtil.intElement(cell, "Value");
            } else if (type.equals("xsd:integer")) {
                return XmlaOlap4jUtil.integerElement(cell, "Value");
            } else if (type.equals("xsd:double")) {
                return XmlaOlap4jUtil.doubleElement(cell, "Value");
            } else if (type.equals("xsd:float")) {
                return XmlaOlap4jUtil.floatElement(cell, "Value");
            } else if (type.equals("xsd:long")) {
                return XmlaOlap4jUtil.longElement(cell, "Value");
            } else if (type.equals("xsd:boolean")) {
                return XmlaOlap4jUtil.booleanElement(cell, "Value");
            } else {
                return XmlaOlap4jUtil.stringElement(cell, "Value");
            }
        } catch (Exception e) {
            throw getHelper().createException(
                "Error while casting a cell value to the correct java type for"
                + " its XSD type " + type,
                e);
        }
    }

    /**
     * Creates metadata for a cell set, given the DOM of the XMLA result.
     *
     * @param root Root node of XMLA result
     * @return Metadata describing this cell set
     * @throws OlapException on error
     */
    private XmlaOlap4jCellSetMetaData createMetaData(Element root)
        throws OlapException
    {
        final Element olapInfo =
            findChild(root, MDDATASET_NS, "OlapInfo");
        final Element cubeInfo =
            findChild(olapInfo, MDDATASET_NS, "CubeInfo");
        final Element cubeNode =
            findChild(cubeInfo, MDDATASET_NS, "Cube");
        final Element cubeNameNode =
            findChild(cubeNode, MDDATASET_NS, "CubeName");
        final String cubeName = gatherText(cubeNameNode);

        // REVIEW: If there are multiple cubes with the same name, we should
        // qualify by catalog and schema. Currently we just take the first.
        XmlaOlap4jCube cube =
            lookupCube(
                olap4jStatement.olap4jConnection.olap4jDatabaseMetaData,
                cubeName);
        if (cube == null) {
            throw getHelper().createException(
                "Internal error: cube '" + cubeName + "' not found");
        }
        // REVIEW: We should not modify the connection. It is not safe, because
        // connection might be shared between multiple statements with different
        // cubes. Caller should call
        //
        // connection.setCatalog(
        //   cellSet.getMetaData().getCube().getSchema().getCatalog().getName())
        //
        // before doing metadata queries.
        try {
            this.olap4jStatement.olap4jConnection.setCatalog(
                cube.getSchema().getCatalog().getName());
        } catch (SQLException e) {
            throw getHelper().createException(
                "Internal error: setting catalog '"
                + cube.getSchema().getCatalog().getName()
                + "' caused error");
        }
        final Element axesInfo =
            findChild(olapInfo, MDDATASET_NS, "AxesInfo");
        final List<Element> axisInfos =
            findChildren(axesInfo, MDDATASET_NS, "AxisInfo");
        final List<CellSetAxisMetaData> axisMetaDataList =
            new ArrayList<CellSetAxisMetaData>();
        XmlaOlap4jCellSetAxisMetaData filterAxisMetaData = null;
        for (Element axisInfo : axisInfos) {
            final String axisName = axisInfo.getAttribute("name");
            Axis axis = lookupAxis(axisName);
            final List<Element> hierarchyInfos =
                findChildren(axisInfo, MDDATASET_NS, "HierarchyInfo");
            final List<Hierarchy> hierarchyList =
                new ArrayList<Hierarchy>();
            /*
            <OlapInfo>
                <AxesInfo>
                    <AxisInfo name="Axis0">
                        <HierarchyInfo name="Customers">
                            <UName name="[Customers].[MEMBER_UNIQUE_NAME]"/>
                            <Caption name="[Customers].[MEMBER_CAPTION]"/>
                            <LName name="[Customers].[LEVEL_UNIQUE_NAME]"/>
                            <LNum name="[Customers].[LEVEL_NUMBER]"/>
                            <DisplayInfo name="[Customers].[DISPLAY_INFO]"/>
                        </HierarchyInfo>
                    </AxisInfo>
                    ...
                </AxesInfo>
                <CellInfo>
                    <Value name="VALUE"/>
                    <FmtValue name="FORMATTED_VALUE"/>
                    <FormatString name="FORMAT_STRING"/>
                </CellInfo>
            </OlapInfo>
             */
            final List<XmlaOlap4jCellSetMemberProperty> propertyList =
                new ArrayList<XmlaOlap4jCellSetMemberProperty>();
            for (Element hierarchyInfo : hierarchyInfos) {
                final String hierarchyName = hierarchyInfo.getAttribute("name");
                Hierarchy hierarchy = lookupHierarchy(cube, hierarchyName);
                hierarchyList.add(hierarchy);
                for (Element childNode : childElements(hierarchyInfo)) {
                    String tag = childNode.getLocalName();
                    if (standardProperties.contains(tag)) {
                        continue;
                    }
                    final String propertyUniqueName =
                        childNode.getAttribute("name");
                    final XmlaOlap4jCellSetMemberProperty property =
                        new XmlaOlap4jCellSetMemberProperty(
                            propertyUniqueName,
                            hierarchy,
                            tag);
                    propertyList.add(property);
                }
            }
            final XmlaOlap4jCellSetAxisMetaData axisMetaData =
                new XmlaOlap4jCellSetAxisMetaData(
                    olap4jStatement.olap4jConnection,
                    axis,
                    hierarchyList,
                    propertyList);
            if (axis.isFilter()) {
                filterAxisMetaData = axisMetaData;
            } else {
                axisMetaDataList.add(axisMetaData);
            }
        }
        if (filterAxisMetaData == null) {
            filterAxisMetaData =
                new XmlaOlap4jCellSetAxisMetaData(
                    olap4jStatement.olap4jConnection,
                    Axis.FILTER,
                    Collections.<Hierarchy>emptyList(),
                    Collections.<XmlaOlap4jCellSetMemberProperty>emptyList());
        }
        final Element cellInfo =
            findChild(olapInfo, MDDATASET_NS, "CellInfo");
        List<XmlaOlap4jCellProperty> cellProperties =
            new ArrayList<XmlaOlap4jCellProperty>();
        for (Element element : childElements(cellInfo)) {
            cellProperties.add(
                new XmlaOlap4jCellProperty(
                    element.getLocalName(),
                    element.getAttribute("name")));
        }
        return
            new XmlaOlap4jCellSetMetaData(
                olap4jStatement,
                cube,
                filterAxisMetaData,
                axisMetaDataList,
                cellProperties);
    }

    /**
     * Looks up a cube among all of the schemas in all of the catalogs
     * in this connection.
     *
     * <p>If there are several with the same name, returns the first.
     *
     * @param databaseMetaData Database metadata
     * @param cubeName Cube name
     * @return Cube, or null if not found
     * @throws OlapException on error
     */
    private XmlaOlap4jCube lookupCube(
        XmlaOlap4jDatabaseMetaData databaseMetaData,
        String cubeName) throws OlapException
    {
        for (XmlaOlap4jCatalog catalog
            : databaseMetaData.getCatalogObjects())
        {
            for (Schema schema : catalog.getSchemas()) {
                for (Cube cube : schema.getCubes()) {
                    if (cubeName.equals(cube.getName())) {
                        return (XmlaOlap4jCube) cube;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Looks up a hierarchy in a cube with a given name or, failing that, a
     * given unique name. Throws if not found.
     *
     * @param cube Cube
     * @param hierarchyName Name (or unique name) of hierarchy.
     * @return Hierarchy
     * @throws OlapException on error
     */
    private Hierarchy lookupHierarchy(XmlaOlap4jCube cube, String hierarchyName)
        throws OlapException
    {
        Hierarchy hierarchy = cube.getHierarchies().get(hierarchyName);
        if (hierarchy == null) {
            for (Hierarchy hierarchy1 : cube.getHierarchies()) {
                if (hierarchy1.getUniqueName().equals(hierarchyName)) {
                    hierarchy = hierarchy1;
                    break;
                }
            }
            if (hierarchy == null) {
                throw getHelper().createException(
                    "Internal error: hierarchy '" + hierarchyName
                    + "' not found in cube '" + cube.getName() + "'");
            }
        }
        return hierarchy;
    }

    /**
     * Looks up an Axis with a given name.
     *
     * @param axisName Name of axis
     * @return Axis
     */
    private Axis lookupAxis(String axisName) {
        if (axisName.startsWith("Axis")) {
            final Integer ordinal =
                Integer.valueOf(axisName.substring("Axis".length()));
            return Axis.Factory.forOrdinal(ordinal);
        } else {
            return Axis.FILTER;
        }
    }

    public CellSetMetaData getMetaData() {
        return metaData;
    }

    public Cell getCell(List<Integer> coordinates) {
        return getCellInternal(coordinatesToOrdinal(coordinates));
    }

    public Cell getCell(int ordinal) {
        return getCellInternal(ordinal);
    }

    public Cell getCell(Position... positions) {
        if (positions.length != getAxes().size()) {
            throw new IllegalArgumentException(
                "cell coordinates should have dimension " + getAxes().size());
        }
        List<Integer> coords = new ArrayList<Integer>(positions.length);
        for (Position position : positions) {
            coords.add(position.getOrdinal());
        }
        return getCell(coords);
    }

    /**
     * Returns a cell given its ordinal.
     *
     * @param pos Ordinal
     * @return Cell
     * @throws IndexOutOfBoundsException if ordinal is not in range
     */
    private Cell getCellInternal(int pos) {
        final Cell cell = cellMap.get(pos);
        if (cell == null) {
            if (pos < 0 || pos >= maxOrdinal()) {
                throw new IndexOutOfBoundsException();
            } else {
                // Cell is within bounds, but is not held in the cache because
                // it has no value. Manufacture a cell with an empty value.
                return new XmlaOlap4jCell(
                    this, pos, null, "",
                    Collections.<Property, Object>emptyMap());
            }
        }
        return cell;
    }

    /**
     * Returns a string describing the maximum coordinates of this cell set;
     * for example "2, 3" for a cell set with 2 columns and 3 rows.
     *
     * @return description of cell set bounds
     */
    private String getBoundsAsString() {
        StringBuilder buf = new StringBuilder();
        int k = 0;
        for (CellSetAxis axis : getAxes()) {
            if (k++ > 0) {
                buf.append(", ");
            }
            buf.append(axis.getPositionCount());
        }
        return buf.toString();
    }

    public List<CellSetAxis> getAxes() {
        return immutableAxisList;
    }

    public CellSetAxis getFilterAxis() {
        return filterAxis;
    }

    /**
     * Returns the ordinal of the last cell in this cell set. This is the
     * product of the cardinalities of all axes.
     *
     * @return ordinal of last cell in cell set
     */
    private int maxOrdinal() {
        int modulo = 1;
        for (CellSetAxis axis : axisList) {
            modulo *= axis.getPositionCount();
        }
        return modulo;
    }

    public List<Integer> ordinalToCoordinates(int ordinal) {
        List<CellSetAxis> axes = getAxes();
        final List<Integer> list = new ArrayList<Integer>(axes.size());
        int modulo = 1;
        for (CellSetAxis axis : axes) {
            int prevModulo = modulo;
            modulo *= axis.getPositionCount();
            list.add((ordinal % modulo) / prevModulo);
        }
        if (ordinal < 0 || ordinal >= modulo) {
            throw new IndexOutOfBoundsException(
                "Cell ordinal " + ordinal
                + ") lies outside CellSet bounds ("
                + getBoundsAsString() + ")");
        }
        return list;
    }

    public int coordinatesToOrdinal(List<Integer> coordinates) {
        List<CellSetAxis> axes = getAxes();
        if (coordinates.size() != axes.size()) {
            throw new IllegalArgumentException(
                "Coordinates have different dimension " + coordinates.size()
                    + " than axes " + axes.size());
        }
        int modulo = 1;
        int ordinal = 0;
        int k = 0;
        for (CellSetAxis axis : axes) {
            final Integer coordinate = coordinates.get(k++);
            if (coordinate < 0 || coordinate >= axis.getPositionCount()) {
                throw new IndexOutOfBoundsException(
                    "Coordinate " + coordinate
                    + " of axis " + k
                    + " is out of range ("
                    + getBoundsAsString() + ")");
            }
            ordinal += coordinate * modulo;
            modulo *= axis.getPositionCount();
        }
        return ordinal;
    }

    public boolean next() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void close() throws SQLException {
        this.closed = true;
    }

    public boolean wasNull() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public byte getByte(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public short getShort(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getInt(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public long getLong(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public float getFloat(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public double getDouble(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(
        int columnIndex, int scale) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Date getDate(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Time getTime(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public byte getByte(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public short getShort(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getInt(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public long getLong(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public float getFloat(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public double getDouble(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(
        String columnLabel, int scale) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Date getDate(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Time getTime(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public InputStream getUnicodeStream(String columnLabel)
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int findColumn(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isBeforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isAfterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void beforeFirst() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void afterLast() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean first() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean last() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean absolute(int row) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean relative(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getType() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getConcurrency() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBigDecimal(
        int columnIndex, BigDecimal x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTimestamp(
        int columnIndex, Timestamp x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(
        int columnIndex, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(
        int columnIndex, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(
        int columnIndex, Reader x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateObject(
        int columnIndex, Object x, int scaleOrLength) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNull(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean(
        String columnLabel, boolean x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBigDecimal(
        String columnLabel, BigDecimal x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateString(String columnLabel, String x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBytes(String columnLabel, byte x[]) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateTimestamp(
        String columnLabel, Timestamp x) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(
        String columnLabel, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(
        String columnLabel, InputStream x, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(
        String columnLabel, Reader reader, int length) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateObject(
        String columnLabel, Object x, int scaleOrLength) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(
        int columnIndex, Map<String, Class<?>> map) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Clob getClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Array getArray(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Object getObject(
        String columnLabel, Map<String, Class<?>> map) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Ref getRef(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Clob getClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Array getArray(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(
        int columnIndex, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(
        String columnLabel, Calendar cal) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public URL getURL(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public URL getURL(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    // implement Wrapper

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /**
     * Implementation of {@link Member} for a member which is not present
     * in the cube (probably because the member is a calculated member
     * defined in the query).
     */
    private static class XmlaOlap4jSurpriseMember
        implements XmlaOlap4jMemberBase
    {
        private final XmlaOlap4jCellSet cellSet;
        private final Level level;
        private final Hierarchy hierarchy;
        private final int lnum;
        private final String caption;
        private final String uname;

        /**
         * Creates an XmlaOlap4jSurpriseMember.
         *
         * @param cellSet Cell set
         * @param level Level
         * @param hierarchy Hierarchy
         * @param lnum Level number
         * @param caption Caption
         * @param uname Member unique name
         */
        XmlaOlap4jSurpriseMember(
            XmlaOlap4jCellSet cellSet,
            Level level,
            Hierarchy hierarchy,
            int lnum,
            String caption,
            String uname)
        {
            this.cellSet = cellSet;
            this.level = level;
            this.hierarchy = hierarchy;
            this.lnum = lnum;
            this.caption = caption;
            this.uname = uname;
        }

        public final XmlaOlap4jCube getCube() {
            return cellSet.metaData.cube;
        }

        public final XmlaOlap4jConnection getConnection() {
            return getCatalog().olap4jDatabaseMetaData.olap4jConnection;
        }

        public final XmlaOlap4jCatalog getCatalog() {
            return getCube().olap4jSchema.olap4jCatalog;
        }

        public Map<Property, Object> getPropertyValueMap() {
            return Collections.emptyMap();
        }

        public NamedList<? extends Member> getChildMembers()
        {
            return Olap4jUtil.emptyNamedList();
        }

        public int getChildMemberCount() {
            return 0;
        }

        public Member getParentMember() {
            return null;
        }

        public Level getLevel() {
            return level;
        }

        public Hierarchy getHierarchy() {
            return hierarchy;
        }

        public Dimension getDimension() {
            return hierarchy.getDimension();
        }

        public Type getMemberType() {
            return Type.UNKNOWN;
        }

        public boolean isAll() {
            return false; // FIXME
        }

        public boolean isChildOrEqualTo(Member member) {
            return false; // FIXME
        }

        public boolean isCalculated() {
            return false; // FIXME
        }

        public int getSolveOrder() {
            return 0; // FIXME
        }

        public ParseTreeNode getExpression() {
            return null;
        }

        public List<Member> getAncestorMembers() {
            return Collections.emptyList(); // FIXME
        }

        public boolean isCalculatedInQuery() {
            return true; // probably
        }

        public Object getPropertyValue(Property property) {
            return null;
        }

        public String getPropertyFormattedValue(Property property) {
            return null;
        }

        public void setProperty(Property property, Object value)
        {
            throw new UnsupportedOperationException();
        }

        public NamedList<Property> getProperties() {
            return Olap4jUtil.emptyNamedList();
        }

        public int getOrdinal() {
            return -1; // FIXME
        }

        public boolean isHidden() {
            return false;
        }

        public int getDepth() {
            return lnum;
        }

        public Member getDataMember() {
            return null;
        }

        public String getName() {
            return caption;
        }

        public String getUniqueName() {
            return uname;
        }

        public String getCaption(Locale locale) {
            return caption;
        }

        public String getDescription(Locale locale) {
            return null;
        }
    }
}

// End XmlaOlap4jCellSet.java
