/*
 * Copyright 2016  Erik Pohle
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
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

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
    private int maxWidth = 0;

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
     * Enables the header for this table. The header is disabled by default
     */
    public void enableHeader() {
        isHeaderEnabled = true;
        header = new ArrayList<>();
    }

    /**
     * Appends a single cell to the header.
     *
     * @param builder The builder for this single cell
     * @throws IllegalStateException If the header is disabled
     * @see #enableHeader()
     */
    public void putHeaderCell(@NonNull ITableCellBuilder builder) {
        if (!isHeaderEnabled) {
            throw new IllegalStateException("Enable Header first");
        }
        checkIfNull(builder);
        header.add(builder);
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

    private TableRow buildRow(Context c, List<ITableCellBuilder> cells, @Nullable Consumer<View> consumer) {
        TableRow row = new TableRow(c);
        row.setLayoutParams(layoutParams);
        for (ITableCellBuilder builder : cells) {
            View child = builder.build(c);
            if (consumer != null) {
                consumer.accept(child);
            }
            row.addView(child);
        }
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
        return row;
    }

    /**
     * Builds the table. Creates a new table, adds the header to the top (if enabled) and the appends
     * the body
     *
     * @return The generated table view
     */
    public
    @NonNull
    TableLayout build(Context c) {
        return build(c, null, null);
    }

    /**
     * Builds the table. If the given table layout is not null, the contents are appended to the existing table.
     * If it is null, a new table is created. In both cases the table that was edited, is returned.
     * If the header is enabled, its content is inserted into the top of the table.
     *
     * @param context       The current context
     * @param existingTable An existing table layout to append the contents, if null a new table will be created
     * @return The edited table
     */
    public
    @NonNull
    TableLayout build(Context context, @NonNull TableLayout existingTable) {
        return build(context, existingTable, null);
    }

    /**
     * Builds the table. If the given table layout is not null, the contents are appended to the existing table.
     * If it is null, a new table is created. In both cases the table that was edited, is returned.
     * If the external header is null, the header is added to the top of the table, if not the header
     * contents are appended to the existing header. If the header is disabled, no header content is appended in any case.
     *
     * @param context        The current context
     * @param existing       An existing table layout to append the contents, if null a new table will be created
     * @param externalHeader An existing (external) header to append the header content,
     *                       if null the header will be inserted into the top of the table
     * @return The edited table
     */
    public
    @NonNull
    TableLayout build(Context context, @Nullable TableLayout existing, @Nullable ViewGroup externalHeader) {
        //check if the last row is the biggest one
        if (currentRow != null) {
            maxWidth = Math.max(maxWidth, currentRow.size());
        }

        TableLayout table = existing;
        ViewGroup headerContainer = externalHeader;
        if (table == null) {
            table = new TableLayout(context);
        }
        if (headerContainer == null) {
            headerContainer = table;
        }
        if (isHeaderEnabled) {
            //create Header
            TableRow headerRow = buildRow(context, header, headerConsumer);
            headerContainer.addView(headerRow);
        }
        for (List<ITableCellBuilder> row : tableRows) {
            //create one row
            TableRow tableRow = buildRow(context, row, bodyConsumer);
            table.addView(tableRow);
        }
        return table;
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
