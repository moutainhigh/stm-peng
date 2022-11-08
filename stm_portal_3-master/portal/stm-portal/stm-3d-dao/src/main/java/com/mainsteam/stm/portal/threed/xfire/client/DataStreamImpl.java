
package com.mainsteam.stm.portal.threed.xfire.client;

import javax.jws.WebService;

@WebService(serviceName = "DataStream", targetNamespace = "http://service.webservice.monitoring.uinv.com", endpointInterface = "client.DataStreamPortType")
public class DataStreamImpl
    implements DataStreamPortType
{


    public String closeAlarm(String str) {
        throw new UnsupportedOperationException();
    }

    public String pushAlarm(String str) {
        throw new UnsupportedOperationException();
    }

    public String pushAlarm1(String str, boolean rule) {
        throw new UnsupportedOperationException();
    }

    public String pushMonitor1(String str, boolean rule) {
        throw new UnsupportedOperationException();
    }

    public String addRackEquipment(String str) {
        throw new UnsupportedOperationException();
    }

    public String getNodeTree() {
        throw new UnsupportedOperationException();
    }

    public String getNodeAppendTypeForTree() {
        throw new UnsupportedOperationException();
    }

    public String updateRackEquipment(String str) {
        throw new UnsupportedOperationException();
    }

    public String closeMonitor(String str) {
        throw new UnsupportedOperationException();
    }

    public String deleteRackEquipment(String str) {
        throw new UnsupportedOperationException();
    }

    public String pushMonitor(String str) {
        throw new UnsupportedOperationException();
    }

    public String getProductInfo() {
        throw new UnsupportedOperationException();
    }

}
