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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Jiongxuan Zhang
 */
public class MainApplication {

    public static void main(String[] args) {
        System.out.println();
        System.out.println("====================================================================");
        System.out.println();
        System.out
                .println(" Welcome to choose Deapk by Jiongxuan (Version: 2.0)");
        System.out.println("    The most convenient source decompiling tool by far.");
        System.out.println();
        System.out
                .println(" Decompile the APK files to Android project");
        System.out.println("    with only ONE STEP.");
        System.out.println();
        System.out.println("====================================================================");

        if (args.length < 1) {
            usage();
            return;
        }

        List<String> vaildFileList = new ArrayList<String>();
        for (String file : args) {
            if (file.endsWith(".apk")) {
                vaildFileList.add(file);
            }
        }

        if (vaildFileList.size() < 1) {
            usage();
            return;
        }

        boolean hasSucceed = false;

        for (int i = 0; i < vaildFileList.size(); i++) {
            System.out.println();
            System.out.println(">>> Starting Deapk now!" + "(" + (i + 1) + "/" + vaildFileList.size() + ")");
            System.out.println("    This may take a few seconds to complete.");
            System.out.println();

            String vaildFile = vaildFileList.get(i);
            Deapk deapk = new Deapk(vaildFile);
            if (deapkNow(deapk)) {
                System.out.println();
                System.out.println(">>> Deapked the " + deapk.getProjectNameIfExists() + " project complete!");
                hasSucceed = true;
            }
        }

        if (hasSucceed) {
            System.out.println();
            System.out.println("Congratulations!");
            System.out.println("You have successfully Deapked these projects, and now " +
                    "you can open Eclipse and import it directly through the path \"File -> Import\".");
            System.out.println("Then you can enjoy the fun as a \"hacker\" to the full!");
        }

        help();
        waitFor();
    }

    private static boolean deapkNow(Deapk deapk) {
        if (!deapk.isApkExists()) {
            System.out.println("!!! ERROR: No such a file: " + deapk.getApkPath());
            return false;
        }

        if (deapk.isProjectExists()) {
            String date = "";
            long nowDays = new Date().getTime() / 1000 / 60 / 60 / 24;
            long projectDays = deapk.getLastDate().getTime() / 1000 / 60 / 60 / 24;
            long betweenDays = nowDays - projectDays;
            if (betweenDays <= 0) {
                date = "today";
            } else if (betweenDays == 1){
                date = "yesterday";
            } else if (betweenDays <= 100){
                date = betweenDays + " days before";
            } else {
                date = "a long time before";
            }
            System.out.println("??? Question for you: ");
            System.out.println("???    File path: " + deapk.getApkPath());
            System.out.println("???");
            System.out.println("???    This project has been Deapked " + date + ".");
            System.out.println("???    Please confirm whether it will be redone?");
            System.out.println("??? Press 'y' to restart, otherwise to ignore.");

            String response = "";
            BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
            try {
                response = strin.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response.isEmpty()
                    || response.substring(0, 1).compareToIgnoreCase("y") != 0) {
                return false;
            }
        }

        return deapk.start();
    }

    private static void help() {
        System.out.println();
        System.out
                .println("If you want to learn more exciting details and the latest developments about Deapk, please visit this site at any time:");
        System.out.println();
        System.out.println("    http://jiongxuan.github.io/deapk/");
        System.out.println();
    }

    public static void waitFor() {
        System.out.print("Press enter to exit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void usage() {
        System.out.println();
        System.out.println("Usage:");
        System.out.println("    deapk <apk_path>");
        System.out.println();
        System.out.println("Support for multiple files to deapk. Just like:");
        System.out.println("    deapk <apk1> <apk2> <*.apk>...");
        System.out.println();
        System.out.println("i.e.");
        System.out.println("    deapk haoke.apk");
        System.out.println("    deapk sin.apk cos.apk tan.apk");
        System.out.println("    deapk *.apk");
        System.out.println();
        System.out.println("So easy, right?");
    }
}
