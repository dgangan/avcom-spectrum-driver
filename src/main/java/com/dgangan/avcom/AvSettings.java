package com.dgangan.avcom;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

//(byte) 1, 1500000, 5000, (byte) -30, Rbw.RBW_100KHZ
public class AvSettings {

    public static AvSettings getAvcomSettingsFromHwDescription(byte[] HwDescriptionResponse) {
        AvUtil.verifyMessageType(HwDescriptionResponse, AvMessages.msgType.HW_DESCRIPTION);
        List<Byte> hwDescriptionBytesList = Arrays.asList(ArrayUtils.toObject(HwDescriptionResponse));
        byte curRL = hwDescriptionBytesList.get(16);
        byte curRBW = hwDescriptionBytesList.get(17);
        byte curRF = (byte) (hwDescriptionBytesList.get(19) - 9);
        int curCF = ByteBuffer.wrap(ArrayUtils.toPrimitive(hwDescriptionBytesList.subList(8, 12).toArray(new Byte[0]))).getInt() / 10;
        int curSP = ByteBuffer.wrap(ArrayUtils.toPrimitive(hwDescriptionBytesList.subList(12, 16).toArray(new Byte[0]))).getInt() / 10;
        return new AvSettings(curRF, curCF, curSP, curRL, AvMessages.byteToRbw.get(curRBW));
    }

    private byte port;
    private int centralFreq;
    private int span;
    private byte refLevel;
    private byte rbw;  //Stored as byte reference value
    private int curIEF=0;

    public AvSettings(int port, int centralFreq, int span, int refLevel) {
        this.port = (byte) port;
        this.centralFreq = centralFreq * 10;
        this.span = span * 10;
        this.refLevel = (byte) refLevel;
    }

    public AvSettings(int port, int centralFreq, int span, int refLevel, int rbw) {
        this(port, centralFreq, span, refLevel);
        this.rbw = AvMessages.rbwToByte.get(rbw);
    }

    public AvSettings(int port, int centralFreq, int span, int refLevel, byte rbw) {
        this(port, centralFreq, span, refLevel);
        this.rbw = rbw;
    }

    public AvSettings(int port, int centralFreq, int span, int refLevel, int rbw, int curIEF){
        this(port, centralFreq, span, refLevel, rbw);
        this.curIEF = curIEF;
    }

    public AvSettings(int port, int centralFreq, int span, int refLevel, byte rbw, int curIEF){
        this(port, centralFreq, span, refLevel, rbw);
        this.curIEF = curIEF;
    }

    public byte getPort() {
        return port;
    }

    public void setPort(byte port) {
        this.port = port;
    }

    public int getCentralFreq() {
        return centralFreq;
    }

    public void setCentralFreq(int centralFreq) {
        this.centralFreq = centralFreq;
    }

    public int getSpan() {
        return span;
    }

    public void setSpan(int span) {
        this.span = span;
    }

    public byte getRefLevel() {
        return refLevel;
    }

    public void setRefLevel(byte refLevel) {
        this.refLevel = refLevel;
    }

    public byte getRbw() {
        return rbw;
    }

    public void setRbw(byte rbw) {
        this.rbw = rbw;
    }

    public int getCurIEF() {
        return curIEF;
    }

    public void setCurIEF(int curIEF) {
        this.curIEF = curIEF;
    }

    @Override
    public String toString() {
        return "AvcomSettings{" +
                "port=" + port +
                ", centralFreq=" + centralFreq +
                ", span=" + span +
                ", refLevel=" + refLevel +
                ", rbw=" + AvMessages.byteToRbw.get(rbw) +
                '}';
    }
}
