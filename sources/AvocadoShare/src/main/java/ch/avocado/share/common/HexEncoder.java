package ch.avocado.share.common;

/**
 * Encode a value as hexadecimal.
 */
public class HexEncoder {
    final private static char[] hexArray = "0123456789abcdef".toCharArray();

    /**
     * Converts a byte array to a hexadecimal string.
     * @param bytes The byte array to convert
     * @return The string representing the values encoded in hexadecimal
     */
    public static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
