#This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

#Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
#in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan

import subprocess
from os import listdir
from os.path import isfile, join
import argparse



ATTACKS_FILE_NAME="attacks_info"
RECEIVED_FILE_NAME="received_info"
APPLICATION_DIR="applicationNT"
SUMO_DIR="sumo"
MAPPING_DIR="mapping3"
VSIMRTI_COMMAND="./vsimrti.sh"
VSIMRTI_USER = ""
VSIMRTI_SCENARIO = " -c scenarios/ids/vsimrti/vsimrti_config.xml -w 0"
NUMBER_OF_SCENARIOS = 0
INIT_NUMBER=0


def call_vsimrti():
    command = VSIMRTI_COMMAND + " -u " + VSIMRTI_USER + " " + VSIMRTI_SCENARIO
    print("Running VSimRTI: " + command)
    subprocess.check_call(command, cwd="/home/vsimrti/", shell=True)  

def copy_db_file(scenario_number):
    command = "cp ../gualtar"+str(scenario_number)+".db ../"+APPLICATION_DIR+"/gualtar.db"
    print("Copying db files: " + command)
    subprocess.call(command, shell=True)

def copy_sumo_file(scenario_number):
    command = "cp ../"+SUMO_DIR+"/gualtar"+str(scenario_number)+"/sumo_config.json"+" ../"+SUMO_DIR+"/sumo_config.json"
    print("Copying sumo files: "+command)
    subprocess.call(command, shell=True)

def copy_mapping_file(scenario_number):
    command = "cp ../"+MAPPING_DIR+"/gualtar"+str(scenario_number)+"/mapping_config.json"+" ../"+MAPPING_DIR+"/mapping_config.json"
    print("Copying sumo files: "+command)
    subprocess.call(command, shell=True)

def fix_result_name(scenario_number):
    files = [f for f in listdir("/home/vsimrti/")]
    for file in files:
        if ATTACKS_FILE_NAME in file:
            command = "mv /home/vsimrti/"+file+" /home/vsimrti/attacks_gualtar_"+str(scenario_number)+".csv"
            print(command)
            subprocess.call(command, shell=True)
        elif RECEIVED_FILE_NAME in file:
            command = "mv /home/vsimrti/"+file+" /home/vsimrti/received_gualtar_"+str(scenario_number)+".csv"
            print(command)
            subprocess.call(command, shell=True)
    subprocess.call("mv /home/vsimrti/density.csv /home/vsimrti/density_"+str(scenario_number)+".csv", shell=True)

def getParsedOptions():
    parser = argparse.ArgumentParser(description="Script that allows to run multiple VSimRTI scenarios It runs scenarios from initscenario to initiscenario + numscenarios")
    parser.add_argument("-u", "--user", help="VSimRTI user", dest="user")
    parser.add_argument("-n", "--numscenarios", help="number of scenarios to run", dest="num_scenarios")
    parser.add_argument("-i", "--initsceario", help="scenario to init", dest="init_scenario")
    args = parser.parse_args()
    return args.user, args.num_scenarios, args.init_scenario
    

if __name__ == "__main__":
    vsim_user, num_scenarios, init_num = getParsedOptions()
    print("user: %s" % vsim_user)
    print("num_scenarios %d" %int(num_scenarios))
    print("init scenario %d" %int(init_num))

    VSIMRTI_USER = vsim_user
    INIT_NUMBER = int(init_num) -1
    NUMBER_OF_SCENARIOS = int(num_scenarios)

    for i in range(INIT_NUMBER, NUMBER_OF_SCENARIOS):
        scenario = i + 1
        print("Running scenario %d" % (scenario))
        copy_db_file(scenario)
        copy_sumo_file(scenario)
        copy_mapping_file(scenario)
        call_vsimrti()
        fix_result_name(scenario)
        print("\n\n")


