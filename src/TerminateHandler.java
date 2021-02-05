package src;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.nio.*;
import java.lang.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import java.util.concurrent.TimeUnit;

public class TerminateHandler implements Runnable {
    private int interval;
    private PeerAdmin peerAdmin;
    private Random rand = new Random();
    private ScheduledFuture<?> job = null;
    private ScheduledExecutorService scheduler = null;

    TerminateHandler(PeerAdmin padmin) {
        this.peerAdmin = padmin;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startJob(int timeinterval) {
        this.interval = timeinterval*2;
        this.job = scheduler.scheduleAtFixedRate(this, 30, this.interval, TimeUnit.SECONDS);
    }

    public void run() {
        try {
            if(this.peerAdmin.checkIfDone()) {
                this.peerAdmin.closeHandlers();
                this.cancelJob();
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelJob() {
        this.scheduler.shutdownNow();
    }
}
