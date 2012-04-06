/*
// $Id$
//
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
package org.olap4j.mdx.parser.impl;

import org.olap4j.impl.Olap4jUtil;
import org.olap4j.mdx.ParseRegion;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.parser.MdxParseException;
import org.olap4j.mdx.parser.MdxParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link org.olap4j.mdx.parser.MdxParser MDX Parser}.
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public class DefaultMdxParserImpl implements MdxParser {
    private boolean debug = false;
    private final FunTable funTable = new FunTable() {
        public boolean isProperty(String s) {
            return s.equalsIgnoreCase("CHILDREN");
        }
    };

    /**
     * Creates a DefaultMdxParserImpl.
     */
    public DefaultMdxParserImpl() {
        super();
    }

    public SelectNode parseSelect(String mdx) {
        try {
            return new MdxParserImpl(mdx, debug, funTable, false)
                .selectStatement();
        } catch (TokenMgrError e) {
            throw convertException(mdx, e);
        } catch (ParseException e) {
            throw convertException(mdx, e);
        }
    }

    public ParseTreeNode parseExpression(String mdx) {
        try {
            return new MdxParserImpl(mdx, debug, funTable, false).expression();
        } catch (TokenMgrError e) {
            throw convertException(mdx, e);
        } catch (ParseException e) {
            throw convertException(mdx, e);
        }
    }

    /**
     * Converts the exception so that it looks like the exception produced by   
     * JavaCUP. (Not that that format is ideal, but it minimizes test output    
     * changes during the transition from JavaCUP to JavaCC.)                   
     *
     * @param queryString MDX query string                                      
     * @param pe JavaCC parse exception                                         
     * @return Wrapped exception                                                
     */
    private RuntimeException convertException(
        String queryString,
        Throwable pe)
    {
        ParseRegion parseRegion = null;
        String message = null;
        if (pe instanceof TokenMgrError) {
            Pattern pattern =
                Pattern.compile(
                    "Lexical error at line ([0-9]+), column ([0-9]+)\\. .*");
            final Matcher matcher = pattern.matcher(pe.getMessage());
            if (matcher.matches()) {
                Olap4jUtil.discard(matcher);
                int line = Integer.parseInt(matcher.group(1));
                int column = Integer.parseInt(matcher.group(2));
                parseRegion = new ParseRegion(line, column);
                message = pe.getMessage();
            }
        } else if (pe instanceof ParseException
            && pe.getMessage().startsWith("Encountered "))
        {
            Token errorToken = ((ParseException) pe).currentToken.next;
            parseRegion =
                new ParseRegion(
                    errorToken.beginLine,
                    errorToken.beginColumn,
                    errorToken.endLine,
                    errorToken.endColumn);
            message = "Syntax error at line "
                + parseRegion.getStartLine()
                + ", column "
                + parseRegion.getStartColumn()
                + ", token '"
                + errorToken.image
                + "'";
        }
        Throwable e;
        if (parseRegion != null) {
            e = new MdxParseException(
                parseRegion,
                message);
        } else {
            e = pe;
        }
        throw new RuntimeException(
            "Error while parsing MDX statement '" + queryString + "'",
            e);
    }

    interface FunTable {
        boolean isProperty(String s);
    }
}

// End DefaultMdxParserImpl.java
