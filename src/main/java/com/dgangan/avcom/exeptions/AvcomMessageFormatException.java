package com.dgangan.avcom.exeptions;

public class AvcomMessageFormatException extends Exception{
    public AvcomMessageFormatException(String message){
        super(message);
    }

    public AvcomMessageFormatException(){
        super("Invalid Avcom message response format");
    }
}
