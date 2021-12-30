package com.dgangan.avcom;

import org.apache.commons.lang3.Range;

import java.util.HashMap;

public class AvHwInfo {

    private byte prodId;
    private byte majorVer;
    private byte minorVer;
    private byte availRF;  //Number of ports
    private String SN;
    private HashMap<Integer, Range<Integer>> portRefCap;  //Reference level capabilities of ports

    public AvHwInfo(byte prodId, byte majorVer, byte minorVer, byte availRF, String SN) {
        this.prodId = prodId;
        this.majorVer = majorVer;
        this.minorVer = minorVer;
        this.availRF = availRF;
        this.SN = SN;
    }

    public HashMap<Integer, Range<Integer>> getPortRefCap() {
        return portRefCap;
    }

    public void setPortRefCap(HashMap<Integer, Range<Integer>> portRefCap) {
        this.portRefCap = portRefCap;
    }

    public byte getProdId() {
        return prodId;
    }

    public void setProdId(byte prodId) {
        this.prodId = prodId;
    }

    public byte getMajorVer() {
        return majorVer;
    }

    public void setMajorVer(byte majorVer) {
        this.majorVer = majorVer;
    }

    public byte getMinorVer() {
        return minorVer;
    }

    public void setMinorVer(byte minorVer) {
        this.minorVer = minorVer;
    }

    public byte getAvailRF() {
        return availRF;
    }

    public void setAvailRF(byte availRF) {
        this.availRF = availRF;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    @Override
    public String toString() {
        return "AvcomHWInfo{" +
                "prodId=" + prodId +
                ", majorVer=" + majorVer +
                ", minorVer=" + minorVer +
                ", availRF=" + availRF +
                ", SN='" + SN + '\'' +
                ", portRefCap=" + portRefCap +
                '}';
    }
}
