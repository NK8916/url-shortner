package com.example.urlshortner.utils;

import io.seruco.encoding.base62.Base62;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Base62IdCodec {
    private static final Base62 BASE62 = Base62.createInstance();

    private Base62IdCodec() {
    }

    public static String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative");
        }

        byte[] bytes = BigInteger.valueOf(value).toByteArray();
        byte[] encoded = BASE62.encode(bytes);
        return new String(encoded, StandardCharsets.US_ASCII);
    }

    public static long decode(String alias) {
        if (alias == null || alias.isEmpty()) {
            throw new IllegalArgumentException("alias cannot be null/empty");
        }
        byte[] decoded = BASE62.decode(alias.getBytes(StandardCharsets.US_ASCII));
        BigInteger bigInt = new BigInteger(decoded);
        long value = bigInt.longValueExact();
        if (value < 0) {
            throw new IllegalStateException("Decoded negative value, something is wrong");
        }
        return value;
    }

}
