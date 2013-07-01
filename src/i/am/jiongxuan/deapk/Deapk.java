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

import brut.androlib.Androlib;
import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.androlib.res.AndrolibResources;

import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.reader.DexFileReader;
import com.googlecode.dex2jar.v3.Dex2jar;
import com.googlecode.dex2jar.v3.DexExceptionHandlerImpl;

import org.apache.commons.io.FilenameUtils;

import i.am.jiongxuan.deapk.jd.core.Decompiler;
import i.am.jiongxuan.deapk.jd.core.DecomplieEntry;
import i.am.jiongxuan.deapk.jd.core.DecomplieEnumeration;
import i.am.jiongxuan.util.FileUtils;
import i.am.jiongxuan.util.UnZip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Jiongxuan Zhang
 */
public class Deapk {
    private String mApkPath;
    private String mProjectPath;
    private String mClassesDexPath;
    private String mClassesJarPath;
    private String mSrcPath;

    private File mApkFile;
    private File mProjectDir;

    private GenerateProjectOperator mGenerateProjectOperator;

    public Deapk(String apkPath) {
        mApkPath = apkPath;
        mApkFile = new File(apkPath);

        String apkName = mApkPath.substring(0, mApkPath.lastIndexOf('.'));
        mProjectPath = apkName + "_project" + File.separator;
        mProjectDir = new File(mProjectPath);

        mClassesDexPath = mProjectPath + "classes.dex";
        mClassesJarPath = mProjectPath + "classes.jar";
        mSrcPath = mProjectPath + "src" + File.separator;

        mGenerateProjectOperator = new GenerateProjectOperator(
                mProjectPath);
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

    public String getProjectPath() {
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
                decodeClassDex() &&
                decodeJavaCodes() &&
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
            return true;
        } catch (AndrolibException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
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

    public boolean decodeClassDex() {
        System.out.print(">>> (3/5) Generating all Java Class files...");

        String classesJarName = mProjectPath + "classes";
        String classesJarPath = classesJarName + ".jar";

        try {
            DexFileReader reader = new DexFileReader(new File(mClassesDexPath));

            int count = reader.getClassSize();
            String remain = "(Estimate " + (count / 100) + " seconds)";
            System.out.print(remain);

            DexExceptionHandlerImpl handler = new DexExceptionHandlerImpl()
                    .skipDebug(true);

            Dex2jar.from(reader).withExceptionHandler(handler).reUseReg(true)
                    .topoLogicalSort(true).skipDebug(true)
                    .optimizeSynchronized(true).printIR(false).verbose(false)
                    .to(classesJarPath);

            if (handler != null) {
                Map<Method, Exception> exceptions = handler.getExceptions();
                if (exceptions != null && exceptions.size() > 0) {
                    File errorFile = new File(
                            FilenameUtils.getBaseName(classesJarName)
                                    + "-error.zip");
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

    public boolean decodeJavaCodes() {
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
                    File saveFile = new File(mSrcPath + relativePath);
                    File saveParentDir = saveFile.getParentFile();
                    if (!saveParentDir.exists()) {
                        saveParentDir.mkdirs();
                    }

                    String source = entry.getContent();
                    if (source != null && source != "") {
                        FileUtils.writeTo(source, new FileOutputStream(saveFile));
                    } else {
                        failureList.add(relativePath);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            current ++;
        }
        percentWritter.end(true);

        Collections.sort(failureList);

        for (String failurePath : failureList) {
            System.out.println("ERROR: Unable to decompile the file: "
                    + failurePath);
        }

        String printString = String
                .format("          Decompiled %d java source files, succeed %d files, error %d files",
                        count, count - failureList.size(), failureList.size());
        System.out.println(printString);

        return true;
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
