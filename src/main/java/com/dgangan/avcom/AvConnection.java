package com.dgangan.avcom;

import com.dgangan.avcom.exeptions.AvcomMessageFormatException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class AvConnection {

    public static void send(Socket socket, byte[] msg) throws IOException {
        OutputStream dOut = socket.getOutputStream();
        dOut.write(msg, 0, msg.length);
        dOut.flush();
    }

    public static byte[] read(Socket socket, int bufSize) throws AvcomMessageFormatException {
        byte[] rcvBuffer = new byte[bufSize]; //Maximum size of received message
        byte[] rcvByteArray = new byte[0];
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            rcvBuffer[0] = in.readByte();    //Read STX flag
            rcvBuffer[1] = in.readByte();    //Read first LEN byte
            rcvBuffer[2] = in.readByte();    //Read second LEN byte
            ByteBuffer byteBuffer = ByteBuffer.wrap(rcvBuffer, 1, 2);
            int bytesToRead = byteBuffer.getShort();
            in.readFully(rcvBuffer, 3, bytesToRead);
            rcvByteArray = Arrays.copyOf(rcvBuffer,bytesToRead+3);
            byte stxFlag = rcvByteArray[0];
            byte etxFlag = rcvByteArray[rcvByteArray.length-1];
            if(stxFlag != AvMessages.STX && etxFlag != AvMessages.ETX)   //Check that message format is correct
                throw new AvcomMessageFormatException();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rcvByteArray;
    }
}
