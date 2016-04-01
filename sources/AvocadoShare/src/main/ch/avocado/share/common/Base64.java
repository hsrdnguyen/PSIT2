package ch.avocado.share.common;

/**
 * Base64 encoder
 */
public class Base64 {

    private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    private static int[] toInt = new int[128];

    static {
        for (int i = 0; i < ALPHABET.length; i++) {
            toInt[ALPHABET[i]] = i;
        }
    }

    /**
     * Translates the specified byte array into Base64 string.
     *
     * @param buffer the byte array (not null)
     * @return the translated Base64 string (not null)
     */
    public static String encode(byte[] buffer) {
        if(buffer == null) throw new IllegalArgumentException("buffer is null");
        int size = buffer.length;
        char[] ar = new char[((size + 2) / 3) * 4];
        int a = 0;
        int i = 0;
        while (i < size) {
            byte b0 = buffer[i++];
            byte b1 = (i < size) ? buffer[i++] : 0;
            byte b2 = (i < size) ? buffer[i++] : 0;

            int mask = 0x3F;
            ar[a++] = ALPHABET[(b0 >> 2) & mask];
            ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
            ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
            ar[a++] = ALPHABET[b2 & mask];
        }
        switch (size % 3) {
            case 1:
                ar[--a] = '=';
            case 2:
                ar[--a] = '=';
        }
        return new String(ar);
    }

    /**
     * Translates the specified Base64 string into a byte array.
     *
     * @param value the Base64 string (not null)
     * @return the byte array (not null)
     */
    public static byte[] decode(String value) {
        if(value == null) throw new IllegalArgumentException("value is null");
        byte[] buffer = new byte[getDecodedLength(value)];
        int mask = 0xFF;
        int index = 0;
        for (int i = 0; i < value.length(); i += 4) {
            int c0 = toInt[value.charAt(i)];
            int c1 = toInt[value.charAt(i + 1)];
            buffer[index++] = (byte) (((c0 << 2) | (c1 >> 4)) & mask);
            if (index >= buffer.length) {
                return buffer;
            }
            int c2 = toInt[value.charAt(i + 2)];
            buffer[index++] = (byte) (((c1 << 4) | (c2 >> 2)) & mask);
            if (index >= buffer.length) {
                return buffer;
            }
            int c3 = toInt[value.charAt(i + 3)];
            buffer[index++] = (byte) (((c2 << 6) | c3) & mask);
        }
        return buffer;
    }

    /**
     * @param value the Base64 string (not null)
     * @return The size of the byte array once the value is decoded.
     */
    private static int getDecodedLength(String value) {
        int delta = 0;
        if (value.endsWith("=")) {
            if (value.endsWith("==")) {
                delta = 2;
            } else {
                delta = 1;
            }
        }
        return value.length() * 3 / 4 - delta;
    }

}
