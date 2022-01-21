import com.dgangan.avcom.AvPreset;
import com.dgangan.avcom.AvSpectrum;
import com.dgangan.avcom.AvWaveform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            executor.submit(checkSpectrumTask);
        }
        try {
            latch.await();
            wfList.forEach(System.out::println);
            //Add all waveforms to DB
            wfList.clear();
            System.out.println(">");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
