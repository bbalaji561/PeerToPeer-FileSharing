package src;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.rmi.Remote;
import java.util.HashMap;

import src.CommonConfig;
import src.PeerAdmin;
import src.PeerHandler;
import src.PeerInfoConfig;
import src.RemotePeerInfo;

public class PeerServer implements Runnable {
	private String peerID;
	private ServerSocket listener;
	private PeerAdmin peerAdmin;
	private boolean dead;

	public PeerServer(String peerID, ServerSocket listener, PeerAdmin admin) {
		this.peerID = peerID;
		this.listener = listener;
		this.peerAdmin = admin;
		this.dead = false;
	}

	public void run() {
		while (!this.dead) {
			try {
				Socket neighbour = this.listener.accept();
				PeerHandler neighbourHandler = new PeerHandler(neighbour, this.peerAdmin);
				new Thread(neighbourHandler).start();
				String addr = neighbour.getInetAddress().toString();
				int port = neighbour.getPort();
			} 
			catch (SocketException e) {
				break;
			} 
			catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
