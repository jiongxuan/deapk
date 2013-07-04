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

/**
 * @author Jiongxuan Zhang
 */
public class OSInfo {

    public enum Platform {
        Any("any"),
        Linux("Linux"),
        Mac_OS("Mac OS"),
        Mac_OS_X("Mac OS X"),
        Windows("Windows"),
        OS2("OS/2"),
        Solaris("Solaris"),
        SunOS("SunOS"),
        MPEiX("MPE/iX"),
        HP_UX("HP-UX"),
        AIX("AIX"),
        OS390("OS/390"),
        FreeBSD("FreeBSD"),
        Irix("Irix"),
        Digital_Unix("Digital Unix"),
        NetWare_411("NetWare"),
        OSF1("OSF1"),
        OpenVMS("OpenVMS"),
        Others("Others");

        private Platform(String desc) {
            this.description = desc;
        }

        public String toString() {
            return description;
        }

        private String description;
    }

    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String ARCH = System.getProperty("os.arch").toLowerCase();

    private static OSInfo _instance = new OSInfo();

    private Platform platform;

    private OSInfo() {
    }

    public static boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    public static boolean isMacOS() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0;
    }

    public static boolean isMacOSX() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

    public static boolean isOS2() {
        return OS.indexOf("os/2") >= 0;
    }

    public static boolean isSolaris() {
        return OS.indexOf("solaris") >= 0;
    }

    public static boolean isSunOS() {
        return OS.indexOf("sunos") >= 0;
    }

    public static boolean isMPEiX() {
        return OS.indexOf("mpe/ix") >= 0;
    }

    public static boolean isHPUX() {
        return OS.indexOf("hp-ux") >= 0;
    }

    public static boolean isAix() {
        return OS.indexOf("aix") >= 0;
    }

    public static boolean isOS390() {
        return OS.indexOf("os/390") >= 0;
    }

    public static boolean isFreeBSD() {
        return OS.indexOf("freebsd") >= 0;
    }

    public static boolean isIrix() {
        return OS.indexOf("irix") >= 0;
    }

    public static boolean isDigitalUnix() {
        return OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0;
    }

    public static boolean isNetWare() {
        return OS.indexOf("netware") >= 0;
    }

    public static boolean isOSF1() {
        return OS.indexOf("osf1") >= 0;
    }

    public static boolean isOpenVMS() {
        return OS.indexOf("openvms") >= 0;
    }

    public static boolean is32Bit() {
        return ARCH.equalsIgnoreCase("x86")             // Windows
                || ARCH.equalsIgnoreCase("x86_32")      // Mac
                || ARCH.equalsIgnoreCase("i386")        // Linux
                || ARCH.equalsIgnoreCase("i486")
                || ARCH.equalsIgnoreCase("i586")
                || ARCH.equalsIgnoreCase("i686");
    }

    public static boolean is64Bit() {
        return ARCH.equalsIgnoreCase("x64")             // Windows
                || ARCH.equalsIgnoreCase("x86_64")
                || ARCH.equalsIgnoreCase("amd64");     // Mac/Linux
    }

    public static Platform getOSname() {
        if (isAix()) {
            _instance.platform = Platform.AIX;
        } else if (isDigitalUnix()) {
            _instance.platform = Platform.Digital_Unix;
        } else if (isFreeBSD()) {
            _instance.platform = Platform.FreeBSD;
        } else if (isHPUX()) {
            _instance.platform = Platform.HP_UX;
        } else if (isIrix()) {
            _instance.platform = Platform.Irix;
        } else if (isLinux()) {
            _instance.platform = Platform.Linux;
        } else if (isMacOS()) {
            _instance.platform = Platform.Mac_OS;
        } else if (isMacOSX()) {
            _instance.platform = Platform.Mac_OS_X;
        } else if (isMPEiX()) {
            _instance.platform = Platform.MPEiX;
        } else if (isNetWare()) {
            _instance.platform = Platform.NetWare_411;
        } else if (isOpenVMS()) {
            _instance.platform = Platform.OpenVMS;
        } else if (isOS2()) {
            _instance.platform = Platform.OS2;
        } else if (isOS390()) {
            _instance.platform = Platform.OS390;
        } else if (isOSF1()) {
            _instance.platform = Platform.OSF1;
        } else if (isSolaris()) {
            _instance.platform = Platform.Solaris;
        } else if (isSunOS()) {
            _instance.platform = Platform.SunOS;
        } else if (isWindows()) {
            _instance.platform = Platform.Windows;
        } else {
            _instance.platform = Platform.Others;
        }
        return _instance.platform;
    }
}
