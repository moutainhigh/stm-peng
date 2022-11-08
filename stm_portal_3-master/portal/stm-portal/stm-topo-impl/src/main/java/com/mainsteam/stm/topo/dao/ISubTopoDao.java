package com.mainsteam.stm.topo.dao;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.SubTopoBo;

import java.util.List;
/**
 * 操作子拓扑
 * @author 富强
 *
 */
public interface ISubTopoDao extends IBaseDao<SubTopoBo>{
	/**
	 * 更新子拓扑排序顺序
	 * @param subtopo
	 */
	public void updateSort(SubTopoBo subtopo);
	
	/**
	 * 保存
	 * @param subTopoBo
	 * @return
	 */
	public int save(SubTopoBo subTopoBo);
	
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
	
	/**
	 * 获取所有数据
	 * @return
	 */
	public List<SubTopoBo> getAll();
	
	/**
	 * 添加子拓扑
	 * @param sb 子拓扑对象
	 * @return 生成的子拓扑id
	 */
	Long add(SubTopoBo sb);
	/**
	 * 获取所有子拓扑列表
	 * @return
	 */
	List<SubTopoBo> all();
	/**
	 * 通过parentid来获取子拓扑列表
	 * @param id parentId
	 * @return 子拓扑列表
	 */
	List<SubTopoBo> getByParentId(Long id);

    /**
     * 统计id下子记录数
     *
     * @param id
     * @return
     */
    Long countByParentId(Long id);

    /**
	 * 通过ip查询包含该ip的所有子拓扑
	 * @param ip 待查询的ip
	 * @return 子拓扑列表
	 */
	List<SubTopoBo> getSubToposByIp(String ip);
	/**
	 * 更新属性
	 * @param sb 已经实例化的sb
	 */
	void updateAttr(SubTopoBo sb);
	/**
	 * 通过id获取子拓扑的简单属性，不包括svgdom属性
	 * @param id
	 * @return
	 */
	SubTopoBo getSimpleAttr(Long id);
	/**
	 * 通过id获取子拓扑列表
	 * @param subtopoIds
	 * @return
	 */
	List<SubTopoBo> getSubToposByIds(List<Long> subtopoIds);
	
	SubTopoBo getById(Long topoId);

	/**
	 * 子拓扑名字验证
	 *
	 * @param parentId
	 * @param subTopoName
	 * @return >1：名字已存在 0：名字不存在
	 */
	int subTopoNameValidation(Long parentId, String subTopoName);
	/**
	 * 获取所有拓扑id
	 * @return
	 */
	List<Long> getAllTopoIds();

	public List<SubTopoBo> getByName(String name);

	public void init();

	List<SubTopoBo> getChildrenTopos(Long topoId);

	public int roomCount();

	public void removeById(Long id);

	public Long getSubTopoId(String name);

	public int deleteSubTopoByName(String name);
}
