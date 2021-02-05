package src;
import java.util.*;

import src.RemotePeerInfo;

import java.io.*;
public class PeerInfoConfig {
	private HashMap<String,RemotePeerInfo> peerInfoMap;
	private ArrayList<String> peerList;

	public PeerInfoConfig(){
		this.peerInfoMap = new HashMap<>();
		this.peerList = new ArrayList<>();
	}

	public void loadConfigFile()
	{
		String st;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while((st = in.readLine()) != null) {
				String[] tokens = st.split("\\s+");
				this.peerInfoMap.put(tokens[0],new RemotePeerInfo(tokens[0], tokens[1], tokens[2], tokens[3]));
				this.peerList.add(tokens[0]);
			}
			in.close();
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	public RemotePeerInfo getPeerConfig(String peerID){
		return this.peerInfoMap.get(peerID);
	}

	public HashMap<String, RemotePeerInfo> getPeerInfoMap(){
		return this.peerInfoMap;
	}

	public ArrayList<String> getPeerList(){
		return this.peerList;
	}
}
