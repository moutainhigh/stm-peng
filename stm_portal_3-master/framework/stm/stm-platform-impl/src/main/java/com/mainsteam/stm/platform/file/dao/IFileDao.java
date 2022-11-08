package com.mainsteam.stm.platform.file.dao;

import java.util.List;

import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.bean.FileModelQuery;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface IFileDao {
	
	public long insertFile(FileModel fileModel);
	
	public void deleteFile(long fileID);
	
	public FileModel getFileById(long fileId);
	
	public void updateFileLock(long fileId,int lock);
	
	public void updateFile(FileModel fileModel);
	
	/**
	 * 分页获取文件
	 * @return
	 */
	public List<FileModel> getFiles(Page<FileModel, FileModelQuery> page);
	
	/**
	 * 获取所有默认配置文件
	 * @return
	 */
	public List<FileModel> getAllDefaultsFiles();

	/**
	 * 批量插入文件
	 * @param fileModels
	 */
	public void insertFiles(List<FileModel> fileModels);
	
	

	/**
	 * 通过一组文件id删除文件信息
	 * @param fileIds
	 * @author	ziwen
	 * @date	2019年10月16日
	 */
	public void deleteFileByIds(List<Long> fileIds);
	
}
