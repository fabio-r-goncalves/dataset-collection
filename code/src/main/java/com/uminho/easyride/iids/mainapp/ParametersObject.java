/************
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan
************ */

package com.uminho.easyride.iids.mainapp;

import com.dcaiti.vsimrti.rti.geometry.GeoPoint;

public class ParametersObject {
    private GeoPoint lastPosition;
    private double lastAcc;
    private double lastSpeed;
    private double lastHeading;
    private long lastTime;

    public ParametersObject(GeoPoint lastPosition, double lastAcc, double lastSpeed, double lastHeading, long lastTime) {
        this.lastPosition = lastPosition;
        this.lastAcc = lastAcc;
        this.lastSpeed = lastSpeed;
        this.lastHeading = lastHeading;
        this.lastTime = lastTime;
    }

    public GeoPoint getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(GeoPoint lastPosition) {
        this.lastPosition = lastPosition;
    }

    public double getLastAcc() {
        return lastAcc;
    }

    public void setLastAcc(double lastAcc) {
        this.lastAcc = lastAcc;
    }

    public double getLastSpeed() {
        return lastSpeed;
    }

    public void setLastSpeed(double lastSpeed) {
        this.lastSpeed = lastSpeed;
    }

    public double getLastHeading() {
        return lastHeading;
    }

    public void setLastHeading(double lastHeading) {
        this.lastHeading = lastHeading;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
}
