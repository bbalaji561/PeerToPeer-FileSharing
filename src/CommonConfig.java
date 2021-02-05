package src;

import java.io.*;
import java.util.*;

public class CommonConfig {
    public int NumberOfPreferredNeighbors;
    public int UnchokingInterval;
    public int OptimisticUnchokingInterval;
    public String FileName;
    public int FileSize;
    public int PieceSize;

    public void loadCommonFile() {
        try {
            // System.out.println(System.getProperty("user.dir"));
            FileReader fobj = new FileReader("Common.cfg");
            Scanner fReader = new Scanner(fobj);
            while (fReader.hasNextLine()) {
                String line = fReader.nextLine();
                String[] temp = line.split(" ");
                if (temp[0].equals("NumberOfPreferredNeighbors")) {
                    this.NumberOfPreferredNeighbors = Integer.parseInt(temp[1]);
                } 
                else if (temp[0].equals("UnchokingInterval")) {
                    this.UnchokingInterval = Integer.parseInt(temp[1]);
                } 
                else if (temp[0].equals("OptimisticUnchokingInterval")) {
                    this.OptimisticUnchokingInterval = Integer.parseInt(temp[1]);
                } 
                else if (temp[0].equals("FileName")) {
                    this.FileName = temp[1];
                } 
                else if (temp[0].equals("FileSize")) {
                    this.FileSize = Integer.parseInt(temp[1]);
                } 
                else if (temp[0].equals("PieceSize")) {
                    this.PieceSize = Integer.parseInt(temp[1]);
                } else {
                    // Do Nothing
                }
            }
            fReader.close();
        } 
        catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
