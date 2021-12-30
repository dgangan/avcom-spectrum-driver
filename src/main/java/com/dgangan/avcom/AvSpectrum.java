package com.dgangan.avcom;

import com.dgangan.avcom.exeptions.AvcomMessageFormatException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Range;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AvSpectrum {

    private int spectrumId;
    private Socket socket;
    private String avcomIp;
    private int avcomPort;
    private AvSettings avSettings = null;
    //Hardware Information
    private byte prodId;
    private String version;
    private byte availRF;  //Number of ports
    private String SN;
    private HashMap<Integer, Range<Integer>> portRefCapability;  //Reference level capabilities of ports

    public AvSpectrum(int spectrumId, String avcomIp, int avcomPort) {
        this.avcomIp = avcomIp;
        this.avcomPort = avcomPort;
        this.spectrumId = spectrumId;
    }

    public int getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(int spectrumId) {
        this.spectrumId = spectrumId;
    }

    public byte getProdId() {
        return prodId;
    }

    public void setProdId(byte prodId) {
        this.prodId = prodId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public HashMap<Integer, Range<Integer>> getPortRefCapability() {
        return portRefCapability;
    }

    public void setPortRefCapability(HashMap<Integer, Range<Integer>> portRefCap) {
        this.portRefCapability = portRefCap;
    }

    public String getAvcomIp() {
        return avcomIp;
    }

    public int getAvcomPort() {
        return avcomPort;
    }

    public void setAvcomIp(String avcomIp) {
        this.avcomIp = avcomIp;
    }

    public void setAvcomPort(int avcomPort) {
        this.avcomPort = avcomPort;
    }

    public AvSettings getAvcomSettings() {
        return avSettings;
    }

    public void setAvcomSettings(AvSettings avSettings) {
        this.avSettings = avSettings;
    }

    public void setAvcomHwInfo(byte prodId, byte majorVer, byte minorVer, byte availRF, String SN, HashMap<Integer, Range<Integer>> portRefCapability ){
            this.prodId = prodId;
            this.version = majorVer +"." + minorVer;
            this.availRF = availRF;
            this.SN = SN;
            this.portRefCapability = portRefCapability;
        }

    public void fetchHwInfo() throws IOException, AvcomMessageFormatException {

        send(AvMessages.GET_HW_DESCRIPTION);
        byte[] avcomHwDescription = read();
        send(AvMessages.GET_LNB_AND_FIX_LO);
        byte[] avcomLnbAndFixLO = read();

        AvUtil.verifyMessageType(avcomHwDescription, AvMessages.msgType.HW_DESCRIPTION);
        AvUtil.verifyMessageType(avcomLnbAndFixLO, AvMessages.msgType.LNB_AND_FIX_LO);
        List<Byte> hwDescriptionBytesList = Arrays.asList(ArrayUtils.toObject(avcomHwDescription));
        List<Byte> lnbPowerDescriptionByteList = Arrays.asList(ArrayUtils.toObject(avcomLnbAndFixLO));

        //Collecting HW information
        byte prodId = hwDescriptionBytesList.get(4);
        byte majorVer = hwDescriptionBytesList.get(5);
        byte minorVer = hwDescriptionBytesList.get(6);
        byte availRf = (byte) (hwDescriptionBytesList.get(20) -10);
        String SN = new String(ArrayUtils.toPrimitive(hwDescriptionBytesList.subList(29,45).toArray(new Byte[0])));
        byte[] portsFixedGain = ArrayUtils.toPrimitive(lnbPowerDescriptionByteList.subList(37, 43).toArray(new Byte[0]));

        //Filling Avcom port reference level capability
        HashMap<Integer,Range<Integer>> portRefCapMap = new HashMap<>();
        byte ports70dbCapability = hwDescriptionBytesList.get(59);
        for (int i=0; i<6; i++) {
            int minVal = -10 - portsFixedGain[i];
            int maxVal = -50 - portsFixedGain[i];
            if(AvUtil.isBitSet(ports70dbCapability, i))
                maxVal-=20;
            portRefCapMap.put(i+1, Range.between(minVal,maxVal));
        }
        this.setAvcomHwInfo(prodId, majorVer, minorVer, availRf, SN, portRefCapMap);
    }

    public AvSettings fetchAvcomSettings() throws IOException, AvcomMessageFormatException {
        send(AvMessages.GET_HW_DESCRIPTION);
        byte[] avcomHwDescription = read();
        this.avSettings = AvSettings.getAvcomSettingsFromHwDescription(avcomHwDescription);
        return this.avSettings;
    }

    public AvWaveform getWaveform() throws IOException, AvcomMessageFormatException {
        send(AvMessages.GET_WAVEFORM);
        byte[] avcomWaveformBytes = read();
        AvUtil.verifyMessageType(avcomWaveformBytes, AvMessages.msgType.WAVEFORM);
        List<Byte> waveformResponseByteList = Arrays.asList(ArrayUtils.toObject(avcomWaveformBytes));

        //Collecting AvcomSetting information
        List<Byte> waveformDataBytes = waveformResponseByteList.subList(4,324);;
        byte curRL = waveformResponseByteList.get(333);
        byte curRBW = waveformResponseByteList.get(334);
        byte curRF = (byte) (waveformResponseByteList.get(335) - 9);
        int curCF = ByteBuffer.wrap(ArrayUtils.toPrimitive(waveformResponseByteList.subList(325, 329).toArray(new Byte[0]))).getInt() / 10;
        byte [] curIEFbytes = {waveformResponseByteList.get(338), waveformResponseByteList.get(339)};
        int curIEF = (short) ((curIEFbytes[0] << 8) | curIEFbytes[1]);
        int curSP = ByteBuffer.wrap(ArrayUtils.toPrimitive(waveformResponseByteList.subList(329, 333).toArray(new Byte[0]))).getInt() / 10;
        AvSettings avSettings = new AvSettings(curRF, curCF, curSP, curRL, curRBW, curIEF);

        //Collecting waveform data
        List<List<Double>> waveformData = new ArrayList<>();
        double wfStartFreq = (curCF - curIEF) - 0.5*curSP;
        double wfFreqStep = curSP/319d;
        double wfPointFreq = wfStartFreq;
        for(Byte b : waveformDataBytes){
            List<Double> p = new ArrayList<>();
            double wfPointPower = 0.2d * (int) (b & 0xff) + (curRL-40);
            p.add(AvUtil.roundAvoid(wfPointFreq,0));
            p.add(AvUtil.roundAvoid(wfPointPower,2));
            waveformData.add(p);
            wfPointFreq += wfFreqStep;
        }
        return new AvWaveform(this, avSettings, waveformData);
    }

    public void changeSettings(int port, int centralFreq, int span, int refLevel, int rbw) throws IOException, InterruptedException, AvcomMessageFormatException {
        this.send(AvMessages.getChangeSettingsMessage(new AvSettings(port, centralFreq, span, refLevel, rbw)));
        Thread.sleep(400);
        this.fetchAvcomSettings();
    }

    public void send(byte[] msg) throws IOException {
        AvConnection.send(this.socket, msg);
    }

    public byte[] read() throws AvcomMessageFormatException{
        return AvConnection.read(socket, 1024);
    }

    public void connect() throws IOException {
        this.socket = new Socket(avcomIp, avcomPort);
    }

    public void disconnect() throws IOException {
        this.socket.close();
    }
}
