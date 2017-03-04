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

package sheepshead.manager.serialization;


import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import sheepshead.manager.session.Session;

public final class SerializationActions {
    public static final String sessionSaveDirectory = "saved";

    //class will only contain static methods
    private SerializationActions() {
    }


    public static Session loadSession(@NonNull File from, @NonNull CSVRules format) throws IOException, SessionDataCorruptedException {
        SessionCSVReader reader = new SessionCSVReader(format);
        try {
            FileInputStream fis = new FileInputStream(from);
            Session session = reader.readFrom(fis);
            fis.close();
            return session;
        } catch (IOException | SessionDataCorruptedException e) {
            System.out.println("Could not load save file: " + e.getMessage());
            e.printStackTrace();
            //rename the file and keep it for debugging purposes
            String newName = "corrupted_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                    .format(Calendar.getInstance().getTime()) + from.getName();
            File corr = new File(from.getParentFile(), newName);
            boolean success = from.renameTo(corr);
            System.out.print("Renamed corrupted file to " + newName);
            if (success) {
                System.out.println(" with success");
            } else {
                System.out.println(" with a failure");
            }
            throw e;
        }
    }
}
