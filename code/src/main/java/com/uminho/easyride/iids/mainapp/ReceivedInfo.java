/************
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan
************ */

package com.uminho.easyride.iids.mainapp;

import com.dcaiti.vsimrti.rti.objects.v2x.ReceivedV2XMessage;
import com.dcaiti.vsimrti.rti.objects.v2x.V2XMessage;
import com.dcaiti.vsimrti.rti.objects.v2x.cam.CAM;
import com.dcaiti.vsimrti.rti.objects.v2x.cam.VehicleAwarenessData;

public class ReceivedInfo {
    private String receiverId;
    private String senderId;
    private long receivedTime;
    private long diffTime;
    private double heading;
    private double speed;
    private double longAcceleration;
    private long generationTime;
    private double elevation;
    private double latitude;
    private double longitude;
    private int bitLen;

    private double diffHeading;
    private double diffPos;
    private double diffElevation;
    private double diffSpeed;
    private double diffAcceL;

    public ReceivedInfo() {
    }

    public ReceivedInfo(V2XMessage v2XMessage) {

    }

    public ReceivedInfo(String receiverId, String senderId, long receivedTime, long diffTime, double heading, double speed, double longAcceleration, long generationTime, double elevation, double latitude, double longitude, int bitLen) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.receivedTime = receivedTime;
        this.diffTime = diffTime;
        this.heading = heading;
        this.speed = speed;
        this.longAcceleration = longAcceleration;
        this.generationTime = generationTime;
        this.elevation = elevation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bitLen = bitLen;
    }

    public void v2xToReceivedInfo(String receiverId, ReceivedV2XMessage receivedV2XMessage, long diffTime, double diffHeading, double diffPos, double diffElevation, double diffSpeed, double diffAcceL) {
        V2XMessage v2XMessage = receivedV2XMessage.getMessage();
        if (v2XMessage instanceof CAM) {

            CAM cam = (CAM) v2XMessage;
            this.receiverId = receiverId;
            this.senderId = cam.getUnitID();
            this.receivedTime = receivedV2XMessage.getTime();
            this.diffTime = diffTime;
            VehicleAwarenessData awarenessData = (VehicleAwarenessData) cam.getAwarenessData();
            
            this.heading = awarenessData.getHeading();
            this.speed = awarenessData.getSpeed();
            this.longAcceleration = awarenessData.getLongitudinalAcceleration();
          
            
            this.generationTime = cam.getGenerationTime();
            this.latitude = cam.getPosition().getLatitude();
            this.longitude = cam.getPosition().getLongitude();
            this.bitLen = receivedV2XMessage.getMessage().getEncodedV2XMessage().getBytes().length;
            this.diffTime = diffTime;
            this.diffAcceL = diffAcceL;
            this.diffElevation = diffElevation;
            this.diffHeading = diffHeading;
            this.diffPos = diffPos;
            this.diffSpeed = diffSpeed;
        }
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public long getDiffTime() {
        return diffTime;
    }

    public void setDiffTime(long diffTime) {
        this.diffTime = diffTime;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getLongAcceleration() {
        return longAcceleration;
    }

    public void setLongAcceleration(double longAcceleration) {
        this.longAcceleration = longAcceleration;
    }

    public long getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(long generationTime) {
        this.generationTime = generationTime;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getBitLen() {
        return bitLen;
    }

    public void setBitLen(int bitLen) {
        this.bitLen = bitLen;
    }

    public double getDiffHeading() {
        return diffHeading;
    }

    public void setDiffHeading(double diffHeading) {
        this.diffHeading = diffHeading;
    }

    public double getDiffPos() {
        return diffPos;
    }

    public void setDiffPos(double diffPos) {
        this.diffPos = diffPos;
    }

    public double getDiffElevation() {
        return diffElevation;
    }

    public void setDiffElevation(double diffElevation) {
        this.diffElevation = diffElevation;
    }

    public double getDiffSpeed() {
        return diffSpeed;
    }

    public void setDiffSpeed(double diffSpeed) {
        this.diffSpeed = diffSpeed;
    }

    public double getDiffAcceL() {
        return diffAcceL;
    }

    public void setDiffAcceL(double diffAcceL) {
        this.diffAcceL = diffAcceL;
    }

    @Override
    public String toString() {
        return "ReceivedInfo{" +
                "receiverId='" + receiverId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receivedTime=" + receivedTime +
                ", diffTime=" + diffTime +
                ", heading=" + heading +
                ", speed=" + speed +
                ", longAcceleration=" + longAcceleration +
                ", generationTime=" + generationTime +
                ", elevation=" + elevation +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", bitLen=" + bitLen +
                ", diffHeading=" + diffHeading +
                ", diffPos=" + diffPos +
                ", diffElevation=" + diffElevation +
                ", diffSpeed=" + diffSpeed +
                ", diffAcceL=" + diffAcceL +
                '}';
    }

    public String toCSVString() {

        return receiverId + "," +
                senderId + "," +
                receivedTime + "," +
                diffTime + "," +
                heading + "," +
                speed + "," +
                longAcceleration + "," +
                generationTime + "," +
                elevation + "," +
                latitude + "," +
                longitude + "," +
                bitLen + "," +
                diffPos + "," +
                diffSpeed + "," +
                diffHeading + "," +
                diffElevation + "," +
                diffAcceL + ",";
    }
}
