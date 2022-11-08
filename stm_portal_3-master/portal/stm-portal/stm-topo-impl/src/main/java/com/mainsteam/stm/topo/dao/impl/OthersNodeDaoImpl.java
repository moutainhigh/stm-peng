package com.mainsteam.stm.topo.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.dao.IOthersNodeDao;
import com.mainsteam.stm.topo.util.TopoHelper;
@Repository
public class OthersNodeDaoImpl extends BaseDao<OtherNodeBo> implements IOthersNodeDao{
	@Autowired
	@Qualifier(value="stm_topo_othernode_seq")
	private ISequence seq;
	
	@Autowired
	public OthersNodeDaoImpl(@Qualifier(value=BaseDao.SESSION_DEFAULT) SqlSessionTemplate session) {
		super(session, IOthersNodeDao.class.getName());
	}
	@Override
	public void deleteByIds(List<Long> deleteIds) {
		if(deleteIds.isEmpty()){
			return ;
		}
		this.getSession().delete(getNamespace()+"deleteByIds",deleteIds);
	}
	@Override
	public void saveList(List<OtherNodeBo> saves) {
		for(OtherNodeBo on:saves){
			on.setId(seq.next());
			this.getSession().insert(getNamespace()+"save",on);
		}
	}
	@Override
	public void updateList(List<OtherNodeBo> updates) {
		for(OtherNodeBo on:updates){
			OtherNodeBo nb = getById(on.getId());
			JSONObject dbAttr = nb.parseAttr(JSONObject.class);
			TopoHelper.mixin(dbAttr,(JSONObject)on.parseAttr(JSONObject.class));
			on.setAttr(dbAttr.toJSONString());
			this.getSession().update(getNamespace()+"update",on);
		}
	}
	@Override
	public List<OtherNodeBo> getBySubTopoId(Long id) {
		Map<String,Long> param = new HashMap<String,Long>();
		if(id==null){
			param.put("id",0l);
		}else{
			param.put("id",id);
		}
		return this.getSession().selectList(getNamespace()+"getBySubTopoId",param);
	}
	@Override
	public ISequence getSeq() {
		return this.seq;
	}
	@Override
	public List<OtherNodeBo> getAll() {
		return this.getSession().selectList(getNamespace()+"getAll");
	}
	@Override
	public int save(OtherNodeBo otherNodeBo) {
		if(null == otherNodeBo.getId()) otherNodeBo.setId(this.getSeq().next());
		return this.getSession().update(getNamespace()+"save", otherNodeBo);
	}
	@Override
	public OtherNodeBo getById(Long id) {
		Map<String,Long> param = new HashMap<String,Long>();
		param.put("id",id);
		return this.getSession().selectOne(getNamespace()+"getById",param);
	}
	@Override
	public void updateAttr(OtherNodeBo otherNodeBo){
		if(otherNodeBo!=null && otherNodeBo.getId()!=null){
			if(getById(otherNodeBo.getId())!=null){
				String attr=getById(otherNodeBo.getId()).getAttr();
				JSONObject dbAttr=JSON.parseObject(attr);
				TopoHelper.mixin(dbAttr, JSON.parseObject(otherNodeBo.getAttr()));
				otherNodeBo.setAttr(dbAttr.toJSONString());
				this.getSession().update(getNamespace()+"updateAttr",otherNodeBo);
			}else{
				return;
			}

		}
	}
	@Override
	public int isCabinetRepeatName(String name, Long subTopoId) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("subTopoId",subTopoId);
		param.put("cabinet","'%cabinet%'");
		param.put("name", "'%\"text\":\""+name+"\"%'");
		int count = this.getSession().selectOne(getNamespace()+"isCabinetRepeatName",param);
		return count;
	}
	@Override
	public List<Long> getRoomByNodeIds(List<Long> ids) {
		if(ids.isEmpty()){
			return new ArrayList<Long>(0);
		}
		List<OtherNodeBo> cabinets = getAllCabinets();
		List<Long> subtopoIds = new ArrayList<Long>(2);
		Map<String,Integer> repeateFlag = new HashMap<String,Integer>(2);
		Long[] tmpIds = ids.toArray(new Long[0]);
		Map<String,Integer> idMap = TopoHelper.idToMap(tmpIds);
		for(OtherNodeBo cb:cabinets){
			JSONObject attr = cb.parseAttr(JSONObject.class);
			if(attr.containsKey("rows")){
				Integer[] cbids = attr.getJSONArray("rows").toArray(new Integer[0]);
				for(Integer cbid : cbids){
					if(idMap.containsKey(cbid.toString()) && !repeateFlag.containsKey(cb.getSubTopoId().toString())){
						subtopoIds.add(cb.getSubTopoId());
						repeateFlag.put(cb.getSubTopoId().toString(),1);
					}
				}
			}
		}
		return subtopoIds;
	}
	@Override
	public List<OtherNodeBo> getAllCabinets() {
		Map<String,Object> param = new HashMap<String,Object>(1);
		param.put("cabinet","'%cabinet%'");
		List<OtherNodeBo> ob = this.getSession().selectList(getNamespace()+"getAllCabinets",param);
		return ob;
	}
	@Override
	public List<OtherNodeBo> findCabinetInRoom(Long id){
		Map<String,Object> param = new HashMap<String,Object>(2);
		param.put("cabinet","'%cabinet%'");
		param.put("subTopoId",id);
		List<OtherNodeBo> ob = this.getSession().selectList(getNamespace()+"findCabinetInRoom",param);
		return ob;
	}
	@Override
	public List<OtherNodeBo> getByIds(Long[] ids) {
		if(ids==null || ids.length==0){
			return new ArrayList<OtherNodeBo>(0);
		}else{
			Map<String,Object> param = new HashMap<String,Object>(1);
			param.put("ids",ids);
			return this.getSession().selectList(getNamespace()+"getByIds",param);
		}
	}
	/* (non-Javadoc)
	 * @see com.mainsteam.stm.topo.dao.IOthersNodeDao#updateOtherZIndexById(java.util.HashMap)
	 */
	@Override
	public void updateOtherZIndexById(@SuppressWarnings("rawtypes") HashMap map) {
		this.getSession().update(getNamespace()+"updateOtherZIndexById",map);
	}
}
