package com.dgangan.avcom;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import lombok.Data;

//(byte) 1, 1500000, 5000, (byte) -30, Rbw.RBW_100KHZ
@Data
public class AvSettings {

    private byte port;
    private int centralFreq;
    private int span;
    private byte refLevel;
    private int rbw;
    private int curIEF=0;

    public AvSettings(int port, int centralFreq, int span, int refLevel, int rbw) {
        this.port = (byte) port;
        this.centralFreq = centralFreq * 10;
        this.span = span * 10;
        this.refLevel = (byte) refLevel;
        this.rbw = rbw;
    }

    public AvSettings(int port, int centralFreq, int span, int refLevel, int rbw, int curIEF){
        this(port, centralFreq, span, refLevel, rbw);
        this.curIEF = curIEF;
    }
}
