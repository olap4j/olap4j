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
package org.olap4j;

import java.util.List;

/**
 * Listener interface for receiving events when the contents of a
 * {@link CellSet} have changed.
 *
 * <p>NOTE: This functionality is experimental and is subject to change or
 * removal without notice.
 *
 * <p>The client can ask the server to provide the listener with a specific
 * {@link Granularity granularity} of events, but the server can decline to
 * provide that granularity.
 *
 * <p>Fine granularity deals with changes such as cell values changing (and
 * reports the before and after value, before and after formatted value),
 * positions being deleted, positions being changed.
 *
 * <p>When an atomic change happens on the server (say a cache flush, if the
 * server is mondrian) then an event will arrive on the client containing all of
 * those changes. Although {@link CellSetChange#getCellChanges} and
 * {@link CellSetChange#getAxisChanges} return lists, the client should assume
 * that all of the events in these lists simultaneously.
 *
 * <p>At any point, the server is free to throw up its hands and say 'there are
 * too many changes' by sending null values for {@code getCellChanges} or
 * {@code getAxisChanges}. This prevents situations where there are huge numbers
 * of changes that might overwhelm the server, the network link, or the client,
 * such as might happen if a large axis is re-sorted.
 *
 * <p>The client should always be ready for that to happen (even for providers
 * that claim to provide fine granularity events), and should re-execute the
 * query to get the cell set. In fact, we recommend that clients re-execute the
 * query to get a new cellset whenever they get an event. Then the client can
 * use the details in the event to highlight cells that have changed.
 *
 * <h3>Notes for implementors</h3>
 *
 * <p>The purpose of registering a listener before creating a cell set is to
 * ensure that no events "leak out" between creating a cell set and registering
 * a listener, or while a statement is being re-executed to produce a new cell
 * set.
 *
 * <p>The {@link #cellSetOpened(CellSet)} and {@link #cellSetClosed(CellSet)}
 * methods are provided so that the listener knows what is going on when a
 * statement is re-executed. In particular, suppose a statement receives an
 * change event decides to re-execute. The listener is attached to the
 * statement, so receives notifications about both old and new cell sets. The
 * driver implicitls closes the previous cell set and calls
 * {@code cellSetClosed}, then calls {@code cellSetOpened} with the new cell
 * set.
 *
 * <p>If changes are occurring regularly on the server, there will soon be a
 * call to {@link #cellSetChanged}. It is important to note that this event
 * contains only changes that have occurred since the new cell set was opened.
 *
 * <p>The granularity parameter is provided to {@link OlapStatement#addListener}
 * for the server's benefit. If granularity is only {@link Granularity#COARSE},
 * the server may be able to store less information in order to track the cell
 * set.
 */
public interface CellSetListener {

    /**
     * Invoked when a cell set is opened.
     *
     * @param cellSet Cell set
     */
    void cellSetOpened(CellSet cellSet);

    /**
     * Invoked when a cell set is closed.
     *
     * @param cellSet Cell set
     */
    void cellSetClosed(CellSet cellSet);

    /**
     * Invoked when a cell set has changed.
     *
     * @param cellSetChange Change descriptor
     */
    void cellSetChanged(CellSetChange cellSetChange);

    /**
     * Granularity of notifications that should be sent to a cellset listener.
     */
    enum Granularity {
        FINE,
        COARSE
    }

    /**
     * Description of changes that have occurred to the cell set.
     */
    interface CellSetChange {
        /**
         * Returns the cell set affected by this change.
         *
         * @return Cell set affected by this change.
         */
        CellSet getCellSet();

        /**
         * Returns a list of cells that have changed, or null if the server
         * cannot provide detailed changes.
         *
         * <p>The server is always at liberty to provide a {@code CellSetChange}
         * without a detailed list of changes, even if
         * {@link Granularity#COARSE} was specified when the listener was
         * attached. Here are some typical reasons:<ul>
         *
         * <li>If there are very many changes. (Transmitting these changes over
         * the network would be costly, and the user interface also might
         * struggle to redisplay so many cells.)
         *
         * <li>If the axes have changed significantly. (If an axis position has
         * changed, all of the cells at that position will necssarily have
         * changed.)
         *
         * <li>If the client did not ask for detailed changes
         *
         * <li>If the the provider is not capable of giving detailed changes.
         * </ul>
         */
        List<CellChange> getCellChanges();

        /**
         * Returns a list of axis changes, or null if server cannot provide
         * detailed changes.
         *
         * <p>The reasons why this method returns null are similar to the
         * reasons why {@link #getCellChanges()} returns null.
         *
         * @return List of changes to positions on axes, or null if the server
         * cannot provide detailed changes.
         */
        List<AxisChange> getAxisChanges();
    }

    /**
     * Description of a change to a particular {@link Cell}; part of a
     * {@link CellSetChange}.
     */
    interface CellChange {
        /**
         * Returns the cell before the change.
         */
        Cell getBeforeCell();

        /**
         * Returns the cell after the change.
         */
        Cell getAfterCell();
    }

    /**
     * Description of a change to a particular {@link CellSetAxis}; part of a
     * {@link CellSetChange}.
     */
    interface AxisChange {
        /**
         * Returns the axis affected by this change.
         *
         * @return Axis affected by this change
         */
        CellSetAxis getAxis();

        /**
         * Returns the position before the change. Null if the change created a
         * new position.
         *
         * @return Position before the change, or null if the position is newly
         * created
         */
        Position getBeforePosition();

        /**
         * Returns the position after the change. Null if the change deleted
         * this position.
         *
         * @return Position after the change, or null if the position is deleted
         */
        Position getAfterPosition();
    }
}

// End CellSetListener.java
