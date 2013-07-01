/*
 * De-Apk - A tools for "APK -> Android Project"
 * Copyright (c) 2013-2014 Jiongxuan Zhang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package i.am.jiongxuan.deapk;

/**
 * @author Jiongxuan Zhang
 *
 */
public class PercentWritter {
    private int mLastBits = 0;

    public void print(int current, int total) {
        String remove = getRemoveString();

        String percentString = "(" + (int)(((double) current / total) * 100)
                + "%)";
        mLastBits = percentString.length();
        System.out.print(remove + percentString);
    }

    private String getRemoveString() {
        String remove = "";
        for (int i = 0; i < mLastBits; i++) {
            remove += "\b";
        }
        return remove;
    }

    public void end(boolean remove) {
        System.out.println(remove ? getRemoveString() : "");
        mLastBits = 0;
    }
}
