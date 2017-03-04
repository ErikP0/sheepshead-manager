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
import android.support.annotation.Nullable;

/**
 * A ChainableExport is one element of a chain of possible asynchronous actions that in the end lead
 * to an file/session export.
 * Each chain element receives an argument from its caller, computes or performs its action and
 * supplies the next chain element with the computed result.
 * The start element receives its argument from the caller of the whole chain. The concept can be
 * compared to a function composition where there desired behaviour is accomplished through
 * side-effects and no "end result" is computed.<br>
 * <br>
 * Example:
 * The email export is implemented by chaining
 * <li>a dialog for configuring the export format {@link CSVRuleDialog} </li>
 * <li>a file export for saving the session to a location from where it can be accessed by mail clients {@link FileExport}</li>
 * <li>the select email client action {@link EmailExport}</li>
 * <br>
 * Note that the last two chain elements are computed asynchronously as the dialog needs user input.
 * <br>
 * <br>
 * Implementation details:<br>
 * {@link #performAction(U, Activity)}: This specifies the action of each ChainableExport implementation.
 * When the computation is finished, call {@link #nextAction(V, Activity)} to call the next action
 *
 * @param <U> The input argument type
 * @param <V> The output argument type
 */
public abstract class ChainableExport<U, V> {

    /**
     * The next chain action, or null if this is the last element in the chain
     */
    private
    @Nullable
    ChainableExport<V, ?> nextExport;

    /**
     * Creates a new ChainableExport with the given action as the next action
     *
     * @param next The next chain element, or null if this is supposed to be the last element in the chain
     */
    protected ChainableExport(@Nullable ChainableExport<V, ?> next) {
        nextExport = next;
    }

    /**
     * Creates this ChainableExport as the last element in the export action chain
     */
    protected ChainableExport() {
        this(null);
    }

    /**
     * Performs the action with the given parameter.<br>
     * Note: Use {@link #nextAction(V, Activity)} to pass the computation result to the next chain
     * element
     *
     * @param input    The given parameter
     * @param activity The current activity
     */
    protected abstract void performAction(U input, Activity activity);

    /**
     * Starts the action chain downwards from this element.
     *
     * @param input    The input for the computation
     * @param activity The current activity
     */
    public void startActionChain(U input, Activity activity) {
        performAction(input, activity);
    }

    /**
     * Calls and forwards the output to the next chain element, if there is one
     *
     * @param output   The computed output
     * @param activity The current activity
     */
    protected void nextAction(V output, Activity activity) {
        if (nextExport != null) {
            nextExport.startActionChain(output, activity);
        }
    }
}
