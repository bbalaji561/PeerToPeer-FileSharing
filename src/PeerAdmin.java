package src;

import java.io.*;
import java.lang.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import src.PeerHandler;
import src.PeerInfoConfig;
import src.PeerLogger;
import src.PeerServer;
import src.RemotePeerInfo;
import src.ChokeHandler;
import src.OptimisticUnchokeHandler;

public class PeerAdmin {
	private String peerID;
	private RemotePeerInfo myConfig;
	private HashMap<String, RemotePeerInfo> peerInfoMap;
	private ArrayList<String> peerList;
	private volatile HashMap<String, PeerHandler> joinedPeers;
	private volatile HashMap<String, Thread> joinedThreads;
	private volatile ServerSocket listener;
	private PeerServer server;
	private CommonConfig commonConfig;
	private PeerInfoConfig peerInfoConfig;
	private volatile PeerLogger logger;
	private volatile HashMap<String, BitSet> piecesAvailability;
	private volatile String[] requestedInfo;
	private volatile HashSet<String> unChokedList;
	private volatile HashSet<String> interestedList;
	private volatile String optUnchockedPeer;
	private int pieceCount;
	private volatile RandomAccessFile fileRaf;
	private volatile ChokeHandler chHandler;
	private volatile OptimisticUnchokeHandler optHandler;
	private volatile TerminateHandler terminateHandler;
	private volatile HashMap<String, Integer> downloadRate;
	private Thread serverThread;
	private volatile Boolean iamDone;

	public PeerAdmin(String peerID) {
		this.peerID = peerID;
		this.peerInfoMap = new HashMap<>();
		this.piecesAvailability = new HashMap<>();
		this.peerList = new ArrayList<>();
		this.joinedPeers = new HashMap<>();
		this.joinedThreads = new HashMap<>();
		this.commonConfig = new CommonConfig();
		this.peerInfoConfig = new PeerInfoConfig();
		this.logger = new PeerLogger(this.peerID);
		this.iamDone = false;
		this.unChokedList = new HashSet<>();
		this.interestedList = new HashSet<>();
		this.initPeer();
		this.chHandler = new ChokeHandler(this);
		this.downloadRate = new HashMap<>();
		this.optHandler = new OptimisticUnchokeHandler(this);
		this.terminateHandler = new TerminateHandler(this);
		this.chHandler.startJob();
		this.optHandler.startJob();
	}

	public void initPeer() {
		try {
			this.commonConfig.loadCommonFile();
			this.peerInfoConfig.loadConfigFile();
			this.pieceCount = this.calcPieceCount();
			this.requestedInfo = new String[this.pieceCount];
			this.myConfig = this.peerInfoConfig.getPeerConfig(this.peerID);
			this.peerInfoMap = this.peerInfoConfig.getPeerInfoMap();
			this.peerList = this.peerInfoConfig.getPeerList();
			String filepath = "peer_" + this.peerID;
			File file = new File(filepath);
			file.mkdir();
			String filename = filepath + "/" + getFileName();
			file = new File(filename);
			if (!hasFile()) {
				file.createNewFile();
			}
			this.fileRaf = new RandomAccessFile(file, "rw");
			if (!hasFile()) {
				this.fileRaf.setLength(this.getFileSize());
			}
			this.initializePieceAvailability();
			this.startServer();
			this.createNeighbourConnections();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startServer() {
		try {
			this.listener = new ServerSocket(this.myConfig.peerPort);
			this.server = new PeerServer(this.peerID, this.listener, this);
			this.serverThread = new Thread(this.server);
			this.serverThread.start();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createNeighbourConnections() {
		try {
			Thread.sleep(5000);
			for (String pid : this.peerList) {
				if (pid.equals(this.peerID)) {
					break;
				} 
				else {
					RemotePeerInfo peer = this.peerInfoMap.get(pid);
					Socket temp = new Socket(peer.peerAddress, peer.peerPort);
					PeerHandler p = new PeerHandler(temp, this);
					p.setEndPeerID(pid);
					this.addJoinedPeer(p, pid);
					Thread t = new Thread(p);
					this.addJoinedThreads(pid, t);
					t.start();
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initializePieceAvailability() {
		for (String pid : this.peerInfoMap.keySet()) {
			BitSet availability = new BitSet(this.pieceCount);
			if (this.peerInfoMap.get(pid).containsFile == 1) {
				availability.set(0, this.pieceCount);
				this.piecesAvailability.put(pid, availability);
			} 
			else {
				availability.clear();
				this.piecesAvailability.put(pid, availability);
			}
		}
	}

	public synchronized void writeToFile(byte[] data, int pieceindex) {
		try {
			int position = this.getPieceSize() * pieceindex;
			this.fileRaf.seek(position);
			this.fileRaf.write(data);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized byte[] readFromFile(int pieceindex) {
		try {
			int position = this.getPieceSize() * pieceindex;
			int size = this.getPieceSize();
			if (pieceindex == getPieceCount() - 1) {
				size = this.getFileSize() % this.getPieceSize();
			}
			this.fileRaf.seek(position);
			byte[] data = new byte[size];
			this.fileRaf.read(data);
			return data;
		} 
		catch (Exception e) {
			e.printStackTrace();

		}
		return new byte[0];
	}

	public HashMap<String, Integer> getDownloadRates() {
		HashMap<String, Integer> rates = new HashMap<>();
		for (String key : this.joinedPeers.keySet()) {
			rates.put(key, this.joinedPeers.get(key).getDownloadRate());
		}
		return rates;
	}

	public synchronized void broadcastHave(int pieceIndex) {
		for (String key : this.joinedPeers.keySet()) {
			this.joinedPeers.get(key).sendHaveMessage(pieceIndex);
		}
	}

	public synchronized void updatePieceAvailability(String peerID, int index) {
		this.piecesAvailability.get(peerID).set(index);
	}

	public synchronized void updateDownloadRate(String endpeerid) {
		this.downloadRate.put(endpeerid, this.downloadRate.get(endpeerid) + 1);
	}

	public synchronized void updateBitset(String peerID, BitSet b) {
		this.piecesAvailability.remove(peerID);
		this.piecesAvailability.put(peerID, b);
	}

	public synchronized void addJoinedPeer(PeerHandler p, String endpeerid) {
		this.joinedPeers.put(endpeerid, p);
	}

	public synchronized void addJoinedThreads(String epeerid, Thread th) {
		this.joinedThreads.put(epeerid, th);
	}

	public synchronized HashMap<String, Thread> getJoinedThreads() {
		return this.joinedThreads;
	}

	public PeerHandler getPeerHandler(String peerid) {
		return this.joinedPeers.get(peerid);
	}

	public BitSet getAvailabilityOf(String pid) {
		return this.piecesAvailability.get(pid);
	}

	public synchronized boolean checkIfInterested(String endpeerid) {
		BitSet end = this.getAvailabilityOf(endpeerid);
		BitSet mine = this.getAvailabilityOf(this.peerID);
		for (int i = 0; i < end.size() && i < this.pieceCount; i++) {
			if (end.get(i) == true && mine.get(i) == false) {
				return true;
			}
		}
		return false;
	}

	public synchronized void setRequestedInfo(int id, String peerID) {
		this.requestedInfo[id] = peerID;
	}

	public synchronized int checkForRequested(String endpeerid) {
		BitSet end = this.getAvailabilityOf(endpeerid);
		BitSet mine = this.getAvailabilityOf(this.peerID);
		for (int i = 0; i < end.size() && i < this.pieceCount; i++) {
			if (end.get(i) == true && mine.get(i) == false && this.requestedInfo[i] == null) {
				setRequestedInfo(i, endpeerid);
				return i;
			}
		}
		return -1;
	}

	public synchronized void resetRequested(String endpeerid) {
		for (int i = 0; i < this.requestedInfo.length; i++) {
			if (this.requestedInfo[i] != null && this.requestedInfo[i].compareTo(endpeerid) == 0) {
				setRequestedInfo(i, null);
			}
		}
	}

	public String getPeerID() {
		return this.peerID;
	}

	public PeerLogger getLogger() {
		return this.logger;
	}

	public boolean hasFile() {
		return this.myConfig.containsFile == 1;
	}

	public int getNoOfPreferredNeighbors() {
		return this.commonConfig.NumberOfPreferredNeighbors;
	}

	public int getUnchockingInterval() {
		return this.commonConfig.UnchokingInterval;
	}

	public int getOptimisticUnchockingInterval() {
		return this.commonConfig.OptimisticUnchokingInterval;
	}

	public String getFileName() {
		return this.commonConfig.FileName;
	}

	public int getFileSize() {
		return this.commonConfig.FileSize;
	}

	public int getPieceSize() {
		return this.commonConfig.PieceSize;
	}

	public int calcPieceCount() {
		int len = (getFileSize() / getPieceSize());
		if (getFileSize() % getPieceSize() != 0) {
			len += 1;
		}
		return len;
	}

	public int getPieceCount() {
		return this.pieceCount;
	}

	public int getCompletedPieceCount() {
		return this.piecesAvailability.get(this.peerID).cardinality();
	}

	public synchronized void addToInterestedList(String endPeerId) {
		this.interestedList.add(endPeerId);
	}

	public synchronized void removeFromInterestedList(String endPeerId) {
		if (this.interestedList != null) {
			this.interestedList.remove(endPeerId);
		}
	}

	public synchronized void resetInterestedList() {
		this.interestedList.clear();
	}

	public synchronized HashSet<String> getInterestedList() {
		return this.interestedList;
	}

	public synchronized boolean addUnchokedPeer(String peerid) {
		return this.unChokedList.add(peerid);
	}

	public synchronized HashSet<String> getUnchokedList() {
		return this.unChokedList;
	}

	public synchronized void resetUnchokedList() {
		this.unChokedList.clear();
	}

	public synchronized void updateUnchokedList(HashSet<String> newSet) {
		this.unChokedList = newSet;
	}

	public synchronized void setOptimisticUnchokdPeer(String peerid) {
		this.optUnchockedPeer = peerid;
	}

	public synchronized String getOptimisticUnchokedPeer() {
		return this.optUnchockedPeer;
	}

	public synchronized boolean checkIfAllPeersAreDone() {
		for (String peer : this.piecesAvailability.keySet()) {
			if (this.piecesAvailability.get(peer).cardinality() != this.pieceCount) {
				return false;
			}
		}
		return true;
	}

	public synchronized OptimisticUnchokeHandler getoptHandler() {
		return this.optHandler;
	}

	public synchronized ChokeHandler getchHandler() {
		return this.chHandler;
	}

	public synchronized RandomAccessFile getRefFile() {
		return this.fileRaf;
	}

	public synchronized ServerSocket getListener() {
		return this.listener;
	}

	public synchronized Thread getServerThread() {
		return this.serverThread;
	}

	public synchronized Boolean checkIfDone() {
		return this.iamDone;
	}

	public synchronized void closeHandlers() {
		for (String peer : this.joinedThreads.keySet()) {
			this.joinedThreads.get(peer).stop();
		}
	}

	public synchronized void cancelChokes() {
		try {
			this.getoptHandler().cancelJob();
			this.getchHandler().cancelJob();
			this.resetUnchokedList();
			this.setOptimisticUnchokdPeer(null);
			this.resetInterestedList();
			this.getRefFile().close();
			this.getLogger().closeLogger();
			this.getListener().close();
			this.getServerThread().stop();
			this.iamDone = true;
			this.terminateHandler.startJob(6);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
