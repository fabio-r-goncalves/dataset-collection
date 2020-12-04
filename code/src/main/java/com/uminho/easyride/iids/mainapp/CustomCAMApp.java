/************
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan
************ */


package com.uminho.easyride.iids.mainapp;

import com.dcaiti.vsimrti.fed.applicationNT.ambassador.SimulationKernel;
import com.dcaiti.vsimrti.fed.applicationNT.ambassador.simulationUnit.applicationInterfaces.CommunicationApplication;
import com.dcaiti.vsimrti.fed.applicationNT.ambassador.simulationUnit.applicationInterfaces.VehicleApplication;
import com.dcaiti.vsimrti.fed.applicationNT.ambassador.simulationUnit.applications.AbstractApplication;
import com.dcaiti.vsimrti.fed.applicationNT.ambassador.simulationUnit.communication.AdHocModuleConfiguration;
import com.dcaiti.vsimrti.fed.applicationNT.ambassador.simulationUnit.operatingSystem.VehicleOperatingSystem;
import com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.CEtsi;
import com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.CheckDelta;
import com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.Data;
import com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.Reason;
import com.dcaiti.vsimrti.geographic.GeometryHelper;
import com.dcaiti.vsimrti.rti.enums.VehicleClass;
import com.dcaiti.vsimrti.rti.eventScheduling.Event;
import com.dcaiti.vsimrti.rti.geometry.GeoPoint;
import com.dcaiti.vsimrti.rti.network.AdHocChannel;
import com.dcaiti.vsimrti.rti.objects.TIME;
import com.dcaiti.vsimrti.rti.objects.address.DestinationAddressContainer;
import com.dcaiti.vsimrti.rti.objects.address.TopocastDestinationAddress;
import com.dcaiti.vsimrti.rti.objects.v2x.AckV2XMessage;
import com.dcaiti.vsimrti.rti.objects.v2x.MessageRouting;
import com.dcaiti.vsimrti.rti.objects.v2x.ReceivedV2XMessage;
import com.dcaiti.vsimrti.rti.objects.v2x.V2XMessage;
import com.dcaiti.vsimrti.rti.objects.v2x.cam.CAM;
import com.dcaiti.vsimrti.rti.objects.v2x.cam.CAMContent;
import com.dcaiti.vsimrti.rti.objects.v2x.cam.VehicleAwarenessData;
import com.dcaiti.vsimrti.rti.objects.vehicle.VehicleInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CustomCAMApp extends AbstractApplication<VehicleOperatingSystem>
        implements VehicleApplication, CommunicationApplication {

    private Data data;
    private boolean isAttacker;
    private long lastAttack;
    private long attackDuration;
    private boolean isAttacking;
    
    private long INTERVAL;
    private int ATTACK_MAX_INTERVAL_SECONDS;
    private long newInterval;
    private boolean first;
    private TypesOfAttacks attackType;

    //hashmap to store the CAM parameters reveiced in a previous message
    private HashMap<String, ParametersObject> diffParameters;
    //cam field that is going to be replaced by random values
    private int camFieldToChange = 0;

    private HashMap<Integer, ArrayList<String>> vehicleMessagesPerSeccond;

    private long lastInstant;
    private double totalDensity;
    private int totalIntervals;
    private int maxVehicles = 0;


    @Override
    public void setUp() {
        initVars();
        
        getLog().infoSimTime(this, "Initialize application");
        //just to know if it is the first time running
        first = true;

        //object to store how many vehicles are in the simulation
        //not being used
        CountSimVehicles.getInstace().increaseVehicleNumber();
        getLog().debugSimTime(this, "Added vehicle to vehicle count");
        try {
            //Reads from the properties file if the vehicles should perform attacks
            boolean performAttacks = ReadProperties.getInstance().getPerformAttacks();
            getLog().infoSimTime(this, "Perform attacks {}", performAttacks);
            ATTACK_MAX_INTERVAL_SECONDS = ReadProperties.getInstance().getMaxAttackInterval();
            getLog().infoSimTime(this, "ATTACK_MAX_INTERVAL_SECONDS {}", ATTACK_MAX_INTERVAL_SECONDS);
            INTERVAL = ReadProperties.getInstance().getDefaultInterval();
            getLog().infoSimTime(this, "INTERVAL {}", INTERVAL);
            camFieldToChange = ReadProperties.getInstance().getCAMFieldToAttack();
            getLog().infoSimTime(this, "camFieldToChange {}", camFieldToChange);
            
            if (performAttacks) {
                //Randomly defined as true or false with a probability defined, indicating if the vehicle is going to be an attacker
                isAttacker = (RandomGenerator.getInstance().getInt(100)) < ReadProperties.getInstance()
                        .getAttackerProbability();
                getLog().infoSimTime(this, "Is attacker {}", isAttacker);
                
                //if the vehicle is an attacker, the attack type is going to be read from the properties file
                if (isAttacker) {
                    attackType = TypesOfAttacks.valueOf(ReadProperties.getInstance().getAttackType());
                    getLog().infoSimTime(this, "Attack Selected {}", attackType);
                    
                }
                getLog().infoSimTime(this, "Activate Communication Module");
                activateCommunicationModule();                
                
                //Initiate the first sample, this will trigger start the application running and chained events
                firstSample();
            }
        } catch (Exception e) {
            e.printStackTrace();
            getLog().warnSimTime(this, e.getStackTrace().toString());
            System.exit(-1);
        }
    }

    private void initVars(){
        totalIntervals = 0;
        totalDensity = 0;
        vehicleMessagesPerSeccond = new HashMap<>();
        diffParameters = new HashMap<>();
        lastAttack = 0;
        attackDuration = 0;
        isAttacking = false;
        newInterval = 0;
        lastInstant = 10;
    }

    @Override
    public void receiveV2XMessage(ReceivedV2XMessage receivedV2XMessage) {

        V2XMessage v2XMessage = receivedV2XMessage.getMessage();
        getLog().debugSimTime(this, "Message Received {}", v2XMessage);
        if(!(v2XMessage instanceof CAM)){
            getLog().debugSimTime(this, "Not CAM, Discarding");
            return;
        }
        else {
            CAM cam = (CAM) v2XMessage;
            long lastTime;
            GeoPoint lastPosition;
            double lastHeading;
            double lastSpeed;
            double lastAcc;
            getLog().debugSimTime(this, "CAM Received {}", cam);

            calculateVehiclesInRange(cam);
            
            //update the last received parameters, if not existing initialize with 0
            if (diffParameters.containsKey(cam.getUnitID())) {

                ParametersObject parametersObject = diffParameters.get(cam.getUnitID());
                lastTime = parametersObject.getLastTime();
                lastPosition = parametersObject.getLastPosition();
                lastHeading = parametersObject.getLastHeading();
                lastSpeed = parametersObject.getLastSpeed();
                lastAcc = parametersObject.getLastAcc();

            } else {
                lastTime = receivedV2XMessage.getTime();
                lastPosition = cam.getPosition();
                lastHeading = 0;
                lastSpeed = 0;
                lastAcc = 0;
            }

            long diff = receivedV2XMessage.getTime() - lastTime;

            AssyncronousWriter assyncronousWriter = null;

            VehicleAwarenessData vehicleAwarenessData = (VehicleAwarenessData) cam.getAwarenessData();

   

            ParametersObject parametersObject = new ParametersObject(
                cam.getPosition(), 
                vehicleAwarenessData.getLongitudinalAcceleration(), 
                vehicleAwarenessData.getSpeed(), 
                vehicleAwarenessData.getHeading(),
                receivedV2XMessage.getTime());

            double diffHeading = parametersObject.getLastHeading() - lastHeading;
            double diffPos = GeometryHelper.getDistance(lastPosition, parametersObject.getLastPosition());
            double diffSpeed = parametersObject.getLastSpeed() - lastSpeed;
            double diffAcc = parametersObject.getLastAcc() - lastAcc;

            
            //store received values
            diffParameters.put(cam.getUnitID(), parametersObject);
            //write to file
            try {
                getLog().debugSimTime(this, "Writing Parameters to File {}", parametersObject);
                assyncronousWriter = AssyncronousWriter.getInstance();
                //0 corresponds to the elevation, it is not being used at the moment
                assyncronousWriter.writeInfo(getOperatingSystem().getId(), receivedV2XMessage, diff, diffHeading,
                        diffPos, 0, diffSpeed, diffAcc);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                getLog().warnSimTime(this, e.getStackTrace().toString());
                System.exit(-1);
            }

        }

    }

    private void calculateVehiclesInRange(CAM cam){
        getLog().debugSimTime(this, "Calculating Vehicles in Range per second - Started");
        //from 10 to 10 secconds calulates the total density
        //total intervals calculates how many secconds have elapsed since the vehicle started couting
        //this to calculate the maximum vehicles that have beein in range and the average
        if ((getOperatingSystem().getSimulationTimeMs() / 1000) - lastInstant >= 10) {
            lastInstant = (getOperatingSystem().getSimulationTimeMs() / 1000);

            for (ArrayList<String> list : vehicleMessagesPerSeccond.values()) {
                totalIntervals = totalIntervals + 1;
                totalDensity = totalDensity + list.size();
                if (list.size() > maxVehicles) {
                    maxVehicles = list.size();
                }
            }

            vehicleMessagesPerSeccond = new HashMap<>();
        }

        int instant = (int) (getOperatingSystem().getSimulationTimeMs() / 1000 - lastInstant);
        if (!vehicleMessagesPerSeccond.containsKey(instant)) {
            vehicleMessagesPerSeccond.put(instant, new ArrayList<>());
        }
        if (!vehicleMessagesPerSeccond.get(instant).contains(cam.getUnitID())) {
            vehicleMessagesPerSeccond.get(instant).add(cam.getUnitID());
        }
        getLog().debugSimTime(this, "Calculating Vehicles in Range per second - Done");
    }

    @Override
    public void receiveV2XMessageAcknowledgement(AckV2XMessage ackV2XMessage) {

    }

    @Override
    public void beforeGetAndResetUserTaggedValue() {

    }

    @Override
    public void afterGetAndResetUserTaggedValue() {

    }

    @Override
    public void beforeSendCAM() {

    }

    @Override
    public void afterSendCAM() {

    }

    @Override
    public void beforeSendV2XMessage() {

    }

    @Override
    public void afterSendV2XMessage() {

    }

    @Override
    public void beforeUpdateConnection() {

    }

    @Override
    public void afterUpdateConnection() {

    }

    @Override
    public void beforeUpdateVehicleInfo() {

    }

    @Override
    public void afterUpdateVehicleInfo() {

    }

    

    private void activateCommunicationModule() {
        getOperatingSystem().getAdHocModule().enable(new AdHocModuleConfiguration() //
                .addRadio().channel(AdHocChannel.CCH).power(50).create());
    }

    private void firstSample() {
        // create the first event to sample with a possible random timer offset
        getLog().debugSimTime(this, "First Sample");
        long randomOffset = (long) (getRandom().nextDouble() * 2);
        long nextEvent = getOperatingSystem().getSimulationTime() + INTERVAL + randomOffset;
        final Event event = new Event(nextEvent, this);
        getLog().debugSimTime(this, "Next Event at {}", nextEvent);
        getOperatingSystem().getEventManager().addEvent(event);
    }

    private void sample() throws IOException {
        //chained event that will repeate during all the vehicle lifespan 
        Event event;
        // create a new event to sample something in a specific interval
        if (!isAttacker) {
            //if the vehicle is not the attacker, the next event will only repeat itself after the 100 ms, the minimum CAM generation time
            newInterval = INTERVAL;
            event = new Event(getOperatingSystem().getSimulationTime() + newInterval, this);
            getOperatingSystem().getEventManager().addEvent(event);
            return;
        } 
        if (!isAttacking) {
            //if the vehicle is in its attacking period
            int rand = RandomGenerator.getInstance().getInt(100);
            //generates a random number and if its smaller than the threshold defined for the the attacker it starts attacking;
            //for example, if there is only a 1% change of the vehicle starting to attack, only if rand is smaller that 1, the vehicle goes to attacking mode
            if (rand < ReadProperties.getInstance().getAttackProbability()) {
                isAttacking = true;

                newInterval = getNewInterval();

                attackDuration = RandomGenerator.getInstance().getInt(ATTACK_MAX_INTERVAL_SECONDS);
                lastAttack = getOperatingSystem().getSimulationTime();
                getLog().infoSimTime(this, "Started attacking {}", new Object[]{newInterval, newInterval / TIME.NANO_SECOND, attackDuration, });
                //indicate that is the first attack cicle
                first = true;
            } else {
                //if the vehicle does not start to attack, set the event interval to the default value
                newInterval = INTERVAL;
            }
        }

        event = new Event(getOperatingSystem().getSimulationTime() + newInterval, this);
        getOperatingSystem().getEventManager().addEvent(event);
    }

    private long getNewInterval() throws IOException {
        if (attackType == TypesOfAttacks.DOS) {
             //the divider will be used to calculate the new interval. this is the time to the next event
            double divider = (RandomGenerator.getInstance().getInt(ReadProperties.getInstance().getAttackInterval())
                    + ReadProperties.getInstance().getMinAttackInterval()) / (double) ReadProperties.getInstance().getDivider();
            //so, if this a DoS attack, the vehicle will send message with much higher frequencies
            newInterval = (long) (INTERVAL * divider);
        } else {
            //if it is a fabrication attack, the cam field to change has to be read from the properties file
            //Read from file to be implemented
            if (attackType == TypesOfAttacks.RANDOM_CAM_VALUE) {
                //field to define which cam field is to be changed
                camFieldToChange = 1;
                // speed
                // heading
                // longitudinalAcceleration
                getLog().infoSimTime(this, "CAM field to change {}", camFieldToChange);
            }
            newInterval = INTERVAL;
        }

        return newInterval;
    }
    //send CAM method
    private void sendCAM(boolean isAttack) throws FileNotFoundException {
       getLog().debugSimTime(this, "sending cam");
       //storing the simulation time at which the cam is to be sent
        AssyncronousWriter.getInstance().writeAttacks(getOperatingSystem().getId(),
                getOperatingSystem().getSimulationTime(), isAttack);
       
        if (attackType == TypesOfAttacks.RANDOM_CAM_VALUE && isAttacking) {
            //if a fabrication attack is selected, an altered CAM is sent
            sendChangedCAM();
        } else {
            //otherwise a normal cam is sent
            getOperatingSystem().getAdHocModule().sendCAM();
        }
    }

    @Override
    public void tearDown() {
        //write density to file
        PrintWriter densityWriter;
        FileOutputStream densityOutputStream;
        try {
            densityOutputStream = new FileOutputStream("density.csv", true);
            densityWriter = new PrintWriter(densityOutputStream);
            densityWriter.println( getOperatingSystem().getId() + "," + (totalDensity/totalIntervals)+","+maxVehicles);
            densityWriter.close();
            densityOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            getLog().warnSimTime(this, e.getStackTrace().toString());
        }
        
        
        CountSimVehicles.getInstace().decreaseVehicleNumber();
        getLog().infoSimTime(this, "density {}", new Object[]{(totalDensity/totalIntervals), maxVehicles});
        
    }

    @Override
    public void processEvent(Event event) throws FileNotFoundException {
        if (!isValidStateAndLog()) {
            return;
        }
        try {
            //firstly verify make all the neede operations and setup the next event
            sample();
        } catch (IOException e) {
            e.printStackTrace();
            getLog().warnSimTime(this, e.getStackTrace().toString());
            System.exit(-1);
        }
        //check if a CAM is supposed to be sent
        checkData(isAttacking);
        //if is attacking and not the first cicle verify if it should continue the attack
        if (isAttacking && !first) {
            if (lastAttack + attackDuration * TIME.SECOND < getOperatingSystem().getSimulationTime()) {
                isAttacking = false;
                getLog().infoSimTime(this, "Stopped attack {}", (getOperatingSystem().getSimulationTime() - lastAttack) / TIME.SECOND);
            }
        }
        first = false;
    }

    private void checkData(boolean isAttack) throws FileNotFoundException {

        // first initialize the data
        if (data == null) {
            data = generateETSIData();
            return;
        }

        Data newData = generateETSIData();

        if (data == null || newData == null) {
            getLog().infoSimTime(this, "Could not check delta. Data are not available yet.");
        }

        CEtsi maxDelta = new CEtsi();
        //As From the ETSI documentation
        maxDelta.maxInterval = (long) 1000000000;
        maxDelta.minInterval = (long) 100000000;
        maxDelta.positionChange = 4.0;
        maxDelta.headingChange = 4.0;
        maxDelta.velocityChange = 0.5;

        CheckDelta checkDelta = checkMaxDelta(data, newData, maxDelta);

        // debug output
        if (getLog().isDebugEnabled()) {
            if (checkDelta != null) {
                //getLog().debugSimTime(this, "Message will be sent: reason: {}, delta: {}", checkDelta.reason,checkDelta.getDeltaValue());

            } else {
                //getLog().debugSimTime(this, "No message will be sent.");
            }
        }

        if (checkDelta != null || isAttack) {
            if(isAttack){
                getLog().debugSimTime(this, "Message will be sent: reason: Attack");
            }else{
                getLog().debugSimTime(this, "Message will be sent: reason: {}, delta: {}", checkDelta.reason,checkDelta.getDeltaValue());
            }
            data = newData;
            sendCAM(isAttack);
        }
    }

    private CheckDelta checkMaxDelta(final Data oldData, final Data newData, final CEtsi maxDelta) {
        final CheckDelta checkDelta = new CheckDelta();

        if (maxDelta.minInterval != null) {
            // are we still under the minimum time limit?
            final long lDelta = newData.time - oldData.time;
            if (lDelta < maxDelta.minInterval) {
                checkDelta.lDelta = lDelta;
                checkDelta.reason = Reason.MININTERVAL;
                return checkDelta;
            }
        }

        if (maxDelta.maxInterval != null) {
            final long lDelta = newData.time - oldData.time;
            // did we exceed the maximum time limit?
            if (lDelta >= maxDelta.maxInterval) {
                checkDelta.lDelta = lDelta;
                checkDelta.reason = Reason.MAXINTERVAL;
                return checkDelta;
            }
        }

        if (maxDelta.headingChange != null) {
            // did the heading change?
            final double dDelta = Math.abs(newData.heading - oldData.heading);
            if (dDelta > maxDelta.headingChange) {
                checkDelta.dDelta = dDelta;
                checkDelta.reason = Reason.HEADINGCHANGE;
                return checkDelta;
            }
        }

        if (maxDelta.velocityChange != null) {
            // did the velocity change?
            final double dDelta = (newData.velocity - oldData.velocity);
            if (dDelta > maxDelta.velocityChange) {
                checkDelta.dDelta = dDelta;
                checkDelta.reason = Reason.VELOCITYCHANGE;
                return checkDelta;
            }
        }

        if (maxDelta.positionChange != null) {
            // did the position change?
            double dDelta = 0d;
            if (newData.projectedPosition != null && oldData.projectedPosition != null) {
                dDelta = oldData.projectedPosition.distanceTo(newData.projectedPosition);
            } else if (newData.position != null && oldData.position != null) {
                dDelta = GeometryHelper.orthodromicDistance(oldData.position, newData.position);
            }
            if (dDelta > maxDelta.positionChange) {
                checkDelta.dDelta = dDelta;
                checkDelta.reason = Reason.POSITIONCHANGE;
                return checkDelta;
            }
        }

        return null;
    }

    public Data generateETSIData() {

        VehicleInfo vi = this.getOperatingSystem().getNavigationModule().getVehicleInfo();
        if (vi == null) {
            return null;
        } else {
            Data myData = new Data();

            myData.heading = vi.getHeading();

            myData.time = this.getOperatingSystem().getSimulationTime();
            myData.position = vi.getPosition();
            myData.projectedPosition = vi.getProjectedPosition();
            myData.heading = vi.getHeading();
            myData.velocity = vi.getSpeed();

            return myData;
        }
    }

    CAM assembleCAMMessage(MessageRouting mr) {
        VehicleInfo vehicleInfo = getOperatingSystem().getNavigationModule().getVehicleInfo();
        if (vehicleInfo == null) {
            // getOsLog().warn("Cannot assemble CAM because " +
            // this.getOperatingSystem().getId() + " isn't ready yet.");
            return null;
        }

        int longitudinalAcceleration = VehicleAwarenessData.LONGITUDINAL_ACC_UNAVAILABLE;
        if (vehicleInfo.getLongitudinalAcceleration() != null) {
            // we get m/s^2 but need 0.1m/s^2
            longitudinalAcceleration = ((Double) (vehicleInfo.getLongitudinalAcceleration() * 10)).intValue();

            // now check against borders
            if (longitudinalAcceleration <= VehicleAwarenessData.LONGITUDINAL_ACC_MAX_NEGATIVE) {
                longitudinalAcceleration = VehicleAwarenessData.LONGITUDINAL_ACC_MAX_NEGATIVE;
            } else if (longitudinalAcceleration >= VehicleAwarenessData.LONGITUDINAL_ACC_MAX_POSITIVE) {
                longitudinalAcceleration = VehicleAwarenessData.LONGITUDINAL_ACC_MAX_POSITIVE;
            }
        }

        double speed = vehicleInfo.getSpeed();
        double heading = vehicleInfo.getHeading();

        if (attackType == TypesOfAttacks.RANDOM_CAM_VALUE) {

            switch (camFieldToChange) {
            case 0:
                speed = (RandomGenerator.getInstance().getDouble()) * (double) 14;
                break;
            case 1:
                heading = (RandomGenerator.getInstance().getDouble()) * (double) 370;
                break;
            case 2:
                longitudinalAcceleration = (int) ((RandomGenerator.getInstance().getDouble()) * (51))-40;
                break;
            }
        }

        VehicleAwarenessData awarenessData = new VehicleAwarenessData(VehicleClass.Car, speed, heading,
                getOperatingSystem().getInitialVehicleType().getLength(), 0, vehicleInfo.getDriveDirection(),
                vehicleInfo.getRoadPosition().getLaneIndex(), longitudinalAcceleration);

        return new CAM(Objects.requireNonNull(mr),
                new CAMContent(SimulationKernel.SimulationKernel.getCurrentSimulationTime(), awarenessData,
                        getOperatingSystem().getId(), getOperatingSystem().getPosition(), new byte[2]));
    }

    void sendChangedCAM() {

        DestinationAddressContainer dest = DestinationAddressContainer.createTopocastDestinationAddressAdHoc(
                TopocastDestinationAddress.getBroadcastSingleHop(), AdHocChannel.CCH);
        final MessageRouting routing = new MessageRouting(dest, getOperatingSystem().generateSourceAddressContainer());
        final CAM cam = this.assembleCAMMessage(routing);
        getOs().getAdHocModule().sendV2XMessage(cam);

    }

}
