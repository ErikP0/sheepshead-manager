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


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;

/**
 * Data container used by {@link MultiViewFixedSizeExpanableListAdapter} for defining custom header
 * behaviour.
 * For the implementor:
 * Try to avoid saving a Widget as a member, {@link #fill(View)} might not be called before a call of
 * {@link #save(Bundle, View)} or {@link #load(Bundle, View)}, leaving the member possibly in a null
 * state. Instead use {@link #findView(View, int)} every time accessing a widget
 */
public abstract class AbstractListHeader {

    /**
     * A layout id this header element represents
     */
    @LayoutRes
    private int headerLayout;

    /**
     * Creates a new header element with the given layout resource id
     *
     * @param layout
     */
    public AbstractListHeader(@LayoutRes int layout) {
        headerLayout = layout;
    }

    /**
     * @return a resource layout id for this element
     */
    public
    @LayoutRes
    int getHeaderLayout() {
        return headerLayout;
    }

    /**
     * Used to populate/fill the given <code>headerView</code>/ or other widgets with values
     *
     * @param headerView the view used for this header, the headerView layout id equals {@link #getHeaderLayout()}
     */
    public abstract void fill(View headerView);

    /**
     * Called when the View using this {@linkplain AbstractListHeader} is closed/paused or performs
     * a context change.
     * The implementor should use this method to save header-specific values (like a state of a widget
     * that is part of the header).
     *
     * @param saveTo     A bundle to put in the values to save
     * @param headerView the view used for this header
     */
    public abstract void save(Bundle saveTo, View headerView);

    /**
     * Called when the View using this {@linkplain AbstractListHeader} is reopened/resumed or gets
     * context changed to.
     * The implementor should use this method to load all header-specific values that were saved in
     * {@link #save(Bundle, View)}.
     *
     * @param loadFrom   A bundle that contains the saved data
     * @param headerView the view used for this header
     */
    public abstract void load(Bundle loadFrom, View headerView);

    /**
     * Looks for a view with <code>id</code> as a child of <code>v</code>.
     *
     * @param v   The view to look into
     * @param id  The id of the view to find
     * @param <W> The Type of the view to find
     * @return
     */
    protected <W extends View> W findView(View v, @IdRes int id) {
        return (W) (v.findViewById(id));
    }
}
