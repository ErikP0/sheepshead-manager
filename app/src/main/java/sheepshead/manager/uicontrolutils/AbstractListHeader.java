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

public abstract class AbstractListHeader {

    @LayoutRes
    private int headerLayout;

    public AbstractListHeader(@LayoutRes int layout) {
        headerLayout = layout;
    }

    public
    @LayoutRes
    int getHeaderLayout() {
        return headerLayout;
    }

    public abstract void fill(View headerView);

    public abstract void save(Bundle saveTo, View headerView);

    public abstract void load(Bundle loadFrom, View headerView);

    protected <W extends View> W findView(View v, @IdRes int id) {
        return (W) (v.findViewById(id));
    }
}
