package i.am.jiongxuan.deapk;

import org.apache.commons.lang3.SystemUtils;

/**
 * @author Jiongxuan Zhang
 */
public class ArchUtils {
    public static final boolean is32Bit() {
        return SystemUtils.OS_ARCH.equalsIgnoreCase("x86")             // Windows
                || SystemUtils.OS_ARCH.equalsIgnoreCase("x86_32")      // Mac
                || SystemUtils.OS_ARCH.equalsIgnoreCase("i386")        // Linux
                || SystemUtils.OS_ARCH.equalsIgnoreCase("i486")
                || SystemUtils.OS_ARCH.equalsIgnoreCase("i586")
                || SystemUtils.OS_ARCH.equalsIgnoreCase("i686");
    }

    public static final boolean is64Bit() {
        return SystemUtils.OS_ARCH.equalsIgnoreCase("x64")             // Windows
                || SystemUtils.OS_ARCH.equalsIgnoreCase("x86_64")
                || SystemUtils.OS_ARCH.equalsIgnoreCase("amd64");     // Mac/Linux
    }
}
