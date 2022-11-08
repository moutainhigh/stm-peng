package com.mainsteam.stm.plugin.smis.collect.factory;

import com.mainsteam.stm.plugin.smis.collect.*;
import com.mainsteam.stm.plugin.smis.exception.SMISException;

import javax.wbem.WBEMException;

public class SMISCollectorFactory {

    public static SMISCollector createDiskArrayCollector(SMISProvider provider) throws WBEMException, SMISException {
        if (provider.getVendor() == null)
            return new DiskArrayCollector(provider);
        switch (provider.getVendor()) {
            case SMISProvider.VENDOR_IBM_TSSVC:
                return new IBMTSSVCCollector(provider);
            case SMISProvider.VENDOR_HP:
                return new HPQCollector(provider);
            case SMISProvider.VENDOR_NETAPP_ONTAP:
                return new ONTAPCollector(provider);
            default:
                return new DiskArrayCollector(provider);
        }
    }

    public static SMISCollector createDiskArrayCollector(SMISProvider provider, String name) throws WBEMException, SMISException {
        if (provider.getVendor() == null)
            return new DiskArrayCollector(provider, name);
        switch (provider.getVendor()) {
            case SMISProvider.VENDOR_HITACHI:
                return new HITACHICollector(provider, name);
            case SMISProvider.VENDOR_IBM_TSSVC:
                return new IBMTSSVCCollector(provider, name);
            case SMISProvider.VENDOR_HP:
                return new HPQCollector(provider, name);
            case SMISProvider.VENDOR_NETAPP_ONTAP:
                return new ONTAPCollector(provider, name);
            case SMISProvider.VENDOR_EMC:
                return new EMCCollector(provider, name);
//            case SMISProvider.VENDOR_EMC_UNITY:
//                return new EMCUNITYCollector(provider, name);
            case SMISProvider.VENDOR_HP3PAR:
            	return new HP3ParCollector(provider, name);
            case SMISProvider.VENDOR_IBMLSI:
            	return new IBMLSICollector(provider, name);
            case SMISProvider.VENDOR_DELL:
            	return new DELLCollector(provider, name);
            case SMISProvider.VENDOR_HUAWEI:
            	return new HUAWEICollector(provider, name);
            case SMISProvider.VENDOR_HPMSA:
            	return new HPMSACollector(provider, name);
            case SMISProvider.VENDOR_HUAWEIV:
            	return new HUAWEIVCollector(provider, name);
            case SMISProvider.VENDOR_IBMTSDS:
            	return new IBMTSDSCollector(provider, name);
            case SMISProvider.VENDOR_MacroSAN:
            	return new MacroSANCollector(provider, name);
            case SMISProvider.VENDOR_Sugon:
            	return new SugonCollector(provider, name);
            case SMISProvider.VENDOR_HPEVA:
            	return new HPEVACollector(provider, name);
            default:
                return new DiskArrayCollector(provider, name);
        }
    }
}
