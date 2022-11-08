package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.TopoIconBo;



/**
 * <li>拓扑图片表dao接口定义</li>
 * <li>文件名称: IIconDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年10月9日
 * @author zwx
 */
public interface IIconDao extends IBaseDao<TopoIconBo> {
	
	/**
	 * 根据ids获取拓扑图片
	 * @param ids
	 * @return List<TopoIconBo>
	 */
	public List<TopoIconBo> getIiconsByIds(Long ids[]);
	
	/**
	 * 根据ids列表删除图片数据
	 * @return int 影响行数
	 */
	public int deleteImgesByIds(Long ids[]);
	
	/**
	 * 分类型获取拓扑图片
	 * @param type 类型：icon,cmd等，用来标注此图标干什么的
	 * @return List<TopoIconBo>
	 */
	public List<TopoIconBo> getIicons(String type);
	
	/**
	 * 新增图片
	 * @param bo
	 * @return
	 */
	public int save(TopoIconBo bo);
	
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
	
	public List<TopoIconBo> getAll();
	
	/**
	 * 清空表
	 */
	public void truncateAll();
}
