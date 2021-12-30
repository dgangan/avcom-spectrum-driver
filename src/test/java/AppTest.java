import com.dgangan.avcom.AvSpectrum;
import com.dgangan.avcom.AvWaveform;
import com.dgangan.avcom.db.DBCPDataSource;
import com.dgangan.avcom.db.DbWorker;
import com.dgangan.avcom.exeptions.AvcomMessageFormatException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class AppTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        AvSpectrum avcomLab = new AvSpectrum(0, "10.0.0.1",26482);
        AvSpectrum avcomGifec = new AvSpectrum(1, "10.0.0.2",26482);
        try {
            avcomLab.connect();
            avcomGifec.connect();
            DBCPDataSource.createConnectionPool();
            for (int i = 0; i < 36000; i++) {
                ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
                //objectMapper.writeValue(new File("target/wf.json"), wf);
                List<AvWaveform> wfList= new ArrayList<>();
                avcomLab.changeSettings(1, 1000000, 70000, -30, 300);
                AvWaveform avcomWf = avcomLab.getWaveform();
                avcomWf.setTag("NS1_FWD_Tx");
                wfList.add(avcomWf);
                avcomLab.changeSettings(1, 1100000, 70000, -30, 300);
                avcomWf = avcomLab.getWaveform();
                avcomWf.setTag("NS2_FWD_Tx");
                wfList.add(avcomWf);
                avcomGifec.changeSettings(1, 1669000, 10000, -30, 100);
                avcomWf = avcomGifec.getWaveform();
                avcomWf.setTag("G_NS1_FWD_Tx");
                wfList.add(avcomWf);
                avcomGifec.changeSettings(2, 1419000, 10000, -50, 100);
                avcomWf = avcomGifec.getWaveform();
                avcomWf.setTag("G_NS1_RTN_Tx");
                wfList.add(avcomWf);
                avcomGifec.changeSettings(2, 1413000, 5000, -50, 100);
                avcomWf = avcomGifec.getWaveform();
                avcomWf.setTag("G_NS1_RTN_Rx");
                wfList.add(avcomWf);
                DbWorker.batchInsertWaveforms(wfList);
                Thread.sleep(5000);
            }

        } catch(AvcomMessageFormatException | SQLException e){
            e.printStackTrace();
        }
        avcomLab.disconnect();
        avcomGifec.disconnect();
    }
}

