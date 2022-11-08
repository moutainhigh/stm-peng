package com.mainsteam.stm.portal.business.api;


import java.util.List;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.bo.BizWarnViewBo;
import com.mainsteam.stm.system.um.user.bo.User;


/**
 * <li>文件名称: IBizServiceApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月7日
 * @author   caoyong
 */
public interface IBizServiceApi {
	/**
	 * 新增业务服务
	 * 
	 * @param bizServiceBo
	 * @return
	 */
	Long insert(BizServiceBo bizServiceBo) throws Exception;

	/**
	 * 删除业务服务
	 * 
	 * @param id
	 * @return
	 */
	int del(long id) throws Exception;
	
	/**
	 * 按名字模糊查询
	 * 
	 * @param List<BizServiceBo>
	 * @return
	 */
	public List<BizServiceBo> getByName(String name);

	/**
	 * 修改业务服务
	 * 
	 * @param bizServiceBo
	 * @return
	 */
	int update(BizServiceBo bizServiceBo) throws Exception;

	/**
	 * 获取单个业务服务
	 * 
	 * @param id
	 * @return
	 */
	BizServiceBo get(long id);

	/**
	 * 获取所有业务服务
	 * 
	 * @return
	 */
	List<BizServiceBo> getList() throws Exception;
	
	/**
	 * 通过当前业务应用对象查询告警分页信息数据
	 * @param page
	 * @param bizServiceBo
	 * @param status
	 * @throws Exception
	 */
	public void selectWarnViewPage(Page<BizWarnViewBo, BizWarnViewBo> page,
			BizServiceBo bizServiceBo,String status) throws Exception;
	/**
	 * 查询所有的系统账户(不包含超级管理员)
	 * @return
	 */
	public List<User> getSystemUsers();
	/**
	 * 获取业务应用(业务应用自身及包含的资源的最新状态)
	 * @param id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getNewlyState(Long id);
	/**
	 * 业务单位业务服务自动生成拓扑，业务应用，业务资源最新状态数据
	 * @param ids
	 * @param resourceIds
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List getNewlyStateByIds(Long[] ids,Long[] resourceIds);
	/**
	 * 统一告警未恢复，已恢复包含的业务告警关联的业务应用ids集合接口方法
	 * @param user 当前用户
	 * @return
	 */
	public List<String> getSourceIds(ILoginUser user);
	/**
	 * 根据业务应用id查询业务应用状态
	 * @param id
	 * @return
	 */
	public InstanceStateEnum getStateById(Long id);
	/**
	 * 根据资源实例ID，查询所属的业务应用
	 * @param containInstanceId
	 * @return
	 */
	public List<BizServiceBo> getAllBuessinessApplication(long containInstanceId);

	void getListPage(Page<BizServiceBo, Object> page) throws Exception;
	
}
