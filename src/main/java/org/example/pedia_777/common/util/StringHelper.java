package org.example.pedia_777.common.util;

import java.util.Arrays;

public class StringHelper {
    public static String[] splitSafely(String source) {

        if (source == null || source.isBlank()) {
            return new String[0];
        }

        return Arrays.stream(source.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }
}
