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

package i.am.jiongxuan.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * @author Jiongxuan Zhang
 */
public class UnZip {
    public static void extract(String fileName, String unZipDir) throws IOException {
        File f = new File(unZipDir);
        if (!f.exists()) {
            f.mkdirs();
        }

        ZipEntry entry;
        ZipFile zipfile = new ZipFile(fileName);
        Enumeration<? extends ZipEntry> entries = zipfile.entries();
        while (entries.hasMoreElements()) {
            entry = (ZipEntry) entries.nextElement();

            File file = new File(unZipDir + entry.getName());
            if (file.exists()) {
                continue;
            }

            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            FileUtils.writeTo(zipfile.getInputStream(entry), new FileOutputStream(file));
        }
        zipfile.close();
    }

    public static boolean makeDir(String unZipDir) {
        boolean b = false;
        try {
            File f = new File(unZipDir);
            if (!f.exists()) {
                b = f.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return b;
        }
        return b;
    }
}
