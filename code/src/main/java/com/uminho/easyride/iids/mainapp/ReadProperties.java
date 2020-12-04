/************
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan
************ */

package com.uminho.easyride.iids.mainapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ReadProperties
 */
public class ReadProperties {
    Properties properties;
    private static ReadProperties instance = null;

    private ReadProperties() throws IOException {
        InputStream input = new FileInputStream("scenarios/ids/applicationNT/props.conf");

        properties = new Properties();
        properties.load(input);
    }

    public static ReadProperties getInstance() throws IOException {
        if(instance == null){
            instance = new ReadProperties();
        }
        return instance;
    }

    public boolean getPerformAttacks(){
        String prop = properties.getProperty(VarDefenitions.PERFORM_ATTACKS);
        if(prop != null && prop.equalsIgnoreCase("true")){
            return true;
        }
        return false;
    }

    public int getAttackInterval(){
        String prop = properties.getOrDefault(VarDefenitions.ATTACK_INTERVAL, VarDefenitions.ATTACK_INTERVAL_DEFAULT).toString();
        return Integer.parseInt(prop);
    }

    public int getDivider(){
        String prop = properties.getOrDefault(VarDefenitions.DIVIDER, VarDefenitions.DIVIDER_DEFAULT).toString();
        return Integer.parseInt(prop);
    }

    public int getAttackerProbability(){
        String prop = properties.getOrDefault(VarDefenitions.ATTACKER_PROBABILITY, VarDefenitions.ATTACKER_PROBABILITY_DEFAULT).toString();
        return Integer.parseInt(prop);
    }

    public int getAttackProbability(){
        String prop = properties.getOrDefault(VarDefenitions.ATTACK_PROBABILITY, VarDefenitions.ATTACKER_PROBABILITY_DEFAULT).toString();
        return Integer.parseInt(prop);
    }

    public int getMinAttackInterval(){
        String prop = properties.getOrDefault(VarDefenitions.MIN_ATTACK_TIME, VarDefenitions.MIN_ATTACK_TIME_DEFAULT).toString();
        return Integer.parseInt(prop);
    }
    
    public String getAttackType(){
        String prop = properties.getOrDefault(VarDefenitions.ATTACK_TYPE, VarDefenitions.ATTACK_TYPE_DEFAULT).toString();
        return prop;
    }

    public int getMaxAttackInterval(){
        String prop = properties.getOrDefault(VarDefenitions.MAX_ATTACK_INTERVAL, VarDefenitions.MAX_ATTACK_INTERVAL_DEFAULT).toString();
        return Integer.parseInt(prop);
    }

    public int getDefaultInterval(){
        String prop = properties.getOrDefault(VarDefenitions.DEFAULT_INTERVAL, VarDefenitions.DEFAULT_INTERVAL_DEFAULT).toString();
        return Integer.parseInt(prop);
    }

    public int getCAMFieldToAttack(){
        String prop = properties.getOrDefault(VarDefenitions.CAM_FIELD_TO_ATTACK, VarDefenitions.CAM_FIELD_TO_ATTACK_DEFAULT).toString();
        return Integer.parseInt(prop);
    }
}