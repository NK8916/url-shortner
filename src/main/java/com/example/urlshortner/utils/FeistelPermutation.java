package com.example.urlshortner.utils;

public final class FeistelPermutation {

    private final int bits;        // e.g. 42 for 7-char base62
    private final int rounds;      // e.g. 4
    private final int secret;      // keep stable (config)

    private final long mask;
    private final int halfBits;
    private final int halfMask;

    public FeistelPermutation(int bits, int rounds, int secret) {
        if (bits <= 0 || bits >= 63 || (bits % 2 != 0)) {
            throw new IllegalArgumentException("bits must be even and in (0,63). Example: 42");
        }
        if (rounds <= 0) throw new IllegalArgumentException("rounds must be > 0");

        this.bits = bits;
        this.rounds = rounds;
        this.secret = secret;

        this.mask = (1L << bits) - 1;
        this.halfBits = bits / 2;
        this.halfMask = (1 << halfBits) - 1; // valid because halfBits <= 31 here
    }

    /** Permute x within [0, 2^bits) bijectively. */
    public long permute(long x) {
        x &= mask;

        int left  = (int) ((x >>> halfBits) & halfMask);
        int right = (int) (x & halfMask);

        for (int round = 0; round < rounds; round++) {
            int f = roundFunction(right, round);
            int newLeft = right;
            int newRight = left ^ f;
            left = newLeft;
            right = newRight & halfMask;
        }

        return (((long) left) << halfBits) | (right & halfMask);
    }

    /** Reverse permute() exactly. */
    public long unpermute(long x) {
        x &= mask;

        int left  = (int) ((x >>> halfBits) & halfMask);
        int right = (int) (x & halfMask);

        for (int round = rounds - 1; round >= 0; round--) {
            int f = roundFunction(left, round);
            int newRight = left;
            int newLeft = right ^ f;
            left = newLeft & halfMask;
            right = newRight & halfMask;
        }

        return (((long) left) << halfBits) | (right & halfMask);
    }

    public long maxValue() { return mask; }
    public int bits() { return bits; }

    // ---- internal mixer (not crypto, just a strong avalanche) ----

    private int roundFunction(int r, int round) {
        int x = r ^ (secret + round * 0x9E3779B9);
        x = mix32(x);
        return x & halfMask;
    }

    private static int mix32(int x) {
        x ^= (x >>> 16);
        x *= 0x7feb352d;
        x ^= (x >>> 15);
        x *= 0x846ca68b;
        x ^= (x >>> 16);
        return x;
    }
}
