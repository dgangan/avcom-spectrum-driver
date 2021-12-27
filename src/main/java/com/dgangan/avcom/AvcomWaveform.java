package com.dgangan.avcom;

import java.util.Date;
import java.util.Map;

public class AvcomWaveform {

    private int spectrumId;
    private Date datetime;
    private AvcomSettings waveformSettings;
    private Map<Integer,Double> waveform;

    public int getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(int spectrumId) {
        this.spectrumId = spectrumId;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public AvcomSettings getWaveformSettings() {
        return waveformSettings;
    }

    public void setWaveformSettings(AvcomSettings waveformSettings) {
        this.waveformSettings = waveformSettings;
    }

    public Map<Integer, Double> getWaveform() {
        return waveform;
    }

    public void setWaveform(Map<Integer, Double> waveform) {
        this.waveform = waveform;
    }

    @Override
    public String toString() {
        return "AvcomWaveform{" +
                "datetime=" + datetime +
                ", waveformSettings=" + waveformSettings +
                ", waveform=" + waveform +
                '}';
    }
}
