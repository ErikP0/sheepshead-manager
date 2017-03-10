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
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

/**
 * Adapter for a {@link android.widget.ExpandableListView}.
 * Some naming:
 * A {@linkplain android.widget.ExpandableListView} is a list of header-item-pairs.
 * By default, only the header of each pair is visible in the list. By clicking on the header, the
 * corresponding item expands below its header.
 * <p>
 * This adapter supports a (at construction time) fixed amount of header-item-pairs. Each header/item
 * can have a different layout. The inflated views of each header and item are saved, so there is no
 * need to save/load the views state when a item is closed/expanded. However, if there is the need for
 * saving the header/item state, all headers and items will be notified using {@link AbstractListHeader#save(Bundle, View)},
 * {@link AbstractListHeader#load(Bundle, View)}, {@link AbstractListItem#save(Bundle, View)} and
 * {@link AbstractListItem#load(Bundle, View)}.
 *
 * @param <H> The type of Header used
 * @param <I> The type of Item used
 */
public class MultiViewFixedSizeExpanableListAdapter<H extends AbstractListHeader, I extends AbstractListItem> extends BaseExpandableListAdapter {

    private Context context;
    /**
     * Container for the views of the items
     */
    private View[] savedItemViews;
    /**
     * Container for the views of the headers
     */
    private View[] savedHeaderViews;
    /**
     * All items
     */
    private I[] items;
    /**
     * All headers
     */
    private H[] headers;

    /**
     * Constructs this adapter using <code>itemHeader</code> and <code>items</code> as header-item-pairs
     * for this list. The first header-item-pair would be <code>itemHeader[0], items[0]</code> and so on.
     *
     * @param context    A context for this adapter
     * @param itemHeader all headers for the pairs
     * @param items      all items for the pairs
     * @throws IllegalArgumentException if <code>itemHeader</code> and <code>items</code> do not
     *                                  have the same length
     */
    public MultiViewFixedSizeExpanableListAdapter(@NonNull Context context, @NonNull H[] itemHeader, @NonNull I[] items) {
        if (itemHeader.length != items.length) {
            throw new IllegalArgumentException("The arrays containing headers and items must have the same length");
        }
        this.context = context;
        this.items = items;
        headers = itemHeader;
        savedItemViews = new View[this.items.length];
        savedHeaderViews = new View[this.items.length];
    }

    /**
     * @param position The position of the header-item pair in the list. The first pair has position 0,
     *                 the last pair has position <code>getGroupCount()-1</code>
     * @return The header data container at list index <code>position</code>
     */
    public H getHeader(int position) {
        return headers[position];
    }

    /**
     * @param position The position of the header-item pair in the list. The first pair has position 0,
     *                 the last pair has position <code>getGroupCount()-1</code>
     * @return The item data container at list index <code>position</code>
     */
    public I getItem(int position) {
        return items[position];
    }

    /**
     * Saves the states of all header-item-pairs into the given bundle
     *
     * @param toSave bundle to save the states into
     */
    public void saveInstanceState(Bundle toSave) {
        for (int i = 0; i < items.length; i++) {
            AbstractListHeader header = getHeader(i);
            AbstractListItem item = getItem(i);
            header.save(toSave, getOrCreateView(savedHeaderViews, i, header.getHeaderLayout()));
            item.save(toSave, getOrCreateView(savedItemViews, i, item.getLayout()));
        }
    }

    /**
     * Loades the states of all header-item-pairs using data from the given bundle
     *
     * @param loadFrom bundle containing the saved data
     */
    public void loadInstanceState(Bundle loadFrom) {
        for (int i = 0; i < items.length; i++) {
            AbstractListHeader header = getHeader(i);
            AbstractListItem item = getItem(i);
            header.load(loadFrom, getOrCreateView(savedHeaderViews, i, header.getHeaderLayout()));
            item.load(loadFrom, getOrCreateView(savedItemViews, i, item.getLayout()));
        }
    }

    /**
     * @param position The position of the header-item pair in the list. The first pair has position 0,
     *                 the last pair has position <code>getGroupCount()-1</code>
     * @return the view used by the header at list index <code>position</code>
     */
    public View getHeaderView(int position) {
        return getOrCreateView(savedHeaderViews, position, getHeader(position).getHeaderLayout());
    }

    /**
     * @param position The position of the header-item pair in the list. The first pair has position 0,
     *                 the last pair has position <code>getGroupCount()-1</code>
     * @return the view used by the item at list index <code>position</code>
     */
    public View getItemView(int position) {
        return getOrCreateView(savedItemViews, position, getItem(position).getLayout());
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //the group view is equal to the header view
        AbstractListHeader headerItem = getHeader(groupPosition);
        //get the saved header view or inflate a new one if it does not exist
        View headerView = getOrCreateView(savedHeaderViews, groupPosition, headerItem.getHeaderLayout());

        //fill it with custom data
        headerItem.fill(headerView);
        return headerView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //the child view is equal to the item view
        AbstractListItem item = getItem(groupPosition);
        //get the saved item view or inflate a new one if it does not exist
        View itemView = getOrCreateView(savedItemViews, groupPosition, item.getLayout());

        return itemView;
    }

    /**
     * Returns a valid view from <code>savedViews[position]</code>. If the array holds a null value
     * at index <code>position</code> a new view is inflated using <code>layout</code>, stored in the
     * array and then returned
     *
     * @param savedViews views that may or may not hold the view to return
     * @param position   index where to look in <code>savedViews</code>
     * @param layout     layout id for inflating if no view is found at <code>position</code>
     * @return a valid view
     */
    private
    @NonNull
    View getOrCreateView(View[] savedViews, int position, @LayoutRes int layout) {
        View result = savedViews[position];
        if (result == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            savedViews[position] = result = infalInflater.inflate(layout, null);
        }
        return result;
    }

    @Override
    public int getGroupCount() {
        return items.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getHeader(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getItem(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
