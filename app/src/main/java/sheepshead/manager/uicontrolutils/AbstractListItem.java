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
 * Data container for the use in any kind of list. Extend from this class to define custom list item
 * behaviour.
 * For the implementor:
 * Try to avoid saving a Widget as a member, there is no guarantee that {@link #save(Bundle, View)}
 * is called before {@link #load(Bundle, View)}, leaving the member possibly in a nullstate. Instead
 * use {@link #findView(View, int)} every time accessing a widget
 */
public abstract class AbstractListItem {

    /**
     * The layout of this list item
     */
    @LayoutRes
    private int layout;

    /**
     * Creates a new list item with the given layout
     *
     * @param layoutId Layout for this list item
     */
    public AbstractListItem(@LayoutRes int layoutId) {
        layout = layoutId;
    }

    /**
     * @return a resource layout id for this element
     */
    public
    @LayoutRes
    int getLayout() {
        return layout;
    }

    /**
     * Called when the View using this {@linkplain AbstractListItem} is closed/paused or performs
     * a context change.
     * The implementor should use this method to save item-specific values (like a state of a widget
     * that is part of the item).
     *
     * @param saveTo   A bundle to put in the values to save
     * @param itemView the view used for this item
     */
    public abstract void save(Bundle saveTo, View itemView);

    /**
     * Called when the View using this {@linkplain AbstractListItem} is reopened/resumed or gets
     * context changed to.
     * The implementor should use this method to load all item-specific values that were saved in
     * {@link #save(Bundle, View)}.
     *
     * @param loadFrom A bundle that contains the saved data
     * @param itemView the view used for this header
     */
    public abstract void load(Bundle loadFrom, View itemView);

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
