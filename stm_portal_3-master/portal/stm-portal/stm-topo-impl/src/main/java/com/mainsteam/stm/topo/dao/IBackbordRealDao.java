package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.BackbordBo;


/**
 * <li>背板实时表dao接口定义</li>
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
public interface IBackbordRealDao extends IBaseDao<BackbordBo> {
	
	/**
	 * 清空表
	 */
	public void truncateAll();

	/**
	 * 保存实时背板信息
	 * @param backbordBo
	 * @return
	 */
	public int save(BackbordBo backbordBo);
	
	/**
	 * 查询所有数据
	 * @return
	 */
	public List<BackbordBo> getAll();
	
	/**
	 * 根据资源ID批量更新实时背板信息
	 * @param baseId	背板默认值id
	 * @param info		背板信息（json）
	 * @return
	 */
	public int batchAddUpdateInfo(Long baseId, String info);
	
	/**
	 * 根据资源ID批量更新实时背板信息
	 * @param baseId	背板默认值id
	 * @param info		背板信息（json）
	 * @return
	 */
	public int batchUpdateBackbordRealInfo(Long baseId, String info);

	/**
	 * 保存时更新背板信息（解决oracle过长字符无法插入问题）
	 * @param id	id
	 * @param info	背板信息（json）
	 * @return
	 */
	public int addUpdateInfo(Long instanceId, String info);
	
	/**
	 * 保存背板信息
	 * @param baseId		默认背板id
	 * @param indstaceId	资源实例id
	 * @param info			背板信息（json）
	 * @return
	 */
	public int addOrUpdateBackbordRealInfo(Long baseId,Long instanceId, String info);
	
	/**
	 * 查询背板实时信息
	 * @param instanceId
	 * @return
	 */
	public BackbordBo getBackbordRealInfo(Long instanceId);

	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
}
