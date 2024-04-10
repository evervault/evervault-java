package com.evervault.utils;

public class Bytes {
    public static final byte[] concat(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }

        byte[] result = new byte[totalLength];

        int currentPosition = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentPosition, array.length);
            currentPosition += array.length;
        }

        return result;
    }

    public static byte[] extract(byte[] array, int begin, int length) {
        byte[] result = new byte[length];
        System.arraycopy(array, begin, result, 0, length);
        return result;
    }

}
