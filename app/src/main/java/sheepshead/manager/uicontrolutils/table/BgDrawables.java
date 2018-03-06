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

/**
 * Collection of drawable resources for the different states a cell can have
 */
public class BgDrawables {
    /**
     * The normal state for a content cell (no header)
     */
    private @DrawableRes
    int bgCell;
    /**
     * The state of a header cell
     */
    private @DrawableRes
    int bgHeader;
    /**
     * The state of a header cell, if the table is sorted by the column of this header cell in ascending order
     */
    private @DrawableRes
    int bgHeaderAsc;
    /**
     * The state of a header cell, if the table is sorted by the column of this header cell in descending order
     */
    private @DrawableRes
    int bgHeaderDesc;

    /**
     * @param bgCell       background of a normal cell
     * @param bgHeader     background of a header cell
     * @param bgHeaderAsc  background of a header cell when the table is sorted in ascending order
     * @param bgHeaderDesc background of a header cell when the table is sorted in descending order
     */
    public BgDrawables(int bgCell, int bgHeader, int bgHeaderAsc, int bgHeaderDesc) {
        this.bgCell = bgCell;
        this.bgHeader = bgHeader;
        this.bgHeaderAsc = bgHeaderAsc;
        this.bgHeaderDesc = bgHeaderDesc;
    }

    /**
     * @return background of a normal cell
     */
    @DrawableRes
    int getBgCell() {
        return bgCell;
    }

    /**
     * @return background of a header cell
     */
    @DrawableRes
    int getBgHeader() {
        return bgHeader;
    }

    /**
     * @return background of a header cell when the table is sorted in ascending order
     */
    @DrawableRes
    int getBgHeaderAsc() {
        return bgHeaderAsc;
    }

    /**
     * @return background of a header cell when the table is sorted in descending order
     */
    @DrawableRes
    int getBgHeaderDesc() {
        return bgHeaderDesc;
    }
}
