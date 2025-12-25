package com.cs.jeyz9.condoswiftapi.services;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;

public class SimilarityUtil {
    private static final JaroWinklerSimilarity JW =
            new JaroWinklerSimilarity();

    private SimilarityUtil() {}

    public static double similarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;

        return JW.apply(normalize(s1), normalize(s2));
    }

    private static String normalize(String s) {
        return s.toLowerCase()
                .replaceAll("[^a-z0-9ก-๙ ]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
