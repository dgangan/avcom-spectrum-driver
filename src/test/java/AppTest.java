//import com.dgangan.avcom.AvSettings;
//import com.dgangan.avcom.AvSpectrum;
//import com.dgangan.avcom.AvWaveform;
//import com.dgangan.avcom.db.DBCPDataSource;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//
//
//import java.util.*;
//import java.util.concurrent.*;
//
//public class AppTest {
//
//    public static List<AvSpectrum> getSpectrumList(){
////        AvSpectrum avcom0 = new AvSpectrum(0, "10.0.0.1",26482);
//        AvSpectrum avcom1 = new AvSpectrum(1, "172.18.255.242",26482);
////        avcom0.addAvSettingsPresets(new AvSettings(1, 1000000, 70000, -30, 300, "NS1_FWD_Tx"));
////        avcom0.addAvSettingsPresets(new AvSettings(1, 1100000, 70000, -30, 300, "NS2_FWD_Tx"));
//        avcom1.addAvSettingsPresets(new AvSettings(1, 1669000, 10000, -30, 100, "G_NS1_FWD_Tx"));
//        avcom1.addAvSettingsPresets(new AvSettings(2, 1419000, 10000, -50, 100, "G_NS1_RTN_Tx"));
//        avcom1.addAvSettingsPresets(new AvSettings(2, 1413000, 5000, -50, 100, "G_NS1_RTN_Rx"));
//        List<AvSpectrum> av = new ArrayList<>();
////        av.add(avcom0);
//        av.add(avcom1);
//        return av;
//    }
//
//    public static void main(String[] args){
//        try{
//
//                ExecutorService executor = Executors.newFixedThreadPool(5);
//                ScheduledExecutorService executorMain = Executors.newSingleThreadScheduledExecutor();
//                DBCPDataSource.createConnectionPool();
//                ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
//
//                Runnable mainTask = () ->{
//                    List<AvSpectrum> spectrumList = getSpectrumList();
//                    List<AvWaveform> wfList = Collections.synchronizedList(new ArrayList<>());
//                    CountDownLatch latch = new CountDownLatch(spectrumList.size());
//                    for(AvSpectrum spectrum : spectrumList){
//                        System.out.println(">>.");
//                        latch.countDown();
//                        if(!spectrum.isConnected()){
//                            try {
//                                spectrum.connect();
//                            }catch(Exception e){
//                                System.out.println("Connection failed");
//                            }}
//                        Runnable checkSpectrumTask = () -> {
//                            for(AvSettings spectrumSetting : spectrum.getPresets()){
//                                try {
//                                    spectrum.changeSettings(spectrumSetting);
//                                    System.out.println(spectrumSetting);
//                                    wfList.add(spectrum.getWaveform());
//                                }catch(Exception e){
//                                    System.out.println("Fetching for: " + spectrum.getId() + " failed");
//                                }
//                            }
//                        };
//                        System.out.println(">");
//                        executor.submit(checkSpectrumTask);
//                        System.out.println("<");
//                    }
//                    try {
//                        latch.await();
//                        //DbWorker.batchInsertWaveforms(wfList);
//                    }catch(Exception e){
//                        System.out.println("latch exception");
//                    }
//                };
//
//            executorMain.scheduleWithFixedDelay(mainTask, 0, 5, TimeUnit.SECONDS);
//
//
//            //executor.shutdown();
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//
//    }
//}
//
