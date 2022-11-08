package com.mainsteam.stm.topo.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

/**
 * <li>背板管理业务接口定义</li>
 * <li>文件名称: IBackbordApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年9月23日
 * @author zwx
 */
public interface IBackbordApi {
	
	/**
	 * 获取一个设备的所有接口，并根据查询条件过滤
	 * @param instanceId
	 * @param searchVal
	 * @return
	 */
	public JSONArray getInterfaceBySearchVal(Long instanceId,String searchVal);
	
	/**
	 * 导入背板xml数据
	 * @param file
	 * @return int 影响行数
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	public int importBackbordXml(MultipartFile file) throws IOException, DocumentException;
	
	/**
	 * 导出背板信息为xml
	 * @param instanceId
	 * @param ip
	 * @return Object[]
	 */
	public Object[] createBackborcXmlDom(Long instanceId,String ip);
	
	/**
	 * 根据设备资源实例id获取背板信息
	 * @param instanceId
	 * @return
	 */
	public String getBackbordBo(Long instanceId);
	
	/**
	 * 保存背板信息
	 * @param instanceId	资源实例id
	 * @param info		背板信息（json）
	 * @param isBatch	是否批量保存
	 * @return
	 */
	public void saveBackbord(Long instanceId,String info,Boolean isBatch);
	/**
	 * 获取设备分类列表
	 * @return
	 */
	public String categoryDeviceInfo(Set<Long> domainSet);
	/**
	 * 获取一个设备的所有接口
	 * @param instanceId
	 * @return
	 */
	public JSONArray getAllIfs(Long instanceId);

    public JSONArray getAllIfs2(Long instanceId);

	public String getDetailInfo(Long instanceId);

	public JSONObject getPortTipInfo(Long instanceId);

	public JSONObject downDeviceInfo(Long mainInstanceId, Long subInstanceId);
}
