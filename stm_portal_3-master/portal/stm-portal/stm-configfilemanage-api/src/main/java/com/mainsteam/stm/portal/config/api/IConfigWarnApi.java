package com.mainsteam.stm.portal.config.api;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigWarnBo;
import com.mainsteam.stm.portal.config.bo.ConfigWarnRuleBo;
import com.mainsteam.stm.portal.config.bo.ConfigWarnViewBo;
import com.mainsteam.stm.portal.config.vo.ConditionVo;

/**
 * <li>文件名称: IConfigWarnApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月23日
 * @author   caoyong
 */
public interface IConfigWarnApi {
	/**
	 * 查询告警
	 * @param id
	 * @return
	 */
	ConfigWarnBo get(Long id) throws Exception;
	/**
	 * 告警设置分页数据
	 * @param page
	 */
	void selectByPage(Page<ConfigWarnBo, ConfigWarnBo> page) throws Exception;
	/**
	 * 查询告警一览分页数据
	 * @param page
	 * @param all 是否全部数据(true所有,false当前页数据)
	 * @param export(是否导出Excel操作：true/false)
	 * @throws Exception
	 */
	void selectWarnViewPage(Page<ConfigWarnViewBo, ConfigWarnViewBo> page,
			boolean all,boolean export,ConditionVo condition) throws Exception;
	/**
	 * 新增告警基本信息
	 * 
	 * @param configWarnBo
	 * @return
	 */
	
	int insert(ConfigWarnBo configWarnBo) throws Exception;
	
	/**
	 * 修改告警基本信息
	 * 
	 * @param configWarnBo
	 * @return
	 */
	int update(ConfigWarnBo configWarnBo) throws Exception;
	
	/**
	 * 批量删除告警
	 * 
	 * @param ids
	 * @return
	 */
	int batchDel(Long[] ids) throws Exception;
	/**
	 * 查询除去已经添加进配置文件管理告警资源关联表中资源的配置文件设备资源(左侧数据)
	 * @param id 告警id
	 * @param searchKey 查询关键字(设备名称或ip地址)
	 */
	List<ResourceInstance> getLeftResourceInstanceList(Long id,String searchKey) throws Exception;
	/**
	 * 查询除去已经添加进配置文件管理告警资源关联表中资源的配置文件设备资源(右侧数据)
	 * @param id 告警id
	 */
	List<ResourceInstance> getRightResourceInstanceList(Long id) throws Exception;
	/**
	 * 根据告警id查询所有的关联的接收规则（接收人员和接收方式）
	 * @param id 告警id
	 * @return
	 */
	List<ConfigWarnRuleBo> getWarnRulesById(Long id) throws Exception;
}
