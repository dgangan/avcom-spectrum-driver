package com.dgangan.avcom;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class AvWaveform {

    private int spectrumId;
    private Instant time;
    private AvSettings waveformSettings;
    private List<List<Double>> waveformData;
    private String presetName = "";
    private UUID presetUUID;

    public AvWaveform(AvSpectrum avSpectrum, AvSettings avSettings,List<List<Double>> waveformData){
        this.spectrumId = avSpectrum.getId();
        this.waveformSettings = avSettings;
        this.waveformData = waveformData;
        this.time = Instant.now();
    }

    public AvWaveform(AvSpectrum avSpectrum, AvPreset avPreset,List<List<Double>> waveformData){
        this.spectrumId = avSpectrum.getId();
        this.time = Instant.now();
        this.waveformSettings = avPreset.getSettings();
        this.waveformData = waveformData;
    }
}
