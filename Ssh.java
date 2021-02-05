import java.io.*;

// Steps for pubkey generation:
// 1. Run "ssh-keygen" - Don't give passphrase
// 2. ssh-copy-id -i "your key" user@host // to one machine is enough

public class Ssh {
   public static void main(String[] args) {
      String username = "dreddy";	//Login UserID
      String projPath = "/cise/homes/dreddy/CN/project"; // path of the project where PeerProcess binary
                                                              // is
      String pubKey = "rsakey"; // location of the generated key
      try {
         Runtime.getRuntime().exec("ssh -i " + pubKey + " " + username +
         "@lin114-00.cise.ufl.edu cd " + projPath
         + " ; java PeerProcess 1001 ");
         Runtime.getRuntime().exec("ssh -i " + pubKey + " " + username +
         "@lin114-01.cise.ufl.edu cd " + projPath
         + " ; java PeerProcess 1002 ");
         Runtime.getRuntime().exec("ssh -i " + pubKey + " " + username +
         "@lin114-02.cise.ufl.edu cd " + projPath
         + " ; java PeerProcess 1003 ");
         Runtime.getRuntime().exec("ssh -i " + pubKey + " " + username +
         "@lin114-03.cise.ufl.edu cd " + projPath
         + " ; java PeerProcess 1004 ");
         Runtime.getRuntime().exec("ssh -i " + pubKey + " " + username +
         "@lin114-04.cise.ufl.edu cd " + projPath
         + " ; java PeerProcess 1005 ");
         Runtime.getRuntime().exec("ssh -i " + pubKey + " " + username +
         "@lin114-05.cise.ufl.edu cd " + projPath
         + " ; java PeerProcess 1006 ");
         Runtime.getRuntime().exec("ssh -i " + pubKey + " " + username +
         "@lin114-06.cise.ufl.edu cd " + projPath
         + " ; java PeerProcess 1007 ");
         Runtime.getRuntime().exec("ssh -i " + pubKey + " " + username +
         "@lin114-08.cise.ufl.edu cd " + projPath
         + " ; java PeerProcess 1008 ");
         Runtime.getRuntime().exec("ssh -i " + pubKey + " " + username +
         "@lin114-07.cise.ufl.edu cd " + projPath
         + " ; java PeerProcess 1009 ");
      } catch (Exception e) {
      }
   }
}
