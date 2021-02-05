# PeerToPeer-FileSharing
In this project, a P2P file sharing software similar to BitTorrent is implemented in Java. BitTorrent is a popular P2P protocol for file distribution. Among its interesting features, choking-unchoking mechanism which is one of the most important features of BitTorrent. In the Protocol Description pdf file, you can read the protocol description, which has been modified a little bit from the original BitTorrent protocol. After reading the protocol description carefully, you can follow the implementation specifics shown in the Implementation Specifics section.

## Program Steps:
To run the project, prerequisite is the following:
- Login to the remote machine, copy the source coded, and compile the code: "javac PeerProcess.java".
- To run PeerProcces in the remote machines, we wrote a wrapper. So if we start the wrapper, all the processes will be started in the remote machines.
- To connect to the remote machines via our local machines, we need to first generate the keys.

Follow the below to generate the keys:
1. Run "ssh-keygen" - it will prompt for file name and passphrase, please don't have a passphrase.
2. Run "ssh-copy-id -i <your_key> <username>@<remote_machine>" - Copy the key to one of your remote machines.
- Login to all the remote machines using this key once before you start the wrapper using "ssh -i <your_key> <username>@<remote_machine>". This will avoid the fingerprint prompt message once you start the wrapper.
- Copy the Common.config file, PeerInfo.config file and the actual file to be transferred in the corresponding peer directories under the folder "project/".
- In the Ssh wrapper file, please change the username to yours, projPath to where you run the program and 'pubKey' to your generated rsakey.
- To start the wrapper compile the Ssh.java program - "javac Ssh.java", and run the wrapper - "java Ssh".
