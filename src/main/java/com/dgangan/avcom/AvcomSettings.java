package com.dgangan.avcom;
//(byte) 1, 1500000, 5000, (byte) -30, Rbw.RBW_100KHZ
public class AvcomSettings {

    private byte port;
    private int centralFreq;
    private int span;
    private byte refLevel;
    private byte rbw;
    private int curIEF=0;

    public AvcomSettings(int port, int centralFreq, int span, int refLevel, int rbw) {
        this.port = (byte) port;
        this.centralFreq = centralFreq * 10;
        this.span = span * 10;
        this.refLevel = (byte) refLevel;
        this.rbw = AvcomMessages.rbwToByte.get(rbw);
    }

    public AvcomSettings(int port, int centralFreq, int span, int refLevel, int rbw, int curIEF){
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
                ", rbw=" + AvcomMessages.byteToRbw.get(rbw) +
                '}';
    }
}
