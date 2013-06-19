/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
    */
package org.olap4j.driver.xmla;

import org.olap4j.*;
import org.olap4j.test.TestContext;

import junit.framework.TestCase;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.*;
import java.sql.*;
import java.util.*;


/**
 * Test class for XmlaOlap4jCellSet
 *
 */
public class XmlaOlap4jCellSetTest extends TestCase {

    public void testTypedValues()
        throws SQLException, ClassNotFoundException,
        NoSuchMethodException, InvocationTargetException,
        InstantiationException, IllegalAccessException
    {
        Map<String, Class> valueTags = new HashMap<String, Class>();

        valueTags.put(
            "<Value xsi:type=\"xsd:boolean\">true</Value>",
            Boolean.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:double\">39431.6712</Value>",
            Double.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:int\">3943</Value>",
            Integer.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:integer\">39431</Value>",
            BigInteger.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:positiveInteger\">39431</Value>",
            BigInteger.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:decimal\">39431.6712</Value>",
            BigDecimal.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:short\">3943</Value>",
            Short.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:float\">39431.6712</Value>",
            Float.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:long\">39431</Value>",
            Long.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:byte\">31</Value>",
            Byte.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:unsignedByte\">3943</Value>",
            Short.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:unsignedShort\">39431</Value>",
            Integer.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:unsignedLong\">39431.6712</Value>",
            BigDecimal.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:unsignedInt\">39431</Value>",
            Long.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:string\">39431</Value>",
            String.class);
        valueTags.put(
            "<Value xsi:type=\"xsd:UNKNOWN\">Unknown</Value>",
            String.class);

        MockOlap4jStatement statement = new MockOlap4jStatement(
            (XmlaOlap4jConnection) new XmlaTester(TestContext.instance())
                .createConnection());

        XmlaOlap4jCellSet cellSet = new StubbedOlap4jCellSet(statement);

        for (String value : valueTags.keySet()) {
            statement.setResponse(
                templateResponse.replace(
                    "${VALUE}", value));

            cellSet.populate();
            assertEquals(
                "Incorrect datatype conversion for value tag: \n"
                + value + "\n",
                valueTags.get(value),
                cellSet.getCell(0).getValue().getClass());
        }
    }


    class MockOlap4jStatement extends XmlaOlap4jStatement {

        private String response;

        MockOlap4jStatement(XmlaOlap4jConnection olap4jConnection) {
            super(olap4jConnection);
        }

        void setResponse(String response) {
            this.response = response;
        }

        byte[] getBytes() throws OlapException {
            return response.getBytes();
        }

        public void closeOnCompletion() throws SQLException {
        }

        public boolean isCloseOnCompletion() throws SQLException {
            return false;
        }
    }

    class StubbedOlap4jCellSet extends XmlaOlap4jCellSet {

        public StubbedOlap4jCellSet(
            XmlaOlap4jStatement olap4jStatement)
        {
            super(olap4jStatement);
        }

        public RowId getRowId(
            int columnIndex) throws SQLException
        {
            return null;
        }

        public RowId getRowId(
            String columnLabel) throws SQLException
        {
            return null;
        }

        public void updateRowId(
            int columnIndex, RowId x) throws SQLException
        {
        }

        public void updateRowId(
            String columnLabel, RowId x) throws SQLException
        {
        }

        public int getHoldability() throws SQLException
        {
            return 0;
        }

        public boolean isClosed() throws SQLException
        {
            return false;
        }

        public void updateNString(
            int columnIndex, String nString) throws SQLException
        {
        }

        public void updateNString(
            String columnLabel, String nString) throws SQLException
        {
        }

        public void updateNClob(
            int columnIndex, NClob nClob) throws SQLException
        {
        }

        public void updateNClob(
            String columnLabel, NClob nClob) throws SQLException
        {
        }

        public NClob getNClob(
            int columnIndex) throws SQLException
        {
            return null;
        }

        public NClob getNClob(
            String columnLabel) throws SQLException
        {
            return null;
        }

        public SQLXML getSQLXML(
            int columnIndex) throws SQLException
        {
            return null;
        }

        public SQLXML getSQLXML(
            String columnLabel) throws SQLException
        {
            return null;
        }

        public void updateSQLXML(
            int columnIndex, SQLXML xmlObject) throws SQLException
        {
        }

        public void updateSQLXML(
            String columnLabel, SQLXML xmlObject) throws SQLException
        {
        }

        public String getNString(
            int columnIndex) throws SQLException
        {
            return null;
        }

        public String getNString(
            String columnLabel) throws SQLException
        {
            return null;
        }

        public Reader getNCharacterStream(
            int columnIndex) throws SQLException
        {
            return null;
        }

        public Reader getNCharacterStream(
            String columnLabel) throws SQLException
        {
            return null;
        }

        public void updateNCharacterStream(
            int columnIndex, Reader x, long length) throws SQLException
        {
        }

        public void updateNCharacterStream(
            String columnLabel, Reader reader, long length) throws SQLException
        {
        }

        public void updateAsciiStream(
            int columnIndex, InputStream x, long length) throws SQLException
        {
        }

        public void updateBinaryStream(
            int columnIndex, InputStream x, long length) throws SQLException
        {
        }

        public void updateCharacterStream(
            int columnIndex, Reader x, long length) throws SQLException
        {
        }

        public void updateAsciiStream(
            String columnLabel, InputStream x, long length) throws SQLException
        {
        }

        public void updateBinaryStream(
            String columnLabel, InputStream x, long length) throws SQLException
        {
        }

        public void updateCharacterStream(
            String columnLabel, Reader reader, long length) throws SQLException
        {
        }

        public void updateBlob(
            int columnIndex, InputStream inputStream,
            long length) throws SQLException
        {
        }

        public void updateBlob(
            String columnLabel, InputStream inputStream,
            long length) throws SQLException
        {
        }

        public void updateClob(
            int columnIndex, Reader reader, long length) throws SQLException
        {
        }

        public void updateClob(
            String columnLabel, Reader reader, long length) throws SQLException
        {
        }

        public void updateNClob(
            int columnIndex, Reader reader, long length) throws SQLException
        {
        }

        public void updateNClob(
            String columnLabel, Reader reader, long length) throws SQLException
        {
        }

        public void updateNCharacterStream(
            int columnIndex, Reader x) throws SQLException
        {
        }

        public void updateNCharacterStream(
            String columnLabel, Reader reader) throws SQLException
        {
        }

        public void updateAsciiStream(
            int columnIndex, InputStream x) throws SQLException
        {
        }

        public void updateBinaryStream(
            int columnIndex, InputStream x) throws SQLException
        {
        }

        public void updateCharacterStream(
            int columnIndex, Reader x) throws SQLException
        {
        }

        public void updateAsciiStream(
            String columnLabel, InputStream x) throws SQLException
        {
        }

        public void updateBinaryStream(
            String columnLabel, InputStream x) throws SQLException
        {
        }

        public void updateCharacterStream(
            String columnLabel, Reader reader) throws SQLException
        {
        }

        public void updateBlob(
            int columnIndex, InputStream inputStream) throws SQLException
        {
        }

        public void updateBlob(
            String columnLabel, InputStream inputStream) throws SQLException
        {
        }

        public void updateClob(
            int columnIndex, Reader reader) throws SQLException
        {
        }

        public void updateClob(
            String columnLabel, Reader reader) throws SQLException
        {
        }

        public void updateNClob(
            int columnIndex, Reader reader) throws SQLException
        {
        }

        public void updateNClob(
            String columnLabel, Reader reader) throws SQLException
        {
        }

        public <T> T getObject(
            int columnIndex, Class<T> type) throws SQLException
        {
            return null;
        }

        public <T> T getObject(
            String columnLabel, Class<T> type) throws SQLException
        {
            return null;
        }
    }


    private static final String templateResponse =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
        + "           <soap:Header/>\n"
        + "           <soap:Body>n"
        + "<xmla:ExecuteResponse xmlns:xmla=\"urn:schemas-microsoft-com:xml-analysis\">\n"
        + "  <xmla:return>\n"
        + "    <root xmlns=\"urn:schemas-microsoft-com:xml-analysis:mddataset\" "
        + " xmlns:EX=\"urn:schemas-microsoft-com:xml-analysis:exception\" "
        + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
        + "      <xsd:schema elementFormDefault=\"qualified\" "
        + "targetNamespace=\"urn:schemas-microsoft-com:xml-analysis:mddataset\""
        + " xmlns=\"urn:schemas-microsoft-com:xml-analysis:mddataset\" "
        + " xmlns:sql=\"urn:schemas-microsoft-com:xml-sql\" "
        + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
        + "        <xsd:complexType name=\"MemberType\">\n"
        + "          <xsd:sequence>\n"
        + "            <xsd:element name=\"UName\" type=\"xsd:string\"/>\n"
        + "            <xsd:element name=\"Caption\" type=\"xsd:string\"/>\n"
        + "            <xsd:element name=\"LName\" type=\"xsd:string\"/>\n"
        + "            <xsd:element name=\"LNum\" type=\"xsd:unsignedInt\"/>\n"
        + "            <xsd:element name=\"DisplayInfo\" type=\"xsd:unsignedInt\"/>\n"
        + "            <xsd:sequence maxOccurs=\"unbounded\" minOccurs=\"0\">\n"
        + "              <xsd:any maxOccurs=\"unbounded\" processContents=\"lax\"/>\n"
        + "            </xsd:sequence>\n"
        + "          </xsd:sequence>\n"
        + "          <xsd:attribute name=\"Hierarchy\" type=\"xsd:string\"/>\n"
        + "        </xsd:complexType>\n"
        + "        <xsd:complexType name=\"PropType\">\n"
        + "          <xsd:attribute name=\"name\" type=\"xsd:string\"/>\n"
        + "        </xsd:complexType>\n"
        + "        <xsd:complexType name=\"TupleType\">\n"
        + "          <xsd:sequence maxOccurs=\"unbounded\">\n"
        + "            <xsd:element name=\"Member\" type=\"MemberType\"/>\n"
        + "          </xsd:sequence>\n"
        + "        </xsd:complexType>\n"
        + "        <xsd:complexType name=\"MembersType\">\n"
        + "          <xsd:sequence maxOccurs=\"unbounded\">\n"
        + "            <xsd:element name=\"Member\" type=\"MemberType\"/>\n"
        + "          </xsd:sequence>\n"
        + "          <xsd:attribute name=\"Hierarchy\" type=\"xsd:string\"/>\n"
        + "        </xsd:complexType>\n"
        + "        <xsd:complexType name=\"TuplesType\">\n"
        + "          <xsd:sequence maxOccurs=\"unbounded\">\n"
        + "            <xsd:element name=\"Tuple\" type=\"TupleType\"/>\n"
        + "          </xsd:sequence>\n"
        + "        </xsd:complexType>\n"
        + "        <xsd:complexType name=\"CrossProductType\">\n"
        + "          <xsd:sequence>\n"
        + "            <xsd:choice maxOccurs=\"unbounded\" minOccurs=\"0\">\n"
        + "              <xsd:element name=\"Members\" type=\"MembersType\"/>\n"
        + "              <xsd:element name=\"Tuples\" type=\"TuplesType\"/>\n"
        + "            </xsd:choice>\n"
        + "          </xsd:sequence>\n"
        + "          <xsd:attribute name=\"Size\" type=\"xsd:unsignedInt\"/>\n"
        + "        </xsd:complexType>\n"
        + "        <xsd:complexType name=\"OlapInfo\">\n"
        + "          <xsd:sequence>\n"
        + "            <xsd:element name=\"CubeInfo\">\n"
        + "              <xsd:complexType>\n"
        + "                <xsd:sequence>\n"
        + "                  <xsd:element maxOccurs=\"unbounded\" name=\"Cube\">\n"
        + "                    <xsd:complexType>\n"
        + "                      <xsd:sequence>\n"
        + "                        <xsd:element name=\"CubeName\" type=\"xsd:string\"/>\n"
        + "                      </xsd:sequence>\n"
        + "                    </xsd:complexType>\n"
        + "                  </xsd:element>\n"
        + "                </xsd:sequence>\n"
        + "              </xsd:complexType>\n"
        + "            </xsd:element>\n"
        + "            <xsd:element name=\"AxesInfo\">\n"
        + "              <xsd:complexType>\n"
        + "                <xsd:sequence>\n"
        + "                  <xsd:element maxOccurs=\"unbounded\" name=\"AxisInfo\">\n"
        + "                    <xsd:complexType>\n"
        + "                      <xsd:sequence>\n"
        + "                        <xsd:element maxOccurs=\"unbounded\" minOccurs=\"0\" "
        + "name=\"HierarchyInfo\">\n"
        + "                          <xsd:complexType>\n"
        + "                            <xsd:sequence>\n"
        + "                              <xsd:sequence maxOccurs=\"unbounded\">\n"
        + "                                <xsd:element name=\"UName\" type=\"PropType\"/>\n"
        + "                                <xsd:element name=\"Caption\" type=\"PropType\"/>\n"
        + "                                <xsd:element name=\"LName\" type=\"PropType\"/>\n"
        + "                                <xsd:element name=\"LNum\" type=\"PropType\"/>\n"
        + "                                <xsd:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"DisplayInfo\" type=\"PropType\"/>\n"
        + "                              </xsd:sequence>\n"
        + "                              <xsd:sequence>\n"
        + "                                <xsd:any maxOccurs=\"unbounded\" minOccurs=\"0\" processContents=\"lax\"/>\n"
        + "                              </xsd:sequence>\n"
        + "                            </xsd:sequence>\n"
        + "                            <xsd:attribute name=\"name\" type=\"xsd:string\" use=\"required\"/>\n"
        + "                          </xsd:complexType>\n"
        + "                        </xsd:element>\n"
        + "                      </xsd:sequence>\n"
        + "                      <xsd:attribute name=\"name\" type=\"xsd:string\"/>\n"
        + "                    </xsd:complexType>\n"
        + "                  </xsd:element>\n"
        + "                </xsd:sequence>\n"
        + "              </xsd:complexType>\n"
        + "            </xsd:element>\n"
        + "            <xsd:element name=\"CellInfo\">\n"
        + "              <xsd:complexType>\n"
        + "                <xsd:sequence>\n"
        + "                  <xsd:sequence maxOccurs=\"unbounded\" minOccurs=\"0\">\n"
        + "                    <xsd:choice>\n"
        + "                      <xsd:element name=\"Value\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"FmtValue\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"BackColor\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"ForeColor\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"FontName\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"FontSize\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"FontFlags\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"FormatString\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"NonEmptyBehavior\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"SolveOrder\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"Updateable\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"Visible\" type=\"PropType\"/>\n"
        + "                      <xsd:element name=\"Expression\" type=\"PropType\"/>\n"
        + "                    </xsd:choice>\n"
        + "                  </xsd:sequence>\n"
        + "                  <xsd:sequence maxOccurs=\"unbounded\" minOccurs=\"0\">\n"
        + "                    <xsd:any maxOccurs=\"unbounded\" processContents=\"lax\"/>\n"
        + "                  </xsd:sequence>\n"
        + "                </xsd:sequence>\n"
        + "              </xsd:complexType>\n"
        + "            </xsd:element>\n"
        + "          </xsd:sequence>\n"
        + "        </xsd:complexType>\n"
        + "        <xsd:complexType name=\"Axes\">\n"
        + "          <xsd:sequence maxOccurs=\"unbounded\">\n"
        + "            <xsd:element name=\"Axis\">\n"
        + "              <xsd:complexType>\n"
        + "                <xsd:choice maxOccurs=\"unbounded\" minOccurs=\"0\">\n"
        + "                  <xsd:element name=\"CrossProduct\" type=\"CrossProductType\"/>\n"
        + "                  <xsd:element name=\"Tuples\" type=\"TuplesType\"/>\n"
        + "                  <xsd:element name=\"Members\" type=\"MembersType\"/>\n"
        + "                </xsd:choice>\n"
        + "                <xsd:attribute name=\"name\" type=\"xsd:string\"/>\n"
        + "              </xsd:complexType>\n"
        + "            </xsd:element>\n"
        + "          </xsd:sequence>\n"
        + "        </xsd:complexType>\n"
        + "        <xsd:complexType name=\"CellData\">\n"
        + "          <xsd:sequence>\n"
        + "            <xsd:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"Cell\">\n"
        + "              <xsd:complexType>\n"
        + "                <xsd:sequence maxOccurs=\"unbounded\">\n"
        + "                  <xsd:choice>\n"
        + "                    <xsd:element name=\"Value\"/>\n"
        + "                    <xsd:element name=\"FmtValue\" type=\"xsd:string\"/>\n"
        + "                    <xsd:element name=\"BackColor\" type=\"xsd:unsignedInt\"/>\n"
        + "                    <xsd:element name=\"ForeColor\" type=\"xsd:unsignedInt\"/>\n"
        + "                    <xsd:element name=\"FontName\" type=\"xsd:string\"/>\n"
        + "                    <xsd:element name=\"FontSize\" type=\"xsd:unsignedShort\"/>\n"
        + "                    <xsd:element name=\"FontFlags\" type=\"xsd:unsignedInt\"/>\n"
        + "                    <xsd:element name=\"FormatString\" type=\"xsd:string\"/>\n"
        + "                    <xsd:element name=\"NonEmptyBehavior\" type=\"xsd:unsignedShort\"/>\n"
        + "                    <xsd:element name=\"SolveOrder\" type=\"xsd:unsignedInt\"/>\n"
        + "                    <xsd:element name=\"Updateable\" type=\"xsd:unsignedInt\"/>\n"
        + "                    <xsd:element name=\"Visible\" type=\"xsd:unsignedInt\"/>\n"
        + "                    <xsd:element name=\"Expression\" type=\"xsd:string\"/>\n"
        + "                  </xsd:choice>\n"
        + "                </xsd:sequence>\n"
        + "                <xsd:attribute name=\"CellOrdinal\" type=\"xsd:unsignedInt\" use=\"required\"/>\n"
        + "              </xsd:complexType>\n"
        + "            </xsd:element>\n"
        + "          </xsd:sequence>\n"
        + "        </xsd:complexType>\n"
        + "        <xsd:element name=\"root\">\n"
        + "          <xsd:complexType>\n"
        + "            <xsd:sequence maxOccurs=\"unbounded\">\n"
        + "              <xsd:element name=\"OlapInfo\" type=\"OlapInfo\"/>\n"
        + "              <xsd:element name=\"Axes\" type=\"Axes\"/>\n"
        + "              <xsd:element name=\"CellData\" type=\"CellData\"/>\n"
        + "            </xsd:sequence>\n"
        + "          </xsd:complexType>\n"
        + "        </xsd:element>\n"
        + "      </xsd:schema>\n"
        + "      <OlapInfo>\n"
        + "        <CubeInfo>\n"
        + "          <Cube>\n"
        + "            <CubeName>HR</CubeName>\n"
        + "          </Cube>\n"
        + "        </CubeInfo>\n"
        + "        <AxesInfo>\n"
        + "          <AxisInfo name=\"Axis0\">\n"
        + "            <HierarchyInfo name=\"Measures\">\n"
        + "              <UName name=\"[Measures].[MEMBER_UNIQUE_NAME]\"/>\n"
        + "              <Caption name=\"[Measures].[MEMBER_CAPTION]\"/>\n"
        + "              <LName name=\"[Measures].[LEVEL_UNIQUE_NAME]\"/>\n"
        + "              <LNum name=\"[Measures].[LEVEL_NUMBER]\"/>\n"
        + "              <DisplayInfo name=\"[Measures].[DISPLAY_INFO]\"/>\n"
        + "            </HierarchyInfo>\n"
        + "          </AxisInfo>\n"
        + "          <AxisInfo name=\"Axis1\">\n"
        + "            <HierarchyInfo name=\"Employees\">\n"
        + "              <UName name=\"[Employees].[MEMBER_UNIQUE_NAME]\"/>\n"
        + "              <Caption name=\"[Employees].[MEMBER_CAPTION]\"/>\n"
        + "              <LName name=\"[Employees].[LEVEL_UNIQUE_NAME]\"/>\n"
        + "              <LNum name=\"[Employees].[LEVEL_NUMBER]\"/>\n"
        + "              <DisplayInfo name=\"[Employees].[DISPLAY_INFO]\"/>\n"
        + "            </HierarchyInfo>\n"
        + "          </AxisInfo>\n"
        + "          <AxisInfo name=\"SlicerAxis\">\n"
        + "            <HierarchyInfo name=\"Time\">\n"
        + "              <UName name=\"[Time].[MEMBER_UNIQUE_NAME]\"/>\n"
        + "              <Caption name=\"[Time].[MEMBER_CAPTION]\"/>\n"
        + "              <LName name=\"[Time].[LEVEL_UNIQUE_NAME]\"/>\n"
        + "              <LNum name=\"[Time].[LEVEL_NUMBER]\"/>\n"
        + "              <DisplayInfo name=\"[Time].[DISPLAY_INFO]\"/>\n"
        + "            </HierarchyInfo>\n"
        + "          </AxisInfo>\n"
        + "        </AxesInfo>\n"
        + "        <CellInfo>\n"
        + "          <Value name=\"VALUE\"/>\n"
        + "          <FmtValue name=\"FORMATTED_VALUE\"/>\n"
        + "          <FormatString name=\"FORMAT_STRING\"/>\n"
        + "        </CellInfo>\n"
        + "      </OlapInfo>\n"
        + "      <Axes>\n"
        + "        <Axis name=\"Axis0\">\n"
        + "          <Tuples>\n"
        + "            <Tuple>\n"
        + "              <Member Hierarchy=\"Measures\">\n"
        + "                <UName>[Measures].[Org Salary]</UName>\n"
        + "                <Caption>Org Salary</Caption>\n"
        + "                <LName>[Measures].[MeasuresLevel]</LName>\n"
        + "                <LNum>0</LNum>\n"
        + "                <DisplayInfo>0</DisplayInfo>\n"
        + "              </Member>\n"
        + "            </Tuple>\n"
        + "          </Tuples>\n"
        + "        </Axis>\n"
        + "        <Axis name=\"Axis1\">\n"
        + "          <Tuples>\n"
        + "            <Tuple>\n"
        + "              <Member Hierarchy=\"Employees\">\n"
        + "                <UName>[Employees].[All Employees]</UName>\n"
        + "                <Caption>All Employees</Caption>\n"
        + "                <LName>[Employees].[(All)]</LName>\n"
        + "                <LNum>0</LNum>\n"
        + "                <DisplayInfo>65537</DisplayInfo>\n"
        + "              </Member>\n"
        + "            </Tuple>\n"
        + "            <Tuple>\n"
        + "              <Member Hierarchy=\"Employees\">\n"
        + "                <UName>[Employees].[Sheri Nowmer]</UName>\n"
        + "                <Caption>Sheri Nowmer</Caption>\n"
        + "                <LName>[Employees].[Employee Id]</LName>\n"
        + "                <LNum>1</LNum>\n"
        + "                <DisplayInfo>7</DisplayInfo>\n"
        + "              </Member>\n"
        + "            </Tuple>\n"
        + "          </Tuples>\n"
        + "        </Axis>\n"
        + "        <Axis name=\"SlicerAxis\">\n"
        + "          <Tuples>\n"
        + "            <Tuple>\n"
        + "              <Member Hierarchy=\"Time\">\n"
        + "                <UName>[Time].[1997]</UName>\n"
        + "                <Caption>1997</Caption>\n"
        + "                <LName>[Time].[Year]</LName>\n"
        + "                <LNum>0</LNum>\n"
        + "                <DisplayInfo>4</DisplayInfo>\n"
        + "              </Member>\n"
        + "            </Tuple>\n"
        + "          </Tuples>\n"
        + "        </Axis>\n"
        + "      </Axes>\n"
        + "      <CellData>\n"
        + "        <Cell CellOrdinal=\"0\">\n"
        + "          ${VALUE}\n"
        + "        </Cell>\n"
        + "      </CellData>\n"
        + "    </root>\n"
        + "  </xmla:return>\n"
        + "</xmla:ExecuteResponse>"
        + "</soap:Body></soap:Envelope>\n";

}

// End XmlaOlap4jCellSetTest.java