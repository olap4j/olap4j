/*
// $Id: UnmodifiableArrayList.java 482 2012-01-05 23:27:27Z jhyde $
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
package org.olap4j.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Efficiently writes strings of spaces.
 */
public class Spacer {
    private static final ReentrantLock lock = new ReentrantLock();

    private static char[] spaces = {' '};

    private int n;

    /** Creates a Spacer with zero spaces. */
    public Spacer() {
        this(0);
    }

    /** Creates a Spacer with a given number of spaces. */
    public Spacer(int n) {
        this.n = n;
    }

    /** Sets the current number of spaces. */
    public Spacer set(int n) {
        this.n = n;
        return this;
    }

    /** Returns the current number of spaces. */
    public int get() {
        return n;
    }

    /** Increases the current number of spaces by {@code n}. */
    public Spacer add(int n) {
        this.n += n;
        return this;
    }

    /** Reduces the current number of spaces by {@code n}. */
    public Spacer subtract(int n) {
        this.n -= n;
        return this;
    }

    /** Returns a string of the current number of spaces. */
    public String toString() {
        return new String(getSpaces(n), 0, n);
    }

    /** Appends current number of spaces to a {@link StringBuilder}. */
    public StringBuilder spaces(StringBuilder buf) {
        buf.append(getSpaces(n), 0, n);
        return buf;
    }

    /** Appends current number of spaces to a {@link Writer}. */
    public Writer spaces(Writer buf) throws IOException {
        buf.write(getSpaces(n), 0, n);
        return buf;
    }

    /** Appends current number of spaces to a {@link StringWriter}. */
    public StringWriter spaces(StringWriter buf) {
        buf.write(getSpaces(n), 0, n);
        return buf;
    }

    /** Appends current number of spaces to a {@link PrintWriter}. */
    public PrintWriter spaces(PrintWriter buf) {
        buf.write(getSpaces(n), 0, n);
        return buf;
    }

    private static char[] getSpaces(int n) {
        lock.lock();
        try {
            if (spaces.length < n) {
                spaces = new char[n];
                Arrays.fill(spaces, ' ');
            }
            return spaces;
        } finally {
            lock.unlock();
        }
    }
}

// End Spacer.java
