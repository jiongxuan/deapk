/*
 * Deapk - A tools for "APK -> Android Project"
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

package i.am.jiongxuan.deapk.jd.core;

/**
 * @author Jiongxuan Zhang
 */
public class DecomplieEntry {
    private String mJavaPath;
    private String mContent;

    public DecomplieEntry(String javaPath, String content) {
        mJavaPath = javaPath;
        mContent = content;
    }

    public String getJavaPath() {
        return mJavaPath;
    }

    public String getContent() {
        return mContent;
    }
}
