package com.dgangan.avcom;

import com.dgangan.avcom.AvPreset;
import com.dgangan.avcom.AvSettings;
import com.dgangan.avcom.AvSpectrum;
import com.dgangan.avcom.db.DBCPDataSource;
import com.dgangan.avcom.exeptions.AvcomMessageFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MultiThreadingTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        DBCPDataSource.createConnectionPool();
        AvSpectrum avcom1 = new AvSpectrum(1, "172.18.255.242",26482);
        List<AvSpectrum> avList = new ArrayList<>();
        avList.add(avcom1);
        try{
            avcom1.initialize();
            AvPreset Ns1FwdTx = new AvPreset("NS1_FWD_Tx", new AvSettings(1, 1669000, 10000, -20, 100));
            AvPreset Ns1FwdRx = new AvPreset("NS1_FWD_Rx", new AvSettings(2, 1419000, 6500, -70, 100));
            AvPreset Ns1RtnRx = new AvPreset("NS1_RTN_Rx", new AvSettings(2, 1413000, 5000, -70, 100));
            AvPreset carrier1 = new AvPreset("carrier1", new AvSettings(2, 1406500, 8000, -70, 100));
            AvPreset carrier2 = new AvPreset("carrier2", new AvSettings(2, 1374000, 20000, -70, 100));

            avcom1.addPreset(Ns1FwdTx);
            avcom1.addPreset(Ns1FwdRx);
            avcom1.addPreset(Ns1RtnRx);
            avcom1.addPreset(carrier1);
            avcom1.addPreset(carrier2);

            GetDataTask task = new GetDataTask(avList);
            Executors.newSingleThreadScheduledExecutor()
                     .scheduleWithFixedDelay(task, 0, 5, TimeUnit.SECONDS);

        } catch(AvcomMessageFormatException e){
            e.printStackTrace();
        }
    }
}
