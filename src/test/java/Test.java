import com.dgangan.avcom.AvPreset;
import com.dgangan.avcom.AvSettings;
import com.dgangan.avcom.AvSpectrum;
import com.dgangan.avcom.exeptions.AvcomMessageFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) {
        AvSpectrum avcom1 = new AvSpectrum(1, "10.10.255.242",26482);
        List<AvSpectrum> avList = new ArrayList<>();
        avList.add(avcom1);
        try{
            avcom1.initialize();
            AvPreset preset1 = new AvPreset("test1", new AvSettings(1, 1665000, 5000, -10, 1000));
            AvPreset preset2 = new AvPreset("test2", new AvSettings(1, 1365000, 2000, -30, 100));
            avcom1.addPreset(preset1);
            avcom1.addPreset(preset2);
            long startTime2 = System.nanoTime();

            for(int i = 0; i<100; i++) {
                avcom1.changeSettings(avcom1.getPresetByName("test1").getSettings());
                avcom1.changeSettings(avcom1.getPresetByName("test2").getSettings());
                System.out.print("Fetching: " + i +"\r");
            }
            long endTime2 = System.nanoTime();
            long duration2 = (endTime2 - startTime2)/1000000;
            System.out.println("Average fetch time: " + duration2/200 + "ms");



        } catch(AvcomMessageFormatException | IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
