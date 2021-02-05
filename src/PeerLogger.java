package src;

import java.util.*;
import java.io.*;
import java.text.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class PeerLogger {

    private String logFileName;
    private String peerId;
    private FileHandler peerLogFileHandler;
    private SimpleDateFormat dateFormat = null;
    private Logger peerLogger;

    public PeerLogger(String peerId) {
        this.peerId = peerId;
        startLogger();
    }

    public void startLogger() {
        try {
            this.dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
            // String logFileDir = "project";
            // File file = new File(logFileDir);
            // file.mkdir();
            // this.logFileName = logFileDir + "~/log_peer_" + this.peerId + ".log";
            this.logFileName = "log_peer_" + this.peerId + ".log";
            this.peerLogFileHandler = new FileHandler(this.logFileName, false);
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
            this.peerLogFileHandler.setFormatter(new SimpleFormatter());
            this.peerLogger = Logger.getLogger("PeerLogs");
            this.peerLogger.setUseParentHandlers(false);
            this.peerLogger.addHandler(this.peerLogFileHandler);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void genTCPConnLogSender(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] makes a connection to Peer " + "[" + peer + "].");
    }

    public synchronized void genTCPConnLogReceiver(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] is connected from Peer " + "[" + peer + "].");
    }

    public synchronized void changePreferredNeigbors(List<String> neigbors) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        String neighList = "";
        for (String neigh : neigbors) {
            neighList += neigh + ",";
        }
        neighList = neighList.substring(0, neighList.length() - 1);
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] has the preferred neighbors [" + neighList + "].");
    }

    public synchronized void changeOptimisticallyUnchokedNeighbor(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                + "] has the optimistically unchoked neighbor [" + peer + "].");
    }

    public synchronized void unchokedNeighbor(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] is unchoked by [" + peer + "].");
    }

    public synchronized void chokingNeighbor(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId + "] is choked by [" + peer + "].");
    }

    public synchronized void receiveHave(String peer, int index) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                + "] received the ‘have’ message from [" + peer + "] for the piece [" + String.valueOf(index) + "].");
    }

    public synchronized void receiveInterested(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                + "] received the ‘interested’ message from [" + peer + "].");
    }

    public synchronized void receiveNotInterested(String peer) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO, "[" + currTime + "]: Peer [" + this.peerId
                + "] received the ‘not interested’ message from [" + peer + "].");
    }

    public synchronized void downloadPiece(String peer, int ind, int pieces) {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] has downloaded the piece [" + String.valueOf(ind)
                        + "] from [" + peer + "]. Now the number of pieces it has is [" + String.valueOf(pieces)
                        + "].");
    }

    public synchronized void downloadComplete() {
        Calendar c = Calendar.getInstance();
        String currTime = this.dateFormat.format(c.getTime());
        this.peerLogger.log(Level.INFO,
                "[" + currTime + "]: Peer [" + this.peerId + "] has downloaded the complete file.");
    }

    public void closeLogger() {
        try {
            if (this.peerLogFileHandler != null) {
                this.peerLogFileHandler.close();
            }
        } 
        catch (Exception e) {
            System.out.println("Failed to close peer logger");
            e.printStackTrace();
        }
    }

}