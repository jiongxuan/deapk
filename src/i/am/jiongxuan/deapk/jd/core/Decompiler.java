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

import java.io.IOException;

import jd.ide.intellij.JavaDecompiler;

public class Decompiler {
    private JavaDecompiler mDecompiler;
    private String mJarPath;
    private DecomplieEnumeration mDecomplieEnumeration;

    public Decompiler(String jarPath) {
        mDecompiler = new JavaDecompiler();
        mJarPath = jarPath;
    }

    public DecomplieEnumeration getEnumeration() throws IOException {
        if (mDecomplieEnumeration == null) {
            mDecomplieEnumeration = new DecomplieEnumeration(mDecompiler, mJarPath);
        }

        return mDecomplieEnumeration;
    }

    public int getCount() throws IOException {
        return getEnumeration().getCount();
    }
}

