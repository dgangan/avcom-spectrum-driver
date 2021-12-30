package com.dgangan.avcom;

import java.time.Instant;
import java.util.List;

public class AvWaveform {

    private int spectrumId;
    private Instant time;
    private AvSettings waveformSettings;
    private List<List<Double>> waveformData;
    private String tag = "";

    public AvWaveform(AvSpectrum avSpectrum, AvSettings avSettings,List<List<Double>> waveformData){
        this.spectrumId = avSpectrum.getSpectrumId();
        this.waveformSettings = avSettings;
        this.waveformData = waveformData;
        this.time = Instant.now();
    }

    public int getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(int spectrumId) {
        this.spectrumId = spectrumId;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public AvSettings getWaveformSettings() {
        return waveformSettings;
    }

    public void setWaveformSettings(AvSettings waveformSettings) {
        this.waveformSettings = waveformSettings;
    }

    public List<List<Double>> getWaveformData() {
        return waveformData;
    }

    public void setWaveformData(List<List<Double>> waveformData) {
        this.waveformData = waveformData;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    @Override
    public String toString() {
        return "AvcomWaveform{" +
                "time=" + time +
                ", waveformSettings=" + waveformSettings +
                ", waveform=" + waveformData +
                '}';
    }
}
