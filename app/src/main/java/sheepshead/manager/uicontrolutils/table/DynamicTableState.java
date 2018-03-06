/*
 * Copyright 2017  Erik Pohle
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package sheepshead.manager.uicontrolutils.table;


import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents the state of a table from {@link DynamicSizeTableBuilder}.
 * This allows modification (e.g. reordering) of views without creating a new table.
 */
public class DynamicTableState {
    /**
     * Ascending sorting order, i.e. smaller values on top
     */
    public static final int ASCENDING = 0;
    /**
     * Descending sorting order, i.e. bigger values on top
     */
    public static final int DESCENDING = 1;

    /**
     * The main table view
     */
    private final TableLayout table;
    /**
     * The left upper cell container
     */
    private final LinearLayout leftUpperCell;
    /**
     * The fixed header row
     */
    private final TableRow fixedHeader;
    /**
     * The fixed column table
     */
    private final TableLayout fixedColumn;
    /**
     * Indicates that the table has a fixed header
     */
    private final boolean hasFixedHeader;
    /**
     * Indicates that the table has a fixed column
     */
    private final boolean hasFixedColumn;
    /**
     * Information for background drawables of each cell
     */
    private final BgDrawables drawables;

    /**
     * List containing the header cell views for setting the background drawables
     */
    private List<View> headerCells;
    /**
     * List containing row view together with their data model, for sorting & reordering
     */
    private List<RowContent> rows;
    /**
     * The column index by which the table is currently sorted
     */
    private int sortedIndex;
    /**
     * The sorting mode of the current row order (i.e. ascending or descending)
     */
    private int sortedState;
    /**
     * A comparator for sorting one column
     */
    private Comparator<ITableCellBuilder> order;

    /**
     * @param table          The main table view
     * @param leftUpperCell  The left upper cell container
     * @param fixedHeader    The fixed header row
     * @param fixedColumn    The fixed column table
     * @param hasFixedHeader true iff this table uses a fixed header
     * @param hasFixedColumn true iff this table uses a fixed column
     * @param drawables      Information for background drawables of each cell
     */
    DynamicTableState(TableLayout table, LinearLayout leftUpperCell, TableRow fixedHeader,
                      TableLayout fixedColumn, boolean hasFixedHeader, boolean hasFixedColumn,
                      BgDrawables drawables) {
        this.table = table;
        this.leftUpperCell = leftUpperCell;
        this.fixedHeader = fixedHeader;
        this.fixedColumn = fixedColumn;
        this.hasFixedHeader = hasFixedHeader;
        this.hasFixedColumn = hasFixedColumn;
        this.drawables = drawables;
    }

    /**
     * Adds all child views of from to {@link #headerCells}
     *
     * @param from parent container of views to add
     */
    private void addHeaderCells(TableRow from) {
        for (int i = 0; i < from.getVirtualChildCount(); i++) {
            headerCells.add(from.getVirtualChildAt(i));
        }
    }

    /**
     * Initializes {@link #headerCells}. Depending on the table properties (fixed header/column)
     * different views are used as header views.
     */
    private void setHeaderCells() {
        headerCells = new ArrayList<>();
        if (hasFixedHeader) {
            if (hasFixedColumn) {
                headerCells.add(leftUpperCell.getChildAt(0));
            }
            addHeaderCells(fixedHeader);
        } else {
            if (hasFixedColumn) {
                TableRow firstColumn = (TableRow) fixedColumn.getChildAt(0);
                headerCells.add(firstColumn.getVirtualChildAt(0));
            }
            addHeaderCells((TableRow) table.getChildAt(0));
        }
    }

    /**
     * Sets the data model for this table and specifies a comparison order.
     *
     * @param builders the data model
     * @param order    the comparison order for each column
     */
    public void setContent(List<List<ITableCellBuilder>> builders, Comparator<ITableCellBuilder> order) {
        setHeaderCells();

        int rowCount = hasFixedHeader ? builders.size() : builders.size() - 1;
        rows = new ArrayList<>(rowCount);

        int index = 0;
        for (List<ITableCellBuilder> content : builders) {
            if (!hasFixedHeader && index == 0) {
                continue;
            }
            TableRow columnRow = hasFixedColumn ? (TableRow) fixedColumn.getChildAt(index) : null;
            rows.add(new RowContent((TableRow) table.getChildAt(index), columnRow, content));
            index++;
        }
        sortedIndex = 0;
        sortedState = DESCENDING;
        this.order = order;
    }

    /**
     * Reorders all table rows by sorting the column at given index.
     * The sorting mode (ascending/descending) is cyclic:
     * If the table was sorted by a different column before, the table will be sorted in ascending order using the column at given index.
     * If the table was already sorted by the column at given index, the sorting order will cycle through all sorting modes (ascending, descending, ascending, ...)
     *
     * @param column the index of the column to sort by
     * @throws IllegalStateException     iff {@link #setContent(List, Comparator)} has not been called before
     * @throws IndexOutOfBoundsException iff column is out of range
     */
    public void reorder(int column) {
        if (rows == null || headerCells == null) {
            throw new IllegalStateException("No content set! Use setContent(...)");
        }
        if (column < 0 || column >= headerCells.size()) {
            throw new IndexOutOfBoundsException("column: " + column);
        }
        //reset previous header cell to normal
        headerCells.get(sortedIndex).setBackgroundResource(drawables.getBgHeader());

        if (sortedIndex == column) {
            sortedState = (sortedState + 1) % 2;//next state
        } else {
            sortedState = ASCENDING;
        }
        sortedIndex = column;
        Collections.sort(rows, new RowComparator(sortedState, sortedIndex));
        table.removeAllViews();
        fixedColumn.removeAllViews();
        for (RowContent content : rows) {
            table.addView(content.getRow());
            TableRow columnRow = content.getFirstColumnRow();
            if (columnRow != null) {
                fixedColumn.addView(columnRow);
            }
        }

        //set current header cell to ascending/descending
        @DrawableRes int bgSorted = -1;
        switch (sortedState) {
            case ASCENDING:
                bgSorted = drawables.getBgHeaderAsc();
                break;
            case DESCENDING:
                bgSorted = drawables.getBgHeaderDesc();
                break;
            default:
                throw new IllegalStateException("Unknown state " + sortedState);
        }
        headerCells.get(sortedIndex).setBackgroundResource(bgSorted);
    }

    /**
     * Class to associate the table row views of one row with its data model
     */
    private static class RowContent {
        /**
         * The row in the main table
         */
        private TableRow row;
        /**
         * The row in the fixed column table, may be null iff the table does not use fixedColumn
         */
        private @Nullable
        TableRow firstColumnRow;
        /**
         * The data model of this row. Note that regardless of the use of
         * fixedColumn the first builder in this list is the data model for the first column
         */
        private List<ITableCellBuilder> content;

        /**
         * @param row            the row in the main table
         * @param firstColumnRow the row in the fixed column table, may be null
         * @param content        the data model for this row
         */
        RowContent(TableRow row, @Nullable TableRow firstColumnRow, List<ITableCellBuilder> content) {
            this.row = row;
            this.firstColumnRow = firstColumnRow;
            this.content = content;
        }

        /**
         * @return the row view in the main table
         */
        TableRow getRow() {
            return row;
        }

        /**
         * @return the data model of this row
         */
        List<ITableCellBuilder> getContent() {
            return content;
        }

        /**
         * @return the row view in the fixed column table, may be null
         */
        @Nullable
        TableRow getFirstColumnRow() {
            return firstColumnRow;
        }
    }

    /**
     * Comparator for sorting rows by a specified column and sorting mode (ascending or descending).
     */
    private class RowComparator implements Comparator<RowContent> {
        /**
         * The sorting mode, i.e. {@link #ASCENDING} or {@link #DESCENDING}
         */
        private int state;
        /**
         * The index of the column to sort by
         */
        private int column;

        RowComparator(int state, int column) {
            this.state = state;
            this.column = column;
        }

        @Override
        public int compare(RowContent o1, RowContent o2) {
            int res = order.compare(o1.getContent().get(column), o2.getContent().get(column));
            switch (state) {
                case ASCENDING:
                    return res;
                case DESCENDING:
                    return -res;
                default:
                    throw new IllegalStateException("Unknown state " + state);
            }
        }
    }


}
