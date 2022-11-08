package com.mainsteam.stm.topo.dao.impl;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.ISubTopoDao;
import com.mainsteam.stm.topo.enums.TopoType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
public class SubTopoDaoImpl extends BaseDao<SubTopoBo> implements ISubTopoDao{
	private Logger logger = LogManager.getLogger(SubTopoDaoImpl.class);
	@Autowired
	@Qualifier(value="stm_topo_subtopo_seq")
	private ISequence seq;
	@Autowired
	private INodeDao nodeDao;
	
	@Override
	public void updateSort(SubTopoBo subTopoBo){
		this.getSession().update(getNamespace()+"updateSort",subTopoBo);
	}
	
	@Autowired
	public SubTopoDaoImpl(@Qualifier(value=BaseDao.SESSION_DEFAULT) SqlSessionTemplate session) {
		super(session, ISubTopoDao.class.getName());
	}
	@Override
	public void init() {
		logger.info("init subtopo start");
		//初始化
		SubTopoBo sb1 = new SubTopoBo();
		sb1.setName("二层拓扑");
		sb1.setId(0l);
		try {
			getSession().insert(getNamespace()+"add", sb1);
		} catch (Throwable e) {
		}
		
		SubTopoBo sb2 = new SubTopoBo();
		sb2.setName("三层拓扑");
		sb2.setId(1l);
		try {
			getSession().insert(getNamespace()+"add", sb2);
		} catch (Throwable e) {
		}
		
		SubTopoBo sb3 = new SubTopoBo();
		sb3.setName("机房视图");
		sb3.setId(2l);
		try {
			getSession().insert(getNamespace()+"add", sb3);
		} catch (Throwable e) {
		}
	}
	@Override
	public Long add(SubTopoBo sb) {
		//保存子拓扑信息
		if(sb.getParentId()==null){
			sb.setParentId(0l);
		}
		sb.setId(seq.next());
		getSession().insert(getNamespace()+"add", sb);
		return sb.getId();
	}
	@Override
	public List<SubTopoBo> all() {
		return getSession().selectList(getNamespace()+"all");
	}
	@Override
	public List<SubTopoBo> getByParentId(Long id) {
		if(id==null) id=0l;
		Map<String,Long> param = new HashMap<String,Long>();
		param.put("id",id);
		List<SubTopoBo> subtopos = getSession().selectList(getNamespace()+"getByParentId",param);
		if(subtopos!=null && !subtopos.isEmpty()){
			return subtopos;
		}else{
			return new ArrayList<SubTopoBo>();
		}
	}

    @Override
    public Long countByParentId(Long id) {
        return getSession().selectOne(getNamespace() + "countByParentId", id);
    }

    @Override
	public List<SubTopoBo> getChildrenTopos(Long topoId){
		Map<String,Long> param = new HashMap<String,Long>();
		param.put("id",topoId);
		List<SubTopoBo> subtopos = getSession().selectList(getNamespace()+"getByParentId",param);
		if(subtopos!=null && !subtopos.isEmpty()){
			List<SubTopoBo> tmptopos = new ArrayList<SubTopoBo>();
			for(SubTopoBo sb : subtopos){
				tmptopos.addAll(getChildrenTopos(sb.getId()));
			}
			subtopos.addAll(tmptopos);
			return subtopos;
		}else{
			return new ArrayList<SubTopoBo>();
		}
	}
	@Override
	public List<SubTopoBo> getSubToposByIp(String ip) {
		Map<String,String> param = new HashMap<String,String>();
		param.put("ip",ip);
		return getSession().selectList(getNamespace()+"getSubToposByIp",param);
	}
	@Override
	public void updateAttr(SubTopoBo sb) {
		TopoType type = TopoType.byId(sb.getId());
		//对于基础拓扑不能更新parentId
		if(!type.equals(TopoType.UNKNOWN_TOPO)){
			 sb.setParentId(null);
		}
		getSession().update(getNamespace()+"updateAttr",sb);
	}
	@Override
	public SubTopoBo getSimpleAttr(Long id) {
		if(id==null){
			id=0l;
		}
		List<Long> ids = new ArrayList<Long>();
		ids.add(id);
		List<SubTopoBo> sbs = getSession().selectList(getNamespace()+"getSimpleAttr",ids);
		if(sbs.size()>0){
			return sbs.get(0);
		}else if(id==0l||id==1l){
			SubTopoBo sb = new SubTopoBo();
			sb.setName(id==0l?"二层拓扑":"三层拓扑");
			sb.setParentId(null);
			sb.setBgsrc(null);
			sb.setSvgdom(null);
			sb.setId(id);
			getSession().insert(getNamespace()+"add",sb);
			return sb;
		}else{
			return null;
		}
	}
	@Override
	public List<SubTopoBo> getSubToposByIds(List<Long> subtopoIds) {
		if(subtopoIds.size()>0){
			return getSession().selectList(getNamespace()+"getSubToposByIds",subtopoIds);
		}else{
			return new ArrayList<SubTopoBo>();
		}
	}
	@Override
	public SubTopoBo getById(Long topoId) {
		Map<String,Long> map = new HashMap<String,Long>();
		map.put("id", topoId);
		return getSession().selectOne(getNamespace()+"getById",map);
	}
	@Override
	public int subTopoNameValidation(Long parentId, String subTopoName) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("parentId", parentId);
		m.put("subTopoName", subTopoName);
		//#22433 不做parentId限制
		return getSession().selectOne(getNamespace() + "subTopoNameValidation", m);
	}
	@Override
	public List<Long> getAllTopoIds() {
		return getSession().selectList(getNamespace() + "getAllTopoIds");
	}
	@Override
	public ISequence getSeq() {
		return this.seq;
	}
	@Override
	public int save(SubTopoBo subTopoBo) {
		if(null == subTopoBo.getId()) subTopoBo.setId(this.getSeq().next());
		return this.getSession().insert(getNamespace()+"add", subTopoBo);
	}
	@Override
	public List<SubTopoBo> getAll() {
		return this.getSession().selectList(getNamespace()+"getAll");
	}
	@Override
	public List<SubTopoBo> getByName(String name) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("name", name);
		return this.getSession().selectList(getNamespace()+"getByName", map);
	}
	@Override
	public int roomCount() {
		return this.getSession().selectOne(getNamespace()+"roomCount");
	}
	@Override
	public void removeById(Long id) {
		Map<String,Long> map = new HashMap<String,Long>(1);
		map.put("id", id);
		getSession().delete(getNamespace()+"removeById",map);
	}
	@Override
	public Long getSubTopoId(String name) {
		return this.getSession().selectOne(getNamespace()+"getSubTopoId", name);
	}

	@Override
	public int deleteSubTopoByName(String name) {
		return getSession().delete(getNamespace()+"deleteSubTopoByName",name);
	}
}
