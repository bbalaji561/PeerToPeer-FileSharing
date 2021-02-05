
import java.io.*;
import java.util.*;

import src.PeerAdmin;
import src.PeerLogger;

public class PeerProcess {
	public static void main(String[] args) {
		String peerID = args[0];
		PeerAdmin admin = new PeerAdmin(peerID);
	}
}
