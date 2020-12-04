/************
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan
************ */

package com.uminho.easyride.iids.mainapp;

public class AttacksObject {
    private String vehicle;
    private long time;
    private boolean isAttack;

    public AttacksObject(String vehicle, long time, boolean isAttack) {
        this.vehicle = vehicle;
        this.time = time;
        this.isAttack = isAttack;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isAttack() {
        return isAttack;
    }

    public void setAttack(boolean attack) {
        isAttack = attack;
    }

    @Override
    public String toString() {
        return "AttacksObject{" +
                "vehicle='" + vehicle + '\'' +
                ", time=" + time +
                ", isAttack=" + isAttack +
                '}';
    }

    public String toCSVString(){
        return this.vehicle+","+this.time+","+this.isAttack;
    }
}
