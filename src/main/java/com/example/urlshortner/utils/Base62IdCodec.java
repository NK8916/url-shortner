package com.example.urlshortner.utils;

import io.seruco.encoding.base62.Base62;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public final class Base62IdCodec {

    // 7 chars => 62^7 ≈ 3.5T uniques, fits in 42 bits.
    public static final int ALIAS_LEN = 7;

    private static final Base62 BASE62 = Base62.createInstance();

    // choose 42 bits for 7 chars. (if 8 chars -> use 48 bits)
    private static final int BITS = 42;

    // keep secret stable across deployments
    private static final int SECRET = 0x6D2B79F5;

    private static final FeistelPermutation PERM = new FeistelPermutation(BITS, 4, SECRET);

    private Base62IdCodec() {}

    public static String encode(long id) {
        if (id < 0) throw new IllegalArgumentException("id must be non-negative");
        if (id > PERM.maxValue()) {
            throw new IllegalArgumentException("id too large for " + ALIAS_LEN + " chars; bump alias length.");
        }

        long scrambled = PERM.permute(id);

        byte[] bytes = BigInteger.valueOf(scrambled).toByteArray();
        byte[] encoded = BASE62.encode(bytes);
        String s = new String(encoded, StandardCharsets.US_ASCII);

        return leftPad(s, ALIAS_LEN, '0'); // fixed-length
    }

    public static long decode(String alias) {
        if (alias == null || alias.isBlank()) throw new IllegalArgumentException("alias is blank");

        byte[] decodedBytes = BASE62.decode(alias.getBytes(StandardCharsets.US_ASCII));
        BigInteger bi = new BigInteger(1, decodedBytes);

        long scrambled = bi.longValue();

        // if alias was padded, scrambled is still correct; but if it's outside range, reject
        if (scrambled > PERM.maxValue()) {
            throw new IllegalArgumentException("alias out of range for " + ALIAS_LEN + " chars");
        }

        return PERM.unpermute(scrambled);
    }

    private static String leftPad(String s, int len, char pad) {
        if (s.length() >= len) return s;
        StringBuilder sb = new StringBuilder(len);
        for (int i = s.length(); i < len; i++) sb.append(pad);
        sb.append(s);
        return sb.toString();
    }
}
