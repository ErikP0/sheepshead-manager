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

package sheepshead.manager.uicontrolutils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

import sheepshead.manager.R;
import sheepshead.manager.utils.Consumer;
import sheepshead.manager.utils.Optional;

/**
 * Convenience builder class for building a table (TableLayout with multiple TableRows) with an arbitrary amount of columns and rows.
 * The builder works by specifying a generator (implementor of {@link ITableCellBuilder}) for each header and table body cell.
 * Example:
 * Let b1 be a generator creating a TextView displaying "1", b2 one that displays "2":
 * <pre>
 *     builder.enableHeader();
 *     builder.putHeaderCell(b1);
 *     builder.putHeaderCell(b1);
 *     builder.putHeaderCell(b2);
 *     builder.startRow();
 *     builder.putRowCell(b2);
 *     builder.startRow();
 *     builder.putRowCell(b1);
 *     builder.putRowCell(b2);
 *     builder.putRowCell(b2);
 * </pre>
 * Will generate this table:
 * <pre>
 *     1 | 1 | 2
 *     2 |   |
 *     1 | 2 | 2
 * </pre>
 * Note the incomplete first table row ( 2 | |). Spaces/empty cells are inserted automatically to
 * fit the maximum column width
 */
public class DynamicSizeTableBuilder {

    /**
     * the desired layout parameters
     */
    private final TableRow.LayoutParams layoutParams;
    /**
     * A consumer for more customization of header cells
     */
    @Nullable
    private final Consumer<View> headerConsumer;
    /**
     * A consumer for more customization of body cells
     */
    @Nullable
    private final Consumer<View> bodyConsumer;
    /**
     * List of header cells
     */
    private List<ITableCellBuilder> header;
    /**
     * Indicates if a header is desired
     */
    private boolean isHeaderEnabled;
    /**
     * List of table rows (table row aka list of cells)
     */
    private List<List<ITableCellBuilder>> tableRows;
    /**
     * The current row of which the building is currently in progress
     */
    private List<ITableCellBuilder> currentRow;
    /**
     * The amount of columns of this table
     */
    private ITableCellBuilder fixedLeftUpperCell;

    private int maxWidth = 0;

    /**
     * Indicates a fixed header (header will not scroll vertically)
     */
    private boolean fixedHeader;
    /**
     * Indicates a fixed first column in the table (first column will not scroll horizontally)
     */
    private boolean fixedFirstColumn;

    /**
     * Creates a new builder
     *
     * @param params      desired layout params, can be null
     * @param headerSpecs consumer for more customization of each header cell, can be null
     * @param bodySpecs   consumer for more customization of each body cell, can be null
     */
    public DynamicSizeTableBuilder(@Nullable TableRow.LayoutParams params, @Nullable Consumer<View> headerSpecs, @Nullable Consumer<View> bodySpecs) {
        header = null;
        tableRows = new ArrayList<>();
        isHeaderEnabled = false;
        if (params != null) {
            layoutParams = params;
        } else {
            layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        }
        headerConsumer = headerSpecs;
        bodyConsumer = bodySpecs;
    }

    /**
     * Fixes the first column of the table such that it will not scroll horizontally.
     * This is disabled by default.
     */
    public void fixFirstColumn() {
        fixedFirstColumn = true;
    }

    /**
     * Enables the header for this table. The header is disabled by default
     *
     * @param fixed if true, the header is fixed, i.e. will not scroll vertically
     */
    public void enableHeader(boolean fixed) {
        isHeaderEnabled = true;
        fixedHeader = fixed;
        header = new ArrayList<>();
    }

    /**
     * Appends a single cell to the header.
     *
     * @param builder The builder for this single cell
     * @throws IllegalStateException If the header is disabled
     * @see #enableHeader(boolean)
     */
    public void putHeaderCell(@NonNull ITableCellBuilder builder) {
        if (!isHeaderEnabled) {
            throw new IllegalStateException("Enable Header first");
        }
        checkIfNull(builder);
        if (fixedHeader && fixedFirstColumn && fixedLeftUpperCell == null && header.isEmpty()) {
            //builder is the first cell in the header
            fixedLeftUpperCell = builder;
        } else {
            header.add(builder);
        }
    }

    private void checkIfNull(ITableCellBuilder b) {
        if (b == null) {
            throw new IllegalArgumentException("Builder cannot be null! Use " + DynamicSizeTableBuilder.EmptyCellBuilder.class.getSimpleName());
        }
    }

    /**
     * Starts a new, empty row in the body of the table
     */
    public void startRow() {
        if (currentRow != null) {
            maxWidth = Math.max(maxWidth, currentRow.size());
        }
        currentRow = new ArrayList<>();
        tableRows.add(currentRow);
    }

    /**
     * Appends a single table body cell to the current table row
     *
     * @param builder The builder for this single cell
     * @throws IllegalStateException If no row has been started
     */
    public void putRowCell(@NonNull ITableCellBuilder builder) {
        if (currentRow == null) {
            throw new IllegalStateException("Start a row first");
        }
        checkIfNull(builder);
        currentRow.add(builder);
    }

    /**
     * Creates views from the given list of {@link ITableCellBuilder} and adds them to the given row.
     * If the given consumer is not null, it will be called for each created view.
     *
     * @param row      the row to insert into
     * @param c        the creation context
     * @param cells    a list containing builders for all views
     * @param consumer a consumer that accepts the created view, may be null
     */
    private void addToRow(TableRow row, Context c, List<ITableCellBuilder> cells, @Nullable Consumer<View> consumer) {
        for (ITableCellBuilder builder : cells) {
            View child = builder.build(c);
            if (consumer != null) {
                consumer.accept(child);
            }
            row.addView(child);
        }
    }

    /**
     * Creates views from the given list of {@link ITableCellBuilder} and adds them to the given row.
     * If the given consumer is not null, it will be called for each created view.
     * If cells does not contain enough builders to fill the role (maxWidth), empty cells are added.
     *
     * @param row      the row to insert into
     * @param c        the creation context
     * @param cells    a list containing builders for all views
     * @param consumer a consumer that accepts the created view, may be null
     * @see #addToRow(TableRow, Context, List, Consumer)
     */
    private void populateRow(TableRow row, Context c, List<ITableCellBuilder> cells, @Nullable Consumer<View> consumer) {
        addToRow(row, c, cells, consumer);
        int neededSpace = maxWidth - cells.size();
        if (neededSpace > 0) {
            for (int i = 0; i < neededSpace; i++) {
                View child = new EmptyCellBuilder().build(c);
                if (consumer != null) {
                    consumer.accept(child);
                }
                row.addView(child);
            }
        }
    }

    /**
     * Creates a new table row, creates cell views from the given builders and inserts the cell views
     * into the row. If the given consumer is not null, it will be called for each created view.
     *
     * @param c        the creation context
     * @param cells    a list containing builders for all views
     * @param consumer a consumer that accepts the created view, may be null
     * @return a table row containing all cell views as children
     */
    private TableRow buildRow(Context c, List<ITableCellBuilder> cells, @Nullable Consumer<View> consumer) {
        TableRow row = new TableRow(c);
        populateRow(row, c, cells, consumer);
        return row;
    }

    /**
     * Removes the first cell view from the given table row and inserts it as table row into the given
     * table layout.
     *
     * @param context     the creation context for the new table row
     * @param fixedColumn the table layout to insert the new row into
     * @param from        the row to remove the first cell view from
     */
    private void addToFixedColumn(Context context, TableLayout fixedColumn, TableRow from) {
        View firstColumn = from.getVirtualChildAt(0);
        from.removeViewAt(0);
        TableRow firstColumnRow = new TableRow(context);
        firstColumnRow.addView(firstColumn);
        fixedColumn.addView(firstColumnRow);
    }

    /**
     * Builds the table. If the given table layout is not null, the contents are appended to the existing table.
     * If it is null, a new table is created. In both cases the table that was edited, is returned.
     * If the external header is null, the header is added to the top of the table, if not the header
     * contents are appended to the existing header. If the header is disabled, no header content is appended in any case.
     *
     * @param context      The current context
     * @param dynamicTable the dynamic_table layout container
     */
    public void populate(Context context, @NonNull View dynamicTable) {
        //check if the last row is the biggest one
        if (currentRow != null) {
            maxWidth = Math.max(maxWidth, currentRow.size());
        }

        TableLayout table = dynamicTable.findViewById(R.id.dynamic_table_table);
        TableRow headerCont = dynamicTable.findViewById(R.id.dynamic_table_fixed_header);
        TableLayout columnCont = dynamicTable.findViewById(R.id.dynamic_table_fixed_column);
        LinearLayout leftUpperCellCont = dynamicTable.findViewById(R.id.dynamic_table_fixed_left_upper_cell);
        ScrollView verticalMainScroll = dynamicTable.findViewById(R.id.dynamic_table_vertical_scroll);
        ScrollView verticalFixedColumnScroll = dynamicTable.findViewById(R.id.dynamic_table_fixed_column_scroll);
        if (table == null) {
            throw new IllegalStateException("table is null");
        }
        if (headerCont == null) {
            throw new IllegalStateException("header is null");
        }
        if (columnCont == null) {
            throw new IllegalStateException("column is null");
        }
        if (verticalMainScroll == null) {
            throw new IllegalStateException("vertical main scroll is null");
        }
        if (verticalFixedColumnScroll == null) {
            throw new IllegalStateException("vertical fixed column scroll is null");
        }

        //scrolling in either the first column or the rest of the table should scroll the other one as well
        verticalMainScroll.setOnScrollChangeListener((View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
            verticalFixedColumnScroll.scrollTo(scrollX, scrollY);
        });
        verticalFixedColumnScroll.setOnScrollChangeListener((View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
            verticalMainScroll.scrollTo(scrollX, scrollY);
        });


        //clear previous content
        table.removeAllViews();
        leftUpperCellCont.removeAllViews();
        headerCont.removeAllViews();
        columnCont.removeAllViews();

        if (isHeaderEnabled) {
            //create Header
            if (fixedHeader) {
                if (fixedLeftUpperCell != null) {
                    View v = fixedLeftUpperCell.build(context);
                    if (headerConsumer != null) {
                        headerConsumer.accept(v);
                    }
                    leftUpperCellCont.addView(v);
                }
                addToRow(headerCont, context, header, headerConsumer);
            } else {
                TableRow headerRow = buildRow(context, header, headerConsumer);
                if (fixedFirstColumn) {
                    addToFixedColumn(context, columnCont, headerRow);
                }
                table.addView(headerRow);
            }
        }
        for (List<ITableCellBuilder> row : tableRows) {
            //create one row
            TableRow tableRow = buildRow(context, row, bodyConsumer);
            if (fixedFirstColumn) {
                addToFixedColumn(context, columnCont, tableRow);
            }
            table.addView(tableRow);
        }
    }

    /**
     * Interface for building cells for the table
     */
    public interface ITableCellBuilder {

        /**
         * Creates and returns the child view that will be used for the cell
         *
         * @param context The context
         * @return A child view for the cell
         */
        View build(Context context);
    }

    /**
     * Builds an empty table cell (a cell with no content to display)
     */
    public static class EmptyCellBuilder implements ITableCellBuilder {
        Optional<Integer> optWidth;

        private EmptyCellBuilder(Optional<Integer> width) {
            optWidth = width;
        }

        public EmptyCellBuilder() {
            this(Optional.<Integer>empty());
        }

        public EmptyCellBuilder(int width) {
            this(Optional.ofValue(width));
        }

        @Override
        public View build(Context context) {
            View space = new View(context);
            if (!optWidth.isEmpty()) {
                space.setMinimumWidth(optWidth.getValue());
            }
            return space;
        }
    }
}
