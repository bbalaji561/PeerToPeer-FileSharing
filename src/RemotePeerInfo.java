package src;
public class RemotePeerInfo {
	public String peerId;
	public String peerAddress;
	public int peerPort;
	public int containsFile;

	public RemotePeerInfo(String pId, String pAddress, String pPort, String cFile) {
		this.peerId = pId;
		this.peerAddress = pAddress;
		this.peerPort = Integer.parseInt(pPort);
		this.containsFile = Integer.parseInt(cFile);
	}
	
}
