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

import org.olap4j.impl.Named;
import org.olap4j.metadata.MetadataElement;

/**
 * Abstract implementation of {@link MetadataElement}
 * for XML/A providers.
 *
 * @author jhyde
 * @since Dec 5, 2007
 */
abstract class XmlaOlap4jElement implements MetadataElement, Named {
    protected final String uniqueName;
    protected final String name;
    protected final String caption;
    protected final String description;
    private int hash = 0;

    XmlaOlap4jElement(
        String uniqueName,
        String name,
        String caption,
        String description)
    {
        assert uniqueName != null;
        assert description != null;
        assert name != null;
        assert caption != null;
        this.description = description;
        this.uniqueName = uniqueName;
        this.caption = caption;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public boolean isVisible() {
        return true;
    }

    public Object getAnnotations() {
        return null;
    }

    public int hashCode() {
        // By the book implementation of a hash code identifier.
        if (this.hash == 0) {
            hash = (getClass().hashCode() << 8) ^ getUniqueName().hashCode();
        }
        return hash;
    }

    // Keep this declaration abstract as a reminder to
    // overriding classes.
    public abstract boolean equals(Object obj);
}

// End XmlaOlap4jElement.java
