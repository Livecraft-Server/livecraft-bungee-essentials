/*
 * Copyright (c) 2020 Jacob (MediusEcho)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.markdown;

import com.gmail.mediusecho.livecraft_bungee_essentials.util.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownFormat implements Comparable<MarkdownFormat> {

    private final String name;
    private final String replacement;

    private final int priority;
    private final Pattern pattern;

    public MarkdownFormat (final String name, final String regex, final String replacement, final int priority)
    {
        this.name = name;
        this.replacement = ChatColor.translateAlternateColorCodes('&',replacement);
        this.priority = priority;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public int compareTo(@NotNull MarkdownFormat o) {
        return Integer.compare(priority, o.priority);
    }

    public String getName () {
        return name;
    }

    public String parseMarkdown (String string)
    {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find())
        {
            String g0 = matcher.group(0);
            String g1 = matcher.group(1);

            String prefix = string.substring(0, matcher.start());
            String suffix = StringUtil.getLastUsedColorCode(prefix);

            string = string.replace(g0, replacement.replace("{1}", g1) + suffix);
        }
        return string;
    }
}
