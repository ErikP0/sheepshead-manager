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

package sheepshead.manager.activities;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sheepshead.manager.R;
import sheepshead.manager.uicontrolutils.AbstractListHeader;
import sheepshead.manager.uicontrolutils.AbstractListItem;
import sheepshead.manager.uicontrolutils.MultiViewFixedSizeExpanableListAdapter;

/**
 * Customized adapter for the CreateSession-Activity.
 * This adapter can be created via its Builder ({@link CreateSessionListAdapter.Builder}
 */
class CreateSessionListAdapter extends MultiViewFixedSizeExpanableListAdapter<CreateSessionListAdapter.CustomListHeader, AbstractListItem> {

    private CreateSessionListAdapter(Context c, CustomListHeader[] header, AbstractListItem[] items) {
        super(c, header, items);
    }

    /**
     * Applies a new {@link ListItemState} to the header at index <code>position</code>
     *
     * @param position The position of the header-item pair in the list. The first pair has position 0,
     *                 the last pair has position <code>getGroupCount()-1</code>
     * @param newState The new state for the header
     */
    void changeItemState(int position, @NonNull ListItemState newState) {
        View headerView = getHeaderView(position);
        getHeader(position).setState(headerView, newState);
    }

    /**
     * A state for the completeness of each selection header-item-pair.
     */
    enum ListItemState {
        /**
         * Indicates that the selection is missing something or is invalid
         */
        PENDING(android.R.drawable.presence_away),
        /**
         * Indicates that the selection is valid
         */
        COMPLETE(android.R.drawable.presence_online);

        @DrawableRes
        private int iconId;

        ListItemState(@DrawableRes int id) {
            iconId = id;
        }

        public
        @DrawableRes
        int getIcon() {
            return iconId;
        }
    }

    /**
     * A custom header for the CreateSession-Activity. This header consists of a title message and a
     * state icon that shows the completeness of the current section
     */
    static class CustomListHeader extends AbstractListHeader {
        private static final
        @IdRes
        int titleElement = R.id.ListGroupHeader_header_textView;
        private static final
        @IdRes
        int iconElement = R.id.ListGroupHeader_header_icon;

        private String title;
        private ListItemState headerState;

        CustomListHeader(String headerText, ListItemState state) {
            super(R.layout.list_group_header);
            title = headerText;
            headerState = state;
        }

        void setState(View v, @NonNull ListItemState newState) {
            if (!headerState.equals(newState)) {
                headerState = newState;
                updateIcon(v);
            }
        }

        private void updateIcon(View v) {
            ImageView icon = (ImageView) v.findViewById(iconElement);
            icon.setImageBitmap(BitmapFactory.decodeResource(v.getResources(), headerState.getIcon()));
        }

        @Override
        public void fill(View headerView) {
            TextView headerTextView = findView(headerView, titleElement);
            headerTextView.setText(title);
            updateIcon(headerView);
        }

        @Override
        public void save(Bundle saveTo, View headerView) {

        }

        @Override
        public void load(Bundle loadFrom, View headerView) {

        }
    }

    /**
     * Convenience Builder for {@link CreateSessionListAdapter}.
     */
    static class Builder {
        private Context context;
        private List<CustomListHeader> headerList;
        private List<AbstractListItem> itemList;

        /**
         * Creates a new builder with the given Context c
         *
         * @param c The context
         */
        public Builder(@NonNull Context c) {
            context = c;
            headerList = new ArrayList<>();
            itemList = new ArrayList<>();
        }

        /**
         * Adds a new header-item-pair to the {@link CreateSessionListAdapter}
         *
         * @param headerText The title of the header
         * @param state      The state that this pair is in
         * @param item       A {@link AbstractListItem}-Implementation for the item part of this pair
         * @return the index of this pair. This index can be used to retrieve the pair later
         * @see CreateSessionListAdapter#getHeader(int)
         * @see CreateSessionListAdapter#getItem(int)
         */
        int addItem(String headerText, ListItemState state, @NonNull AbstractListItem item) {
            int index = headerList.size();
            headerList.add(new CustomListHeader(headerText, state));
            itemList.add(item);
            return index;
        }

        /**
         * Adds a new header-item-pair to the {@link CreateSessionListAdapter}.
         * This pair has the default state {@link CreateSessionListAdapter.ListItemState#PENDING}
         *
         * @param headerText The title of the header
         * @param item       A {@link AbstractListItem}-Implementation for the item part of this pair
         * @return the index of this pair. This index can be used to retrieve the pair later
         * @see CreateSessionListAdapter#getHeader(int)
         * @see CreateSessionListAdapter#getItem(int)
         */
        int addItem(String headerText, @NonNull AbstractListItem item) {
            return addItem(headerText, ListItemState.PENDING, item);
        }

        /**
         * @return a {@link CreateSessionListAdapter} containing all header-item-pairs that were added
         * prior to this call
         */
        CreateSessionListAdapter build() {
            CustomListHeader[] headers = new CustomListHeader[headerList.size()];
            AbstractListItem[] items = new AbstractListItem[itemList.size()];
            headers = headerList.toArray(headers);
            items = itemList.toArray(items);
            return new CreateSessionListAdapter(context, headers, items);
        }
    }
}
