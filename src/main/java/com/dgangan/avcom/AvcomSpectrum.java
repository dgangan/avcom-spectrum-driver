package com.dgangan.avcom;

import com.dgangan.avcom.exeptions.AvcomMessageFormatException;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class AvcomSpectrum {
    private static AtomicInteger uniqueId=new AtomicInteger();
    private int id;
    private Socket socket;
    private String avcomIp;
    private int avcomPort;
    private AvcomSettings avcomSettings = null;
    private AvcomHWInfo avcomHWInfo = null;

    public AvcomSpectrum(String avcomIp, int avcomPort) {
        this.avcomIp = avcomIp;
        this.avcomPort = avcomPort;
        id=uniqueId.getAndIncrement();
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

    public AvcomSettings getAvcomSettings() {
        return avcomSettings;
    }

    public void setAvcomSettings(AvcomSettings avcomSettings) {
        this.avcomSettings = avcomSettings;
    }

    public AvcomHWInfo getAvcomHWInfo() {
        return avcomHWInfo;
    }

    public void setAvcomHWInfo(AvcomHWInfo avcomHWInfo) {
        this.avcomHWInfo = avcomHWInfo;
    }

    public AvcomHWInfo fetchHWInfo() throws IOException, AvcomMessageFormatException {
        send(AvcomMessages.GET_HW_DESCRIPTION);
        byte[] avcomHwDescription = read();
        send(AvcomMessages.GET_LNB_AND_FIX_LO);
        byte[] avcomLnbAndFixLO = read();
        this.avcomHWInfo = AvcomUtil.getAvcomHWInfoFromHwDescription(avcomHwDescription);
        this.avcomHWInfo.setPortRefCap(AvcomUtil.getPortsRefCapabilities(avcomLnbAndFixLO,avcomHwDescription));
        this.avcomSettings = AvcomUtil.getAvcomSettingsFromHwDescription(avcomHwDescription);
        return this.avcomHWInfo;
    }

    public AvcomSettings fetchAvcomSettings() throws IOException, AvcomMessageFormatException {
        send(AvcomMessages.GET_HW_DESCRIPTION);
        byte[] avcomHwDescription = read();
        this.avcomSettings = AvcomUtil.getAvcomSettingsFromHwDescription(avcomHwDescription);
        return this.avcomSettings;
    }

    public AvcomWaveform fetchWaveform() throws IOException, AvcomMessageFormatException {
        send(AvcomMessages.GET_WAVEFORM);
        byte[] avcomWaveformBytes = read();
        AvcomWaveform avcomWaveform = AvcomUtil.getAvcomWeformFromBytes(avcomWaveformBytes);
        avcomWaveform.setSpectrumId(this.id);
        return avcomWaveform;
    }

    public void changeSettings(int port, int centralFreq, int span, int refLevel, int rbw) throws IOException, InterruptedException, AvcomMessageFormatException {
        this.send(AvcomMessages.getChangeSettingsMessage(new AvcomSettings(port, centralFreq, span, refLevel, rbw)));
        Thread.sleep(400);
        this.fetchAvcomSettings();
    }

    public void send(byte[] msg) throws IOException {
        OutputStream dOut = socket.getOutputStream();
        dOut.write(msg, 0, msg.length);
        dOut.flush();
    }

    public byte[] read() throws AvcomMessageFormatException{
        byte[] rcvBuffer = new byte[1024]; //Maximum size of received message
        byte[] rcvByteArray = new byte[0];
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            rcvBuffer[0] = in.readByte();    //Read STX flag
            rcvBuffer[1] = in.readByte();    //Read first LEN byte
            rcvBuffer[2] = in.readByte();    //Read second LEN byte
            ByteBuffer byteBuffer = ByteBuffer.wrap(rcvBuffer, 1, 2);
            int bytesToRead = byteBuffer.getShort();

            in.readFully(rcvBuffer, 3, bytesToRead);
            rcvByteArray = Arrays.copyOf(rcvBuffer,bytesToRead+3);
            byte stxFlag = rcvByteArray[0];
            byte etxFlag = rcvByteArray[rcvByteArray.length-1];
            if(stxFlag != AvcomMessages.STX && etxFlag != AvcomMessages.ETX)   //Check that message format is correct
                throw new AvcomMessageFormatException();
            //System.out.println(rcvByteArray.length + ": " + stxFlag + "..." + etxFlag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rcvByteArray;
    }
    public void connect(){
        try{
            this.socket = new Socket(avcomIp, avcomPort);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try{
            socket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
