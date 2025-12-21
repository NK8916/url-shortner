package com.example.urlshortner.utils;

import io.seruco.encoding.base62.Base62;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Logger;

public class Base62IdCodec {
    private static final Base62 BASE62 = Base62.createInstance();

    private Base62IdCodec() {
    }

    public static String encode(long value) {
        Logger.getLogger("Base62IdCodec").info("Encoding value: " + value);
        if (value < 0) throw new IllegalArgumentException("Value must be non-negative");

        // 8 bytes, big-endian
        byte[] buf = ByteBuffer.allocate(Long.BYTES).putLong(value).array();

        // Optional: trim leading 0x00 so small numbers become shorter
        int i = 0;
        while (i < buf.length - 1 && buf[i] == 0) i++;
        byte[] trimmed = Arrays.copyOfRange(buf, i, buf.length);

        return new String(BASE62.encode(trimmed), StandardCharsets.US_ASCII);
    }

    public static long decode(String base62) {
        if (base62 == null || base62.isBlank()) throw new IllegalArgumentException("Empty base62");

        byte[] decoded = BASE62.decode(base62.getBytes(StandardCharsets.US_ASCII));

        // left-pad back to 8 bytes
        if (decoded.length > Long.BYTES) {
            throw new IllegalArgumentException("Decoded value too large for long");
        }
        byte[] buf = new byte[Long.BYTES];
        System.arraycopy(decoded, 0, buf, Long.BYTES - decoded.length, decoded.length);

        return ByteBuffer.wrap(buf).getLong();
    }

}
