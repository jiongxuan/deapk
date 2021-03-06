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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jd.ide.intellij.JavaDecompiler;

/**
 * @author Jiongxuan Zhang
 */
public class DecomplieEnumeration implements Enumeration<DecomplieEntry> {

    private JavaDecompiler mDecompiler;
    private File mJarPath;

    private Map<String, String> mJavaToClassPathMap = new HashMap<String, String>();
    private Iterator<Entry<String, String>> mIterator;

    public DecomplieEnumeration(JavaDecompiler decompiler, File jarPath) throws IOException {
        mDecompiler = decompiler;
        mJarPath = jarPath;

        ZipFile zipFile = new ZipFile(mJarPath);
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
            String entryName = zipEntry.getName();
            if (entryName.endsWith(".class")) {
                String classPath = entryName.replaceAll("\\$.*\\.class$", ".class");
                String javaPath  = classPath.toString().replaceAll("\\.class$", ".java");
                if (!mJavaToClassPathMap.containsKey(javaPath)) {
                    mJavaToClassPathMap.put(javaPath, classPath);
                }
            }
        }
        zipFile.close();

        mIterator = mJavaToClassPathMap.entrySet().iterator();
    }

    @Override
    public boolean hasMoreElements() {
        return mIterator.hasNext();
    }

    @Override
    public DecomplieEntry nextElement() {
        while (mIterator.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) mIterator.next();
            String javaPath = entry.getKey();
            String classPath = entry.getValue();
            String content = mDecompiler.decompile(mJarPath.toString(), classPath.toString());
            return new DecomplieEntry(javaPath, content);
        }

        return null;
    }

    public int getCount() {
        return mJavaToClassPathMap.size();
    }
}
