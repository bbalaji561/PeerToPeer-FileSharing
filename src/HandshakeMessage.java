package src;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class HandshakeMessage {
    private String handshakeHeader;
    private String peerID;
    
    public HandshakeMessage(String peerID) {
        this.handshakeHeader = "P2PFILESHARINGPROJ";
        this.peerID = peerID;
    }

    public String getPeerID(){
        return this.peerID;
    }

    public byte[] buildHandShakeMessage() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(this.handshakeHeader.getBytes(StandardCharsets.UTF_8));
            stream.write(new byte[10]);
            stream.write(this.peerID.getBytes(StandardCharsets.UTF_8));
        } 
        catch(Exception e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    public void readHandShakeMessage(byte[] message){
        String msg = new String(message,StandardCharsets.UTF_8);
        this.peerID = msg.substring(28,32);
    }
}
