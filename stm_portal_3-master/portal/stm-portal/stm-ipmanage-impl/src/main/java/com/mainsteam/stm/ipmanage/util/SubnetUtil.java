package com.mainsteam.stm.ipmanage.util;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.ipmanage.bo.Segment;
import com.mainsteam.stm.ipmanage.bo.SubnetBo;

public class SubnetUtil {
    public static void subnetting(SubnetBo f){
        int power2 = 2 ;
        Integer subnetNum=f.getSubnetNum();
        if (f.getIp1()> 255) f.setIp1(  255);
        if (f.getIp2() > 255) f.setIp2(255);
        if (f.getIp3() > 255) f.setIp3(255);
        if (f.getIp4() > 255) f.setIp4(255);
        if ((f.getIp1() > 0) && (f.getIp1() < 127)) f.setNetClass('A');
        if ((f.getIp1() > 127) && (f.getIp1() < 192)) f.setNetClass('B');
        if ((f.getIp1() > 191) && (f.getIp1() < 224)) f.setNetClass('C');
        if ((f.getIp1() < 1) || (f.getIp1() > 223)) {
            f=null;
        }
        if (((f.getNetClass()=='A') && (f.getSelectClass().equals("A"))) || ((f.getSelectClass().equals("A")) && (f.getNetClass()!=null))) {
            if (subnetNum >= 1) f.setNodeNum(16777216 / subnetNum);
            int nwtemp;
            int node;
            if ((node=f.getNodeNum()) > 16777216) {

                subnetNum = 1073741824 / node;
                nwtemp = subnetNum;
                f.setMask1(~((64 / subnetNum) - 1) & 255);
                f.setMask2(0);
                f.setMask3(0);
                f.setMask4(0);
                subnetNum = 1;
            } else {
                f.setNetClass('A');
                subnetNum = 16777216 / node;
                nwtemp = subnetNum;
                f.setMask1(255);
                f.setMask2(~((256 / subnetNum) - 1) & 255);
                f.setMask3(~((65536 / subnetNum) - 1) & 255);
                f.setMask4(~((16777216 / subnetNum) - 1) & 255);
                f.setHasSubnets(true);
                power2 = power2 + 6;
            }
            while (nwtemp > 1) {
                nwtemp = nwtemp / 2;
                power2 = power2 + 1;
            }
            f.setNodeNum(node);
            f.setSubnetNum(subnetNum);
            f.setSubnetBit(power2);
        }
        if (((f.getSelectClass().equals("B")) && (f.getNetClass()=='B')) || ((f.getSelectClass().equals("B")) && (f.getNetClass()!=null))) {
            if (subnetNum > 32768) subnetNum = 32768;
            if (subnetNum >= 1) f.setNodeNum(65536 / subnetNum);
            int nwtemp;
            int node;
            if ((node=f.getNodeNum()) > 65536) {
                subnetNum = 1073741824 / node;
                nwtemp = subnetNum;
                f.setMask1 (~((64 / subnetNum) - 1) & 255);
                f.setMask2 (~((16384 / subnetNum) - 1) & 255);
                f.setMask3(0);
                f.setMask4(0);
                subnetNum = 1;
            } else {
                subnetNum = 65536 / node;
                nwtemp = subnetNum;
                f.setMask1(255);
                f.setMask2(255);
                f.setMask3(~((256 / subnetNum) - 1) & 255);
                f.setMask4(~((65536 / subnetNum) - 1) & 255);
                f.setHasSubnets(true);
                power2 = power2 + 14;
            }
            while (nwtemp > 1) {
                nwtemp = nwtemp / 2;
                power2 = power2 + 1;
            }
            f.setNodeNum(node);
            f.setSubnetNum(subnetNum);
            f.setSubnetBit(power2);
        }
        if (((f.getSelectClass().equals("C")) && (f.getNetClass()=='C')) || ((f.getSelectClass().equals("C")) && (f.getNetClass()!=null))) {
            if (subnetNum > 128) subnetNum = 128;
            if (subnetNum >= 1) f.setNodeNum(256 / subnetNum);
            int nwtemp;
            int node;
            if ((node=f.getNodeNum()) > 256) {

                subnetNum = 1073741824 / node;
                nwtemp = subnetNum;
                f.setMask1(~((64 / subnetNum) - 1) & 255);
                f.setMask2 (~((16384 / subnetNum) - 1) & 255);
                f.setMask3 (~((4194304 / subnetNum) - 1) & 255);
                f.setMask4(0);
                subnetNum = 1;
            } else {
                subnetNum = 256 / node;
                nwtemp = subnetNum;
                f.setMask1(255);
                f.setMask2(255);
                f.setMask3(255);
                f.setMask4(~((256 / subnetNum) - 1) & 255);
                f.setHasSubnets(true);
                power2 = power2 + 22;
            }

            while (nwtemp > 1) {
                nwtemp = nwtemp / 2;
                power2 = power2 + 1;
            }
            f.setNodeNum(node);
            f.setSubnetNum(subnetNum);
            f.setSubnetBit(power2);
        }
    }
    public static SubnetBo listsubnets(SubnetBo f) {
    	subnetting(f);
        //compute(f);
        int networks, nodes;
        if (f.getNetClass() != null) {
            networks = f.getSubnetNum();
            nodes = f.getNodeNum() + 2;
            if (f.getHasSubnets()) {
                int count = 0;
                List<Segment> list = new ArrayList<>();
                if (f.getSelectClass().equals("C")) {
                    nodes = ((256 / networks));
                    int j, topi, topj;
                    Segment bo = null;
                    for (int i = 0; i < 256; i = i + nodes) {
                        j = i + 1;
                        topi = (i + nodes - 1) & 255;
                        topj = topi - 1;
                        if (networks == 128) {
                            j = i;
                            topi = (i + nodes - 1) & 255;
                            topj = topi;
                        }
                        bo = new Segment();
                        bo.setWeb_address(f.getIp1() + "." + f.getIp2() + "." + f.getIp3() + "." + i);
                        bo.setStart_address(f.getIp1() + "." + f.getIp2() + "." + f.getIp3() + "." + j);
                        bo.setEnd_address(f.getIp1() + "." + f.getIp2() + "." + f.getIp3() + "." + topj);
                        bo.setBroadcast_address(f.getIp1() + "." + f.getIp2() + "." + f.getIp3() + "." + topi);
                        bo.setParent_node_id(f.getNode_id());
                        list.add(bo);
                    }
                }
                if (f.getSelectClass().equals("B")) {
                    nodes = ((65536 / networks));
                    Segment bo = null;
                    int j, i4, i3, topi4, topi3, topj;
                    for (int i = 0; i < 65536; i = i + nodes) {
                        count = count + 1;
                        i4 = i & 255;
                        i3 = (i / 256) & 255;
                        j = i4 + 1;
                        topi4 = ((i + nodes) - 1) & 255;
                        topi3 = (((i + nodes) - 1) / 256) & 255;
                        topj = topi4 - 1;
                        if (networks == 32768) {
                            j = i4;
                            topi4 = (i + nodes - 1) & 255;
                            topj = topi4;
                        }
                        bo = new Segment();
                        bo.setWeb_address(f.getIp1() + "." + f.getIp2() + "." + i3 + "." + i4);
                        bo.setStart_address(f.getIp1() + "." + f.getIp2() + "." + i3 + "." + j);
                        bo.setEnd_address(f.getIp1() + "." + f.getIp2() + "." + topi3 + "." + topj);
                        bo.setBroadcast_address(f.getIp1() + "." + f.getIp2() + "." + topi3 + "." + topi4);
                        bo.setParent_node_id(f.getNode_id());
                        list.add(bo);
                        //str += "<tr><td>" + f.oct1.value + "." + f.oct2.value + "." + i3 + "." + i4 + "</td><td>" + f.oct1.value + "." + f.oct2.value + "." + i3 + "." + j + "</td><td>" + f.oct1.value + "." + f.oct2.value + "." + topi3 + "." + topj + "</td><td>" + f.oct1.value + "." + f.oct2.value + "." + topi3 + "." + topi4 + "</td></tr>";
                        if ((count == 256) && (networks > 512)) {
                            //str += "<tr><td>..</td><td>..</td><td>..</td><td>..</td></tr>";
                            i = 65536 - (count * nodes);
                        }
                    }
                }
                if (f.getSelectClass().equals("A")) {
                    nodes = ((16777216 / networks));
                    int j, i2, i3, i4, topi4, topi3, topi2, topj;
                    Segment bo = null;
                    for (int i = 0; i < 16777216; i = i + nodes) {
                        count = count + 1;
                        i4 = i & 255;
                        i3 = (i / 256) & 255;
                        i2 = (i / 65536) & 255;
                        j = i4 + 1;
                        topi4 = ((i + nodes) - 1) & 255;
                        topi3 = (((i + nodes) - 1) / 256) & 255;
                        topi2 = (((i + nodes) - 1) / 65536) & 255;
                        topj = topi4 - 1;
                        if (networks == 8388608) {
                            j = i4;
                            topi4 = (i + nodes - 1) & 255;
                            topj = topi4;
                        }
                        bo = new Segment();
                        bo.setWeb_address(f.getIp1() + "." + i2 + "." + i3 + "." + i4);
                        bo.setStart_address(f.getIp1() + "." + i2 + "." + i3 + "." + j);
                        bo.setEnd_address(f.getIp1() + "." + topi2 + "." + topi3 + "." + topj);
                        bo.setBroadcast_address(f.getIp1() + "." + topi2 + "." + topi3 + "." + topi4);
                        bo.setParent_node_id(f.getNode_id());
                        list.add(bo);
                        //str += "<tr><td>" + f.oct1.value + "." + i2 + "." + i3 + "." + i4 + "</td><td>" + f.oct1.value + "." + i2 + "." + i3 + "." + j + "</td><td>" + f.oct1.value + "." + topi2 + "." + topi3 + "." + topj + "</td><td>" + f.oct1.value + "." + topi2 + "." + topi3 + "." + topi4 + "</td></tr>";
                        if ((count == 256) && (networks > 512)) {
                            i = 16777216 - (count * nodes);
                        }
                    }
                }
                f.setSegments(list);
            } else {
                f.setSegments(null);
            }

        }
        return f;
    }
    public static String getMaskMap(String maskBit) {
        if ("1".equals(maskBit)) return "128.0.0.0";
        if ("2".equals(maskBit)) return "192.0.0.0";
        if ("3".equals(maskBit)) return "224.0.0.0";
        if ("4".equals(maskBit)) return "240.0.0.0";
        if ("5".equals(maskBit)) return "248.0.0.0";
        if ("6".equals(maskBit)) return "252.0.0.0";
        if ("7".equals(maskBit)) return "254.0.0.0";
        if ("8".equals(maskBit)) return "255.0.0.0";
        if ("9".equals(maskBit)) return "255.128.0.0";
        if ("10".equals(maskBit)) return "255.192.0.0";
        if ("11".equals(maskBit)) return "255.224.0.0";
        if ("12".equals(maskBit)) return "255.240.0.0";
        if ("13".equals(maskBit)) return "255.248.0.0";
        if ("14".equals(maskBit)) return "255.252.0.0";
        if ("15".equals(maskBit)) return "255.254.0.0";
        if ("16".equals(maskBit)) return "255.255.0.0";
        if ("17".equals(maskBit)) return "255.255.128.0";
        if ("18".equals(maskBit)) return "255.255.192.0";
        if ("19".equals(maskBit)) return "255.255.224.0";
        if ("20".equals(maskBit)) return "255.255.240.0";
        if ("21".equals(maskBit)) return "255.255.248.0";
        if ("22".equals(maskBit)) return "255.255.252.0";
        if ("23".equals(maskBit)) return "255.255.254.0";
        if ("24".equals(maskBit)) return "255.255.255.0";
        if ("25".equals(maskBit)) return "255.255.255.128";
        if ("26".equals(maskBit)) return "255.255.255.192";
        if ("27".equals(maskBit)) return "255.255.255.224";
        if ("28".equals(maskBit)) return "255.255.255.240";
        if ("29".equals(maskBit)) return "255.255.255.248";
        if ("30".equals(maskBit)) return "255.255.255.252";
        if ("31".equals(maskBit)) return "255.255.255.254";
        if ("32".equals(maskBit)) return "255.255.255.255";
        return "-1";
    }
    public static String getMaskByMaskBit(String maskBit)
    {
        return "".equals(maskBit) ? "error, maskBit is null !" : getMaskMap(maskBit);
    }
    public static int getNetMask(String netmarks)
    {
        StringBuilder sbf;
        String str;
        int inetmask = 0;
        int count = 0;
        String[] ipList = netmarks.split("\\.");
        for (int n = 0; n < ipList.length; n++)
        {
            sbf = toBin(Integer.parseInt(ipList[n]));
            str = sbf.reverse().toString();
            count = 0;
            for (int i = 0; i < str.length(); i++)
            {
                i = str.indexOf('1', i);
                if (i == -1)
                {
                    break;
                }
                count++;
            }
            inetmask += count;
        }
        return inetmask;
    }
    private static StringBuilder toBin(int x)
    {
        StringBuilder result = new StringBuilder();
        result.append(x % 2);
        x /= 2;
        while (x > 0)
        {
            result.append(x % 2);
            x /= 2;
        }
        return result;
    }
    public static void main(String[] args) {
        SubnetBo bo=new SubnetBo();
        bo.setIp1(10);
        bo.setIp2(1);
        bo.setIp3(0);
        bo.setIp4(0);
        bo.setSelectClass("B");
        bo.setSubnetNum(1);
        subnetting(bo);
        SubnetBo subnetBo = listsubnets(bo);
        System.out.println(subnetBo);
//        String s = maskToSubMask(16);
//        System.out.println(s);
    }
}
