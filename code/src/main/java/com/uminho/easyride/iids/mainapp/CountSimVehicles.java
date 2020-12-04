/************
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan
************ */

package com.uminho.easyride.iids.mainapp;

/**
 * CountSimVehicles
 */
public class CountSimVehicles {
    private int numVehicles;
    private static CountSimVehicles instance = null;

    public static CountSimVehicles getInstace(){
        if(instance == null){
            instance = new CountSimVehicles();
        }

        return instance;
    }

    private CountSimVehicles(){
        this.numVehicles = 0;
    }

    public void increaseVehicleNumber(){
        this.numVehicles = this.numVehicles + 1;
    }

    public void decreaseVehicleNumber(){
        this.numVehicles = this.numVehicles - 1;
    }

    public int getVehicleNumber(){
        return this.numVehicles;
    }
    
}