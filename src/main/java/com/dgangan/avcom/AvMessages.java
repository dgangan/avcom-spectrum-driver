package com.dgangan.avcom;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class AvMessages {

    public static byte STX = 0x02;
    public static byte ETX = 0x03;
    public static byte[] GET_HW_DESCRIPTION = {STX, 0x00, 0x03, 0x07, 0x00, ETX};
    public static byte[] GET_LNB_AND_FIX_LO = {STX, 0x00, 0x02, 0x0D, ETX};
    public static byte[] GET_WAVEFORM = {STX, 0x00, 0x03, 0x03, 0x03, ETX};

    public enum msgType{
        HW_DESCRIPTION((byte) 7),
        LNB_AND_FIX_LO((byte) 13),
        WAVEFORM((byte) 9);

        public final int value;
        private msgType(byte value){
            this.value = value;
        }
    }

    public static HashMap<Integer, Byte> rbwToByte = new HashMap<Integer, Byte>(){{
       put(3000, (byte) 0x80);
       put(1000, (byte) 0x40);
       put( 300, (byte) 0x20);
       put( 200, (byte) 0x2);
       put( 100, (byte) 0x10);
       put(  10, (byte) 0x08);
       put(   3, (byte) 0x04);
    }};

    public static HashMap<Byte, Integer> byteToRbw = new HashMap<Byte, Integer>(){{
        put((byte) 0x80, 3000);
        put((byte) 0x40, 1000);
        put((byte) 0x20, 300);
        put((byte) 0x2,  200);
        put((byte) 0x10, 100);
        put((byte) 0x08, 10);
        put((byte) 0x04, 3);
    }};

    public static byte[] getChangeSettingsMessage(AvSettings avSettings){
        byte[] changeSettingsMessage = new byte[19];
        byte[] headerBytes = {STX, 0x00, 0x10, 0x04};
        byte[] trailerBytes = {0x40, 0x00, 0x00, ETX};
        byte[] cfBytes = ByteBuffer.allocate(4).putInt(avSettings.getCentralFreq()).array();
        byte[] spanBytes = ByteBuffer.allocate(4).putInt(avSettings.getSpan()).array();

        //Building the command message
        System.arraycopy(headerBytes,0,changeSettingsMessage,0,4);
        System.arraycopy(cfBytes,0,changeSettingsMessage,4,4);
        System.arraycopy(spanBytes,0,changeSettingsMessage,8,4);
        changeSettingsMessage[12] = avSettings.getRefLevel();
        changeSettingsMessage[13] = rbwToByte.get(avSettings.getRbw());
        changeSettingsMessage[14] = (byte) (avSettings.getPort() + 9);
        System.arraycopy(trailerBytes,0,changeSettingsMessage,15,4);

        return changeSettingsMessage;
    }
}

