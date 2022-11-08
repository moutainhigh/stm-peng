package com.mainsteam.stm.topo.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.bean.FileModelQuery;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.topo.api.ITopoBackupRecoveryApi;
import com.mainsteam.stm.topo.bo.BackbordBo;
import com.mainsteam.stm.topo.bo.GroupBo;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.MacBaseBo;
import com.mainsteam.stm.topo.bo.MacHistoryBo;
import com.mainsteam.stm.topo.bo.MacLatestBo;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.mainsteam.stm.topo.bo.TopoIconBo;
import com.mainsteam.stm.topo.dao.IBackbordRealDao;
import com.mainsteam.stm.topo.dao.IGroupDao;
import com.mainsteam.stm.topo.dao.IIconDao;
import com.mainsteam.stm.topo.dao.ILinkDao;
import com.mainsteam.stm.topo.dao.IMacBaseDao;
import com.mainsteam.stm.topo.dao.IMacHistoryDao;
import com.mainsteam.stm.topo.dao.IMacLatestDao;
import com.mainsteam.stm.topo.dao.IMacRuntimeDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.IOthersNodeDao;
import com.mainsteam.stm.topo.dao.ISettingDao;
import com.mainsteam.stm.topo.dao.ISubTopoDao;
import com.mainsteam.stm.topo.dao.ITopoAuthSettingDao;
import com.mainsteam.stm.topo.dao.ITopoFindDao;
import com.mainsteam.stm.topo.enums.TopoBackupRecoveryTable;
import com.mainsteam.stm.util.DateUtil;

/**
 * 拓扑数据备/恢复业务实现
 * @author zwx
 */
@Service
public class TopoBackupRecoveryImpl implements ITopoBackupRecoveryApi{
	private Logger logger = Logger.getLogger(TopoBackupRecoveryImpl.class);
	
	private final String TOPO_FILE_GROUP = "STM_TOPO_BACKUP";	//拓扑文件上传组名称
	private final String BACKUP_ENCODING = "UTF-8";				//备份还原编码
	//文件客户端操作
	@Resource(name="fileClient")
	private IFileClientApi fileClient;
	//资源实例
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	//权限设置
	@Autowired
	ITopoAuthSettingDao topoAuthSettingDao;
	//背板实时表
	@Autowired
	IBackbordRealDao backbordRealDao;
	//组
	@Autowired
	IGroupDao groupDao;
	//拓扑图的图标
	@Autowired
	IIconDao iconDao;
	//新增MAC表
	@Autowired
	IMacLatestDao macLatestDao;
	//链路
	@Autowired
	ILinkDao linkDao;
	//mac基准表
	@Autowired
	IMacBaseDao macBaseDao;
	//mac历史表
	@Autowired
	IMacHistoryDao macHistoryDao;
	//mac实时表
	@Autowired
	IMacRuntimeDao macRuntimeDao;
	//备节点图元表
	@Autowired
	INodeDao nodeDao;
	//拓扑图上的其他节点
	@Autowired
	IOthersNodeDao othersNodeDao;
	//设置表
	@Autowired
	ISettingDao settingDao;
	//子拓扑
	@Autowired
	ISubTopoDao subTopoDao;
	//拓扑发现
	@Autowired
	ITopoFindDao topoFindDao;
	
	/**
	 * 恢复文件数据
	 * 1.读取文件内容
	 * 2.清空数据并恢复数据
	 */
	@Override
	public void deleteAndRecovery(Long fileId) throws Exception {
		//1.读取文件内容
		FileInputStream fis = fileClient.getFileInputStreamByID(fileId);
		InputStreamReader read = new InputStreamReader(fis,BACKUP_ENCODING);	//考虑到编码格式
		BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        StringBuffer content = new StringBuffer();
        while((lineTxt = bufferedReader.readLine()) != null){
        	content.append(lineTxt);
        }
        fis.close();

        //2.清空数据并恢复数据
        this.recovery(content.toString());
	}
	
	/**
	 * 删除恢复
	 * @param content
	 */
	private void recovery(String content){
		JSONArray recovery = JSONArray.parseArray(content.toString());
		if(null == recovery || recovery.size() < 0) return;
		
		for(Object table:recovery){
			JSONObject recoveryData = (JSONObject) JSONObject.toJSON(table);
			TopoBackupRecoveryTable recoveryTable = TopoBackupRecoveryTable.valueOf(recoveryData.getString("table"));
			JSONArray revoveryDatas = recoveryData.getJSONArray("datas");
			
			switch(recoveryTable){
				case STM_TOPO_AUTH_SETTING:
					if(null != revoveryDatas){
						topoAuthSettingDao.truncateAll();
						for(Object o:revoveryDatas){
							TopoAuthSettingBo bo = JSONObject.parseObject(o.toString(), TopoAuthSettingBo.class);
							topoAuthSettingDao.save(bo);
						}
					}
					break;
				case STM_TOPO_BACKBORD_REAL:
					backbordRealDao.truncateAll();
					for(Object o:revoveryDatas){
						BackbordBo bo = JSONObject.parseObject(o.toString(), BackbordBo.class);
						backbordRealDao.save(bo);
					}
					break;
				case STM_TOPO_GROUP://分组功能暂时去掉了
					groupDao.truncateAll();
					for(Object o:revoveryDatas){
						GroupBo bo = JSONObject.parseObject(o.toString(),GroupBo.class);
						groupDao.save(bo);
					}
					break;
				case STM_TOPO_ICON:
					iconDao.truncateAll();
					for(Object o:revoveryDatas){
						TopoIconBo bo = JSONObject.parseObject(o.toString(),TopoIconBo.class);
						iconDao.save(bo);
					}
					break;
				case STM_TOPO_LATEST_MAC:
					macLatestDao.deleteAll();
					for(Object o:revoveryDatas){
						MacLatestBo bo = JSONObject.parseObject(o.toString(),MacLatestBo.class);
						macLatestDao.insert(bo);
					}
					break;
				case STM_TOPO_LINK:
					topoFindDao.trunkLinkAll();
					for(Object o:revoveryDatas){
						LinkBo bo = JSONObject.parseObject(o.toString(),LinkBo.class);
						this.handleInstanceDeleted(bo);	//如果备份后，再删除链路（会根据instanceId删除资源库链路），然后再恢复就把拓扑的相应链路设置为连线
						linkDao.save(bo);
					}
					break;
				case STM_TOPO_MAC_BASE:
					macBaseDao.deleteAll();
					for(Object o:revoveryDatas){
						MacBaseBo bo = JSONObject.parseObject(o.toString(),MacBaseBo.class);
//						macBaseDao.insertOrUpdate(bo);
						macBaseDao.addOrUpdate(bo);
					}
					break;
				case STM_TOPO_MAC_HISTORY:
					macHistoryDao.truncateAll();
					for(Object o:revoveryDatas){
						MacHistoryBo bo = JSONObject.parseObject(o.toString(),MacHistoryBo.class);
						macHistoryDao.insert(bo);
					}
					break;
				case STM_TOPO_MAC_RUNTIME:
					macRuntimeDao.deleteAll();
					for(Object o:revoveryDatas){
						MacRuntimeBo bo = JSONObject.parseObject(o.toString(),MacRuntimeBo.class);
						macRuntimeDao.insert(bo);
					}
					break;
				case STM_TOPO_NODE:
					topoFindDao.trunkNodeAll();
					for(Object o:revoveryDatas){
						NodeBo bo = JSONObject.parseObject(o.toString(),NodeBo.class);
						nodeDao.insert(bo);
					}
					break;
				case STM_TOPO_OTHERS:
					topoFindDao.trunkOtherNodeAll();
					for(Object o:revoveryDatas){
						OtherNodeBo bo = JSONObject.parseObject(o.toString(),OtherNodeBo.class);
						othersNodeDao.save(bo);
					}
					break;
				case STM_TOPO_SETTING:
					settingDao.truncateAll();
					for(Object o:revoveryDatas){
						SettingBo bo = JSONObject.parseObject(o.toString(),SettingBo.class);
						settingDao.save(bo);
					}
					break;
				case STM_TOPO_SUBTOPO:
					topoFindDao.trunkSubTopoAll();
					for(Object o:revoveryDatas){
						SubTopoBo bo = JSONObject.parseObject(o.toString(),SubTopoBo.class);
						subTopoDao.save(bo);
					}
					break;
				default:break;
			}
		}
	}
	
	/**
	 * 如果备份后，再删除链路（会根据instanceId删除资源库链路），然后再恢复就把拓扑的相应链路设置为连线
	 * 设置链路的instanceId=null,type=line,fromIfIndex=toIfIndex=null
	 * @param linkBo
	 */
	private void handleInstanceDeleted(LinkBo linkBo){
		Long instanceId = linkBo.getInstanceId();
		if(instanceId != null){
			ResourceInstance linkInstance = null;
			try {
				 linkInstance = resourceInstanceService.getResourceInstance(instanceId);
				 if(null == linkInstance){	//拓扑有instanceId，获取资源链路实例【没发生异常】，则表示链路资源实例已经被删除了，则更新设置链路的instanceId=null,type=line
					 linkBo.setInstanceId(null);
					 linkBo.setType("line");
					 linkBo.setFromIfIndex(null);
					 linkBo.setToIfIndex(null);
				 }
			} catch (InstancelibException e) {	//拓扑有instanceId，获取资源链路实例【发生异常】，表示链路资源实例没有删除，则不做任何更新操作
				logger.error("TopoBackupRecoveryImpl.handleInstanceDeleted(),获取链路资源实例异常",e);
			}
		}
	}
	
	@Override
	public void deleteFiles(Long[] ids) throws Exception {
		for(Long id:ids){
			fileClient.deleteFile(id);
		}
	}

	@Override
	public List<FileModel> getBackupFiles(Page<FileModel, FileModelQuery> page){
		List<FileModel> fileList=new ArrayList<FileModel>();
		try {
			String sort = page.getSort();
			page.setSort(null);
			fileList = fileClient.getFileModels(page);
			//排序处理
			if(StringUtils.isNotBlank(sort) && null != fileList){
				String order = page.getOrder().toUpperCase();
				fileList = this.sortFiles(fileList, order);
			}
			page.setDatas(fileList);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Get files faild...",e);
		} 
		return fileList;
	}
	
	/**
	 * 按时间排序文件
	 * @param files
	 * @param order
	 * @return
	 */
	private List<FileModel> sortFiles(List<FileModel> files,String order){
		if ("ASC".equals(order.toUpperCase())) {
			Collections.sort(files, new Comparator<FileModel>() {
				@Override
				public int compare(FileModel bo1, FileModel bo2) {
                   //创建时间
					if(bo1.getFileName() == null && bo2.getFileName() == null){
						return 0;
					}else if(bo1.getFileName() == null && bo2.getFileName() != null){
						return -1;
					}else if(bo1.getFileName() != null && bo2.getFileName() == null){
						return 1;
					}else{
						if(bo1.getFileName().compareToIgnoreCase(bo2.getFileName()) == 0){
							return 0;
						}else if(bo1.getFileName().compareToIgnoreCase(bo2.getFileName()) > 0){
							return 1; 
						}else{
							return -1; 
						}
					}
				}
			});
		}else{
			Collections.sort(files, new Comparator<FileModel>()  {
				@Override
				public int compare(FileModel bo1, FileModel bo2) {
					if(bo1.getFileName() == null && bo2.getFileName() == null){
						return 0;
					}else if(bo1.getFileName() == null && bo2.getFileName() != null){
						return 1;
					}else if(bo1.getFileName() != null && bo2.getFileName() == null){
						return -1;
					}else{
						if(bo1.getFileName().compareToIgnoreCase(bo2.getFileName()) == 0){
							return 0;
						}else if(bo1.getFileName().compareToIgnoreCase(bo2.getFileName()) > 0){
							return -1; 
						}else{
							return 1; 
						}
					}
				}
			});
		}
		return files;
	}
	
	/**
	 * 备份当前拓扑模块所有数据
	 * 1. 查询数据,封装成json
	 * 2. 保存数据到服务器磁盘
	 * @return
	 */
	@Override
	public void backup() {
		//1. 查询数据,封装成json
		JSONArray dataArray = new JSONArray();
		TopoBackupRecoveryTable[] backupTables = TopoBackupRecoveryTable.values();
		for(TopoBackupRecoveryTable table:backupTables){
			this.getBackupData(table, dataArray);
		}
		String content = dataArray.toJSONString();

		//2. 保存数据到服务器磁盘
		this.backupToServer(content);
	}
	
	/**
	 * 获取备份数据
	 * @param table
	 */
	@SuppressWarnings("rawtypes")
	private void getBackupData(TopoBackupRecoveryTable table,JSONArray dataArray){
		List list = new ArrayList();
		long currentSeq = 0;
		JSONObject data = new JSONObject();
		
		switch(table){
			case STM_TOPO_AUTH_SETTING:
				currentSeq = topoAuthSettingDao.getSeq().current();
				list = topoAuthSettingDao.getAll();
				break;
			case STM_TOPO_BACKBORD_REAL:
				currentSeq = backbordRealDao.getSeq().current();
				list = backbordRealDao.getAll();
				break;
			case STM_TOPO_GROUP:
				currentSeq = groupDao.getSeq().current();
				list = groupDao.getAll();
				break;
			case STM_TOPO_ICON:
				currentSeq = iconDao.getSeq().current();
				list = iconDao.getAll();
				break;
			case STM_TOPO_LATEST_MAC:
				currentSeq = macLatestDao.getSeq().current();
				list = macLatestDao.getAll();
				break;
			case STM_TOPO_LINK:
				currentSeq = linkDao.getSeq().current();
				list = linkDao.getAll();
				break;
			case STM_TOPO_MAC_BASE:
				currentSeq = macBaseDao.getSeq().current();
				list = macBaseDao.getAll();
				break;
			case STM_TOPO_MAC_HISTORY:
				currentSeq = macHistoryDao.getSeq().current();
				list = macHistoryDao.getAll();
				break;
			case STM_TOPO_MAC_RUNTIME:
				currentSeq = macRuntimeDao.getSeq().current();
				list = macRuntimeDao.getAll();
				break;
			case STM_TOPO_NODE:
				currentSeq = nodeDao.getSeq().current();
				list = nodeDao.getAll();
				break;
			case STM_TOPO_OTHERS:
				currentSeq = othersNodeDao.getSeq().current();
				list = othersNodeDao.getAll();
				break;
			case STM_TOPO_SETTING:
				currentSeq = settingDao.getSeq().current();
				list = settingDao.getAll();
				break;
			case STM_TOPO_SUBTOPO:
				currentSeq = subTopoDao.getSeq().current();
				list = subTopoDao.getAll();
				break;
			default:break;
		}
		
		data.put("table", table);
		data.put("currentSeq",currentSeq);
		data.put("datas", list);
		dataArray.add(data);
	}
	
	/**
	 * 备份数据到服务器磁盘
	 * @param content
	 * @throws IOException 
	 */
	private void backupToServer(String content){
		byte[] contentTmp = null;
		try {
			contentTmp = content.getBytes(BACKUP_ENCODING);
		} catch (UnsupportedEncodingException e1) {
			logger.error("备份转换字符编码失败");
		}
		ByteArrayInputStream inputStream = new ByteArrayInputStream(contentTmp);
		try {
			String fileName = DateUtil.format(new Date(), DateUtil.DEFAULT_DATETIME_FORMAT);
			fileClient.upLoadFile(TOPO_FILE_GROUP, inputStream, fileName);
		} catch (IOException e) {
			logger.error("备份拓扑数据到服务器磁盘发生异常!",e);
		}
	}
}
