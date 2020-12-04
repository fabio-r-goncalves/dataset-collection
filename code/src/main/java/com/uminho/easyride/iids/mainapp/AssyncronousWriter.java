/************
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan
************ */
package com.uminho.easyride.iids.mainapp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dcaiti.vsimrti.rti.objects.v2x.ReceivedV2XMessage;

public class AssyncronousWriter {
    private static AssyncronousWriter instance = null;
    private final Object infosLock = new Object();
    private final Object attacksLock = new Object();
    private final String INFOS_FILE = "received_info";
    private final String ATTACKS_FILE = "attacks_info";
    private final String EXTENSION = ".csv";
    private List<ReceivedInfo> receivedInfos;
    private List<AttacksObject> attacksObjects;
    private Thread thread;
    private PrintWriter infosWriter;
    private PrintWriter attacksWriter;

    private AssyncronousWriter() throws FileNotFoundException {
        FileOutputStream infosOutputStream = new FileOutputStream(INFOS_FILE + new Date().getTime() + EXTENSION, false);
        FileOutputStream attacksOutputStream = new FileOutputStream(ATTACKS_FILE + new Date().getTime() + EXTENSION, false);
        infosWriter = new PrintWriter(infosOutputStream);
        attacksWriter = new PrintWriter(attacksOutputStream);

        receivedInfos = new ArrayList<>();
        attacksObjects = new ArrayList<>();
        asyncInfoWrite();
        asyncAttacksWrite();
    }

    public static AssyncronousWriter getInstance() throws FileNotFoundException {
        if (instance == null) {
            instance = new AssyncronousWriter();
        }

        return instance;
    }

    public void writeInfo(String receiverId, ReceivedV2XMessage receivedV2XMessage, long diffTime, double diffHeading, double diffPos, double diffElevation, double diffSpeed, double diffAcceL) {
        ReceivedInfo receivedInfo = new ReceivedInfo();
        receivedInfo.v2xToReceivedInfo(receiverId, receivedV2XMessage, diffTime, diffHeading, diffPos, diffElevation, diffSpeed, diffAcceL);
        receivedInfos.add(receivedInfo);
        synchronized (infosLock) {
            infosLock.notify();
        }
    }

    public void writeAttacks(String vehicle, long time, boolean isAttack){
        AttacksObject attacksObject = new AttacksObject(vehicle, time, isAttack);
        attacksObjects.add(attacksObject);

        synchronized (attacksLock){
            attacksLock.notify();
        }
    }

    private void asyncInfoWrite() {
        thread = new Thread(() -> {
            synchronized (infosLock) {
                while (true) {
                    while (receivedInfos.isEmpty()) {
                        try {
                            infosLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    ReceivedInfo receivedInfo = receivedInfos.remove(0);
                    if(receivedInfo != null) {
                        String infoString = receivedInfo.toCSVString();
                        infosWriter.println(infoString);
                        infosWriter.flush();
                    }

                }
            }
        });
        thread.start();
    }

    private void asyncAttacksWrite(){
        thread = new Thread(() -> {
            synchronized (attacksLock) {
                while (true) {
                    while (attacksObjects.isEmpty()) {
                        try {
                            attacksLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    AttacksObject attacksObject = attacksObjects.remove(0);
                    if(attacksObject != null) {
                        attacksWriter.println(attacksObject.toCSVString());
                        attacksWriter.flush();
                    }
                }
            }
        });
        thread.start();
    }
}
