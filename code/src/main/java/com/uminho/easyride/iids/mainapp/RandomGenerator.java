/************
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan
************ */


package com.uminho.easyride.iids.mainapp;

import java.security.SecureRandom;

public class RandomGenerator {
    private SecureRandom secureRandom;
    private static RandomGenerator instance = null;

    private RandomGenerator(){
        secureRandom = new SecureRandom();
    }

    public static RandomGenerator getInstance(){
        if(instance == null){
            instance = new RandomGenerator();
        }

        return instance;
    }

    public int getInt(){
        return secureRandom.nextInt();
    }

    public long getLong(){
        return secureRandom.nextLong();
    }

    public double getDouble(){
        return  secureRandom.nextDouble();
    }

    public  int getInt(int interval){
        return secureRandom.nextInt(interval);
    }
}
