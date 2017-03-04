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

package sheepshead.manager.export;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import sheepshead.manager.appcore.ActivityDescriptor;

public abstract class ChainableExport<U, V> {

    protected
    @Nullable
    ChainableExport<V, ?> nextExport;

    protected ChainableExport(@Nullable ChainableExport<V, ?> next) {
        nextExport = next;
    }

    protected ChainableExport() {
        this(null);
    }

    public static <U, V> ActivityDescriptor.MenuAction asMenuAction(final @NonNull ChainableExport<U, V> e, final U arg) {
        return new ActivityDescriptor.MenuAction() {
            @Override
            public void onAction(Activity activity) {
                e.startActionChain(arg, activity);
            }
        };
    }

    protected abstract void performAction(U input, Activity activity);

    public void startActionChain(U input, Activity activity) {
        performAction(input, activity);
    }

    protected void nextAction(V output, Activity activity) {
        if (nextExport != null) {
            nextExport.startActionChain(output, activity);
        }
    }
}
