package com.dgangan.avcom;

public class AvUtil {

    public static boolean isBitSet(byte b, int bit){
        return (b & (1 << bit)) != 0;
    }

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static void verifyMessageType(byte[] msg, AvMessages.msgType msgType){
        if (msg[3] != msgType.value)
            throw new IllegalArgumentException("Packet type is doesn't match. Expected: " + msgType.value + ", received: " + msg[3]);
    }
}
