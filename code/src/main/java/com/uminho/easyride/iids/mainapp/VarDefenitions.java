/************
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan
************ */

package com.uminho.easyride.iids.mainapp;

/**
 * VarDefenitions
 */
public class VarDefenitions {
    //type of attack to be made -> DOS, RANDOM_CAM_VALUE
    public static final String ATTACK_TYPE = "ATTACK_TYPE";

    //If the vehicles will perform attacks
    public static final String PERFORM_ATTACKS = "PERFORM_ATTACKS";
    //If in DOS, Interval that is going to be used to calculate the divider
    public static final String ATTACK_INTERVAL = "INTERVAL";
    //The attack will be made from MIN_ATTACK_TIME < x < MIN_ATTACK_TIME + INTERVAL
    public static final String MIN_ATTACK_TIME = "MIN_ATTACK_TIME";
    //Value to calculate the new interval
    //New Interval = (MIN_ATTACK_TIME < x < MIN_ATTACK_TIME + INTERVAL)/DIVIDE * INTERVAL
    public static final String DIVIDER = "DIVIDER";
    //Probability of becomming an attacker
    public static final String ATTACKER_PROBABILITY = "ATTACKER_PROB";
    //Probability of starting an attack at each time step
    public static final String ATTACK_PROBABILITY = "ATTACK_PROB";
    //Max attack interval
    public static final String MAX_ATTACK_INTERVAL = "MAX_ATTACK_INTERVAL";
    //Event Interval
    public static final String DEFAULT_INTERVAL = "DEFAULT_INTERVAL";
    //CAM field to be attacked
    public static final String CAM_FIELD_TO_ATTACK = "CAM_FIELD_TO_ATTACK";

    //Default Values
    public static final String ATTACK_TYPE_DEFAULT = "RANDOM_CAM_VALUE";
    public static final String ATTACK_INTERVAL_DEFAULT = "15";
    public static final String DIVIDER_DEFAULT = "100";
    public static final String ATTACKER_PROBABILITY_DEFAULT = "20";
    public static final String ATTACK_PROBABILITY_DEFAULT = "1";
    public static final String MIN_ATTACK_TIME_DEFAULT = "0";
    public static final String MAX_ATTACK_INTERVAL_DEFAULT = "30";
    public static final String DEFAULT_INTERVAL_DEFAULT = "100000000";
    public static final String CAM_FIELD_TO_ATTACK_DEFAULT = "0"
}