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

package i.am.jiongxuan.deapk;

import i.am.jiongxuan.deapk.jd.core.Decompiler;
import i.am.jiongxuan.deapk.jd.core.DecomplieEntry;
import i.am.jiongxuan.deapk.jd.core.DecomplieEnumeration;
import i.am.jiongxuan.util.FileUtils;
import i.am.jiongxuan.util.UnZip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import brut.androlib.Androlib;
import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.androlib.res.AndrolibResources;

import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.reader.DexFileReader;
import com.googlecode.dex2jar.v3.Dex2jar;
import com.googlecode.dex2jar.v3.DexExceptionHandlerImpl;


/**
 * @author Jiongxuan Zhang
 */
public class Deapk {
    private final Path mApkPath;
    private final Path mProjectPath;
    private final Path mClassesDexPath;
    private final Path mClassesJarPath;
    private final Path mClassesJarErrorPath;
    private final Path mErrorPath;
    private final Path mSrcPath;
    private final Path mSmaliPath;

    private final File mApkFile;
    private final File mProjectDir;

    private final GenerateProjectOperator mGenerateProjectOperator;

    public Deapk(String apkPath) {
        mApkPath = Paths.get(apkPath);
        mApkFile = mApkPath.toFile();

        String apkName = mApkPath.getFileName().toString();
        String projectName = apkName.substring(0, apkName.lastIndexOf('.'));
        mProjectPath = mApkPath.resolveSibling(projectName + "_project");
        mProjectDir = mProjectPath.toFile();

        mClassesDexPath = mProjectPath.resolve("classes.dex");
        mClassesJarPath = mProjectPath.resolve("classes.jar");
        mClassesJarErrorPath = mProjectPath.resolve("classes-error.zip");
        mErrorPath = mProjectPath.resolve("error.txt");
        mSrcPath = mProjectPath.resolve("src");
        mSmaliPath = mProjectPath.resolve("smali");

        mGenerateProjectOperator = new GenerateProjectOperator(
                mProjectPath);
    }

    public Path getApkPath() {
        return mApkPath;
    }

    public boolean isApkExists() {
        return mApkFile.exists();
    }

    public boolean isProjectExists() {
        return mProjectDir.exists();
    }

    public Date getLastDate() {
        return new Date(mProjectDir.lastModified());
    }

    public Path getProjectPath() {
        return mProjectPath;
    }

    public String getProjectNameIfExists() {
        return mGenerateProjectOperator.getProjectName();
    }

    public boolean start() {
        if (isProjectExists()) {
            System.out.println(">>> (0/5) Cleaning...");
            FileUtils.deleteDir(getProjectPath());
        }

        return decodeResources() &&
                extractAll() &&
                decodeToClassDex() &&
                decodeToJavaCodes() &&
                writeEclipseProjectFiles();
    }

    public boolean decodeResources() {
        System.out
                .println(">>> (1/5) Decompiling all resource files and smali files...");

        Logger.getLogger(AndrolibResources.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(Androlib.class.getName()).setLevel(Level.OFF);
        ApkDecoder apkDecoder = new ApkDecoder();
        try {
            apkDecoder.setKeepBrokenResources(true);
            apkDecoder.setBaksmaliDebugMode(false);
            apkDecoder.setDebugMode(false);
            apkDecoder.setOutDir(mProjectDir);
            apkDecoder.setApkFile(mApkFile);
            apkDecoder.decode();
        } catch (AndrolibException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        File smaliFile = mSmaliPath.toFile();
        if (smaliFile.exists()) {
            try {
                Files.move(mSmaliPath, mSrcPath);
            } catch (IOException e) {
                e.printStackTrace();
                // Rename the smali to src is not required, skip it if error.
            }
        }

        return true;
    }

    public boolean extractAll() {
        System.out.println(">>> (2/5) Extracting files...");
        try {
            UnZip.extract(mApkPath, mProjectPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean decodeToClassDex() {
        System.out.print(">>> (3/5) Generating all Java Class files...");

        try {
            DexFileReader reader = new DexFileReader(mClassesDexPath.toFile());

            int count = reader.getClassSize();
            String remain = "(Estimate " + (count / 100) + " seconds)";
            System.out.print(remain);

            DexExceptionHandlerImpl handler = new DexExceptionHandlerImpl()
                    .skipDebug(true);

            Dex2jar.from(reader).withExceptionHandler(handler).reUseReg(true)
                    .topoLogicalSort(true).skipDebug(true)
                    .optimizeSynchronized(true).printIR(false).verbose(false)
                    .to(mClassesJarPath.toString());

            if (handler != null) {
                Map<Method, Exception> exceptions = handler.getExceptions();
                if (exceptions != null && exceptions.size() > 0) {
                    File errorFile = mClassesJarErrorPath.toFile();
                    handler.dumpException(reader, errorFile);
                }
            }

            String remove = "";
            for (int i = 0; i < remain.length(); i++) {
                remove += "\b";
            }
            System.out.println(remove);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean decodeToJavaCodes() {
        System.out.print(">>> (4/5) Generating all Java source code files...");

        Decompiler decompiler = new Decompiler(mClassesJarPath);
        ArrayList<String> failureList = new ArrayList<String>();
        DecomplieEnumeration enumeration;
        try {
            enumeration = decompiler.getEnumeration();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        PercentWritter percentWritter = new PercentWritter();
        int count = enumeration.getCount();
        int current = 0;
        while (enumeration.hasMoreElements()) {
            percentWritter.print(current, count);
            try {
                DecomplieEntry entry = (DecomplieEntry) enumeration
                        .nextElement();
                if (entry != null) {
                    String relativePath = entry.getJavaPath();
                    File saveFile = mSrcPath.resolve(relativePath).toFile();
                    File saveParentDir = saveFile.getParentFile();
                    if (!saveParentDir.exists()) {
                        saveParentDir.mkdirs();
                    }

                    String source = entry.getContent();
                    if (source != null && source != "") {
                        FileUtils.writeTo(source, new FileOutputStream(saveFile));
                    } else {
                        failureList.add(relativePath.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            current ++;
        }
        percentWritter.end(true);

        if (failureList.size() > 0) {
            Collections.sort(failureList);
            writeError("Unable to decompile these files:", failureList);
        }

        String printString = String
                .format("          Decompiled %d java source files, succeed %d files, error %d files",
                        count, count - failureList.size(), failureList.size());
        System.out.println(printString);

        return true;
    }

    private void writeError(String errorTitle, List<String> errors) {
        try {
            FileWriter errorWriter = new FileWriter(mErrorPath.toFile());
            errorWriter.write(errorTitle + "\r\n");

            for (String error : errors) {
                errorWriter.write("      " + error + "\r\n");
            }

            if (errorWriter != null) {
                errorWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeEclipseProjectFiles() {
        System.out.println(">>> (5/5) Creating a new Android project...");
        mGenerateProjectOperator.generateGenDir();
        mGenerateProjectOperator.genereateProjectPropertiesFile();
        mGenerateProjectOperator.generateClassPathFile();
        mGenerateProjectOperator.generateProjectFile();

        return true;
    }
}
