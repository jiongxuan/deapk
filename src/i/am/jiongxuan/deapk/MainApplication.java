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
import java.util.Date;

/**
 * @author Jiongxuan Zhang
 */
public class MainApplication {

    public static void main(String[] args) {
        System.out.println();
        System.out.println("====================================================================");
        System.out.println();
        System.out
                .println(" Welcome to choose Deapk by Jiongxuan");
        System.out.println("    The most convenient source decompiling tool by far.");
        System.out.println();
        System.out
                .println(" Decompile the APK files to Android project");
        System.out.println("    with only one step.");
        System.out.println();
        System.out.println("====================================================================");
        System.out.println();

        if (args.length != 1 || !args[0].endsWith(".apk")) {
            usage();
            return;
        }

        Deapk deapk = new Deapk(args[0]);
        if (!deapk.isApkExists()) {
            System.out.println("ERROR: No such a file: " + args[0]);
            System.out.println();
            return;
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
            System.out.println("???    This project has been Deapked " + date + ".");
            System.out.println("???    Please confirm whether it will be redone?");
            System.out.println("??? (Yes/No)");

            String response = "";
            BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
            try {
                response = strin.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response.isEmpty()
                    || response.substring(0, 1).compareToIgnoreCase("y") != 0) {
                return;
            }
        }

        System.out.println();
        System.out.println(">>> Starting Deapk now!");
        System.out.println("    This may take a few seconds to complete.");
        System.out.println();

        if (deapk.start()) {
            System.out.println();
            System.out.println("Congratulations!");
            System.out
                    .println("You have successfully Deapked the " +
                              deapk.getProjectNameIfExists() +
                              " project, and now you can open Eclipse and import it directly through the path \"File -> Import\".");
            System.out.println("Then you can enjoy the fun as a \"hacker\" to the full!");

            System.out.println("");
        }

        help();
        waitFor();
    }

    private static void help() {
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
        System.out.println("Usage:");
        System.out.println("    deapk <apk_path>");
        System.out.println();
        System.out.println("i.e.");
        System.out.println("    deapk haoke.apk");
        System.out.println();
        System.out.println("So easy, right?");
    }
}
