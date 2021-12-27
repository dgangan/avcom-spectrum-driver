package com.dgangan.avcom;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Range;

import java.nio.ByteBuffer;
import java.util.*;

public class AvcomUtil {

    private static boolean isBitSet(byte b, int bit){
        return (b & (1 << bit)) != 0;
    }

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
    public static void verifyMessageType(byte[] msg, AvcomMessages.msgType msgType){
        if (msg[3] != msgType.value)
            throw new IllegalArgumentException("Packet type is doesn't match. Expected: " + msgType.value + ", received: " + msg[3]);
    }
    public static AvcomSettings getAvcomSettingsFromHwDescription(byte[] HwDescriptionResponse) {
        verifyMessageType(HwDescriptionResponse, AvcomMessages.msgType.HW_DESCRIPTION);
        List<Byte> hwDescriptionBytesList = Arrays.asList(ArrayUtils.toObject(HwDescriptionResponse));
        byte curRL = hwDescriptionBytesList.get(16);
        byte curRBW = hwDescriptionBytesList.get(17);
        byte curRF = (byte) (hwDescriptionBytesList.get(19) - 9);
        int curCF = ByteBuffer.wrap(ArrayUtils.toPrimitive(hwDescriptionBytesList.subList(8, 12).toArray(new Byte[0]))).getInt() / 10;
        int curSP = ByteBuffer.wrap(ArrayUtils.toPrimitive(hwDescriptionBytesList.subList(12, 16).toArray(new Byte[0]))).getInt() / 10;
        return new AvcomSettings(curRF, curCF, curSP, curRL, AvcomMessages.byteToRbw.get(curRBW));
    }


    public static AvcomHWInfo getAvcomHWInfoFromHwDescription(byte[] HwDescriptionResponse){
        verifyMessageType(HwDescriptionResponse, AvcomMessages.msgType.HW_DESCRIPTION);
        List<Byte> hwDescriptionBytesList = Arrays.asList(ArrayUtils.toObject(HwDescriptionResponse));
        byte prodID = hwDescriptionBytesList.get(4);
        byte majorVer = hwDescriptionBytesList.get(5);
        byte minorVer = hwDescriptionBytesList.get(6);
        byte availRF = (byte) (hwDescriptionBytesList.get(20) -10);
        String SN = new String(ArrayUtils.toPrimitive(hwDescriptionBytesList.subList(29,45).toArray(new Byte[0])));

        return new AvcomHWInfo(prodID,majorVer,minorVer,availRF,SN);
    }

    public static AvcomWaveform getAvcomWeformFromBytes(byte[] waveformResponse) {
        verifyMessageType(waveformResponse, AvcomMessages.msgType.WAVEFORM);
        List<Byte> waveformResponseByteList = Arrays.asList(ArrayUtils.toObject(waveformResponse));
        List<Byte> waveformData = waveformResponseByteList.subList(4,324);;
        byte curRL = waveformResponseByteList.get(333);
        byte curRBW = waveformResponseByteList.get(334);
        byte curRF = (byte) (waveformResponseByteList.get(335) - 9);
        int curCF = ByteBuffer.wrap(ArrayUtils.toPrimitive(waveformResponseByteList.subList(325, 329).toArray(new Byte[0]))).getInt() / 10;
        byte [] curIEFbytes = {waveformResponseByteList.get(338), waveformResponseByteList.get(339)};
        int curIEF = (short) ((curIEFbytes[0] << 8) | curIEFbytes[1]);
        int curSP = ByteBuffer.wrap(ArrayUtils.toPrimitive(waveformResponseByteList.subList(329, 333).toArray(new Byte[0]))).getInt() / 10;

        AvcomWaveform wf = new AvcomWaveform();
        Map<Integer,Double> wfData = new TreeMap<>();
        double wfStartFreq = (curCF - curIEF) - 0.5*curSP;
        double wfFreqStep = curSP/319d;
        double wfPointFreq = wfStartFreq;
        for(Byte b : waveformData){
            double wfPointPower = 0.2d * (int) (b & 0xff) + (curRL-40);
            wfData.put((int) wfPointFreq,roundAvoid(wfPointPower,2));
            wfPointFreq += wfFreqStep;
        }
        AvcomSettings wfSetings = new AvcomSettings(curRF, curCF, curSP, curRL, AvcomMessages.byteToRbw.get(curRBW), curIEF);
        wf.setWaveformSettings(wfSetings);
        wf.setWaveform(wfData);
        return wf;
    }
    public static HashMap<Integer,Range<Integer>> getPortsRefCapabilities(byte[] LnbPowerDescriptionResponse, byte[] HwDescriptionResponse) {
        verifyMessageType(HwDescriptionResponse, AvcomMessages.msgType.HW_DESCRIPTION);
        verifyMessageType(HwDescriptionResponse, AvcomMessages.msgType.LNB_AND_FIX_LO);
        List<Byte> LnbPowerDescriptionByteList = Arrays.asList(ArrayUtils.toObject(LnbPowerDescriptionResponse));
        List<Byte> hwDescriptionBytesList = Arrays.asList(ArrayUtils.toObject(HwDescriptionResponse));
        HashMap<Integer,Range<Integer>> portRefCapMap = new HashMap<>();
        byte[] portsFixedGain = ArrayUtils.toPrimitive(LnbPowerDescriptionByteList.subList(37, 43).toArray(new Byte[0]));
        byte ports70dbCapability = hwDescriptionBytesList.get(59);
        for (int i=0; i<6; i++) {
            Integer minVal = -10 - portsFixedGain[i];
            Integer maxVal = -50 - portsFixedGain[i];
            if(isBitSet(ports70dbCapability, i))
                maxVal-=20;
            portRefCapMap.put(i+1, Range.between(minVal,maxVal));
        }
        return portRefCapMap;
    }
}
