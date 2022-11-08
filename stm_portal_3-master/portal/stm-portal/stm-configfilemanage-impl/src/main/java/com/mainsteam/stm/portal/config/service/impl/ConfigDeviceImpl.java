package com.mainsteam.stm.portal.config.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.portal.config.api.IConfigDeviceApi;
import com.mainsteam.stm.portal.config.bo.ConfigBackupLogBo;
import com.mainsteam.stm.portal.config.bo.ConfigCustomGroupResourceBo;
import com.mainsteam.stm.portal.config.bo.ConfigDeviceBo;
import com.mainsteam.stm.portal.config.collector.TftpServer;
import com.mainsteam.stm.portal.config.collector.mbean.ConfigBackupMBean;
import com.mainsteam.stm.portal.config.collector.mbean.bean.ConfigReq;
import com.mainsteam.stm.portal.config.collector.mbean.bean.ConfigRsp;
import com.mainsteam.stm.portal.config.dao.IConfigBackupLogDao;
import com.mainsteam.stm.portal.config.dao.IConfigCustomGroupDao;
import com.mainsteam.stm.portal.config.dao.IConfigDeviceDao;
import com.mainsteam.stm.portal.config.dao.IConfigWarnDao;
import com.mainsteam.stm.portal.config.util.ScriptUtil;
import com.mainsteam.stm.portal.config.util.jaxb.Model;
import com.mainsteam.stm.portal.config.util.jaxb.Script;
import com.mainsteam.stm.portal.config.util.jaxb.Scripts;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.um.login.bo.LoginUser;
import com.mainsteam.stm.util.DateUtil;
import com.mainsteam.stm.util.FileUtil;
import com.mainsteam.stm.util.SecureUtil;
import com.mainsteam.stm.util.SortList;


/**
 * <li>文件名称: ConfigDeviceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   caoyong
 */
public class ConfigDeviceImpl implements IConfigDeviceApi,InstancelibListener{
	private static Logger logger = Logger.getLogger(ConfigDeviceImpl.class);
	private IConfigDeviceDao configDeviceDao;
	private IConfigBackupLogDao configBackupLogDao;
	private IConfigCustomGroupDao configCustomGroupDao;
	private IConfigWarnDao configWarnDao;
	@Resource
	private CapacityService capacityService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private OCRPCClient OCRPClient;
	@Resource
	private LocaleNodeService localNodeService;
	@Autowired
	private IFileClientApi fileClient;
	@Resource
	private AlarmService alarmService;
	@Autowired
	private ISearchApi searchApi;
	@Autowired
	private IResourceApi resourceApi;
	private final String VERSION = "version";
	@Override
	public ConfigDeviceBo get(Long id){
		ConfigDeviceBo bo = configDeviceDao.get(id);
		if(null==bo.getIsSave() || 0==bo.getIsSave()){
			try {
				ResourceInstance instance;
				instance = resourceInstanceService.getResourceInstance(bo.getId());
				if(instance!=null){
					String[] community = instance.getDiscoverPropBykey("community");
					if(null!=community && community.length>0){
						bo.setPublicName(SecureUtil.pwdDecrypt(community[0]));
					}
					String[] snmpPort = instance.getDiscoverPropBykey("port");
					if(null!=snmpPort && snmpPort.length>0){
						bo.setSnmpPort(snmpPort[0]);
					}
					//针对配置文件提取数据
					bo.setIntanceName(instance.getShowName());//主机名，设备名称
					bo.setIpAddress(instance.getShowIP());//IP地址
					bo.setResourceTypeId(capacityService.getResourceDefById(instance.getResourceId()).getName());//资源类型
					String[] sysObjectIds = instance.getModulePropBykey("sysObjectID");
					if(sysObjectIds!=null&&sysObjectIds.length>0){
						DeviceType deviceType = capacityService.getDeviceType(sysObjectIds[0]);//根据sysoid取deviceType
						if(deviceType!=null){
							bo.setOsType(deviceType.getOsType());//os类型
							bo.setVendorName(deviceType.getVendorName());//厂商
							bo.setDeviceType(deviceType.getType().name());//设备类型
						}
					}


				}
			} catch (InstancelibException e) {
				e.printStackTrace();
			}
		}
		return bo;
	}
	@Override
	public void selectByPage(Page<ConfigDeviceBo, ConfigDeviceBo> page,String groupId,String ipOrName,LoginUser user) throws Exception {
		Set<IDomain> domainSet = user.getDomainManageDomains();
		
		String sort="";
		Long id = null;
		if(groupId !=null && !"".equals(groupId)){
			id = Long.parseLong(groupId);
			ConfigDeviceBo condition = new ConfigDeviceBo();
			condition.setId(Long.parseLong(groupId));
			page.setCondition(condition);
		}
		if("ipAddress".equals(page.getSort())||"intanceName".equals(page.getSort())){
			sort = page.getSort();
			page.setSort(null);
		}
		ConfigDeviceBo cdBo = new ConfigDeviceBo();
		cdBo.setGroupId(id);
		List<ConfigDeviceBo> datas = configDeviceDao.selectByPage(cdBo);//查出所有加入备份的资源
		//		List<ConfigDeviceBo> datas = page.getRows();
		List<ConfigDeviceBo> result = new ArrayList<ConfigDeviceBo>();
		for(ConfigDeviceBo bo : datas){
			ResourceInstance instance = resourceInstanceService.getResourceInstance(bo.getId());
			if(instance==null) continue;
			//根据域判断是否操作连接信息
			boolean handle = false;
			if(user.isSystemUser()){
				handle = true;
			}else{
				if(domainSet!=null){
					for(IDomain ido:domainSet){
						if(instance.getDomainId()==ido.getId()){
							handle = true;
							break;
						}
					}
				}
			}
			bo.setHandle(handle);
			
			bo.setIntanceName(instance.getShowName());//主机名，设备名称
			bo.setIpAddress(instance.getShowIP());//IP地址
			bo.setResourceTypeId(capacityService.getResourceDefById(instance.getResourceId()).getName());//资源类型
			String[] sysObjectIds = instance.getModulePropBykey("sysObjectID");
			if(sysObjectIds!=null&&sysObjectIds.length>0){
				DeviceType deviceType = capacityService.getDeviceType(sysObjectIds[0]);//根据sysoid取deviceType
				if(deviceType!=null){
					bo.setOsType(deviceType.getOsType());//os类型
					bo.setVendorName(deviceType.getVendorName());//厂商
					bo.setDeviceType(deviceType.getType().name());//设备类型
				}
			}
			if(!StringUtils.isEmpty(ipOrName)){
				if(bo.getIntanceName().contains(ipOrName) || bo.getIntanceName().contains(ipOrName.toLowerCase()) 
						|| bo.getIntanceName().contains(ipOrName.toUpperCase()) || bo.getIpAddress().contains(ipOrName)){
					result.add(bo);
				}
			}else{
				result.add(bo);
			}
		}
		if(!StringUtils.isEmpty(sort)){
			SortList.sort(result, sort, page.getOrder().toLowerCase());
		}
		//做分页
		page.setTotalRecord(result.size());
		List<ConfigDeviceBo> cList = new ArrayList<ConfigDeviceBo>();
		//结果集是否超出当前页面显示条数
		for(int i = (int) page.getStartRow();i<(page.getStartRow()+page.getRowCount()<result.size()?page.getStartRow()+page.getRowCount():result.size());i++){
			if(result.get(i)!=null){
				cList.add((ConfigDeviceBo)result.get(i));
			}
		}
		page.setDatas(cList);
	}
	@Override
	public List<ResourceInstance> getAllExceptionResourceInstanceList(String searchKey) throws Exception{
		List<ResourceInstance> list = new ArrayList<ResourceInstance>();
		CategoryDef categoryDef = capacityService.getCategoryById("NetworkDevice");
		CategoryDef[] categoryDefList = categoryDef.getChildCategorys();
		if (null != categoryDefList && categoryDefList.length > 0) {
			for (int i = 0; i < categoryDefList.length; i++) {
				List<ResourceInstance> curr = resourceInstanceService.getParentInstanceByCategoryId(categoryDefList[i].getId());
				if(null!=curr && curr.size()>0){
					list.addAll(curr);
				}
			}
		}
		CategoryDef snmpCategoryDef = capacityService.getCategoryById("SnmpOthers");
		CategoryDef[] snmpCategoryDefList = snmpCategoryDef.getChildCategorys();
		if (null != snmpCategoryDefList && snmpCategoryDefList.length > 0) {
			for (int i = 0; i < snmpCategoryDefList.length; i++) {
				List<ResourceInstance> curr = resourceInstanceService.getParentInstanceByCategoryId(snmpCategoryDefList[i].getId());
				if(null!=curr && curr.size()>0){
					list.addAll(curr);
				}
			}
		}
		List<ResourceInstance> returnList = new ArrayList<ResourceInstance>();
		List<String> ips = new ArrayList<String>();
		//		List<ConfigDeviceBo> configDeviceBos = configDeviceDao.getAllResources();
		List<ResourceInstance> existInstances = resourceInstanceService.getResourceInstances(configDeviceDao.getAllResourceIds());
		if(null!=list && list.size()>0){
			for(ResourceInstance instance: list){
				if(instance.getDomainId()==0 || existInstances.contains(instance)) continue;
				boolean flag = true;
				for(ResourceInstance cdbo:existInstances){
					if(containsMAC(instance, cdbo)){
						flag = false;
						break;
					}
				}
				if(null!=searchKey && !"".equals(searchKey)){
					if(instance.getShowName() == null && !instance.getShowIP().contains(searchKey)){
						flag = false;
					}else if(instance.getShowName() != null && !instance.getShowName().toUpperCase().contains(searchKey.toUpperCase()) && !instance.getShowIP().contains(searchKey)){
						flag = false;
					}
				}
				if(ips.contains(instance.getShowIP())) flag = false;
				if(flag){
					ips.add(instance.getShowIP());
					returnList.add(instance);
				} 
			}
		}
		return returnList;
	}
	@Override
	public int insert(ConfigDeviceBo bo) {
		bo.setIsSave(0);
		return configDeviceDao.insert(bo);
	}

	@Override
	public int del(long id) {
		return configDeviceDao.del(id);
	}
	@Override
	public int update(ConfigDeviceBo bo) throws Exception{

		//获取snmp其他字段信息(软件版本，设备描述)
		ResourceInstance instance = resourceInstanceService.getResourceInstance(bo.getId());
		if(null!=instance && !StringUtils.isEmpty(instance.getDiscoverNode()) 
				&& !StringUtils.isEmpty(instance.getDiscoverNode())){

			Integer nodeId = Integer.parseInt(instance.getDiscoverNode());
			ConfigBackupMBean configBackupMBean = OCRPClient.getRemoteSerivce(nodeId, ConfigBackupMBean.class);
			//验证是否能登录
			if(!configBackupMBean.checkLoginStatus(bo.getIpAddress(), bo.getUserName(), bo.getPassword(), bo.getLoginType())){
				return -1;
			}

			//记录是否操作
			bo.setIsSave(1);

			if(configBackupMBean!=null){
				String result = configBackupMBean.getBySnmp(bo.getIpAddress(), 
						Integer.parseInt(bo.getSnmpPort()), bo.getPublicName());
				if(!StringUtils.isEmpty(result)){
					String[] arr = result.split("\r");
					for(int i=0;i<arr.length;i++){
						if(arr[i].toLowerCase().contains(VERSION.toLowerCase())){
							bo.setDeviceDesc(arr[i]);
						}
					}
					if(StringUtils.isEmpty(bo.getDeviceDesc())){
						bo.setDeviceDesc(result.split("\r")[0]);
					}
					if(!StringUtils.isEmpty(bo.getDeviceDesc()) 
							&& bo.getDeviceDesc().toLowerCase().contains(VERSION.toLowerCase())){
						String[] vsersions = bo.getDeviceDesc().split("(v|V)(e|E)(r|R)(s|S)(i|I)(o|O)(n|N)");
						if(vsersions == null || vsersions.length <= 1){
							bo.setSoftVersion("");
						}else{
							String version = vsersions[1].split(",")[0].trim();
							if(version.length() > 40){
								bo.setSoftVersion("");
							}else{
								bo.setSoftVersion(version);
							}
						}
					}
				}
			}
		}
		return configDeviceDao.update(bo);
	}

	@Override
	public int batchDel(Long[] ids) {

		int count = configDeviceDao.batchDelConfigDevice(ids);//删除配置文件主表信息
		configDeviceDao.deleteResourceByResourceIds(ids);//删除组资源信息
		List<ConfigDeviceBo> list = new ArrayList<ConfigDeviceBo>();
		if(null!=ids && ids.length>0){
			for(Long id:ids){
				ConfigDeviceBo bo = new ConfigDeviceBo();
				bo.setId(id);
				list.add(bo);
				List<ConfigBackupLogBo> bos=	configBackupLogDao.getConfigHistoryByResourceId(id);//根据fileID删除文件
				for (ConfigBackupLogBo configBackupLogBo :bos) {
					if(configBackupLogBo.getFileId()!=null){
						try {
							fileClient.deleteFile(configBackupLogBo.getFileId());
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
					}
					configBackupLogDao.batchDelCBLByResourceIds(ids);//删除备份历史记录
				}
			}
		}

		for(ConfigDeviceBo bo: list){
			searchApi.delSearchConfigFile(this.convert2ResourceBizRel(bo));
		}
		return count;
	}

	@Override
	public int batchInsert(Long[] ids,String groupId) {
		List<ConfigDeviceBo> list = new ArrayList<ConfigDeviceBo>();
		if(null!=ids && ids.length>0){
			List<ResourceInstance> instances = null;
			try {
				instances = resourceInstanceService.getResourceInstances(Arrays.asList(ids));
			} catch (InstancelibException e) {
				e.printStackTrace();
			}
			for(Long id:ids){
				ConfigDeviceBo bo = new ConfigDeviceBo();
				bo.setId(id);
				bo.setIsSave(0);
				if(null!=instances && instances.size()>0){
					for(ResourceInstance instance : instances){
						if(instance.getId() == id.longValue()){
							bo.setIpAddress(instance.getShowIP());
							break;
						}
					}
				}
				list.add(bo);
			}
		}
		if(null!=groupId && !"".equals(groupId)){
			List<ConfigCustomGroupResourceBo> poList=new ArrayList<ConfigCustomGroupResourceBo>();
			for(Long id:ids){
				ConfigCustomGroupResourceBo cgrPo=new ConfigCustomGroupResourceBo();
				cgrPo.setGroupId(Long.parseLong(groupId));
				cgrPo.setResourceID(String.valueOf(id));
				poList.add(cgrPo);
			}
			//存储Group 绑定的resource
			configCustomGroupDao.batchInsert(poList);
		}
		int count = configDeviceDao.batchInsert(list);
		for(ConfigDeviceBo bo: list){
			searchApi.saveSearchConfigFile(this.convert2ResourceBizRel(bo));
		}
		return count;
	}

	public IConfigDeviceDao getConfigDeviceDao() {
		return configDeviceDao;
	}

	public void setConfigDeviceDao(IConfigDeviceDao configDeviceDao) {
		this.configDeviceDao = configDeviceDao;
	}

	@Override
	public List<ConfigDeviceBo> getDeviceExcept(Long id) {
		ConfigDeviceBo bo = new ConfigDeviceBo();
		bo.setBackupId(id);
		return configDeviceDao.getExcept(bo);
	}

	@Override
	public List<ConfigDeviceBo> getDeviceByPlanId(Long id) {
		return configDeviceDao.getByPlanId(id);
	}
	public IConfigCustomGroupDao getConfigCustomGroupDao() {
		return configCustomGroupDao;
	}
	public void setConfigCustomGroupDao(IConfigCustomGroupDao configCustomGroupDao) {
		this.configCustomGroupDao = configCustomGroupDao;
	}
	@Override
	public List<String> readfilebylist(String filePath) {
		//    	File file=new File("D:/test_1.txt");
		//    	File file=new File(filePath);
		String content = "";
		List<String> contentList = new ArrayList<String>();
		FileReader cfgfile = null;
		BufferedReader inTwo = null;
		try {
			File file = fileClient.getFileByID(Long.parseLong(filePath));
			cfgfile=new FileReader(file.toString());
			inTwo=new BufferedReader(cfgfile);
			while((content = inTwo.readLine())!=null){
				contentList.add(content);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			try{
				if(inTwo!=null){
					inTwo.close();
					inTwo=null;
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			try{
				if(cfgfile!=null){
					cfgfile.close();
					cfgfile=null;
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return contentList;
	}
	@Override
	public List<List<String>> comparecfgfile(String filePath1, String filePath2) {
		//		File file1 = new File("D:/test_1.txt");
		//		File file2 = new File("D:/test_2.txt");
		String content1 = "";
		String content2 = "";
		List<String> contentList1 = new ArrayList<String>();
		List<String> contentList2 = new ArrayList<String>();
		FileReader cfgfile1 = null;
		FileReader cfgfile2 = null;
		BufferedReader inTwo1 = null;
		BufferedReader inTwo2 = null;
		try {
			File file1 = fileClient.getFileByID(Long.parseLong(filePath1));
			File file2 = fileClient.getFileByID(Long.parseLong(filePath2));
			cfgfile1 = new FileReader(file1.toString());
			cfgfile2 = new FileReader(file2.toString());
			inTwo1 = new BufferedReader(cfgfile1);
			inTwo2 = new BufferedReader(cfgfile2);
			while ((content1 = inTwo1.readLine()) != null) {
				contentList1.add(content1);
			}
			while ((content2 = inTwo2.readLine()) != null) {
				contentList2.add(content2);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cfgfile1 != null) {
				try {
					cfgfile1.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (cfgfile2 != null) {
				try {
					cfgfile2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (inTwo1 != null) {
				try {
					inTwo1.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (inTwo2 != null) {
				try {
					inTwo2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		int minlength = Math.min(contentList1.size(), contentList2.size());
		int[][] yper = new int[contentList1.size()][contentList2.size()];
		for (int i = 0; i < contentList1.size(); i++) {
			String str1 = contentList1.get(i);
			for (int j = 0; j < contentList2.size(); j++) {
				String str2 = contentList2.get(j);
				boolean flag = false;
				if (str2.equals(str1)) {
					flag = true;
				}
				if (flag) {
					yper[i][j] = 1;
				} else {
					yper[i][j] = 0;
				}
			}
		}

		int yNlength = contentList1.size() + 1;
		int xNlength = contentList2.size() + 1;
		int[][] yN = new int[yNlength][xNlength];
		for (int i = 0; i < yNlength; i++) {
			for (int j = 0; j < xNlength; j++) {
				yN[i][j] = 0;
			}
		}
		for (int n = 0; n < minlength; n++) {
			int y = (yNlength - 2) - n;
			int x = (xNlength - 2) - n;
			for (int i = x; i >= 0; i--) {
				int mid = Math.max(yN[y + 1][i + 1] + yper[y][i], yN[y][i + 1]);
				yN[y][i] = Math.max(mid, yN[y + 1][i]);
			}
			for (int j = y; j >= 0; j--) {
				int mid = Math.max(yN[j + 1][x + 1] + yper[j][x], yN[j][x + 1]);
				yN[j][x] = Math.max(mid, yN[j + 1][x]);
			}
		}

		int maxN = 0;
		for (int i = 0; i < yNlength; i++) {
			for (int j = 0; j < xNlength; j++) {
				if (yN[i][j] > maxN) {
					maxN = yN[i][j];
				}
			}
		}
		List<String> startx = new ArrayList<String>();
		List<String> starty = new ArrayList<String>();
		for (int i = 0; i < contentList1.size(); i++) {
			for (int j = 0; j < contentList2.size(); j++) {
				if (yper[i][j] == 1 && yN[i][j] == maxN) {
					starty.add(String.valueOf(i));
					startx.add(String.valueOf(j));
				}
			}
		}

		int yDlength = contentList1.size() + 1;
		int xDlength = contentList2.size() + 1;
		int[][] yD = new int[yDlength][xDlength];
		for (int i = 0; i < yDlength; i++) {
			for (int j = 0; j < xDlength; j++) {
				yD[i][j] = 0;
			}
		}
		for (int n = 0; n < minlength; n++) {
			int y = (yDlength - 2) - n;
			int x = (xDlength - 2) - n;
			for (int i = x; i >= 0; i--) {
				if (yper[y][i] == 1) {
					yD[y][i] = yD[y + 1][i + 1] + 1;
				} else {
					if (yN[y][i + 1] >= yN[y + 1][i]) {
						yD[y][i] = yD[y][i + 1];
					} else {
						yD[y][i] = yD[y + 1][i] + 1;
					}
				}
			}
			for (int j = y; j >= 0; j--) {
				if (yper[j][x] == 1) {
					yD[j][x] = yD[j + 1][x + 1] + 1;
				} else {
					if (yN[j][x + 1] >= yN[j + 1][x]) {
						yD[j][x] = yD[j][x + 1];
					} else {
						yD[j][x] = yD[j + 1][x] + 1;
					}
				}
			}
		}

		int minDx = 0;
		int minDy = 0;
		int minD = 0;
		for (int i = 0; i < startx.size(); i++) {
			int x = Integer.parseInt(startx.get(i));
			int y = Integer.parseInt(starty.get(i));
			if (i == 0) {
				minDx = x;
				minDy = y;
				minD = yD[y][x];
			} else {
				if (yD[y][x] < minD) {
					minDx = x;
					minDy = y;
					minD = yD[y][x];
				}
			}
		}

		// insert blank row
		List<String> insertList1 = new ArrayList<String>();
		List<String> insertList2 = new ArrayList<String>();
		List<String> colorList = new ArrayList<String>();
		int y = minDy;
		int x = minDx;
		int row = 0;
		int ylength = contentList1.size();
		int xlength = contentList2.size();
		if (y > x) {
			int a = y - x;
			for (int n = 0; n < a; n++) {
				insertList2.add(String.valueOf(n));
				row++;
			}
		} else if (x > y) {
			int a = x - y;
			for (int m = 0; m < a; m++) {
				insertList1.add(String.valueOf(m));
				row++;
			}
		} else {
			if (x != 0) {
				for (int m = 0; m < x; m++) {
					colorList.add(String.valueOf(m));
					row++;
				}
			}
		}
		while (y != ylength && x != xlength) {
			if (yper[y][x] == 1) {
				y++;
				x++;
				row++;
			} else {
				if (yN[y][x + 1] >= yN[y + 1][x]) {
					insertList1.add(String.valueOf(row));
					row++;
					x++;
				} else {
					insertList2.add(String.valueOf(row));
					y++;
					row++;
				}
			}
		}
		List<List<String>> result = new ArrayList<List<String>>();
		List<String> t1 = new ArrayList<String>();
		List<String> t2 = new ArrayList<String>();
		List<String> c = new ArrayList<String>();
		try {
			for (int i = 0; i < insertList1.size(); i++) {
				t1.add(insertList1.get(i));
			}
			for (int i = 0; i < insertList2.size(); i++) {
				t2.add(insertList2.get(i));
			}
			for (int i = 0; i < colorList.size(); i++) {
				c.add(colorList.get(i));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
		}
		result.add(t1);
		result.add(t2);
		result.add(c);
		return result;
	}
	public String backupResourcesByIds(Long[] ids,boolean backupType){
		StringBuffer msg=new StringBuffer("");
		try {
			logger.info("config job start...................");
			//备份文件时间戳
			Date currDate = new Date();
			String timestamp = DateUtil.format(currDate, "_yy.MM.dd_HH.mm.ss.txt");
			//得到脚本配置文件
			Scripts scripts = ScriptUtil.getScripts();
			//获取采集器mbean
			Map<Integer, ConfigBackupMBean> mbeanMap = new HashMap<Integer, ConfigBackupMBean>();
			ConfigBackupMBean mbean = null;
			for(Long id : ids){
				//得到配置设备
				ConfigDeviceBo bo = configDeviceDao.get(id);
				//通过id得到资源
				ResourceInstance instance = resourceInstanceService.getResourceInstance(id);
				if(instance != null && !StringUtils.isEmpty(instance.getDiscoverNode())){
					//设置名称,告警的时候使用
					bo.setIntanceName(instance.getShowName());
					//得到采集器id
					Integer discoverNode = Integer.parseInt(instance.getDiscoverNode());
					try{
						if((mbean=mbeanMap.get(discoverNode))==null){
							mbeanMap.put(discoverNode,mbean=OCRPClient.getRemoteSerivce(discoverNode, ConfigBackupMBean.class));
						}
					}catch(Exception e){
						logger.error(instance.getShowName()+"调用RPC出错", e);
						msg.append(bo.getIntanceName()+"调用RPC出错"+"</br>");
						continue;
					}
					if(mbean != null){
						String[] oids = instance.getModulePropBykey("sysObjectID");
						Model model = ScriptUtil.getModel(scripts,oids[0]);
						if(model != null){
							//保存一个状态改变了的配置文件，最后一个存库，保证查找该次备份时状态为改变
							ConfigBackupLogBo changeStatusBo = null;

							if(model.getScripts() == null || model.getScripts().size() <= 0){
								logger.error(instance.getShowName()+"未配置备份命令");
								msg.append(instance.getShowName()+"未配置备份命令"+"</br>");
								continue;
							}

							for(Script script : model.getScripts()){
								try{

									ConfigBackupLogBo returnBo = executeConfigBackup(script, bo, mbean, id, timestamp, backupType, changeStatusBo, msg, instance,0);
									if(returnBo != null){
										changeStatusBo = returnBo;
									}
								}catch(Exception e){
									logger.error(instance.getShowName()+"备份出错!原因："+e.getMessage(),e);
									msg.append(instance.getShowName()+"备份出错!原因："+e.getMessage()+"</br>");
									//添加备份失败历史记录
									configBackupLogDao.insert(convertConfigBackupLogBo(bo,null,backupType,script.getFileName(),e.getMessage()));
									continue;
								}
							}

							if(changeStatusBo != null){
								Date lastDate = new Date();
								Calendar calendar = new GregorianCalendar(); 
								calendar.setTime(lastDate); 
								calendar.add(calendar.SECOND,1);
								lastDate=calendar.getTime();  
								changeStatusBo.setBackupTime(lastDate);
								configBackupLogDao.insert(changeStatusBo);
							}

						}else{
							logger.error(instance.getShowName()+"未找到脚本");
							msg.append(instance.getShowName()+"未找到脚本"+"</br>");
							continue;
						}
					}else{
						logger.error(instance.getShowName()+"未取到对应的mbean");
						msg.append(instance.getShowName()+"未取到对应的mbean"+"</br>");
						continue;
					}
				}else{
					msg.append("对应node节点为空！"+"</br>");
					return msg.toString();	
				}
			}
		} catch (Exception e) {
			logger.error("执行备份异常",e);
			msg.append("执行备份异常"+e.getMessage()+"</br>");
			return msg.toString();
		}
		return msg.toString();
	}

	private ConfigBackupLogBo executeConfigBackup(Script script,ConfigDeviceBo bo,ConfigBackupMBean mbean,Long id,String timestamp
			,boolean backupType,ConfigBackupLogBo changeStatusBo,StringBuffer msg,ResourceInstance instance,Integer backupCount) throws Exception{

		if("tftp".equals(script.getType())){
			//使用tftp方式拿配置文件
			ConfigReq req = new ConfigReq(bo.getIpAddress(),bo.getUserName(),bo.getPassword(),script.getFileName(),script.getCmd(),bo.getEnableUserName(),bo.getEnablePassword());
			ConfigRsp rsp;
			if("1".equals(bo.getLoginType())){//ssh
				rsp = mbean.getBySsh(req);
			}else{//telnet(默认方式)
				rsp = mbean.getByTelnet(req);
			}
			if(!StringUtils.isEmpty(rsp.getFile())){
				ByteArrayInputStream stream = new ByteArrayInputStream(rsp.getFile().getBytes("UTF-8"));
				//上传到文件服务器
				Long fileId =  fileClient.upLoadFile("STM_CONFIG_DIR"+File.separator+id,stream, script.getFileName()+timestamp);
				//添加备份成功历史记录
				ConfigBackupLogBo logBo = convertConfigBackupLogBo(bo,fileId,backupType,script.getFileName(),rsp.getRemoteInfo());
				if(changeStatusBo == null && logBo.getChangeState() == 1){
					return logBo;
				}else{
					configBackupLogDao.insert(logBo);
				}
			}else{

				//添加失败判断是否网络原因
				if(isNetworkBusy(rsp.getRemoteInfo())){

					//判断备份类型
					if(backupType){

						//手动备份,提示即可
						configBackupLogDao.insert(convertConfigBackupLogBo(bo,null,backupType,script.getFileName(),rsp.getRemoteInfo()));
						msg.append(instance.getShowName()+"备份出错!原因：网络繁忙，请稍后再试</br>");

					}else{

						//系统备份,尝试三次
						if(backupCount < 3){
							backupCount++;
							return executeConfigBackup(script, bo, mbean, id, timestamp, backupType,changeStatusBo, msg, instance,backupCount);
						}else{
							configBackupLogDao.insert(convertConfigBackupLogBo(bo,null,backupType,script.getFileName(),rsp.getRemoteInfo()));
							msg.append(instance.getShowName()+"备份出错!原因：网络繁忙，请稍后再试</br>");
						}

					}

				}else{
					//添加备份失败历史记录
					configBackupLogDao.insert(convertConfigBackupLogBo(bo,null,backupType,script.getFileName(),rsp.getRemoteInfo()));
					msg.append(instance.getShowName()+"备份出错!原因：执行脚本错误</br>");
				}

			}
		}else if("show".equals(script.getType())){
			//使用show方式拿配置文件(预留)
			//getRemoteInfo然后截取
		}

		return null;
	}

	private boolean isNetworkBusy(String content){
		String[] remoteInfo = content.split("\\r?\\n");
		if(!remoteInfo[remoteInfo.length - 1].contains("#") &&
				!remoteInfo[remoteInfo.length - 1].contains(">") &&
				!remoteInfo[remoteInfo.length - 1].contains("/$")){
			logger.error("remoteInfo[remoteInfo.length - 1]:" + remoteInfo[remoteInfo.length - 1]);
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 新增至backup——history历史表bo 如果文件内容改变还要产生告警
	 * @param ConfigDeviceBo设备bo
	 * @param fileId		上传至文件服务器的文件id
	 * @param backupType	是否是手动备份
	 * @param fileName 文件名(未加时间戳)
	 * @param remoteInfo 远程信息
	 * @param date currDate 时间信息
	 * @return
	 * @throws Exception 
	 */
	private ConfigBackupLogBo convertConfigBackupLogBo(ConfigDeviceBo bo,Long fileId,boolean backupType,
			String fileName,String remoteInfo) throws Exception{
		ConfigBackupLogBo logBo = new ConfigBackupLogBo();
		logBo.setId(bo.getId());
		logBo.setBackupTime(new Date());
		logBo.setBackupType(backupType?1:0);//手动/自动备份
		logBo.setRemoteInfo(remoteInfo);
		logBo.setBackupState(fileId==null?2:1);
		if(fileId != null){//读取文件成功
			//产生告警(文件内容发生变化)并且当前次备份记录的状态变为红色(1);未变化时不产生告警状态为正常(0);备份失败时(2)
			Map<String, Object> startupMap = new HashMap<String, Object>();
			startupMap.put("resourceId",String.valueOf(bo.getId()));
			startupMap.put("fileName", fileName);
			Long lastFileId = configBackupLogDao.getNewlyFileIdById(startupMap);

			boolean changeState = false;//是否改变
			if(lastFileId!=null){//上一次备份文件成功，取得上一次备份成功的fileId,然后根据fileId取得文件流
				changeState = !FileUtil.compareFile(fileClient.getFileInputStreamByID(fileId),
						fileClient.getFileInputStreamByID(lastFileId));
				if(changeState){//文件变化产生告警
					AlarmSenderParamter alarmSenderParamter = new AlarmSenderParamter();
					alarmSenderParamter.setDefaultMsgTitle("("+bo.getIntanceName()+")"+bo.getIpAddress()+
							"配置文件/"+fileName+"发生变更");
					alarmSenderParamter.setDefaultMsg("("+bo.getIntanceName()+")"+bo.getIpAddress()+
							"配置文件/"+fileName+"发生变更");
					alarmSenderParamter.setGenerateTime(new Date());
					alarmSenderParamter.setLevel(InstanceStateEnum.WARN);//红色改变
					alarmSenderParamter.setProvider(AlarmProviderEnum.OC4);
					alarmSenderParamter.setSysID(SysModuleEnum.CONFIG_MANAGER);
					alarmSenderParamter.setSourceID(String.valueOf(bo.getId()));
					alarmSenderParamter.setSourceIP(bo.getIpAddress());
					alarmSenderParamter.setSourceName(bo.getIntanceName());
					alarmSenderParamter.setRuleType(AlarmRuleProfileEnum.configfile_manager);
					Long profileID = configWarnDao.getWarnIdByResourceId(bo.getId());
					if(null!=profileID) alarmSenderParamter.setProfileID(profileID);
					ResourceInstanceBo res =  resourceApi.getResource(bo.getId());
					if(null!=res){
						alarmSenderParamter.setExt0(SysModuleEnum.CONFIG_MANAGER.name());//(res.getResourceId());
						String[] parentResource = resourceApi.getCategoryParents(res.getCategoryId());
						if (parentResource.length > 2) {
							alarmSenderParamter.setExt1(SysModuleEnum.CONFIG_MANAGER.name());//(parentResource[0]);
							alarmSenderParamter.setExt2(SysModuleEnum.CONFIG_MANAGER.name());//(parentResource[1]);
						}
					}
					alarmSenderParamter.setExt3(String.valueOf(lastFileId));//比较文件时用上一个文件ID
					alarmSenderParamter.setExt4(String.valueOf(fileId));//比较文件时用当前文件ID
					alarmSenderParamter.setExt5(fileClient.getFileByID(lastFileId).getName());//比较文件时用上一个文件Name
					alarmSenderParamter.setExt6(fileClient.getFileByID(fileId).getName());//比较文件时用当前文件Name
					alarmService.notify(alarmSenderParamter);
				}
			}
			logBo.setChangeState(changeState?1:0);//0:正常 1:文件较上次产生变化
			logBo.setFileId(fileId);
		}else{
			logBo.setChangeState(2);//备份失败
		}
		return logBo;
	}
	@Override
	public List<Long> getAllResourceIds(){
		return configDeviceDao.getAllResourceIds();
	}
	public IConfigBackupLogDao getConfigBackupLogDao() {
		return configBackupLogDao;
	}
	public void setConfigBackupLogDao(IConfigBackupLogDao configBackupLogDao) {
		this.configBackupLogDao = configBackupLogDao;
	}
	/**
	 * 添加/删除网络设备资源时转ResourceBizRel
	 * @param bo
	 * @return
	 */
	private ResourceBizRel convert2ResourceBizRel(ConfigDeviceBo bo){
		ResourceBizRel model = new ResourceBizRel();
		model.setBizId(bo.getId());
		model.setNav("配置文件管理-"+"设备一览-"+bo.getId());
		model.setResourceId(bo.getId());
		return model;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void listen(InstancelibEvent instancelibEvent){
		try {
			if(instancelibEvent.getEventType()==EventEnum.INSTANCE_DELETE_EVENT){
				// deleteIds 需要删除的资源实例Id 集合
				List<Long> deleteIds = (List<Long>)instancelibEvent.getSource();
				//模块实现自己删除资源实例相关操作逻辑
				if(deleteIds!=null && deleteIds.size()>0){
					long[] ids = new long[deleteIds.size()];
					for(int i=0;i<deleteIds.size();i++){
						ids[i] = deleteIds.get(i);
						//删除关联的设备数据
						configDeviceDao.del(deleteIds.get(i));
					}
					//根据资源ids集合删除资源与配置组关系表中数据
					configCustomGroupDao.deleteGroupAndResourceRelationById(ids);
					//根据资源ids集合删除资源与告警设置关系表中数据
					configWarnDao.batchDelCWRByResourceIds(deleteIds.toArray(new Long[]{}));
					//根据资源ids集合删除资源相关的有历史备份数据
					configBackupLogDao.batchDelCBLByResourceIds(deleteIds.toArray(new Long[]{}));
					logger.info("del relations of configfiledevice resource successful");
				}
			}
		} catch (Exception e) {
			logger.error("del relations of configfiledevice resource failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}		
	}
	public IConfigWarnDao getConfigWarnDao() {
		return configWarnDao;
	}
	public void setConfigWarnDao(IConfigWarnDao configWarnDao) {
		this.configWarnDao = configWarnDao;
	}
	/**
	 * 通过mac地址比较两个资源是否同一台设备
	 * @param oldResourceInstance
	 * @param newResourceInstance
	 * @return
	 */
	private boolean containsMAC(ResourceInstance oldResourceInstance,
			ResourceInstance newResourceInstance) {
		if (logger.isDebugEnabled()) {
			logger.debug("containsMAC method start ");
		}
		boolean isSame = false;
		/*
		 * 只是判断设备是否相同，设备下面的子设备不做判断 只针对有CategoryId的资源实例
		 */
		if (StringUtils.isEmpty(oldResourceInstance.getCategoryId())
				|| StringUtils.isEmpty(newResourceInstance.getCategoryId())) {
			if(logger.isDebugEnabled()){
				logger.debug("oldCategoryId:"+ oldResourceInstance.getCategoryId());
				logger.debug("newCategoryId:"+ newResourceInstance.getCategoryId());
				logger.debug("isSame:" + isSame);
			}
			return isSame;
		}
		String[] oldMacList = oldResourceInstance
				.getModulePropBykey(MetricIdConsts.METRIC_MACADDRESS);
		String[] newMacList = newResourceInstance
				.getModulePropBykey(MetricIdConsts.METRIC_MACADDRESS);
		if (oldMacList == null || newMacList == null) {
			if (logger.isDebugEnabled()) {
				if (oldMacList == null) {
					logger.debug("oldMacList:null");
				}
				if (newMacList == null) {
					logger.debug("newMacList:null");
				}
				logger.debug("isSame:" + isSame);
				logger.debug("containsMAC method end ");
			}
			return isSame;
		}
		/*
		 * （两设备MAC的交集数量/设备最多数） > 规定的比例 表示一个设备
		 */
		int maxSize = 0;

		int oldSize = oldMacList.length;
		int newSize = newMacList.length;

		if (oldSize >= newSize) {
			maxSize = oldSize;
		} else {
			maxSize = newSize;
		}

		List<String> oldMac = Arrays.asList(oldMacList);
		List<String> newMac = Arrays.asList(newMacList);
		// 取两个集合的交集
		Collection<?> intersection = CollectionUtils.intersection(oldMac,
				newMac);
		float actualMacProportion = (float) intersection.size() / maxSize;
		/*
		 * 交集个数如果大于或等于定义的MAC 地址相同比例 表示同一设备，否则不是同一设备
		 */
		if (actualMacProportion >= 0.5f) {
			isSame = true;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("oldMac: " + oldMac);
			logger.debug("newMac: " + newMac);
			logger.debug("intersection: " + intersection.size());
			logger.debug("actualMacProportion: " + actualMacProportion);
			logger.debug("isSame:" + isSame);
			logger.debug("containsMAC method end ");
		}
		return isSame;
	}
	public String recoveryResources(String filePath,Long id){
		StringBuffer msg=new StringBuffer("");
		try {
			//获取配置文件
			File file = fileClient.getFileByID(Long.parseLong(filePath));
			String fileName = file.getName();
			//得到脚本配置文件
			Scripts scripts = ScriptUtil.getReScripts();
			//获取采集器mbean
			Map<Integer, ConfigBackupMBean> mbeanMap = new HashMap<Integer, ConfigBackupMBean>();
			ConfigBackupMBean mbean = null;
			//得到配置设备
			ConfigDeviceBo bo = configDeviceDao.get(id);
			//通过id得到资源
			ResourceInstance instance = resourceInstanceService.getResourceInstance(id);
			if(instance != null && !StringUtils.isEmpty(instance.getDiscoverNode())){
				//得到采集器id
				Integer discoverNode = Integer.parseInt(instance.getDiscoverNode());
				try{
					if((mbean=mbeanMap.get(discoverNode))==null){
						mbeanMap.put(discoverNode,mbean=OCRPClient.getRemoteSerivce(discoverNode, ConfigBackupMBean.class));
					}
				}catch(Exception e){
					logger.error(instance.getShowName()+"调用RPC出错", e);
					msg.append(bo.getIntanceName()+"调用RPC出错"+"</br>");
				}
				if(mbean != null){
					try{
						mbean.copyFile(file, fileName);
					}catch(Exception e){
						logger.error(instance.getShowName()+"写入文件出错", e);
						msg.append(bo.getIntanceName()+"写入文件出错"+"</br>");
					}
					String[] oids = instance.getModulePropBykey("sysObjectID");
					Model model = ScriptUtil.getModel(scripts,oids[0]);
					if(model == null || model.getScripts() == null || model.getScripts().size() <= 0){
						logger.error(instance.getShowName()+"未配置恢复脚本");
						msg.append(instance.getShowName()+"未配置恢复脚本"+"</br>");
						return msg.toString();
					}
					for(Script script : model.getScripts()){
						try{
							executeConfigRecovery(fileName,script, bo, mbean, id, msg, instance);
						}catch(Exception e){
							logger.error(instance.getShowName()+"恢复出错!原因："+e.getMessage(),e);
							msg.append(instance.getShowName()+"恢复出错!原因："+e.getMessage()+"</br>");
							continue;
						}
					}
				}else{
					logger.error(instance.getShowName()+"未取到对应的mbean");
					msg.append(instance.getShowName()+"未取到对应的mbean"+"</br>");
				}
			}else{
				msg.append("对应node节点为空！"+"</br>");
				return msg.toString();	
			}
		} catch (Exception e) {
			logger.error("执行恢复异常",e);
			msg.append("执行恢复异常"+e.getMessage()+"</br>");
			return msg.toString();
		}
		return msg.toString();
	}
	private ConfigBackupLogBo executeConfigRecovery(String fileName,Script script,ConfigDeviceBo bo,ConfigBackupMBean mbean,Long id,StringBuffer msg,ResourceInstance instance) throws Exception{

		if("tftp".equals(script.getType())){
			//上传至tftp
			ConfigReq req = new ConfigReq(bo.getIpAddress(),bo.getUserName(),bo.getPassword(),fileName,script.getCmd(),bo.getEnableUserName(),bo.getEnablePassword());
			ConfigRsp rsp;
			if("1".equals(bo.getLoginType())){//ssh
				rsp = mbean.getReBySsh(req);
			}else{//telnet(默认方式)
				rsp = mbean.getReByTelnet(req);
			}					
		}else{
			msg.append(instance.getShowName()+"恢复出错!原因：执行脚本错误</br>");
		}
		return null;
	}

}
