package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.topo.bo.MacBaseBo;
import com.mainsteam.stm.topo.bo.MacHistoryBo;
import com.mainsteam.stm.topo.bo.MacLatestBo;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * <li>设备IP_MAC_PORT管理接口定义</li>
 * <li>文件名称: IMacApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年9月10日
 * @author zwx
 */
public interface IMacApi {
	
	/**
	 * 根据主机IP查询上联设备所有IP集合
	 * @param ipInterfaces
	 * @return List<JSONObject>
	 */
	public List<String> getUpdeviceIpsByHostIp(String HostIp);
	
	/**
	 * 根据主机mac查询上联设备所有IP集合
	 * @param ipInterfaces
	 * @return List<JSONObject>
	 */
	public List<String> getUpdeviceIpsByHostMac(String HostMac);
	
	/**
	 * 根据上联设备的ip和接口批量查询下联设备的ip-mac-port列表信息
	 * @param ipInterfaces
	 * @return List<JSONObject>
	 */
	public List<JSONObject> getBatchUpDeviceMacInfos(String[] ipInterfaces);
	
	/**
	 * 根据上联设备的实例id和接口查询下联设备的ip-mac-port列表信息
	 * @param instanceId	上联设备实例id
	 * @param upDeviceInterface	上联设备接口名称
	 * @return List<MacRuntimeBo>
	 */
	public List<MacRuntimeBo> getUpDeviceMacInfos(Long instanceId,String upDeviceInterface);
	
	/**
	 * 根据上联设备的ip和接口查询下联设备的ip-mac-port列表信息
	 * @param upDeviceIp	上联设备ip
	 * @param upDeviceInterface	上联设备接口名称
	 * @return List<MacRuntimeBo>
	 */
	public List<MacRuntimeBo> getUpDeviceMacInfos(String upDeviceIp,String upDeviceInterface);
	
	/**
	 * 获取ip-mac-port告警配置信息
	 * @param key
	 * @return
	 */
	public String getAlarmSetting(String key);
	
	/**
	 * 检查mac是基准表是否存在
	 * @param ids
	 * @param addType
	 * @return
	 */
	public boolean checkMacExist(Long[] ids,String addType);
	
	/**
	 * 导入mac基准表excel数据
	 * @param file
	 * @throws IOException 
	 */
	public int importMacExcel(MultipartFile file) throws IOException;
	
	/**
	 * 导出指定基准mac数据
	 * @param ids
	 * @param exportType (selected:导出选择数据，all：导出所有数据)
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public Workbook exportBaseMacs(Long[] ids,String exportType);
	
	/**
	 * 新增MAC数据刷新
	 */
	public void refreshLatestIpPortMac();
	
	/**
	 * 根据ids删除[历史变更]
	 * @param ids
	 * @return
	 */
	public int deleteMacHistoryByIds(Long[] ids);
	
	/**
	 * 删除[历史变更]mac
	 * @param macs
	 * @return
	 */
	public int deleteMacHistoryByMacs(String macs[]);
	
	/**
	 * 保存[基准表]mac
	 * @param macBaseBo
	 * @return int 影响的行数
	 */
	public int saveBaseMac(MacBaseBo macBaseBo);
	
	/**
	 * 删除[基准表]mac
	 * @param ids
	 * @return
	 */
	public void deleteBaseMac(Long[] ids);
	
	/**
	 * 全部加入[基准表]mac
	 */
	public int addAllBaseMac();
	
	/**
	 * 加入[基准表]mac
	 * @param ids
	 * @param addType(runtime:实时表,history:历史表,latest:新增mac表)
	 */
	public int addBaseMac(Long[] ids,String addType);
	
	/**
	 * 根据条件分页查询[历史变更]数据(mac重复,时间排除最近时间一条)
	 * @param page
	 * @return
	 */
	public void selectMacSubHistoryByPage(Page<MacHistoryBo, MacHistoryBo> page);
	
	/**
	 * 根据条件分页查询[历史变更]数据(mac不重复,时间为最大)
	 * @param page
	 * @return
	 */
	public void selectMacHistoryByPage(Page<MacHistoryBo, MacHistoryBo> page);
	
	/**
	 * 根据条件分页查询新增MAC数据
	 * @param page
	 * @return
	 */
	public void selectLatestMacByPage(Page<MacLatestBo, MacLatestBo> page);
	
	/**
	 * 根据条件分页查询基准表信息
	 * @param page
	 * @return
	 */
	public void selectMacBaseByPage(Page<MacBaseBo, MacBaseBo> page);
	
	/**
	 * 根据条件分页查询实时表信息
	 * @param page
	 * @return
	 */
	public void selectMacRuntimeByPage(Page<MacRuntimeBo, MacRuntimeBo> page);

    /**
     * 更新上联备注
     *
     * @param id
     * @param upRemarks
     * @return
     */
    public boolean updateUpRemarks(Long id, String upRemarks);
}
