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


import android.content.Context;
import android.view.View;

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