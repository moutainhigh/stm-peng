package com.mainsteam.stm.platform.file.dao.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;







import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.bean.FileModelQuery;
import com.mainsteam.stm.platform.file.dao.IFileDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;


public class FileDaoImpl extends BaseDao<FileModel> implements IFileDao{

	@Autowired
	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public FileDaoImpl(SqlSessionTemplate session) {
		super(session, IFileDao.class.getName());
	}
	
	@Override
	public List<FileModel> getFiles(Page<FileModel, FileModelQuery> page) {
		return getSession().selectList(getNamespace()+SQL_COMMOND_PAGE_SELECT,page);
	}

	@Override
	public long insertFile(FileModel fileModel) {
		getSession().insert("insertFile", fileModel);
		return fileModel.getId();
	}


	@Override
	public void deleteFile(long fileID) {
		getSession().delete("delFileByID", fileID);
	}

	@Override
	public FileModel getFileById(long fileId) {
		// TODO Auto-generated method stub
		return getSession().selectOne("getFileById", fileId);
	}

	@Override
	public void updateFileLock(long fileId, int lock) {
		FileModel fm=new FileModel();
		fm.setId(fileId);
		fm.setLock(lock);
		getSession().update("updateFileLock", fm);
	}

	@Override
	public void updateFile(FileModel fileModel) {
		getSession().update("updateFile", fileModel);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.platform.file.dao.IFileDao#getFileByIds(java.util.List)
	 */
	@Override
	public void deleteFileByIds(List<Long> fileIds) {
		getSession().delete("deleteFileByIds", fileIds);
	}
	
	/**
	 * 获取所有默认配置文件
	 * @return
	 */
	@Override
	public List<FileModel> getAllDefaultsFiles(){
		return getSession().selectList("getAllDefaultsFiles");
	}

	/**
	 * 批量插入文件
	 * @param fileModels
	 */
	@Override
	public void insertFiles(List<FileModel> fileModels){
		
		try(
			SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (FileModel fileModel : fileModels) {
				batchSession.insert("insertFiles", fileModel);

			}
			batchSession.commit();
		}
		
		
	
	}
	
}
