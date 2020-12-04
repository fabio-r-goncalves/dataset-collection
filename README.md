
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan

**The datasets described in the aboce paper are available at:** [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.4304411.svg)](https://doi.org/10.5281/zenodo.4304411)



# Synthesizing Datasets With Security Threats for Vehicular Ad-Hoc Networks

Implementation of the solution described in "F. Gonc¸alves, B. Ribeiro, O´ . Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan."

It allows generating datasets with (and without) attacks using several maps. Currently, only two attacks are implemented:

- DoS attack
- Fabrication attack

The datasets described in the paper can be obtained at: **To be available shortly**

To facilitate the dataset to be generated, two scripts can be found in the  **scripts** folder:

- **run_script.py** : allow to run several scenarios using VSimRTI;
- **merger.py** : allows to merge the files generated from VSimRTI;

## Running several scenarios

Using the script **run_script.py** it is possible to run several scenarios. This command receives the arguments:

- -u: the VSimRTI user (needs to obtain a license from VSimRTI team);
- -n: the number of scenarios to be run;
- -i: the number of the scenario where the count will be initiated;

This script will run through the several **sumo** and **mapping** dirs and run the scenarios indicated in command line parameters.

## Merge the ouputed scenarios

The VSimRTI application will generate two different files, one with the received messages and the other with the sent. The script **merger.py** can be used to merge these two files. This command receives the arguments:

- -o: the directory where the files output from the VSimRTI is located;
- -d: the directory where the files will be copied to;
- -m: the directory where the merged files will be located;

## Added scenarios

A few scenarios have already been added on top of the ones from the paper. Their description will be added to this README file.

