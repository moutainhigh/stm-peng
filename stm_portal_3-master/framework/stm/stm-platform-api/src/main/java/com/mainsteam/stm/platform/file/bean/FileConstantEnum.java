package com.mainsteam.stm.platform.file.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
* <li>文件名称: FileConstantEnum.java</li>
* <li>公　　司: 武汉美新翔盛科技有限公司</li>
* <li>版权所有: 版权所有(C)2019-2020</li>
* <li>修改记录: ...</li>
* <li>内容摘要: ...</li>
* <li>其他说明: ...</li>
* @version  ms.stm
* @since    2019年10月27日
* @author   Wang
* @tags
 */
public enum FileConstantEnum {

	FILE_CONSTANT_README_TXT(1,"readme.txt","文件目录的表述文件","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG(1000,"yingyong.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_DEP_IMG(1001,"pe.png","业务单位默认图标","all",0),
	FILE_CONSTANT_BIZ_SER_IMG(1002,"service.png","业务服务默认图标","all",0),
	FILE_CONSTANT_BIZ_SNAPSHOOT_IMG(1003,"ywst.jpg","业务快照默认图标","all",0),
	
	FILE_CONSTANT_BIZ_MAIN_IMG_1(1011,"biz_1.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_2(1012,"biz_2.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_3(1013,"biz_3.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_4(1014,"biz_4.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_5(1015,"biz_5.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_6(1016,"biz_6.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_7(1017,"biz_7.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_8(1018,"biz_8.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_9(1019,"biz_9.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_10(1020,"biz_10.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_11(1021,"biz_11.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_12(1022,"biz_12.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_13(1023,"biz_13.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_14(1024,"biz_14.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_15(1025,"biz_15.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_16(1026,"biz_16.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_17(1027,"biz_17.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_18(1028,"biz_18.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_19(1029,"biz_19.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_20(1030,"biz_20.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_21(1031,"biz_21.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_22(1032,"biz_22.png","业务应用默认图标","all",0),
	FILE_CONSTANT_BIZ_MAIN_IMG_23(1033,"biz_23.png","业务应用默认图标","all",0),
	
	FILE_CONSTANT_BIZ_RES_IMG_1(1041,"biz_res_1.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_2(1042,"biz_res_2.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_3(1043,"biz_res_3.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_4(1044,"biz_res_4.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_5(1045,"biz_res_5.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_6(1046,"biz_res_6.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_7(1047,"biz_res_7.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_8(1048,"biz_res_8.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_9(1049,"biz_res_9.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_10(1050,"biz_res_10.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_11(1051,"biz_res_11.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_12(1052,"biz_res_12.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_13(1053,"biz_res_13.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_14(1054,"biz_res_14.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_15(1055,"biz_res_15.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_16(1116,"biz_res_16.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_17(1117,"biz_res_17.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_18(1118,"biz_res_18.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_19(1119,"biz_res_19.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_20(1120,"biz_res_20.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_21(1121,"biz_res_21.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_22(1122,"biz_res_22.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_23(1123,"biz_res_23.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_24(1124,"biz_res_24.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_25(1125,"biz_res_25.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_26(1126,"biz_res_26.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_27(1127,"biz_res_27.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_28(1128,"biz_res_28.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_29(1129,"biz_res_29.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_30(1130,"biz_res_30.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_31(1131,"biz_res_31.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_32(1132,"biz_res_32.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_33(1133,"biz_res_33.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_34(1134,"biz_res_34.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_35(1135,"biz_res_35.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_36(1136,"biz_res_36.png","业务资源默认图标","all",0),
	FILE_CONSTANT_BIZ_RES_IMG_37(1137,"biz_res_37.png","业务资源默认图标","all",0),
	
	FILE_CONSTANT_BIZ_BG_IMG_1(1061,"biz_bg_1.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_2(1062,"biz_bg_2.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_3(1063,"biz_bg_3.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_4(1064,"biz_bg_4.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_5(1065,"biz_bg_5.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_6(1066,"biz_bg_6.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_7(1067,"biz_bg_7.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_8(1068,"biz_bg_8.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_9(1069,"biz_bg_9.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_10(1070,"biz_bg_10.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_11(1071,"biz_bg_11.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_12(1072,"biz_bg_12.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_13(1073,"biz_bg_13.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_14(1074,"biz_bg_14.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_15(1075,"biz_bg_15.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_16(1076,"biz_bg_16.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_17(1077,"biz_bg_17.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_18(1078,"biz_bg_18.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_19(1079,"biz_bg_19.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_20(1080,"biz_bg_20.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_21(1081,"biz_bg_21.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_22(1082,"biz_bg_22.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_23(1083,"biz_bg_23.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_24(1084,"biz_bg_24.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_25(1085,"biz_bg_25.png","业务背景默认图片","all",0),
	FILE_CONSTANT_BIZ_BG_IMG_26(1086,"biz_bg_26.png","业务背景默认图片","all",0),
	
	// 墨绿色皮肤菜单图标
	FILE_CONSTANT_RIGHT_NAV_HOME(2001,"home.png","首页菜单图标","darkgreen",1),
	FILE_CONSTANT_RIGHT_NAV_SYSTEM(2002,"system_management.png","系统管理菜单图标","darkgreen",2),
	FILE_CONSTANT_RIGHT_NAV_RESOURCE(2003,"resource_management.png","资源管理菜单图标","darkgreen",3),
	FILE_CONSTANT_RIGHT_NAV_BIZ(2004,"business_management.png","业务管理菜单图标","darkgreen",4),
	FILE_CONSTANT_RIGHT_NAV_TOPO(2005,"topo_management.png","拓扑管理菜单图标","darkgreen",5),
	FILE_CONSTANT_RIGHT_NAV_ALARM(2006,"alarm_management.png","告警管理菜单图标","darkgreen",6),
	FILE_CONSTANT_RIGHT_NAV_NETFLOW(2007,"netflow.png","流量分析菜单图标","darkgreen",7),
	FILE_CONSTANT_RIGHT_NAV_CONFIG_FILE(2008,"config_management.png","配置文件管理菜单图标","darkgreen",8),
	FILE_CONSTANT_RIGHT_NAV_INSPECT(2010,"inspect_management.png","巡检管理菜单图标","darkgreen",10),
	FILE_CONSTANT_RIGHT_NAV_REPORT(2011,"report_management.png","报表管理菜单图标","darkgreen",11),
	FILE_CONSTANT_RIGHT_NAV_VM(2015,"vm.png","虚拟化菜单图标","darkgreen",15),
	FILE_CONSTANT_RIGHT_NAV_STATIST(2016,"statist_query.png","统计查询菜单图标","darkgreen",16),
	// 蓝色皮肤菜单图标
	FILE_CONSTANT_RIGHT_NAV_HOME_BLUE(3001, "home_blue.png", "首页菜单图标", "blue", 1),
	FILE_CONSTANT_RIGHT_NAV_SYSTEM_BLUE(3002, "system_management_blue.png", "系统管理菜单图标", "blue", 2),
	FILE_CONSTANT_RIGHT_NAV_RESOURCE_BLUE(3003, "resource_management_blue.png", "资源管理菜单图标", "blue", 3),
	FILE_CONSTANT_RIGHT_NAV_BIZ_BLUE(3004,"business_management_blue.png","业务管理菜单图标","blue",4),
	FILE_CONSTANT_RIGHT_NAV_TOPO_BLUE(3005,"topo_management_blue.png","拓扑管理菜单图标","blue",5),
	FILE_CONSTANT_RIGHT_NAV_ALARM_BLUE(3006,"alarm_management_blue.png","告警管理菜单图标","blue",6),
	FILE_CONSTANT_RIGHT_NAV_NETFLOW_BLUE(3007,"netflow_blue.png","流量分析菜单图标","blue",7),
	FILE_CONSTANT_RIGHT_NAV_CONFIG_FILE_BLUE(3008,"config_management_blue.png","配置文件管理菜单图标","blue",8),
	FILE_CONSTANT_RIGHT_NAV_INSPECT_BLUE(3010,"inspect_management_blue.png","巡检管理菜单图标","blue",10),
	FILE_CONSTANT_RIGHT_NAV_REPORT_BLUE(3011,"report_management_blue.png","报表管理菜单图标","blue",11),
	FILE_CONSTANT_RIGHT_NAV_VM_BLUE(3015,"vm_blue.png","虚拟化菜单图标","blue",15),
	FILE_CONSTANT_RIGHT_NAV_STATIST_BLUE(3016,"statist_query_blue.png","统计查询菜单图标","blue",16),
	// 红色皮肤菜单图标
	FILE_CONSTANT_RIGHT_NAV_HOME_RED(4001,"home.png","首页菜单图标","red",1),
	FILE_CONSTANT_RIGHT_NAV_SYSTEM_RED(4002,"system_management.png","系统管理菜单图标","red",2),
	FILE_CONSTANT_RIGHT_NAV_RESOURCE_RED(4003,"resource_management.png","资源管理菜单图标","red",3),
	FILE_CONSTANT_RIGHT_NAV_BIZ_RED(4004,"business_management.png","业务管理菜单图标","red",4),
	FILE_CONSTANT_RIGHT_NAV_TOPO_RED(4005,"topo_management.png","拓扑管理菜单图标","red",5),
	FILE_CONSTANT_RIGHT_NAV_ALARM_RED(4006,"alarm_management.png","告警管理菜单图标","red",6),
	FILE_CONSTANT_RIGHT_NAV_NETFLOW_RED(4007,"netflow.png","流量分析菜单图标","red",7),
	FILE_CONSTANT_RIGHT_NAV_CONFIG_FILE_RED(4008,"config_management.png","配置文件管理菜单图标","red",8),
	FILE_CONSTANT_RIGHT_NAV_INSPECT_RED(4010,"inspect_management.png","巡检管理菜单图标","red",10),
	FILE_CONSTANT_RIGHT_NAV_REPORT_RED(4011,"report_management.png","报表管理菜单图标","red",11),
	FILE_CONSTANT_RIGHT_NAV_VM_RED(4015,"vm.png","虚拟化菜单图标","red",15);
	
	//FileID in(1-100000)  
	private long fileId;
	private String fileName;
	private String fileDescription;
	private String skinType;
	private long menuId;
	
	private static Map<String, List<FileConstantEnum>> fileConstantEnumMap = new HashMap<String, List<FileConstantEnum>>();
	
	FileConstantEnum(long fileId,String fileName,String fileDescription, String skinType, long menuId){
		this.fileId=fileId;
		this.fileName=fileName;
		this.skinType=skinType;
		this.menuId=menuId;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}
	
	public String getSkinType() {
		return skinType;
	}

	public void setSkinType(String skinType) {
		this.skinType = skinType;
	}

	public long getMenuId() {
		return menuId;
	}

	public void setMenuId(long menuId) {
		this.menuId = menuId;
	}

	public static List<FileConstantEnum> getFileConstantEnum(String skinType){
		if(fileConstantEnumMap.isEmpty()){
			for(FileConstantEnum fileConstants : FileConstantEnum.values ()){
				if(fileConstantEnumMap.containsKey(fileConstants.getSkinType())){
					fileConstantEnumMap.get(fileConstants.getSkinType()).add(fileConstants);
				}else{
					List<FileConstantEnum> fileConstantsList = new ArrayList<FileConstantEnum>();
					fileConstantsList.add(fileConstants);
					fileConstantEnumMap.put(fileConstants.getSkinType(), fileConstantsList);
				}
			}
		}
		if(fileConstantEnumMap.containsKey(skinType)){
			return fileConstantEnumMap.get(skinType);
		}
		return null;
	}
}
