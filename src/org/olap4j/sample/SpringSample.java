/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.sample;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.olap4j.CellSet;

import javax.sql.DataSource;
import java.sql.ResultSet;

/**
 * Example of olap4j code running inside Spring framework.
 *
 * @author jhyde
 * @version $Id: $
 * @since Oct 11, 2006
 */
public class SpringSample {

    /**
     * Example of an MDX query being executed and the result handled using a
     * {@link ResultSetExtractor}.
     *
     * @param dataSource Data source
     */
    public static void example(DataSource dataSource) {
        JdbcTemplate jt = new JdbcTemplate(dataSource);
        Object o = jt.query(
            "select {[Measures].[Unit Sales]} on columns,\n" +
            " CrossJoin([Gender].Members, [Marital Status].Members) on rows\n" +
            "from [Sales]",
            new ResultSetExtractor() {
                public Object extractData(ResultSet resultSet)
                {
                    CellSet olapResult = (CellSet) resultSet;
                    return "Result has " +
                        olapResult.getAxes().get(0).getPositions().size() +
                        " columns and " +
                        olapResult.getAxes().get(1).getPositions().size() +
                        " rows.";
                }
            });

        // Prints "Result has 1 columns and 9 rows."
        System.out.println(o);
    }
}

// End SpringSample.java
