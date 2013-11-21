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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author Jiongxuan Zhang
 */
public class GenerateProjectOperator {
    private File mProjectFile;
    private String mProjectName;

    public GenerateProjectOperator(File projectFile) {
        mProjectFile = projectFile;
    }

    public void generateGenDir() {
        File genDir = new File(mProjectFile, "gen");
        if (!genDir.exists()) {
            genDir.mkdirs();
        }
    }

    public void genereateProjectPropertiesFile() {
        StringBuilder projectBuilder = new StringBuilder();
        projectBuilder.append("target=android-17");
        try {
            FileUtils.write(new File(mProjectFile, "project.properties"), projectBuilder.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateClassPathFile() {
        Document document = DocumentHelper.createDocument();
        Element classPathElement = document.addElement("classpath");
        classPathElement.addElement("classpathentry")
                .addAttribute("kind", "src").addAttribute("path", "src");
        classPathElement.addElement("classpathentry")
                .addAttribute("kind", "src").addAttribute("path", "gen");
        classPathElement
                .addElement("classpathentry")
                .addAttribute("kind", "con")
                .addAttribute("path",
                        "com.android.ide.eclipse.adt.ANDROID_FRAMEWORK");
        classPathElement.addElement("classpathentry")
                .addAttribute("kind", "con")
                .addAttribute("path", "com.android.ide.eclipse.adt.LIBRARIES");
        classPathElement
                .addElement("classpathentry")
                .addAttribute("kind", "con")
                .addAttribute("path",
                        "com.android.ide.eclipse.adt.DEPENDENCIES")
                .addAttribute("exported", "true");
        classPathElement.addElement("classpathentry")
                .addAttribute("kind", "output")
                .addAttribute("path", "bin/classes");

        try {
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(
                    new File(mProjectFile, ".classpath")));
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateProjectFile() {
        Document document = DocumentHelper.createDocument();
        Element projectDescriptionElement = document
                .addElement("projectDescription");
        projectDescriptionElement.addElement("name").addText(
                getProjectNameInManifestXml());

        Element buildSpecElement = projectDescriptionElement
                .addElement("buildSpec");
        buildSpecElement.addElement("buildCommand").addElement("name")
                .addText("com.android.ide.eclipse.adt.ResourceManagerBuilder");
        buildSpecElement.addElement("buildCommand").addElement("name")
                .addText("com.android.ide.eclipse.adt.PreCompilerBuilder");
        buildSpecElement.addElement("buildCommand").addElement("name")
                .addText("org.eclipse.jdt.core.javabuilder");
        buildSpecElement.addElement("buildCommand").addElement("name")
                .addText("com.android.ide.eclipse.adt.ApkBuilder");

        Element naturesElement = projectDescriptionElement
                .addElement("natures");
        naturesElement.addElement("nature").addText(
                "com.android.ide.eclipse.adt.AndroidNature");
        naturesElement.addElement("nature").addText(
                "org.eclipse.jdt.core.javanature");

        try {
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(
                    new File(mProjectFile, ".project")));
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProjectName() {
        return mProjectName;
    }

    public String getProjectNameInManifestXml() {
        if (mProjectName == null) {
            String packageName = "";
            String versionName = "";
            String versionCode = "";

            File inputXml = new File(mProjectFile, "AndroidManifest.xml");
            SAXReader saxReader = new SAXReader();
            try {
                Document document = saxReader.read(inputXml);
                Element manifestElement = document.getRootElement();
                packageName = manifestElement.attributeValue("package");
                versionName = manifestElement.attributeValue("versionName");
                versionCode = manifestElement.attributeValue("versionCode");
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            mProjectName = packageName.substring(packageName.lastIndexOf('.') + 1) + "_"
                    + versionName + "-" + versionCode;
        }

        return mProjectName;
    }
}
