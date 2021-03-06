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

package sheepshead.manager.appcore;


import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.util.Map;
import java.util.TreeMap;

import sheepshead.manager.utils.Optional;

/**
 * Attribute object for {@link AbstractBaseActivity}.
 * Contains information about toolbar title and back-to-parent navigation.
 * Can be used as static final constant for each activity.
 * Not all setters must be called, attributes that were not set, will be filled with appropriate default values
 */
public class ActivityDescriptor {

    /**
     * The id of the container containing the toolbar widget, if any
     */
    @NonNull
    private Optional<Integer> toolbarId;
    /**
     * The title of the toolbar, if any
     */
    @NonNull
    private Optional<Integer> toolbarTitle;

    /**
     * The menu-resource-id describing the options menu for the toolbar, if available
     */
    @NonNull
    private Optional<Integer> toolbarMenuId;

    /**
     * Describes the actions associated with the option menu items (specific their ids).
     */
    @NonNull
    private Map<Integer, MenuAction> actions;

    /**
     * The layout of this activity
     */
    @LayoutRes
    private int layoutId;
    /**
     * Indicates if this activity is meant to have an "Up"-Navigation in the toolbar
     */
    private boolean hasBackToParent;

    /**
     * Creates a new {@link AbstractBaseActivity} parameter object.
     * Only the layout is mandatory
     *
     * @param layout The layout id of the activity
     */
    public ActivityDescriptor(@LayoutRes int layout) {
        layoutId = layout;
        toolbarId = Optional.empty();
        toolbarTitle = Optional.empty();
        toolbarMenuId = Optional.empty();
        actions = new TreeMap<>();
        hasBackToParent = false;
    }

    /**
     * Sets the given menu to be the options menu of the toolbar
     *
     * @param id The id of the menu resource describing the menu structure
     * @return this instance for chaining convenience
     */
    public ActivityDescriptor toolbarMenu(@MenuRes int id) {
        toolbarMenuId = Optional.ofValue(id);
        return this;
    }

    /**
     * Assigns an action to a option menu item with the given id
     *
     * @param menuItem The id of the option item
     * @param action   The associated action
     * @return this instance for chaining convenience
     * @throws IllegalArgumentException If there already is an action associated with the given item id
     */
    public ActivityDescriptor menuAction(@IdRes int menuItem, @NonNull MenuAction action) {
        if (!actions.containsKey(menuItem)) {
            actions.put(menuItem, action);
            return this;
        } else {
            throw new IllegalArgumentException("Duplicate action for menu id " + menuItem);
        }
    }

    /**
     * Sets the id of a ViewGroup or View containing the toolbar widget
     *
     * @param id The id of the toolbar widget container
     * @return this instance for chaining convenience
     */
    public ActivityDescriptor toolbar(@IdRes int id) {
        toolbarId = Optional.ofValue(id);
        return this;
    }

    /**
     * Sets the id of the title that will be displayed in the toolbar
     *
     * @param id The id of the title for this activity
     * @return this instance for chaining convenience
     */
    public ActivityDescriptor title(@StringRes int id) {
        toolbarTitle = Optional.ofValue(id);
        return this;
    }

    /**
     * Enables the "Up"-Navigation to the parent activity described in the AndroidManifest.xml
     *
     * @return this instance for chaining convenience
     */
    public ActivityDescriptor enableNavigationBackToParent() {
        hasBackToParent = true;
        return this;
    }

    /**
     * @return the layout id
     */
    @LayoutRes
    int getLayoutId() {
        return layoutId;
    }

    /**
     * @return the id of the toolbar widget, if any
     */
    @NonNull
    Optional<Integer> getToolbarId() {
        return toolbarId;
    }

    /**
     * @return the string id of the toolbar title, if any
     */
    @NonNull
    Optional<Integer> getTitle() {
        return toolbarTitle;
    }

    /**
     * @return the menu resource id of the option menu of the toolbar, if any
     */
    @NonNull
    Optional<Integer> getToolbarMenuId() {
        return toolbarMenuId;
    }

    /**
     * @return true if this activity defined a parent in AndroidManifest.xml
     */
    boolean hasNavigationBackToParentEnabled() {
        return hasBackToParent;
    }

    /**
     * Returns the associated action for an item of the toolbar's options menu of this activity
     *
     * @param menuId The id of the menu item
     * @return The associated action for the given menu item id, if any
     */
    @NonNull
    Optional<MenuAction> getActionFor(@IdRes int menuId) {
        return Optional.ofNullable(actions.get(menuId));
    }

    /**
     * Specifies the action that will be called whenever the user selects a menu item of the toolbar's
     * options menu
     */
    public interface MenuAction {
        /**
         * Will be called when the user selects a menu item with which this action is associated
         *
         * @param activity The current activity
         */
        void onAction(Activity activity);
    }


}
