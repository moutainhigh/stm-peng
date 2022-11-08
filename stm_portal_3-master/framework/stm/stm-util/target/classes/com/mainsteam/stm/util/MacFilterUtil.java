package com.mainsteam.stm.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class MacFilterUtil {
	private static final String M_03_00_00_00_00_40 = "03:00:00:00:00:40";
	private static final String M_03_00_00_00_00_20 = "03:00:00:00:00:20";
	private static final String M_03_00_00_00_00_10 = "03:00:00:00:00:10";
	private static final String M_03_00_00_00_00_08 = "03:00:00:00:00:08";
	private static final String M_01_80_C2_00_00_10 = "01:80:c2:00:00:10";
	private static final String M_01_80_C2_00_00_11 = "01:80:c2:00:00:11";
	private static final String M_01_80_C2_00_00_12 = "01:80:c2:00:00:12";
	private static final String M_01_80_C2_00_00_14 = "01:80:c2:00:00:14";
	private static final String M_01_80_C2_00_00_15 = "01:80:c2:00:00:15";
	private static final String M_01_80_C2_00_01_00 = "01:80:c2:00:01:00";
	private static final String M_01_80_C2_00_01_10 = "01:80:c2:00:01:10";
	private static final String M_01_DD_00_FF_FF_FF = "01:dd:00:ff:ff:ff";
	private static final String M_03_00_00_00_00_04 = "03:00:00:00:00:04";
	private static final String M_03_00_00_00_00_02 = "03:00:00:00:00:02";
	private static final String M_03_00_00_00_00_01 = "03:00:00:00:00:01";
	private static final String M_01_DD_01_00_00_00 = "01:dd:01:00:00:00";
	private static final String M_01_80_C2_00_00_0F = "01:80:c2:00:00:0f";
	private static final String M_01_80_C2_00_00_01 = "01:80:c2:00:00:01";
	private static final String M_01_80_C2_00_00_00 = "01:80:c2:00:00:00";
	private static final String M_01_80_24_00_00_00 = "01:80:24:00:00:00";
	private static final String M_01_20_25_7F_FF_FF = "01:20:25:7f:ff:ff";
	private static final String M_01_20_25_00_00_00 = "01:20:25:00:00:00";
	private static final String M_01_00_81_00_01_01 = "01:00:81:00:01:01";
	private static final String M_01_00_81_00_01_00 = "01:00:81:00:01:00";
	private static final String M_01_00_81_00_00_02 = "01:00:81:00:00:02";
	private static final String M_01_00_81_00_00_00 = "01:00:81:00:00:00";
	private static final String M_01_00_5E_FF_FF_FF = "01:00:5e:ff:ff:ff";
	private static final String M_01_00_5E_80_00_00 = "01:00:5e:80:00:00";
	private static final String M_01_00_5E_7F_FF_FF = "01:00:5e:7f:ff:ff";
	private static final String M_09_00_09_00_00_04 = "09:00:09:00:00:04";
	private static final String M_09_00_09_00_00_01 = "09:00:09:00:00:01";
	private static final String M_09_00_07_FF_FF_FF = "09:00:07:ff:ff:ff";
	private static final String M_01_00_5E_00_00_00 = "01:00:5e:00:00:00";
	private static final String M_01_00_1D_52_00_00 = "01:00:1d:52:00:00";
	private static final String M_01_00_1D_42_00_00 = "01:00:1d:42:00:00";
	private static final String M_01_00_1D_00_00_00 = "01:00:1d:00:00:00";
	private static final String M_01_00_10_FF_FF_20 = "01:00:10:ff:ff:20";
	private static final String M_01_00_10_00_00_20 = "01:00:10:00:00:20";
	private static final String M_01_00_0C_DD_DD_DD = "01:00:0c:dd:dd:dd";
	private static final String M_01_00_0C_CC_CC_CC = "01:00:0c:cc:cc:cc";
	private static final String M_00_E0_FC_09_BC_F9 = "00:e0:fc:09:bc:f9";
	private static final String M_09_00_4C_00_00_0F = "09:00:4c:00:00:0f";
	private static final String M_09_00_4E_00_00_02 = "09:00:4e:00:00:02";
	private static final String M_09_00_56_00_00_00 = "09:00:56:00:00:00";
	private static final String M_09_00_56_FE_FF_FF = "09:00:56:fe:ff:ff";
	private static final String M_09_00_56_FF_00_00 = "09:00:56:ff:00:00";
	private static final String M_09_00_56_FF_FF_FF = "09:00:56:ff:ff:ff";
	private static final String M_09_00_6A_00_01_00 = "09:00:6a:00:01:00";
	private static final String M_09_00_77_00_00_00 = "09:00:77:00:00:00";
	private static final String M_09_00_77_00_00_01 = "09:00:77:00:00:01";
	private static final String M_09_00_77_00_00_02 = "09:00:77:00:00:02";
	private static final String M_09_00_7C_01_00_01 = "09:00:7c:01:00:01";
	private static final String M_09_00_7C_01_00_03 = "09:00:7c:01:00:03";
	private static final String M_09_00_7C_01_00_04 = "09:00:7c:01:00:04";
	private static final String M_09_00_7C_02_00_05 = "09:00:7c:02:00:05";
	private static final String M_09_00_7C_05_00_01 = "09:00:7c:05:00:01";
	private static final String M_09_00_7C_05_00_02 = "09:00:7c:05:00:02";
	private static final String M_09_00_87_80_FF_FF = "09:00:87:80:ff:ff";
	private static final String M_09_00_87_90_FF_FF = "09:00:87:90:ff:ff";
	private static final String M_0D_1E_15_BA_DD_06 = "0d:1e:15:ba:dd:06";
	private static final String M_33_33_00_00_00_00 = "33:33:00:00:00:00";
	private static final String M_33_33_FF_FF_FF_FF = "33:33:ff:ff:ff:ff";
	private static final String MAB_00_00_01_00_00 = "ab:00:00:01:00:00";
	private static final String MAB_00_00_02_00_00 = "ab:00:00:02:00:00";
	private static final String MAB_00_00_03_00_00 = "ab:00:00:03:00:00";
	private static final String MAB_00_00_04_00_00 = "ab:00:00:04:00:00";
	private static final String MAB_00_00_05_00_00 = "ab:00:00:05:00:00";
	private static final String MAB_00_03_00_00_00 = "ab:00:03:00:00:00";
	private static final String MAB_00_03_FF_FF_FF = "ab:00:03:ff:ff:ff";
	private static final String MCF_00_00_00_00_00 = "cf:00:00:00:00:00";
	private static final String MFF_FF_00_40_00_01 = "ff:ff:00:40:00:01";
	private static final String MFF_FF_00_60_00_04 = "ff:ff:00:60:00:04";
	private static final String MFF_FF_01_E0_00_04 = "ff:ff:01:e0:00:04";
	private static final String MFF_FF_FF_FF_FF_FF = "ff:ff:ff:ff:ff:ff";
	private static final String M00_00_00_00_00_00 = "00:00:00:00:00:00";
	private static final String M_00_0F_E2_07_F2_E0 = "00:0f:e2:07:f2:e0";
	private static final String M_09_00_2B_00_00_1 = "09:00:2b:00:00:1";
	private static final String _03_00_00_00_00_40 = "03 00 00 00 00 40";
	private static final String _03_00_00_00_00_20 = "03 00 00 00 00 20";
	private static final String _03_00_00_00_00_10 = "03 00 00 00 00 10";
	private static final String _03_00_00_00_00_08 = "03 00 00 00 00 08";
	private static final String _01_80_C2_00_00_10 = "01 80 c2 00 00 10";
	private static final String _01_80_C2_00_00_11 = "01 80 c2 00 00 11";
	private static final String _01_80_C2_00_00_12 = "01 80 c2 00 00 12";
	private static final String _01_80_C2_00_00_14 = "01 80 c2 00 00 14";
	private static final String _01_80_C2_00_00_15 = "01 80 c2 00 00 15";
	private static final String _01_80_C2_00_01_00 = "01 80 c2 00 01 00";
	private static final String _01_80_C2_00_01_10 = "01 80 c2 00 01 10";
	private static final String _01_DD_00_FF_FF_FF = "01 dd 00 ff ff ff";
	private static final String _03_00_00_00_00_04 = "03 00 00 00 00 04";
	private static final String _03_00_00_00_00_02 = "03 00 00 00 00 02";
	private static final String _03_00_00_00_00_01 = "03 00 00 00 00 01";
	private static final String _01_DD_01_00_00_00 = "01 dd 01 00 00 00";
	private static final String _01_80_C2_00_00_0F = "01 80 c2 00 00 0f";
	private static final String _01_80_C2_00_00_01 = "01 80 c2 00 00 01";
	private static final String _01_80_C2_00_00_00 = "01 80 c2 00 00 00";
	private static final String _01_80_24_00_00_00 = "01 80 24 00 00 00";
	private static final String _01_20_25_7F_FF_FF = "01 20 25 7f ff ff";
	private static final String _01_20_25_00_00_00 = "01 20 25 00 00 00";
	private static final String _01_00_81_00_01_01 = "01 00 81 00 01 01";
	private static final String _01_00_81_00_01_00 = "01 00 81 00 01 00";
	private static final String _01_00_81_00_00_02 = "01 00 81 00 00 02";
	private static final String _01_00_81_00_00_00 = "01 00 81 00 00 00";
	private static final String _01_00_5E_FF_FF_FF = "01 00 5e ff ff ff";
	private static final String _01_00_5E_80_00_00 = "01 00 5e 80 00 00";
	private static final String _01_00_5E_7F_FF_FF = "01 00 5e 7f ff ff";
	private static final String _09_00_09_00_00_04 = "09 00 09 00 00 04";
	private static final String _09_00_09_00_00_01 = "09 00 09 00 00 01";
	private static final String _09_00_07_FF_FF_FF = "09 00 07 ff ff ff";
	private static final String _01_00_5E_00_00_00 = "01 00 5e 00 00 00";
	private static final String _01_00_1D_52_00_00 = "01 00 1d 52 00 00";
	private static final String _01_00_1D_42_00_00 = "01 00 1d 42 00 00";
	private static final String _01_00_1D_00_00_00 = "01 00 1d 00 00 00";
	private static final String _01_00_10_FF_FF_20 = "01 00 10 ff ff 20";
	private static final String _01_00_10_00_00_20 = "01 00 10 00 00 20";
	private static final String _01_00_0C_DD_DD_DD = "01 00 0c dd dd dd";
	private static final String _01_00_0C_CC_CC_CC = "01 00 0c cc cc cc";
	private static final String _00_E0_FC_09_BC_F9 = "00 e0 fc 09 bc f9";
	private static final String _09_00_4C_00_00_0F = "09 00 4c 00 00 0f";
	private static final String _09_00_4E_00_00_02 = "09 00 4e 00 00 02";
	private static final String _09_00_56_00_00_00 = "09 00 56 00 00 00";
	private static final String _09_00_56_FE_FF_FF = "09 00 56 fe ff ff";
	private static final String _09_00_56_FF_00_00 = "09 00 56 ff 00 00";
	private static final String _09_00_56_FF_FF_FF = "09 00 56 ff ff ff";
	private static final String _09_00_6A_00_01_00 = "09 00 6a 00 01 00";
	private static final String _09_00_77_00_00_00 = "09 00 77 00 00 00";
	private static final String _09_00_77_00_00_01 = "09 00 77 00 00 01";
	private static final String _09_00_77_00_00_02 = "09 00 77 00 00 02";
	private static final String _09_00_7C_01_00_01 = "09 00 7c 01 00 01";
	private static final String _09_00_7C_01_00_03 = "09 00 7c 01 00 03";
	private static final String _09_00_7C_01_00_04 = "09 00 7c 01 00 04";
	private static final String _09_00_7C_02_00_05 = "09 00 7c 02 00 05";
	private static final String _09_00_7C_05_00_01 = "09 00 7c 05 00 01";
	private static final String _09_00_7C_05_00_02 = "09 00 7c 05 00 02";
	private static final String _09_00_87_80_FF_FF = "09 00 87 80 ff ff";
	private static final String _09_00_87_90_FF_FF = "09 00 87 90 ff ff";
	private static final String _0D_1E_15_BA_DD_06 = "0d 1e 15 ba dd 06";
	private static final String _33_33_00_00_00_00 = "33 33 00 00 00 00";
	private static final String _33_33_FF_FF_FF_FF = "33 33 ff ff ff ff";
	private static final String AB_00_00_01_00_00 = "ab 00 00 01 00 00";
	private static final String AB_00_00_02_00_00 = "ab 00 00 02 00 00";
	private static final String AB_00_00_03_00_00 = "ab 00 00 03 00 00";
	private static final String AB_00_00_04_00_00 = "ab 00 00 04 00 00";
	private static final String AB_00_00_05_00_00 = "ab 00 00 05 00 00";
	private static final String AB_00_03_00_00_00 = "ab 00 03 00 00 00";
	private static final String AB_00_03_FF_FF_FF = "ab 00 03 ff ff ff";
	private static final String CF_00_00_00_00_00 = "cf 00 00 00 00 00";
	private static final String FF_FF_00_40_00_01 = "ff ff 00 40 00 01";
	private static final String FF_FF_00_60_00_04 = "ff ff 00 60 00 04";
	private static final String FF_FF_01_E0_00_04 = "ff ff 01 e0 00 04";
	private static final String FF_FF_FF_FF_FF_FF = "ff ff ff ff ff ff";
	private static final String _00_00_00_00_00_00 = "00 00 00 00 00 00";
	private static final String _00_0F_E2_07_F2_E0 = "00 0f e2 07 f2 e0";
	private static final String _09_00_2B_00_00_1 = "09 00 2b 00 00 1";
	private static final String AB_00_04_01 = "ab 00 04 01";
	private static final String AB_00_04_00 = "ab 00 04 00";
	private static final String _09_00_2B_03 = "09 00 2b 03";
	private static final String _09_00_0D = "09 00 0d";
	private static final String _01_00_3C = "01 00 3c";
	private static final String _00_00_5E_L = "00 00 5e";
	private static final String _00_00_5E_U = "00 00 5E";
	private static final String _00_00_5E_SPACE_L = "00 00 5e";
	private static final String _00_00_5E_SPACE_U = "00 00 5E";

	private static final String MAB_00_04_01 = "ab:00:04:01";
	private static final String MAB_00_04_00 = "ab:00:04:00";
	private static final String M_09_00_2B_03 = "09:00:2b:03";
	private static final String M_09_00_0D = "09:00:0d";
	private static final String M_01_00_3C = "01:00:3c";
	private static final String M_00_00_5E_L = "00:00:5e";
	private static final String M_00_00_5E_U = "00:00:5E";
	private static final String M_00_00_5E_SPACE_L = "00:00:5e";
	private static final String M_00_00_5E_SPACE_U = "00:00:5E";
	
	private static Set<String> filterMacs;
	private static final Set<String> s_filerMacsPart = new HashSet<String>();
	
	static {
		s_filerMacsPart.add(_00_00_5E_L);
		s_filerMacsPart.add(_00_00_5E_U);
		s_filerMacsPart.add(_00_00_5E_SPACE_L);
		s_filerMacsPart.add(_00_00_5E_SPACE_U);
		s_filerMacsPart.add(_01_00_3C);
		s_filerMacsPart.add(_09_00_0D);
		s_filerMacsPart.add(_09_00_2B_03);
		s_filerMacsPart.add(AB_00_04_00);
		s_filerMacsPart.add(AB_00_04_01);

		s_filerMacsPart.add(M_00_00_5E_L);
		s_filerMacsPart.add(M_00_00_5E_U);
		s_filerMacsPart.add(M_00_00_5E_SPACE_L);
		s_filerMacsPart.add(M_00_00_5E_SPACE_U);
		s_filerMacsPart.add(M_01_00_3C);
		s_filerMacsPart.add(M_09_00_0D);
		s_filerMacsPart.add(M_09_00_2B_03);
		s_filerMacsPart.add(MAB_00_04_00);
		s_filerMacsPart.add(MAB_00_04_01);

		s_filerMacsPart.add(_09_00_2B_00_00_1);
		s_filerMacsPart.add(M_09_00_2B_00_00_1);
		
		filterMacs = new HashSet<String>();
		fiilFilterMacs();
	}
	
	private static void fiilFilterMacs() {
		filterMacs.add("01 80 c2 00 00 0e"); // , # LLDP
		filterMacs.add("01 80 c2 00 00 02");// # Something like LLDP
		filterMacs.add("00 e0 2b 00 00 02");// , # Something Extreme
		filterMacs.add("00 e0 2b 00 00 00");// , # Again, Extreme
		filterMacs.add("00 0b ca fe 00 00");
		filterMacs.add("03 00 00 00 00 80");
		filterMacs.add("03 00 00 00 01 00");
		filterMacs.add("03 00 00 00 02 00");
		filterMacs.add("03 00 00 00 04 00");
		filterMacs.add("03 00 00 00 08 00");
		filterMacs.add("03 00 00 00 10 00");
		filterMacs.add("03 00 00 00 20 00");
		filterMacs.add("03 00 00 00 40 00");
		filterMacs.add("03 00 00 00 80 00");
		filterMacs.add("03 00 00 01 00 00");
		filterMacs.add("03 00 00 20 00 00");
		filterMacs.add("03 00 00 80 00 00");
		filterMacs.add("03 00 40 00 00 00");
		filterMacs.add("03 00 ff ff ff ff");
		filterMacs.add("09 00 02 04 00 01");
		filterMacs.add("09 00 02 04 00 02");
		filterMacs.add("09 00 07 00 00 00");
		filterMacs.add("09 00 07 00 00 fc");
		filterMacs.add("09 00 0d 02 00 00");
		filterMacs.add("09 00 0d 02 0a 38");
		filterMacs.add("09 00 0d 02 0a 39");
		filterMacs.add("09 00 0d 02 0a 3c");
		filterMacs.add("09 00 0d 02 ff ff");
		filterMacs.add("09 00 0d 09 00 00");
		filterMacs.add("09 00 1e 00 00 00");
		filterMacs.add("09 00 26 01 00 01");
		filterMacs.add("09 00 2b 00 00 00");
		filterMacs.add("09 00 2b 00 00 01");
		filterMacs.add("09 00 2b 00 00 02");
		filterMacs.add("09 00 2b 00 00 03");
		filterMacs.add("09 00 2b 00 00 04");
		filterMacs.add("09 00 2b 00 00 05");
		filterMacs.add("09 00 2b 00 00 06");
		filterMacs.add("09 00 2b 00 00 07");
		filterMacs.add("09 00 2b 00 00 0f");
		filterMacs.add("09 00 2b 01 00 00");
		filterMacs.add("09 00 2b 01 00 01");
		filterMacs.add("09 00 2b 02 00 00");
		filterMacs.add("09 00 2b 02 01 00");
		filterMacs.add("09 00 2b 02 01 01");
		filterMacs.add("09 00 2b 02 01 02");
		filterMacs.add("09 00 2b 02 01 09");
		filterMacs.add("09 00 2b 04 00 00");
		filterMacs.add("09 00 2b 23 00 00");
		filterMacs.add("09 00 39 00 70 00");
		filterMacs.add("09 00 4c 00 00 00");
		filterMacs.add("09 00 4c 00 00 02");
		filterMacs.add("09 00 4c 00 00 06");
		filterMacs.add("09 00 4c 00 00 0c");

		filterMacs.add("01:80:c2:00:00:0e");// :,:#:LLDP
		filterMacs.add("01:80:c2:00:00:02");// :#:Something:like:LLDP
		filterMacs.add("00:e0:2b:00:00:02");// :,:#:Something:Extreme
		filterMacs.add("00:e0:2b:00:00:00");// :,:#:Again,:Extreme
		filterMacs.add("03:00:00:00:00:80");
		filterMacs.add("03:00:00:00:01:00");
		filterMacs.add("03:00:00:00:02:00");
		filterMacs.add("03:00:00:00:04:00");
		filterMacs.add("03:00:00:00:08:00");
		filterMacs.add("03:00:00:00:10:00");
		filterMacs.add("03:00:00:00:20:00");
		filterMacs.add("03:00:00:00:40:00");
		filterMacs.add("03:00:00:00:80:00");
		filterMacs.add("03:00:00:01:00:00");
		filterMacs.add("03:00:00:20:00:00");
		filterMacs.add("03:00:00:80:00:00");
		filterMacs.add("03:00:40:00:00:00");
		filterMacs.add("03:00:ff:ff:ff:ff");
		filterMacs.add("09:00:02:04:00:01");
		filterMacs.add("09:00:02:04:00:02");
		filterMacs.add("09:00:07:00:00:00");
		filterMacs.add("09:00:07:00:00:fc");
		filterMacs.add("09:00:0d:02:00:00");
		filterMacs.add("00:0b:ca:fe:00:00");
		filterMacs.add("09:00:0d:02:0a:38");
		filterMacs.add("09:00:0d:02:0a:39");
		filterMacs.add("09:00:0d:02:0a:3c");
		filterMacs.add("09:00:0d:02:ff:ff");
		filterMacs.add("09:00:0d:09:00:00");
		filterMacs.add("09:00:1e:00:00:00");
		filterMacs.add("09:00:26:01:00:01");
		filterMacs.add("09:00:2b:00:00:00");
		filterMacs.add("09:00:2b:00:00:01");
		filterMacs.add("09:00:2b:00:00:02");
		filterMacs.add("09:00:2b:00:00:03");
		filterMacs.add("09:00:2b:00:00:04");
		filterMacs.add("09:00:2b:00:00:05");
		filterMacs.add("09:00:2b:00:00:06");
		filterMacs.add("09:00:2b:00:00:07");
		filterMacs.add("09:00:2b:00:00:0f");
		filterMacs.add("09:00:2b:01:00:00");
		filterMacs.add("09:00:2b:01:00:01");
		filterMacs.add("09:00:2b:02:00:00");
		filterMacs.add("09:00:2b:02:01:00");
		filterMacs.add("09:00:2b:02:01:01");
		filterMacs.add("09:00:2b:02:01:02");
		filterMacs.add("09:00:2b:02:01:09");
		filterMacs.add("09:00:2b:04:00:00");
		filterMacs.add("09:00:2b:23:00:00");
		filterMacs.add("09:00:39:00:70:00");
		filterMacs.add("09:00:4c:00:00:00");
		filterMacs.add("09:00:4c:00:00:02");
		filterMacs.add("09:00:4c:00:00:06");
		filterMacs.add("09:00:4c:00:00:0c");
		filterMacs.add(_00_0F_E2_07_F2_E0);
		filterMacs.add(_00_E0_FC_09_BC_F9);
		filterMacs.add(_01_00_0C_CC_CC_CC);
		filterMacs.add(_01_00_0C_DD_DD_DD);
		filterMacs.add(_01_00_10_00_00_20);
		filterMacs.add(_01_00_10_FF_FF_20);
		filterMacs.add(_01_00_1D_00_00_00);
		filterMacs.add(_01_00_1D_42_00_00);
		filterMacs.add(_01_00_1D_52_00_00);
		filterMacs.add(_01_00_5E_00_00_00);
		filterMacs.add(_01_00_5E_7F_FF_FF);
		filterMacs.add(_01_00_5E_80_00_00);
		filterMacs.add(_01_00_5E_FF_FF_FF);
		filterMacs.add(_01_00_81_00_00_00);
		filterMacs.add(_01_00_81_00_00_02);
		filterMacs.add(_01_00_81_00_01_00);
		filterMacs.add(_01_00_81_00_01_01);
		filterMacs.add(_01_20_25_00_00_00);
		filterMacs.add(_01_20_25_7F_FF_FF);
		filterMacs.add(_01_80_24_00_00_00);
		filterMacs.add(_01_80_C2_00_00_00);
		filterMacs.add(_01_80_C2_00_00_01);
		filterMacs.add(_01_80_C2_00_00_0F);
		filterMacs.add(_01_80_C2_00_00_10);
		filterMacs.add(_01_80_C2_00_00_11);
		filterMacs.add(_01_80_C2_00_00_12);
		filterMacs.add(_01_80_C2_00_00_14);
		filterMacs.add(_01_80_C2_00_00_15);
		filterMacs.add(_01_80_C2_00_01_00);
		filterMacs.add(_01_80_C2_00_01_10);
		filterMacs.add(_01_DD_00_FF_FF_FF);
		filterMacs.add(_01_DD_01_00_00_00);
		filterMacs.add(_03_00_00_00_00_01);
		filterMacs.add(_03_00_00_00_00_02);
		filterMacs.add(_03_00_00_00_00_04);
		filterMacs.add(_03_00_00_00_00_08);
		filterMacs.add(_03_00_00_00_00_10);
		filterMacs.add(_03_00_00_00_00_20);
		filterMacs.add(_03_00_00_00_00_40);
		filterMacs.add(_09_00_07_FF_FF_FF);
		filterMacs.add(_09_00_09_00_00_01);
		filterMacs.add(_09_00_09_00_00_04);
		filterMacs.add(_09_00_4C_00_00_0F);
		filterMacs.add(_09_00_4E_00_00_02);
		filterMacs.add(_09_00_56_00_00_00);
		filterMacs.add(_09_00_56_FE_FF_FF);
		filterMacs.add(_09_00_56_FF_00_00);
		filterMacs.add(_09_00_56_FF_FF_FF);
		filterMacs.add(_09_00_6A_00_01_00);
		filterMacs.add(_09_00_77_00_00_00);
		filterMacs.add(_09_00_77_00_00_01);
		filterMacs.add(_09_00_77_00_00_02);
		filterMacs.add(_09_00_7C_01_00_01);
		filterMacs.add(_09_00_7C_01_00_03);
		filterMacs.add(_09_00_7C_01_00_04);
		filterMacs.add(_09_00_7C_02_00_05);
		filterMacs.add(_09_00_7C_05_00_01);
		filterMacs.add(_09_00_7C_05_00_02);
		filterMacs.add(_09_00_87_80_FF_FF);
		filterMacs.add(_09_00_87_90_FF_FF);
		filterMacs.add(_0D_1E_15_BA_DD_06);
		filterMacs.add(_33_33_00_00_00_00);
		filterMacs.add(_33_33_FF_FF_FF_FF);
		filterMacs.add(AB_00_00_01_00_00);
		filterMacs.add(AB_00_00_02_00_00);
		filterMacs.add(AB_00_00_03_00_00);
		filterMacs.add(AB_00_00_04_00_00);
		filterMacs.add(AB_00_00_05_00_00);
		filterMacs.add(AB_00_03_00_00_00);
		filterMacs.add(AB_00_03_FF_FF_FF);
		filterMacs.add(CF_00_00_00_00_00);
		filterMacs.add(FF_FF_00_40_00_01);
		filterMacs.add(FF_FF_00_60_00_04);
		filterMacs.add(FF_FF_01_E0_00_04);
		filterMacs.add(FF_FF_FF_FF_FF_FF);
		filterMacs.add(_00_00_00_00_00_00);
		filterMacs.add(M_00_0F_E2_07_F2_E0);
		filterMacs.add(M_00_E0_FC_09_BC_F9);
		filterMacs.add(M_01_00_0C_CC_CC_CC);
		filterMacs.add(M_01_00_0C_DD_DD_DD);
		filterMacs.add(M_01_00_10_00_00_20);
		filterMacs.add(M_01_00_10_FF_FF_20);
		filterMacs.add(M_01_00_1D_00_00_00);
		filterMacs.add(M_01_00_1D_42_00_00);
		filterMacs.add(M_01_00_1D_52_00_00);
		filterMacs.add(M_01_00_5E_00_00_00);
		filterMacs.add(M_01_00_5E_7F_FF_FF);
		filterMacs.add(M_01_00_5E_80_00_00);
		filterMacs.add(M_01_00_5E_FF_FF_FF);
		filterMacs.add(M_01_00_81_00_00_00);
		filterMacs.add(M_01_00_81_00_00_02);
		filterMacs.add(M_01_00_81_00_01_00);
		filterMacs.add(M_01_00_81_00_01_01);
		filterMacs.add(M_01_20_25_00_00_00);
		filterMacs.add(M_01_20_25_7F_FF_FF);
		filterMacs.add(M_01_80_24_00_00_00);
		filterMacs.add(M_01_80_C2_00_00_00);
		filterMacs.add(M_01_80_C2_00_00_01);
		filterMacs.add(M_01_80_C2_00_00_0F);
		filterMacs.add(M_01_80_C2_00_00_10);
		filterMacs.add(M_01_80_C2_00_00_11);
		filterMacs.add(M_01_80_C2_00_00_12);
		filterMacs.add(M_01_80_C2_00_00_14);
		filterMacs.add(M_01_80_C2_00_00_15);
		filterMacs.add(M_01_80_C2_00_01_00);
		filterMacs.add(M_01_80_C2_00_01_10);
		filterMacs.add(M_01_DD_00_FF_FF_FF);
		filterMacs.add(M_01_DD_01_00_00_00);
		filterMacs.add(M_03_00_00_00_00_01);
		filterMacs.add(M_03_00_00_00_00_02);
		filterMacs.add(M_03_00_00_00_00_04);
		filterMacs.add(M_03_00_00_00_00_08);
		filterMacs.add(M_03_00_00_00_00_10);
		filterMacs.add(M_03_00_00_00_00_20);
		filterMacs.add(M_03_00_00_00_00_40);
		filterMacs.add(M_09_00_07_FF_FF_FF);
		filterMacs.add(M_09_00_09_00_00_01);
		filterMacs.add(M_09_00_09_00_00_04);
		filterMacs.add(M_09_00_4C_00_00_0F);
		filterMacs.add(M_09_00_4E_00_00_02);
		filterMacs.add(M_09_00_56_00_00_00);
		filterMacs.add(M_09_00_56_FE_FF_FF);
		filterMacs.add(M_09_00_56_FF_00_00);
		filterMacs.add(M_09_00_56_FF_FF_FF);
		filterMacs.add(M_09_00_6A_00_01_00);
		filterMacs.add(M_09_00_77_00_00_00);
		filterMacs.add(M_09_00_77_00_00_01);
		filterMacs.add(M_09_00_77_00_00_02);
		filterMacs.add(M_09_00_7C_01_00_01);
		filterMacs.add(M_09_00_7C_01_00_03);
		filterMacs.add(M_09_00_7C_01_00_04);
		filterMacs.add(M_09_00_7C_02_00_05);
		filterMacs.add(M_09_00_7C_05_00_01);
		filterMacs.add(M_09_00_7C_05_00_02);
		filterMacs.add(M_09_00_87_80_FF_FF);
		filterMacs.add(M_09_00_87_90_FF_FF);
		filterMacs.add(M_0D_1E_15_BA_DD_06);
		filterMacs.add(M_33_33_00_00_00_00);
		filterMacs.add(M_33_33_FF_FF_FF_FF);
		filterMacs.add(MAB_00_00_01_00_00);
		filterMacs.add(MAB_00_00_02_00_00);
		filterMacs.add(MAB_00_00_03_00_00);
		filterMacs.add(MAB_00_00_04_00_00);
		filterMacs.add(MAB_00_00_05_00_00);
		filterMacs.add(MAB_00_03_00_00_00);
		filterMacs.add(MAB_00_03_FF_FF_FF);
		filterMacs.add(MCF_00_00_00_00_00);
		filterMacs.add(MFF_FF_00_40_00_01);
		filterMacs.add(MFF_FF_00_60_00_04);
		filterMacs.add(MFF_FF_01_E0_00_04);
		filterMacs.add(MFF_FF_FF_FF_FF_FF);
		filterMacs.add(M00_00_00_00_00_00);
	}
	
	public static Set<String> getFilterMacs() {
		Set<String> t_set = new HashSet<String>();
		for (String mac : filterMacs) {
			t_set.add(mac.toLowerCase());
		}
		return t_set;
	}

	public static void addFilterMac(String mac) {
		filterMacs.add(mac.toLowerCase());
		filterMacs.add(mac.toUpperCase());
	}

	/**
	 * 判断mac是否为需要过滤的mac
	 * @param mac
	 * @return
	 */
	public static boolean isFilterMac(String mac) {
		if (StringUtils.isNotEmpty(mac)) {
			if (!isValidMac(mac)) {
				return true;
			}
			if (filterMacs.contains(mac.toLowerCase())) {
//				LOGGER.info("1 过滤" + mac);
				return true;
			}
		}
		for (String part : s_filerMacsPart) {
			if (mac.toLowerCase().startsWith(part)) {
//				LOGGER.info("2 过滤 " + mac);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 过滤输入的mac集合，返回过滤后的mac集合
	 * @param macs
	 * @return
	 */
	public static Set<String> filterMac(Set<String> macs) {
		Set<String> macResult = new HashSet<String>();
		for (String mac:macs) {
			if (!isFilterMac(mac)) {
				macResult.add(mac);
			}
		}
		
		return macResult;
	}
	
	public static boolean isValidMac(String mac) {

		if (mac == null || StringUtils.isEmpty(mac)) {
			return false;
		}
		return mac
				.matches("[0-9A-Fa-f]{1,2}[:|\\s][0-9A-Fa-f][0-9A-Fa-f][:|\\s][0-9A-Fa-f][0-9A-Fa-f][:|\\s][0-9A-Fa-f][0-9A-Fa-f][:|\\s][0-9A-Fa-f][0-9A-Fa-f][:|\\s][0-9A-Fa-f][0-9A-Fa-f]");
	}
	
	public static void main(String[] agrs) {
		Set<String> macs = new HashSet<String>();
		macs.add("00 00 00 00 00 00");
		macs.add("00:00:00:00:00:00");
		macs.add("36:36:33:30:34");
		macs.add("36:36:33:30:34:11");
		
		Set<String> filterMacs = filterMac(macs);
		
		
		for (String mac:filterMacs) {
			System.out.println(mac);
		}
	}
}
