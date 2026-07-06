package com.cjkfix.util;

import meteordevelopment.meteorclient.MeteorClient;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CJKFontSupport {
    private static int[] extraCodepoints = new int[0];
    private static boolean loaded = false;

    public static void loadCharset() {
        if (loaded) return;//本来要这样写的 -> throw new IllegalStateException("Charset already loaded.");
        try (InputStream in = CJKFontSupport.class.getResourceAsStream("/assets/meteor-client/fonts/charset.txt")) {
            if (in == null) {
                MeteorClient.LOG.warn("charset.txt not found, CJK support disabled.");
                extraCodepoints = new int[0];
                loaded = true;
                return;
            }
            String s = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            extraCodepoints = s.codePoints().distinct().sorted().toArray();
            MeteorClient.LOG.info("Loaded {} extra codepoints for CJK.", extraCodepoints.length);
            loaded = true;
        } catch (Exception e) {
            MeteorClient.LOG.error("Failed to load charset.txt", e);
            extraCodepoints = new int[0];
            loaded = true;
        }
    }

    public static int[] getExtraCodepoints() {
        return extraCodepoints;
    }
}
