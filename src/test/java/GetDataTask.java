package com.dgangan.avcom;

import com.dgangan.avcom.AvPreset;
import com.dgangan.avcom.AvSpectrum;
import com.dgangan.avcom.AvWaveform;
import com.dgangan.avcom.db.DBCPDataSource;
import com.dgangan.avcom.db.DbWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GetDataTask implements Runnable{

    private List<AvWaveform> wfList = Collections.synchronizedList(new ArrayList<>());
    private List<AvSpectrum> avList;
    private ExecutorService executor;

    public GetDataTask(List<AvSpectrum> avList) {
        this.executor = Executors.newFixedThreadPool(6);
        this.avList = avList;

    }

    public void run(){
        CountDownLatch latch = new CountDownLatch(avList.size());
        for(AvSpectrum spectrum : avList){
            //Create runnable object for each spectrum
            Runnable checkSpectrumTask = () -> {
                for(AvPreset preset : spectrum.getPresets()){
                    try {
                        System.out.println("<<<Getting: " + preset);
                        AvWaveform waveform = spectrum.getWaveform(preset);
                        System.out.println(">>>");
                        wfList.add(waveform);
                    }catch(Exception e){
                        System.out.println("Fetching for: " + spectrum.getId() + " failed");
                        e.printStackTrace();
                    }
                }
                latch.countDown();
            };
            //Submit runnable for each spectrum
            executor.submit(checkSpectrumTask);
        }
        try {
            latch.await(30, TimeUnit.SECONDS);
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            //System.out.println(objectMapper.writeValueAsString(wfList);
            wfList.forEach(a -> {
                try {
                    System.out.println(objectMapper.writeValueAsString(a));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
            //Add all waveforms to DB
            DbWorker.batchInsertWaveforms(wfList);
            wfList.clear();
            System.out.println(">");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
