package com.mainsteam.stm.platform.file.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.platform.file.bean.FileConstantEnum;
import com.mainsteam.stm.platform.file.bean.FileGroupEnum;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.bean.FileModelQuery;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * 
* <li>文件名称: IFileClientApi</li>
* <li>公　　司: 武汉美新翔盛科技有限公司</li>
* <li>版权所有: 版权所有(C)2019-2020</li>
* <li>修改记录: ...</li>
* <li>内容摘要: 实现文件上传到 File服务器，从服务器下载文件，文件的操作</li>
* <li>其他说明: ...</li>
* @version  ms.stm
* @since    2019年8月14日
* @author   wangxinghao
* @tags
 */
public interface IFileClientApi {
	
	/**
	 * 获取文件服务器目录
	 * @return
	 */
	public String getRootPath();
	
	/**
	 * File 文件上传
	 * @param file 需要上传的文件
	 * @return 返回文件ID
	 * @throws IOException
	 */
	public Long upLoadFile(File file) throws Exception;
	
	/**
	 * File 文件上传
	 * @param fileGroup 文件组
	 * @param file 需要上传的文件
	 * @return 返回文件ID
	 * @throws IOException
	 */
	public Long upLoadFile(String fileGroup,File file) throws Exception;
	
	/**
	 * File 文件上传
	 * @param fileGroup 系统定义组
	 * @param file 需要上传的文件
	 * @return 返回文件ID
	 * @throws IOException
	 */
	public Long upLoadFile(FileGroupEnum fileGroup,File file) throws Exception;
	
	/**
	 * MultipartFile 文件上传
	 * @param mfile MultipartFile  文件
	 * @return 返回 文件ID
	 * @throws IOException
	 */
	public Long upLoadFile(MultipartFile mfile) throws Exception;
	
	/**
	 * MultipartFile 文件上传
	 * @param fileGroup 文件组
	 * @param mfile MultipartFile  文件
	 * @return 返回 文件ID
	 * @throws IOException
	 */
	public Long upLoadFile(String fileGroup,MultipartFile mfile) throws Exception;
	
	/**
	 * MultipartFile 文件上传
	 * @param fileGroup 文件组
	 * @param mfile MultipartFile  文件
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	public void upLoadFile(String fileGroup,MultipartFile mfile,long fileId) throws Exception;
	
	/**
	 * MultipartFile 文件上传
	 * @param fileGroup 文件组
	 * @param mfile MultipartFile  文件
	 * @return 返回 文件ID
	 * @throws IOException
	 */
	public Long upLoadFile(FileGroupEnum fileGroup,MultipartFile mfile) throws Exception;
	
	/**
	 * 通过文件流上传文件
	 * @param fileIn 文件流
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public Long upLoadFile(FileInputStream fileIn,String fileName) throws Exception;
	/**
	 * 通过文件流上传文件
	 * @param fileGroup
	 * @param fileIn 文件流
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public Long upLoadFile(String fileGroup,FileInputStream fileIn,String fileName) throws Exception;
	
	/**
	 * 通过文件流上传文件
	 * @param fileGroup
	 * @param fileIn 文件流
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public Long upLoadFile(FileGroupEnum fileGroup,FileInputStream fileIn,String fileName) throws Exception;
	
	
	/**
	 * 下载文件
	 * @param fileID 文件ID
	 * @return 文件流 FileInputStream
	 * @throws IOException
	 */
	public FileInputStream getFileInputStreamByID(long fileID) throws Exception;
	
	/**
	 * 下载文件
	 * @param fileID
	 * @return FileModel
	 * @throws IOException
	 */
	public FileModel getFileModelByID(long fileID) throws Exception;
	
	/**
	 * 下载文件
	 * @param fileID
	 * @return File
	 * @throws IOException
	 */
	public File getFileByID(long fileID) throws Exception;
	
	/**
	 * 分页获取文件
	 * @param fileID
	 * @return File
	 * @throws IOException
	 */
	public List<FileModel> getFileModels(Page<FileModel, FileModelQuery> page) throws Exception;
	
	/**
	 * 删除文件
	 * @param fileID
	 * @throws IOException
	 */
	public void deleteFile(long fileID) throws Exception;
	
	
	/**
	 * File 文件上传
	 * @param file 需要上传的文件
	 * @return 返回文件ID
	 * @throws IOException
	 */
	public void updateFile(long fileId,File file) throws Exception;
	
	/**
	 * File 文件上传
	 * @param fileGroup 文件组
	 * @param file 需要上传的文件
	 * @return 返回文件ID
	 * @throws IOException
	 */
	public void updateFile(long fileId,String fileGroup,File file) throws Exception;
	
	/**
	 * File 文件上传
	 * @param fileGroup 系统定义组
	 * @param file 需要上传的文件
	 * @return 返回文件ID
	 * @throws IOException
	 */
	public void updateFile(long fileId,FileGroupEnum fileGroup,File file) throws Exception;
	
	/**
	 * MultipartFile 文件上传
	 * @param mfile MultipartFile  文件
	 * @return 返回 文件ID
	 * @throws IOException
	 */
	public void updateFile(long fileId,MultipartFile mfile) throws Exception;
	
	/**
	 * MultipartFile 文件上传
	 * @param fileGroup 文件组
	 * @param mfile MultipartFile  文件
	 * @return 返回 文件ID
	 * @throws IOException
	 */
	public void updateFile(long fileId,String fileGroup,MultipartFile mfile) throws Exception;
	
	/**
	 * MultipartFile 文件上传
	 * @param fileGroup 文件组
	 * @param mfile MultipartFile  文件
	 * @return 返回 文件ID
	 * @throws IOException
	 */
	public void updateFile(long fileId,FileGroupEnum fileGroup,MultipartFile mfile) throws Exception;
	
	/**
	 * 通过文件流上传文件
	 * @param fileIn 文件流
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public void updateFile(long fileId,FileInputStream fileIn,String fileName) throws Exception;
	/**
	 * 通过文件流上传文件
	 * @param fileGroup
	 * @param fileIn 文件流
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public void updateFile(long fileId,String fileGroup,FileInputStream fileIn,String fileName) throws Exception;
	
	/**
	 * 通过文件流上传文件
	 * @param fileGroup
	 * @param fileIn 文件流
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public void updateFile(long fileId,FileGroupEnum fileGroup,FileInputStream fileIn,String fileName) throws Exception;

	/**
	 * 通过输入流上传文件
	 * @param groupName	分组名称
	 * @param in	输入流
	 * @param fileName	文件名
	 * @return
	 * @author	ziwen
	 * @throws IOException 
	 * @date	2019年10月16日
	 */
	public Long upLoadFile(String groupName,
			InputStream in, String fileName) throws IOException;

	/**
	 * 通过一组文件ID删除文件
	 * @param fileIds
	 * @author	ziwen
	 * @param groupName 
	 * @date	2019年10月16日
	 */
	public void deleteFiles(List<Long> fileIds) throws IOException;
	
	/**
	 * 重新加载默认配置文件
	 * @param fileConstant
	 */
	public void reLoadDefaultFileConstant(FileConstantEnum fileConstant)  throws IOException;
	
	/**
	 * 验证文件是否在文件服务器
	 * @param fileId
	 */
	public boolean isExist(Long fileId);
	

}
