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

public class DynamicSizeTableBuilder {

    private final TableRow.LayoutParams layoutParams;
    private final Consumer<View> headerConsumer;
    private final Consumer<View> bodyConsumer;
    private List<ITableCellBuilder> header;
    private boolean isHeaderEnabled;
    private List<List<ITableCellBuilder>> tableRows;
    private List<ITableCellBuilder> currentRow;
    private int maxWidth = 0;

    public DynamicSizeTableBuilder(@Nullable TableRow.LayoutParams params, @Nullable Consumer<View> headerSpecs, @Nullable Consumer<View> bodySpecs) {
        header = new ArrayList<>();
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

    public void enableHeader() {
        isHeaderEnabled = true;
    }

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

    public void startRow() {
        if (currentRow != null) {
            maxWidth = Math.max(maxWidth, currentRow.size());
        }
        currentRow = new ArrayList<>();
        tableRows.add(currentRow);
    }

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

    public
    @NonNull
    TableLayout build(Context c) {
        return build(c, null, null);
    }

    public
    @NonNull
    TableLayout build(Context context, @NonNull TableLayout existingTable) {
        return build(context, existingTable, null);
    }

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

    public interface ITableCellBuilder {

        View build(Context context);
    }

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
