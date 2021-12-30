import com.dgangan.avcom.AvSpectrum;
import com.dgangan.avcom.AvWaveform;
import com.dgangan.avcom.exeptions.AvcomMessageFormatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class AppTest {
    public static final Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

//    public int insertWf(String url, AvcomWaveform avcom) throws SQLException {
//        Connection conn = DriverManager.getConnection(url);
//        int rows = 0;
//        try (Connection con = DriverManager.getConnection(url);
//             PreparedStatement ps = insertWfPs(con, avcom)) {
//            rows = ps.executeUpdate();
//        } catch (SQLException | JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return rows;
//    }

    public int insertWfBatch(String url, List<AvWaveform> avcomList ) throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        int rows = 0;

        try (Connection con = DriverManager.getConnection(url)){
            PreparedStatement ps = null;

                ps = insertWfPs(conn, avcomList);


           rows =  ps.executeBatch().length;
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }

        return rows;
    }

    private PreparedStatement insertWfPs(Connection con, List<AvWaveform> avcomList) throws SQLException, JsonProcessingException {
        String sql = "INSERT INTO spectrum_plots(wf_time, spectrum_id, wf_data, wf_tag) VALUES (?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(sql);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        //objectMapper.writeValue(new File("target/wf.json"), wf);
        for(AvWaveform avcom : avcomList) {
            String wfJsonString = objectMapper.writeValueAsString(avcom);
            System.out.println(wfJsonString);
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(wfJsonString);
            Timestamp ts = avcom.getTime() != null ? Timestamp.from(avcom.getTime()) : null;
            ps.setTimestamp(1, ts, tzUTC);
            ps.setInt(2, avcom.getSpectrumId());
            ps.setObject(3, jsonObject);
            ps.setString(4, avcom.getTag());
            ps.addBatch();
        }
            return ps;
        }

    public static void main(String[] args) throws IOException, InterruptedException {
        AvSpectrum avcomLab = new AvSpectrum(0, "1.1.1.1",26482);
        AvSpectrum avcomGifec = new AvSpectrum(1, "2.2.2.2",26482);
        try {
            avcomLab.connect();
            avcomGifec.connect();
            String url = "jdbc:postgresql://23.23.23.23/sp?user=sp&password=2323";

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
                avcomWf.setTag("C1_NS1_FWD_Tx");
                wfList.add(avcomWf);
                avcomGifec.changeSettings(2, 1419000, 10000, -50, 100);
                avcomWf = avcomGifec.getWaveform();
                avcomWf.setTag("C1_NS1_RTN_Tx");
                wfList.add(avcomWf);
                avcomGifec.changeSettings(2, 1413000, 5000, -50, 100);
                avcomWf = avcomGifec.getWaveform();
                avcomWf.setTag("C1_NS1_RTN_Rx");
                wfList.add(avcomWf);
                System.out.println(new AppTest().insertWfBatch(url, wfList));
                Thread.sleep(5000);
            }

        } catch(AvcomMessageFormatException | SQLException e){
            e.printStackTrace();
        }
        avcomLab.disconnect();
        avcomGifec.disconnect();
    }
}

