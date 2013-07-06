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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Jiongxuan Zhang
 */
public class FileUtils {

    static final int BUFFER = 2048;

    public static void writeTo(InputStream inputStream,
            OutputStream outputStream) throws IOException {
        byte[] buf = new byte[BUFFER];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
        }

        inputStream.close();
        outputStream.close();
    }

    public static void writeTo(byte[] data, OutputStream outputStream)
            throws IOException {
        writeTo(new ByteArrayInputStream(data), outputStream);
    }

    public static void writeTo(String text, OutputStream outputStream)
            throws IOException {
        writeTo(text.getBytes(), outputStream);
    }

    public static void copyTo(String sourcePath, String targetPath)
            throws IOException {
        writeTo(new FileInputStream(sourcePath), new FileOutputStream(
                targetPath));
    }

    public static boolean deleteDir(String dir) {
        return deleteDir(new File(dir));
    }

    public static boolean deleteDir(File dir) {
        if (!dir.exists()) {
            return true;
        }
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
