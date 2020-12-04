/************
This work is licensed under a Creative Commons Attribution 4.0 International License https://creativecommons.org/licenses/by/4.0/legalcode.

Please refer to this work using: F. Gonçalves, B. Ribeiro, O. Gama, J. Santos, A. Costa, B. Dias, M. J. Nicolau, J. Macedo, and A. Santos, “Synthesizing Datasets with Security Threats for Vehicular Ad-Hoc Networks,” 
in IEEE Globecom 2020: 2020 IEEE Global Communications Conference (GLOBECOM’2020), Taipei, Taiwan
************ */


package com.uminho.easyride.iids.mainapp;


import com.dcaiti.vsimrti.fed.applicationNT.ambassador.simulationUnit.applications.ConfigurableApplication;
import com.dcaiti.vsimrti.fed.applicationNT.ambassador.simulationUnit.communication.AdHocModuleConfiguration;
import com.dcaiti.vsimrti.fed.applicationNT.ambassador.simulationUnit.operatingSystem.OperatingSystem;
import com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.CEtsi;
import com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.CheckDelta;
import com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.Data;
import com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.Reason;
import com.dcaiti.vsimrti.geographic.GeometryHelper;
import com.dcaiti.vsimrti.rti.eventScheduling.Event;
import com.dcaiti.vsimrti.rti.network.AdHocChannel;

/**
 * Abstract application implementing the ETSI standard.
 * ETSI TS 102 637-2 V1.2.1 (2011-03).
 *
 * @author VSimRTI developer team {@literal <vsimrti@fokus.fraunhofer.de>}
 *
 * @see com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.impl.Vehicle
 * @see com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.impl.ChargingStation
 * @see com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.impl.RoadSideUnit
 * @see com.dcaiti.vsimrti.fed.applicationNT.etsiApplications.impl.TrafficLight
 *
 */
public abstract class CustomAbstractEtsiIntervalApplication<OS extends OperatingSystem> extends ConfigurableApplication<CEtsi, OS> {

    private Data data;

    protected CustomAbstractEtsiIntervalApplication() {
        this(CEtsi.class, "ETSIApplication");
    }

    protected CustomAbstractEtsiIntervalApplication(Class<? extends CEtsi> configClazz, String configFileName) {
        super(configClazz, configFileName);
    }

    @Override
    public void setUp() {
        getLog().infoSimTime(this, "Initialize application");
        super.setUp();
        activateCommunicationModule();
        //firstSample();
    }

    protected void activateCommunicationModule() {
        getOperatingSystem().getAdHocModule().enable(new AdHocModuleConfiguration() //
                .addRadio().channel(AdHocChannel.CCH).power(50).create());
    }

    protected void sendCAM() {
        getOperatingSystem().getAdHocModule().sendCAM();
    }

    private void firstSample() {
        // create the first event to sample with a possible random timer offset
        long randomOffset = (long) (getRandom().nextDouble() * getConfiguration().maxStartOffset);
        final Event event = new Event(getOperatingSystem().getSimulationTime() + getConfiguration().minInterval + randomOffset, this);
        getOperatingSystem().getEventManager().addEvent(event);
    }

    private void sample() {
        // create a new event to sample something in a specific interval
        final Event event = new Event(getOperatingSystem().getSimulationTime() + getConfiguration().minInterval, this);
        getOperatingSystem().getEventManager().addEvent(event);
    }

    @Override
    public void processEvent(Event event) throws Exception {
        if (!isValidStateAndLog()) {
            return;
        }
        sample();
        checkData();
    }

    private void checkData() {

        //first initialize the data
        if (data == null) {
            data = generateETSIData();
            return;
        }

        Data newData = generateETSIData();

        if (data == null || newData == null) {
            getLog().infoSimTime(this, "Could not check delta. Data are not available yet.");
        }

        CheckDelta checkDelta = checkMaxDelta(data, newData, getConfiguration());

        // debug output
        if (getLog().isDebugEnabled()) {
            if (checkDelta != null) {
                getLog().debugSimTime(this, "Message will be sent: reason: {}, delta: {}", checkDelta.reason, checkDelta.getDeltaValue());
            } else {
                getLog().debugSimTime(this, "No message will be sent.");
            }
        }

        if (checkDelta != null) {
            data = newData;
            sendCAM();
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

    public abstract Data generateETSIData();

    @Override
    public void tearDown() {
        getLog().infoSimTime(this, "Shutdown application");
    }
}