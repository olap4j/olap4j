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

/**
 * Element that has a display folder.
 *
 * @author Joe Barefoot
 *
 * @deprecated In olap4j 2.0, getDisplayFolder() will be on the Measure and
 * Hierarchy interfaces
 */
@Deprecated // to be removed before 2.0
public interface DisplayFolder {
  /**
   * Returns the path to be used when displaying this element in the
   * user interface.
   *
   * <p>Folder names are separated by a semicolon (;). Nested folders
   * are indicated by a backslash (\).
   *
   * <p>The element is typically a measure or hierarchy. For instance,
   * when a cube has many measures, you can use display folders to
   * categorize the measures and improve the user browsing experience.
   *
   * @return display folder for this Measure or Hierarchy
   */
  String getDisplayFolder();

}

// End DisplayFolder.java
