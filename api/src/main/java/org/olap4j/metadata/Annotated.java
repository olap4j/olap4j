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
package org.olap4j.metadata;

import java.util.Map;

/**
 * An element that has annotations.
 *
 * <p>To see whether an element has annotations, use {@code java.sql.Wrapper}:
 *
 * <blockquote><pre>
 * final Hierarchy hierarchy;
 * if (hierarchy.isWrapperFor(Annotated.class)) {
 *   final Annotated annotated = hierarchy.unwrap(Annotated.class);
 *   System.out.println(annotated.getAnnotationMap().get("displayFolder"));
 * }</code></blockquote>
 *
 * @since olap4j 2.0
 */
public interface Annotated {
    /**
     * Returns a list of annotations.
     *
     * <p>The map may be empty, never null.
     *
     * @return Map from annotation name to annotations.
     */
    Map<String, Annotation> getAnnotationMap();
}

// End Annotated.java
