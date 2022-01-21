import com.dgangan.avcom.AvPreset;
import com.dgangan.avcom.AvSettings;
import com.dgangan.avcom.AvSpectrum;
import com.dgangan.avcom.AvWaveform;
import com.dgangan.avcom.exeptions.AvcomMessageFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        AvSpectrum avcom1 = new AvSpectrum(1, "10.10.255.242",26482);
        AvSpectrum avcom2 = new AvSpectrum(2, "10.10.255.243",26482);
        List<AvSpectrum> avList = new ArrayList<>();
        avList.add(avcom1);
        avList.add(avcom2);
        try{
            avcom1.initialize();
            avcom2.initialize();
            AvPreset preset1 = new AvPreset("test1", new AvSettings(1, 1665000, 5000, -30, 1000));
            AvPreset preset2 = new AvPreset("test2", new AvSettings(1, 1365000, 2000, -50, 100));
            AvPreset preset3 = new AvPreset("test3", new AvSettings(2, 1342000, 5000, -30, 1000));
            AvPreset preset4 = new AvPreset("test4", new AvSettings(4, 1382000, 20000, -50, 1000));
            AvPreset preset11 = new AvPreset("test11", new AvSettings(3, 1665000, 5000, -30, 1000));
            AvPreset preset12 = new AvPreset("test12", new AvSettings(4, 1365000, 2000, -50, 100));
            AvPreset preset13 = new AvPreset("test13", new AvSettings(2, 1342000, 5000, -30, 1000));
            AvPreset preset14 = new AvPreset("test14", new AvSettings(3, 1382000, 20000, -50, 1000));
            AvPreset preset31 = new AvPreset("test31", new AvSettings(1, 1565000, 5000, -30, 1000));
            AvPreset preset32 = new AvPreset("test32", new AvSettings(1, 1465000, 2000, -50, 100));
            AvPreset preset33 = new AvPreset("test33", new AvSettings(2, 1442000, 5000, -30, 1000));
            AvPreset preset34 = new AvPreset("test34", new AvSettings(4, 1482000, 20000, -50, 1000));
            AvPreset preset311 = new AvPreset("test311", new AvSettings(3, 1644000, 5000, -30, 1000));
            AvPreset preset312 = new AvPreset("test312", new AvSettings(4, 1356000, 2000, -50, 100));
            AvPreset preset313 = new AvPreset("test313", new AvSettings(2, 1311000, 5000, -30, 1000));
            AvPreset preset314 = new AvPreset("test314", new AvSettings(3, 1322000, 20000, -50, 1000));
            avcom1.addPreset(preset1);
            avcom1.addPreset(preset3);
            avcom2.addPreset(preset2);
            avcom2.addPreset(preset4);
            avcom1.addPreset(preset11);
            avcom1.addPreset(preset12);
            avcom2.addPreset(preset13);
            avcom2.addPreset(preset14);
            avcom1.addPreset(preset31);
            avcom1.addPreset(preset33);
            avcom2.addPreset(preset32);
            avcom2.addPreset(preset34);
            avcom1.addPreset(preset311);
            avcom1.addPreset(preset312);
            avcom2.addPreset(preset313);
            avcom2.addPreset(preset314);

            GetDataTask task = new GetDataTask(avList);
            Executors.newSingleThreadScheduledExecutor()
                     .scheduleWithFixedDelay(task, 0, 20, TimeUnit.SECONDS);

        } catch(AvcomMessageFormatException e){
            e.printStackTrace();
        }
    }
}
