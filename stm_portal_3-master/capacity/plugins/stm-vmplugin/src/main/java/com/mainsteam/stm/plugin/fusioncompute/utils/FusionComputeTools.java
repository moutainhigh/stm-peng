package com.mainsteam.stm.plugin.fusioncompute.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * TODO urn change uri
 * 
 * @author yuanlb
 * @2016年1月22日 下午2:20:24
 */
public class FusionComputeTools {

    public String  conver2DecimalString(final double value) {
        DecimalFormat t_decimalFormat = new DecimalFormat("0.00");
        t_decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return t_decimalFormat.format(value);
    }
    
    // URN: urn:sites:2F24053A:datastores:3
    // URI: /service/sites/2F24053A/datastores/3

    public String getUrnChangeUri(String urn) {
        String a, b;
        if (!urn.equals("") && null != urn) {
            a = urn.replaceAll("urn", "/service");
            b = a.replaceAll(":", "/");
            return b;
        }
        return urn;
    }

    /**
     * uri转urn
     * 
     * @param uri
     * @return
     */

    // URI: /service/sites/2F24053A/datastores/3
    // URN: urn:sites:2F24053A:datastores:3
    public String getUriChangeUrn(String uri) {
//String uri = "service/sites/2F24053A/hosts/289";
        String a, b;
        if (!uri.equals("") && null != uri) {
            a = uri.replaceAll("/", ":");
            b = a.replaceAll("service", "urn");
//            return b.subSequence(1, b.length()).toString();
            return b.substring(1,b.length());
        }
        return uri;
    }

  /*  *//**
     * 主机uri转site的uri
     * @param args
     *//*
    public String getHostUriChangeSiteUri(String hostUri) {
        String xx = "/service/sites/2F24053A/hosts/289";
        xx.replaceFirst("/hosts/","");
        return xx;
        //hostUri.substring(0,hostUri.length()-3);
    }*/
    
    public static void main(String[] args) {
//        String uri = "/service/sites/2F24053A/datastores/3";
        // System.out.println(new FusionComputeTools().getUrnChangeUri(urn));
        String uri = "/service/sites/2F24053A/hosts/289";
        System.out.println(new FusionComputeTools().getUriChangeUrn(uri));
        
//        System.out.println("@@@ "+new FusionComputeTools().getHostUriChangeSiteUri(uri));
        System.out.println(uri.substring(0,uri.length()-4));
    }
}
