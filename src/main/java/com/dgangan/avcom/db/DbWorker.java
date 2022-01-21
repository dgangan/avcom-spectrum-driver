package com.dgangan.avcom.db;

import com.dgangan.avcom.AvWaveform;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DbWorker {

    public static final Calendar tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    public static int batchInsertWaveforms(List<AvWaveform> avcomList) throws SQLException {
        int rows = 0;
        try (Connection conn = DBCPDataSource.getConnection()){
            PreparedStatement ps = null;
            ps = getWaveformsPreparesStatement(conn, avcomList);
            rows =  ps.executeBatch().length;
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private static PreparedStatement getWaveformsPreparesStatement(Connection con, List<AvWaveform> avcomList) throws SQLException, JsonProcessingException {
        String sql = "INSERT INTO spectrum_plots(wf_time, spectrum_id, wf_data, wf_tag) VALUES (?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(sql);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        //objectMapper.writeValue(new File("target/wf.json"), wf);
        for(AvWaveform avcom : avcomList) {
            String wfJsonString = objectMapper.writeValueAsString(avcom);
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(wfJsonString);
            Timestamp ts = avcom.getTime() != null ? Timestamp.from(avcom.getTime()) : null;
            ps.setTimestamp(1, ts, tzUTC);
            ps.setInt(2, avcom.getSpectrumId());
            ps.setObject(3, jsonObject);
            ps.setString(4, avcom.getPresetName());
            ps.addBatch();
            System.out.println(wfJsonString);
        }
        return ps;
    }
}
