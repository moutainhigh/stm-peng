package com.mainsteam.stm.plugin.smis.object;

import javax.cim.CIMInstance;

public abstract class SMISObject {

    private CIMInstance instance;

    public SMISObject(CIMInstance instance) {
        if (instance == null)
            throw new IllegalArgumentException("The instance can't be null!");
        this.instance = instance;
    }

    public String getPropertyValue(String name) {
        Object t = instance.getPropertyValue(name);
        if (t != null) {
            if (t instanceof Object[]) {
                StringBuilder ret = new StringBuilder("{");
                for (Object tObject : (Object[]) t)
                    ret.append(tObject).append(",");
                ret.deleteCharAt(ret.length() - 1).append("}");
                return ret.toString();
            }
            return t.toString();
        }
        return null;
    }

    public CIMInstance getInstance() {
        return instance;
    }

    public CIMInstance setInstance(CIMInstance instance) {
        return this.instance = instance;
    }

    @Override
    public String toString() {
        return instance.toString();
    }

}
