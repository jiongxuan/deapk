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

package jd.ide.intellij;



import i.am.jiongxuan.util.OSInfo;

import java.io.File;
import java.io.UnsupportedEncodingException;


/**
 * Java Decompiler tool, use native libs to achieve decompilation.
 * <p/>
 * <p>
 * Identify the native lib full path through IntelliJ helpers in
 * {@link SystemInfo}.
 * </p>
 */
public class JavaDecompiler {

    public static String JD_LIB_RELATIVE_PATH = "lib" + File.separator + "jd-core" + File.separator
            + osIdentifier() + File.separator + architecture() + File.separator + libFileName();

    public JavaDecompiler() {
        String pluginPath = JavaDecompiler.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            pluginPath = java.net.URLDecoder.decode(pluginPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        pluginPath = new File(pluginPath).getParent();

        String libPath = pluginPath + File.separator
                + JD_LIB_RELATIVE_PATH;
        loadLibrary(pluginPath, libPath);
    }

    private void loadLibrary(String pluginPath, String libPath) {
        try {
            System.load(libPath);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Something got wrong when loading the Java Decompiler native lib, " +
                            "\nlookup path : " + libPath +
                            "\nplugin path : " + pluginPath, e);
        }
    }

    /**
     * Library filename, depending on the OS identifier.
     *
     * @return lib filename.
     */
    private static String libFileName() {
        if (OSInfo.isMacOSX()) {
            return "libjd-intellij.jnilib";
        } else if (OSInfo.isWindows()) {
            return "jd-intellij.dll";
        } else if (OSInfo.isLinux()) {
            return "libjd-intellij.so";
        }
        throw new IllegalStateException("OS not supported");
    }

    /**
     * Architecture, either 32bit or 64bit.
     *
     * @return x86 or x86_64 for respectively 32bit 64bit architecture.
     */
    private static String architecture() {
        if (OSInfo.is32Bit()) {
            return "x86";
        } else if (OSInfo.is64Bit()) {
            return "x86_64";
        }
        throw new IllegalStateException(
                "Unsupported architecture, only x86 and x86_64 architectures are supported.");
    }

    /**
     * Identify the OS.
     *
     * @return Either macosx, win32, linux
     */
    private static String osIdentifier() {
        if (OSInfo.isMacOSX()) {
            return "macosx";
        } else if (OSInfo.isWindows()) {
            return "win32";
        } else if (OSInfo.isLinux()) {
            return "linux";
        }
        throw new IllegalStateException(
                "Unsupported OS, only windows, linux and mac OSes are supported.");
    }

    /**
     * Actual call to the native lib.
     *
     * @param basePath Path to the root of the classpath, either a path to a
     *            directory or a path to a jar file.
     * @param internalClassName internal name of the class.
     * @return Decompiled class text.
     */
    public native String decompile(String basePath, String internalClassName);
}

