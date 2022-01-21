package com.dgangan.avcom;

import lombok.Data;
import org.apache.commons.lang3.Range;

import java.util.HashMap;

@Data
public class AvHwInfo {

    private byte prodId;
    private String swVersion;
    private byte availRF;  //Number of ports
    private String SN;
    public HashMap<Integer, Range<Integer>> portRefCap;  //Reference level capabilities of ports

    public AvHwInfo(byte prodId, byte majorVer, byte minorVer, byte availRF, String SN) {
        this.prodId = prodId;
        this.swVersion = majorVer+"."+minorVer;
        this.availRF = availRF;
        this.SN = SN;
    }
}
