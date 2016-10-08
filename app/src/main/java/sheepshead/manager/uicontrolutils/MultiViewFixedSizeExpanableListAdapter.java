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
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public class MultiViewFixedSizeExpanableListAdapter<H extends AbstractListHeader, I extends AbstractListItem> extends BaseExpandableListAdapter {

    private Context context;
    private View[] savedChildViews;
    private View[] savedHeaderViews;
    private I[] groupItems;
    private H[] headers;

    public MultiViewFixedSizeExpanableListAdapter(@NonNull Context context, @NonNull H[] itemHeader, @NonNull I[] items) {
        this.context = context;
        groupItems = items;
        headers = itemHeader;
        savedChildViews = new View[groupItems.length];
        savedHeaderViews = new View[groupItems.length];
    }

    public H getHeader(int position) {
        return headers[position];
    }

    public I getItem(int position) {
        return groupItems[position];
    }

    public void saveInstanceState(Bundle toSave) {
        for (int i = 0; i < groupItems.length; i++) {
            AbstractListHeader header = getHeader(i);
            AbstractListItem item = getItem(i);
            header.save(toSave, getOrCreateView(savedHeaderViews, i, header.getHeaderLayout()));
            item.save(toSave, getOrCreateView(savedChildViews, i, item.getLayout()));
        }
    }

    public void loadInstanceState(Bundle loadFrom) {
        for (int i = 0; i < groupItems.length; i++) {
            AbstractListHeader header = getHeader(i);
            AbstractListItem item = getItem(i);
            header.load(loadFrom, getOrCreateView(savedHeaderViews, i, header.getHeaderLayout()));
            item.load(loadFrom, getOrCreateView(savedChildViews, i, item.getLayout()));
        }
    }

    public View getHeaderView(int position) {
        return getOrCreateView(savedHeaderViews, position, getHeader(position).getHeaderLayout());
    }

    public View getItemView(int position) {
        return getOrCreateView(savedChildViews, position, getItem(position).getLayout());
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        AbstractListHeader headerItem = getHeader(groupPosition);
        View headerView = getOrCreateView(savedHeaderViews, groupPosition, headerItem.getHeaderLayout());

        headerItem.fill(headerView);
        return headerView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        AbstractListItem item = getItem(groupPosition);
        View itemView = getOrCreateView(savedChildViews, groupPosition, item.getLayout());


        return itemView;
    }

    private View getOrCreateView(View[] savedViews, int position, @LayoutRes int layout) {
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
        return groupItems.length;
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
