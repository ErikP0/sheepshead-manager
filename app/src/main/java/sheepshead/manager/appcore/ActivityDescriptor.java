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

package sheepshead.manager.appcore;


import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import sheepshead.manager.utils.Optional;

public class ActivityDescriptor {

    @NonNull
    private Optional<Integer> toolbarId;
    @NonNull
    private Optional<Integer> toolbarTitle;

    @LayoutRes
    private int layoutId;
    private boolean hasBackToParent;

    public ActivityDescriptor(@LayoutRes int layout) {
        layoutId = layout;
        toolbarId = Optional.empty();
        toolbarTitle = Optional.empty();
        hasBackToParent = false;
    }

    public ActivityDescriptor toolbar(@IdRes int id) {
        toolbarId = Optional.ofValue(id);
        return this;
    }

    public ActivityDescriptor title(@StringRes int id) {
        toolbarTitle = Optional.ofValue(id);
        return this;
    }

    public ActivityDescriptor enableNavigationBackToParent() {
        hasBackToParent = true;
        return this;
    }

    @LayoutRes
    int getLayoutId() {
        return layoutId;
    }

    @NonNull
    Optional<Integer> getToolbarId() {
        return toolbarId;
    }

    Optional<Integer> getTitle() {
        return toolbarTitle;
    }

    boolean hasNavigationBackToParentEnabled() {
        return hasBackToParent;
    }
}
