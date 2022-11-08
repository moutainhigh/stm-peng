package com.mainsteam.stm.platform.dao;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.exception.BeanInjectedException;

/**
 * <li>文件名称: BaseDao1.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月21日
 * @author   ziwenwen
 */
public class BaseDao<E> implements IBaseDao<E> {
	private SqlSessionTemplate session;
	
	private String iDaoNamespace;
	
	/**
	 * 插入单表数据sql片段id
	 */
	protected static final String SQL_COMMOND_INSERT="insert";
	
	/**
	 * 默认数据源sessionID
	 */
	protected static final String SESSION_DEFAULT="sqlSession";
	
	/**
	 * SQLITE数据源sessionID
	 */
	protected static final String SESSION_SQLITE="sqliteSqlSession";
	
	/**
	 * 批量插入单表数据sql片段id
	 */
	protected static final String SQL_COMMOND_BATCH_INSERT="batchInsert";

	/**
	 * 删除单表数据sql片段id
	 */
	protected static final String SQL_COMMOND_DELETE="del";

	/**
	 * 批量删除单表数据sql片段id
	 */
	protected static final String SQL_COMMOND_BATCH_DELETE="batchDel";

	/**
	 * 修改单表数据sql片段id
	 */
	protected static final String SQL_COMMOND_UPDATE="update";

	/**
	 * 批量修改单表数据sql片段id
	 */
	protected static final String SQL_COMMOND_BATCH_UPDATE="batchUpdate";

	/**
	 * 根据条件获取单表记录数量的sql片段id
	 */
	protected static final String SQL_COMMOND_COUNT="count";

	/**
	 * 根据id获取一条记录sql片段id
	 */
	protected static final String SQL_COMMOND_GET="get";

	/**
	 * 根据条件获取一组记录的sql片段id
	 */
	protected static final String SQL_COMMOND_SELECT="select";
	
	protected static final String SQL_COMMOND_PAGE_SELECT="pageSelect";
	
	private int batchCount=100;
	
	public BaseDao(SqlSessionTemplate session,String iDaoNamespace) {
		this.session=session;
		this.iDaoNamespace=iDaoNamespace+='.';
		getNamespace();
	}
	
	@Override
	public int insert(E entity) {
		return insert(SQL_COMMOND_INSERT,entity);
	}

	@Override
	public int batchInsert(List<E> entities) {
		return batchInsert(SQL_COMMOND_BATCH_INSERT, entities);
	}

	@Override
	public int del(Long pk) {
		return del(SQL_COMMOND_DELETE,pk);
	}

	@Override
	public int del(long pk) {
		return del(SQL_COMMOND_DELETE,pk);
	}
	
	@Override
	public int batchDel(Long[] pks) {
		return batchDel(SQL_COMMOND_BATCH_DELETE, pks);
	}
	
	@Override
	public int batchDel(long[] pks) {
		SqlSession session=getSession();
		int count=0;
		String delCommond=iDaoNamespace+SQL_COMMOND_BATCH_DELETE;
		for(int i=0,len=pks.length;i<len;i++){
			count+=session.delete(delCommond,pks[i]);
		}
		return count;
	}

	@Override
	public int update(E entity) {
		return update(SQL_COMMOND_UPDATE, entity);
	}

	@Override
	public int batchUpdate(List<E> entities) {
		return batchUpdate(SQL_COMMOND_BATCH_UPDATE, entities);
	}

	@Override
	public E get(Long id) {
		return get(SQL_COMMOND_GET, id);
	}

	@Override
	public E get(long id) {
		return get(SQL_COMMOND_GET, id);
	}

	@Override
	public List<E> select(E entity) {
		return select(SQL_COMMOND_SELECT, entity);
	}
	
	/**
	 * 获取SqlSession
	 * @return
	 */
	protected SqlSessionTemplate getSession() {
		if(session==null){
			throw new BeanInjectedException(this.getClass().getName()+
					"没有找到SqlSession，可能原因：没有注入SqlSession！");
		}
		return session;
	}
	
	/**
	 * 后去dao操作接口
	 * @return
	 */
	protected String getNamespace(){
		if(iDaoNamespace==null){
			throw new BeanInjectedException(this.getClass().getName()+
					"没有找到IDaoNamespace，可能原因：没有注入IDaoNamespace！");
		}
		return iDaoNamespace;
	}

	public int getBatchCount() {
		return batchCount;
	}

	public void setBatchCount(int batchCount) {
		this.batchCount = batchCount;
	}

	@Override
	public int insert(String commond, E entity) {
		return getSession().insert(iDaoNamespace+commond,entity);
	}

	@Override
	public int batchInsert(String commond, Collection<? extends Object> entities) {
		return batchOperate(iDaoNamespace+commond, entities);
	}

	@Override
	public int del(String commond,Object key) {
		return getSession().delete(iDaoNamespace+commond,key);
	}

	@Override
	public int batchDel(String commond, Object[] keies) {
		SqlSession session=getSession();
		int count=0;
		String delCommond=iDaoNamespace+commond;
		for(int i=0,len=keies.length;i<len;i++){
			count+=session.delete(delCommond,keies[i]);
		}
		return count;
	}

	@Override
	public int update(String commond, E entity) {
		return getSession().update(iDaoNamespace+commond, entity);
	}

	@Override
	public int batchUpdate(String commond, Collection<E> entities) {
		return batchOperate(iDaoNamespace+commond, entities);
	}

	@Override
	public List<E> select(String commond, Object entity) {
		return getSession().selectList(iDaoNamespace+commond,entity);
	}

	@Override
	public E get(String commond, Object key) {
		return getSession().selectOne(iDaoNamespace+commond, key);
	}

	private int batchOperate(String statement,Collection<? extends Object> objs){
		SqlSession session=getSession();
		int count=0;
		Object[] os=objs.toArray();
		for(int i=0,size=os.length;i<size;i++){
			count+=session.update(statement, os[i]);
		}
		return count;
	}
}
