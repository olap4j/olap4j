/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.*;

import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.sql.*;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import mondrian.olap.Util;

/**
 * Implementation of {@link org.olap4j.CellSet}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id: MondrianOlap4jCellSet.java 22 2007-06-15 02:23:07Z jhyde $
 * @since May 24, 2007
 */
abstract class XmlaOlap4jCellSet implements CellSet {
    final XmlaOlap4jStatement olap4jStatement;
    protected boolean closed;
    private final XmlaOlap4jCellSetMetaData metaData;
    private final InputStream is;

    public XmlaOlap4jCellSet(
        XmlaOlap4jStatement olap4jStatement,
        InputStream is)
    {
        assert olap4jStatement != null;
        this.olap4jStatement = olap4jStatement;
        this.is = is;
        this.closed = false;
        if (olap4jStatement instanceof XmlaOlap4jPreparedStatement) {
            this.metaData =
                ((XmlaOlap4jPreparedStatement) olap4jStatement).cellSetMetaData;
        } else {
            this.metaData =
                new XmlaOlap4jCellSetMetaData(
                    olap4jStatement);
        }
    }

    public CellSetMetaData getMetaData() throws OlapException {
        return metaData;
    }

    public Cell getCell(int ordinal) {
        throw Util.needToImplement(this);
    }

    public List<CellSetAxis> getAxes() {
        throw Util.needToImplement(this);
    }

    public CellSetAxis getFilterAxis() {
        throw Util.needToImplement(this);
    }

    public Cell getCell(List<Integer> coordinates) {
        throw Util.needToImplement(this);
    }

    public List<Integer> ordinalToCoordinates(int ordinal) {
        throw new UnsupportedOperationException();
    }

    public int coordinatesToOrdinal(List<Integer> coordinates) {
        throw new UnsupportedOperationException();
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
        int columnIndex, int scale) throws SQLException {
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
        String columnLabel, int scale) throws SQLException {
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

    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
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
        int columnIndex, BigDecimal x) throws SQLException {
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
        int columnIndex, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(
        int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(
        int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(
        int columnIndex, Reader x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(
        int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateNull(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean(
        String columnLabel, boolean x) throws SQLException {
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
        String columnLabel, BigDecimal x) throws SQLException {
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
        String columnLabel, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream(
        String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream(
        String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateCharacterStream(
        String columnLabel, Reader reader, int length) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void updateObject(
        String columnLabel, Object x, int scaleOrLength) throws SQLException {
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
        int columnIndex, Map<String, Class<?>> map) throws SQLException {
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
        String columnLabel, Map<String, Class<?>> map) throws SQLException {
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
        int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp(
        String columnLabel, Calendar cal) throws SQLException {
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
}

// End XmlaOlap4jCellSet.java
