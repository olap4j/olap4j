/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2008-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.transform;

import org.olap4j.mdx.SelectNode;

/**
 * MDX Query Transformation
 *
 * <p>General interface for transforming an MDX query to another one,
 * according to behavior and parameters encapsulated in implementing
 * classes
 *
 * @author etdub
 * @author jhyde
 * @version $Id$
 * @since Jul 28, 2008
 */
public interface MdxQueryTransform {
    String getName();
    String getDescription();
    SelectNode apply(SelectNode sn);
}

// End MdxQueryTransform.java
