package dev.guillermo.shortener;

/** Encodes numeric ids into short URL-safe codes and back. */
public final class Base62 {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private Base62() {}

    public static String encode(long value) {
        if (value < 0) throw new IllegalArgumentException("value must be >= 0");
        if (value == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(ALPHABET.charAt((int) (value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }

    public static long decode(String code) {
        long result = 0;
        for (char c : code.toCharArray()) {
            int idx = ALPHABET.indexOf(c);
            if (idx < 0) throw new IllegalArgumentException("invalid character: " + c);
            result = result * 62 + idx;
        }
        return result;
    }
}
