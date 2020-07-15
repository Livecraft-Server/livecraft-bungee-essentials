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

package com.gmail.mediusecho.livecraft_bungee_essentials.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TypeUtil {

    private static final Map<Class<?>, Function<String, ?>> parserMap = new HashMap<>();
    static
    {
        parserMap.put(Boolean.class, Boolean::parseBoolean);
        parserMap.put(Float.class, Float::parseFloat);
        parserMap.put(Double.class, Double::parseDouble);
        parserMap.put(Integer.class, Integer::parseInt);
        parserMap.put(String.class, String::valueOf);
    }

    public static Object parseType (String value, Class<?> clazz, Object def)
    {
        if (value == null) {
            return def;
        }

        Function<String, ?> func = parserMap.get(clazz);
        if (func == null) {
            return def;
        }

        try {
            return func.apply(value);
        } catch (Exception e) {
            return def;
        }
    }
}
