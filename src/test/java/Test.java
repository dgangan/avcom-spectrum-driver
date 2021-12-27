import com.dgangan.avcom.AvcomSpectrum;
import com.dgangan.avcom.AvcomWaveform;
import com.dgangan.avcom.exeptions.AvcomMessageFormatException;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        AvcomSpectrum avcom = new AvcomSpectrum("192.168.1.1",26482);
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
            AvcomWaveform wf = avcom.fetchWaveform();
            System.out.println(wf);
            avcom.disconnect();
        } catch(AvcomMessageFormatException e){
            e.printStackTrace();
        }
    }
}
