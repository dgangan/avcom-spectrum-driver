import com.dgangan.avcom.AvSpectrum;
import com.dgangan.avcom.AvWaveform;
import com.dgangan.avcom.exeptions.AvcomMessageFormatException;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        AvSpectrum avcom = new AvSpectrum(9999, "1.1.1.1",26482);
        try{
            avcom.connect();
//            for(int i = 0; i<100; i++) {
//                System.out.println(">>> " + i);
//                avcom.changeSettings(1, 1300005, 5000, -30, 300);
//                avcom.fetchHWInfo();
//                avcom.fetchAvcomSettings();
//                System.out.println(avcom.getAvcomHWInfo());
//                System.out.println(avcom.getAvcomSettings());
//                System.out.println("---");
//                avcom.changeSettings(1, 1450000, 10000, -10, 100);
//                System.out.println(avcom.getAvcomHWInfo());
//                System.out.println(avcom.getAvcomSettings());
//            }
            avcom.changeSettings(1, 1000000, 70000, -30, 300);
            AvWaveform wf = avcom.getWaveform();
            System.out.println(wf);
            avcom.disconnect();
        } catch(AvcomMessageFormatException e){
            e.printStackTrace();
        }
    }
}
