package com.dgangan.avcom;

import com.dgangan.avcom.exeptions.AvcomMessageFormatException;
import lombok.Data;
import lombok.Synchronized;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Range;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

@Data
public class AvSpectrum {

    private int id;
    private String name = "";
    private String ip;
    private int port;
    private Socket socket;
    private AvSettings settings;
    private AvHwInfo hwInfo;
    private List<AvPreset> presets = new ArrayList<>();

    public AvSpectrum(int id, String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.id = id;
    }

    public void addPreset(AvPreset preset){
        if(this.presets.stream().noneMatch(a -> a.getName().equals(preset.getName()))){
            presets.add(preset);
        } else {
            throw new IllegalArgumentException(
                    String.format("Preset with name: %s for SA: %s, already exist",preset.getName(), this.getName()));
        }
    }

    public void removePreset(AvPreset preset){
        this.presets.remove(preset);
    }

    public AvPreset getPresetByName(String name){
       return this.presets.stream()
               .filter(a -> a.getName().equals(name))
               .findAny()
               .orElseThrow(() -> new IllegalArgumentException("Preset with name: " + name + " not found"));
    }
    public void removePresetByName(String name){
        this.presets.stream().filter(a -> a.getName().equals(name)).forEach(this::removePreset);
    }

    public void removePresetByUUID(UUID uuid){
        this.presets.stream().filter(a -> a.getUuid().equals(uuid)).forEach(this::removePreset);
    }

    public void initialize() throws AvcomMessageFormatException, IOException {
        connect();
        fetchHwInfo();
        fetchSettings();
    }

    public void fetchHwInfo() throws IOException, AvcomMessageFormatException {
        send(AvMessages.GET_HW_DESCRIPTION);
        byte[] avcomHwDescription = read();
        send(AvMessages.GET_LNB_AND_FIX_LO);
        byte[] avcomLnbAndFixLO = read();
        //Validation of response
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
        this.hwInfo = new AvHwInfo(prodId, majorVer, minorVer, availRf, SN);
        this.hwInfo.setPortRefCap(portRefCapMap);
    }

    public void fetchSettings() throws IOException, AvcomMessageFormatException {
        send(AvMessages.GET_HW_DESCRIPTION);
        byte[] avcomHwDescription = read();
        AvUtil.verifyMessageType(avcomHwDescription, AvMessages.msgType.HW_DESCRIPTION);
        List<Byte> hwDescriptionBytesList = Arrays.asList(ArrayUtils.toObject(avcomHwDescription));
        //Collecting settings information
        byte curRL = hwDescriptionBytesList.get(16);
        byte curRBW = hwDescriptionBytesList.get(17);
        byte curRF = (byte) (hwDescriptionBytesList.get(19) - 9);
        int curCF = ByteBuffer.wrap(ArrayUtils.toPrimitive(hwDescriptionBytesList.subList(8, 12).toArray(new Byte[0]))).getInt() / 10;
        int curSP = ByteBuffer.wrap(ArrayUtils.toPrimitive(hwDescriptionBytesList.subList(12, 16).toArray(new Byte[0]))).getInt() / 10;
        this.settings = new AvSettings(curRF, curCF, curSP, curRL, AvMessages.byteToRbw.get(curRBW));
    }

    public AvWaveform getWaveform(AvPreset preset) throws AvcomMessageFormatException, IOException, InterruptedException {
        this.changeSettings(preset.getSettings());
        AvWaveform waveform = this.getWaveform();
        waveform.setPresetName(preset.getName());
        waveform.setPresetUUID(preset.getUuid());
        return waveform;
    }

    private AvWaveform getWaveform() throws IOException, AvcomMessageFormatException {
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

    public void changeSettings(AvSettings avSettings) throws IOException, InterruptedException, AvcomMessageFormatException {
        this.send(AvMessages.getChangeSettingsMessage(avSettings));
        Thread.sleep(400);
        this.fetchSettings();
    }

    public AvSettings getAvcomSettingsFromHwDescription(byte[] HwDescriptionResponse) {
        AvUtil.verifyMessageType(HwDescriptionResponse, AvMessages.msgType.HW_DESCRIPTION);
        List<Byte> hwDescriptionBytesList = Arrays.asList(ArrayUtils.toObject(HwDescriptionResponse));
        byte curRL = hwDescriptionBytesList.get(16);
        byte curRBW = hwDescriptionBytesList.get(17);
        byte curRF = (byte) (hwDescriptionBytesList.get(19) - 9);
        int curCF = ByteBuffer.wrap(ArrayUtils.toPrimitive(hwDescriptionBytesList.subList(8, 12).toArray(new Byte[0]))).getInt() / 10;
        int curSP = ByteBuffer.wrap(ArrayUtils.toPrimitive(hwDescriptionBytesList.subList(12, 16).toArray(new Byte[0]))).getInt() / 10;
        return new AvSettings(curRF, curCF, curSP, curRL, AvMessages.byteToRbw.get(curRBW));
    }

    public void send(byte[] msg) throws IOException {
        AvConnection.send(this.socket, msg);
    }

    public byte[] read() throws AvcomMessageFormatException{
        return AvConnection.read(socket, 1024);
    }

    public void connect() throws IOException {
        this.socket = new Socket(ip, port);
    }

    public void disconnect() throws IOException {
        this.socket.close();
    }
    public boolean isConnected(){
        return this.socket.isConnected();
    }
}
