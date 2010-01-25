/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2010-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.metadata;

import java.util.*;

/**
 * Enumerated value that belongs to a set of constants in the XML for Analysis
 * (XMLA) specification.
 *
 * <p>Every {@code enum} E that implements this interface also has a method to
 * get the {@link org.olap4j.metadata.XmlaConstant.Dictionary} of all its values:
 *
 * <blockquote>public static Dictionary&lt;E&gt; getDictionary();</blockquote>
 *
 * <p>Here is a collection of enum classes and the prefix used to generate
 * their XMLA constant names.
 *
 * <table border='1'>
 * <tr>
 * <th>Prefix</th>
 * <th>Enum class</th>
 * </tr>
 *
 * <tr>
 * <td>DBTYPE_</td>
 * <td>{@link Datatype}</td>
 * </tr>
 *
 * <tr>
 * <td>MD_DIMTYPE_</td>
 * <td>{@link org.olap4j.metadata.Dimension.Type}</td>
 * </tr>
 *
 * <tr>
 * <td>MDLEVEL_TYPE_</td>
 * <td>{@link org.olap4j.metadata.Level.Type}</td>
 * </tr>
 *
 * <tr>
 * <td>MDMEASURE_AGG_</td>
 * <td>{@link org.olap4j.metadata.Measure.Aggregator}</td>
 * </tr>
 *
 * <tr>
 * <td>MDTREEOP_</td>
 * <td>{@link org.olap4j.metadata.Member.TreeOp}</td>
 * </tr>
 *
 * <tr>
 * <td>MD_PROPTYPE_</td>
 * <td>{@link org.olap4j.metadata.Property.ContentType}</td>
 * </tr>
 *
 * <tr>
 * <td>MDPROP_</td>
 * <td>{@link org.olap4j.metadata.Property.TypeFlag}</td>
 * </tr>
 *
 * <tr>
 * <td>none</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.Access}</td>
 * </tr>
 *
 * <tr>
 * <td>MDACTION_TYPE_</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.ActionType}</td>
 * </tr>
 *
 * <tr>
 * <td>none</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.AuthenticationMode}</td>
 * </tr>
 *
 * <tr>
 * <td>none</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.AxisFormat}</td>
 * </tr>
 *
 * <tr>
 * <td>DBTYPE_</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.DBType}</td>
 * </tr>
 *
 * <tr>
 * <td>MDFF_</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.FontFlag}</td>
 * </tr>
 *
 * <tr>
 * <td>none</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.Format}</td>
 * </tr>
 *
 * <tr>
 * <td>DBLITERAL_</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.Literal}</td>
 * </tr>
 *
 * <tr>
 * <td>none</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.Method}</td>
 * </tr>
 *
 * <tr>
 * <td>none</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.ProviderType}</td>
 * </tr>
 *
 * <tr>
 * <td>none</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.Updateable}</td>
 * </tr>
 *
 * <tr>
 * <td>DBPROPVAL_VISUAL_MODE_</td>
 * <td>{@link org.olap4j.metadata.XmlaConstants.VisualMode}</td>
 * </tr>
 *
 * </table>
 *
 * @author jhyde
 * @version $Id: $
 */
public interface XmlaConstant {
    /**
     * Returns the name of this constant as specified by XMLA.
     *
     * <p>Often the name is an enumeration-specific prefix plus the name of
     * the Java enum constant. For example,
     * {@link org.olap4j.metadata.Dimension.Type} has
     * prefix "MD_DIMTYPE_", and therefore this method returns
     * "MD_DIMTYPE_PRODUCTS" for the enum constant
     * {@link org.olap4j.metadata.Dimension.Type#PRODUCTS}.
     *
     * @return ordinal code as specified by XMLA.
     */
    String xmlaName();

    /**
     * Returns the description of this constant.
     *
     * @return Description of this constant.
     */
    String getDescription();

    /**
     * Returns the code of this constant as specified by XMLA.
     *
     * <p>For example, the XMLA specification says that the ordinal of
     * MD_DIMTYPE_PRODUCTS is 8, and therefore this method returns 8
     * for {@link org.olap4j.metadata.Dimension.Type#PRODUCTS}.
     *
     * @return ordinal code as specified by XMLA.
     */
    int xmlaOrdinal();

    interface Dictionary<E extends Enum<E> & XmlaConstant> {

        /**
         * Returns the enumeration value with the given ordinal in the XMLA
         * specification, or null if there is no such.
         *
         * @param xmlaOrdinal XMLA ordinal
         * @return Enumeration value
         */
        E forOrdinal(int xmlaOrdinal);

        /**
         * Returns the enumeration value with the given name in the XMLA
         * specification, or null if there is no such.
         *
         * @param xmlaName XMLA name
         * @return Enumeration value
         */
        E forName(String xmlaName);

        /**
         * Creates a set of values by parsing a mask.
         *
         * @param xmlaOrdinalMask Bit mask
         * @return Set of E values
         */
        Set<E> forMask(int xmlaOrdinalMask);

        /**
         * Converts a set of enum values to an integer by logical OR-ing their
         * codes.
         *
         * @param set Set of enum values
         * @return Bitmap representing set of enum values
         */
        int toMask(Set<E> set);

        /**
         * Returns all values of the enum.
         *
         * <p>This method may be more efficient than
         * {@link Class#getEnumConstants()} because the latter is required to
         * create a new array every call to prevent corruption.
         *
         * @return List of enum values
         */
        List<E> getValues();

        /**
         * Returns the class that the enum values belong to.
         *
         * @return enum class
         */
        Class<E> getEnumClass();
    }
}

// End XmlaConstant.java
