package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.BackbordBo;


/**
 * <li>背板信息默认表dao接口定义</li>
 * <li>文件名称: IMacRuntimeDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年9月10日
 * @author zwx
 */
public interface IBackbordBaseDao extends IBaseDao<BackbordBo> {
	
	/**
	 * 根据厂商查询列表
	 * @param vendor
	 * @return
	 */
	public List<BackbordBo> getBackbordByVendor(String vendor);
	
	/**
	 * 清空背板基表数据
	 * @return int 影响行数
	 */
	public int deleteAll();
	
	/**
	 * 新增背板默认信息
	 * @param bo
	 * @return
	 */
	public int save(BackbordBo bo);
	
	/**
	 * 查询背板默认信息
	 * @param id
	 * @return
	 */
	public BackbordBo getBackbordBaseInfo(String vendor,String type);

	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
}
