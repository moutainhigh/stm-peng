package com.mainsteam.stm.plugin.smis.object;

import org.apache.commons.lang.StringUtils;

import javax.cim.CIMInstance;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class SMISDevice extends SMISObject {

    private Set<SMISDevice> adjacentDevices;

    public SMISDevice(CIMInstance instance) {
        super(instance);
        adjacentDevices = new HashSet<SMISDevice>();
    }

    public SMISDevice(SMISDevice device) {
        super(device.getInstance());
        adjacentDevices = device.getAdjacentDevices();
    }

    public Set<SMISDevice> getAdjacentDevices(String constraintString) {
        HashSet<SMISDevice> ret = new HashSet<SMISDevice>();
        ArrayList<SMISConstraint> constraints = new ArrayList<SMISConstraint>();
        for (String cString : constraintString.split(",")) {
            int equalIndex = cString.indexOf("=");
            constraints.add(new SMISConstraint(cString.substring(0, equalIndex).trim(), cString.substring(equalIndex + 1).trim()));
        }
        for (SMISDevice device : getAdjacentDevices()) {
            boolean flag = true;
            for (SMISConstraint constraint : constraints)
                if (!StringUtils.equalsIgnoreCase(device.getPropertyValue(constraint.key), constraint.value)) {
                    flag = false;
                    break;
                }
            if (flag)
                ret.add(device);
        }
        return ret;
    }

    public Set<SMISDevice> getAdjacentDevices() {
        return adjacentDevices;
    }

    public void setAdjacentDevices(Set<SMISDevice> adjacentDevices) {
        this.adjacentDevices = adjacentDevices;
    }

}
