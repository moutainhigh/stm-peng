package com.mainsteam.stm.ipmanage.impl.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.ipmanage.bo.IpMain;
import com.mainsteam.stm.ipmanage.impl.dao.IpMainMapper;

public class IpMainMapperImpl extends BaseDao<IpMain> implements IpMainMapper {

	public IpMainMapperImpl(SqlSessionTemplate session) {
		super(session,IpMainMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	
	public List<IpMain> getIPList(IpMain ip) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace()+"getIPList", ip);
	}

	
	public int insertIp(IpMain ip) {
		// TODO Auto-generated method stub
		return super.insert(ip);
	}

	
	public int update(IpMain ip) {
		// TODO Auto-generated method stub
		return super.update(ip);
	}

	
	public int delete(Integer id) {
		// TODO Auto-generated method stub
		return super.del(id);
	}

	
	public int resetDepart(Integer departId) {
		// TODO Auto-generated method stub
		return getSession().update(getNamespace()+"resetDepart", departId);
	}

	
	public int getcount(IpMain ip) {
		// TODO Auto-generated method stub
		IpMain selectOne = getSession().selectOne(getNamespace()+"getSameIp", ip);
		if(selectOne!=null){
			return 1;
		}
		return 0;
	}

}
