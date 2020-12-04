#This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

#Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
#in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan

import csv
import time
import argparse
import os
from os import listdir
    
def mergeData(received_path, attacks_path, output_path, file_name_end):
    total_counter = 0
    false_counter = 0
    true_counter = 0
    print("Merging "+received_path + " with " + attacks_path + " storing in " + output_path + "out_" + file_name_end)
    attacks = {}
    start_time = time.time()
    with open(attacks_path) as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        for row in csv_reader:
            attacks[(row[0], row[1])] = row[2]

    received = []        
    with open(received_path) as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        for row in csv_reader:
            try:
                if(int(row[3]) != 0):
                    value = attacks[(row[1], row[7])]
                    if(value in "true"):
                        true_counter = true_counter + 1
                    else:
                        false_counter = false_counter +1
                    total_counter = total_counter + 1
                    row = row[:len(row) -1]
                    
                    row.append(value)
                    received.append(",".join(row))
            except:
                pass

    fp = open(output_path + "out_"+file_name_end, "w")

    for rcvd in received:
        fp.write(rcvd)
        fp.write("\n")
    fp.close()    

    elapsed_time = time.time() - start_time
    print(len(received))
    print("Merged %d records in %f seconds" % (len(received), elapsed_time))
    print("out_"+file_name_end + "\nTrue: %d %f\nFalse: %d %f\nTotal: %d" % 
        (true_counter, (true_counter/total_counter*100), false_counter, (false_counter/total_counter*100), total_counter))



def getParsedOptions():
    parser = argparse.ArgumentParser(description="Script that allows to merge all files outputed from VSimRTI")
    parser.add_argument("-o", "--ordir", help="origin directory", dest="origin_dir")
    parser.add_argument("-d", "--destdir", help="destination directory", dest="destination_dir")
    parser.add_argument("-m", "--mergeddir", help="merged file directory", dest="merged_dir")
    args = parser.parse_args()
    return args.origin_dir, args.destination_dir, args.merged_dir

if __name__ == "__main__":
    origin_dir, destination_dir, merged_dir = getParsedOptions()
    print("Copying files from: " + origin_dir + " to " + destination_dir)
    print("cp " + origin_dir +"*.csv " + destination_dir)
    os.system("cp " + origin_dir +"*.csv " + destination_dir)
    files_in_out_dir = len(listdir(merged_dir))
    for f in listdir(destination_dir):
        if "received_gualtar_" in f:
            file_parts = f.split("received_gualtar_")

            
            mergeData(
                destination_dir + f, 
                destination_dir + "attacks_gualtar"+f.split("received_gualtar")[1], 
                merged_dir,
                file_parts[1])