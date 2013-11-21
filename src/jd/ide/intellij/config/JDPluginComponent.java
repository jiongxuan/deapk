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

package jd.ide.intellij.config;

public class JDPluginComponent {
    private boolean escapeUnicodeCharactersEnabled;
    private boolean omitPrefixThisEnabled;
    private boolean realignLineNumbersEnabled;
    private boolean showLineNumbersEnabled;
    private boolean showMetadataEnabled;

    public boolean isEscapeUnicodeCharactersEnabled() {
        return escapeUnicodeCharactersEnabled;
    }

    public boolean isOmitPrefixThisEnabled() {
        return omitPrefixThisEnabled;
    }

    public boolean isRealignLineNumbersEnabled() {
        return realignLineNumbersEnabled;
    }

    public boolean isShowLineNumbersEnabled() {
        return showLineNumbersEnabled;
    }

    public boolean isShowMetadataEnabled() {
        return showMetadataEnabled;
    }
}
