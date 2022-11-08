package com.team4099.lib.networking;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class NetworkTablesDesktopClient {

    public static void main(String[] args) {
        new NetworkTablesDesktopClient().run();
    }
    public void run() {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        NetworkTable table = inst.getTable("FMSInfo");
        NetworkTableEntry alertEntry = table.getEntry("dubs");
        inst.startClient("localhost");
        inst.startDSClient();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("interrupted");
                return;
            }

            System.out.println(alertEntry.isValid());
            System.out.println(table.getKeys());
            String[] x = alertEntry.getStringArray(new String[0]);
            for (String a: x){
                System.out.println(a);
            }
        }
    }
}