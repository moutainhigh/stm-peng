package com.mainsteam.stm.platform.file.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.platform.file.bean.FileConstantEnum;
import com.mainsteam.stm.platform.file.bean.FileGroupEnum;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.bean.FileModelQuery;
import com.mainsteam.stm.platform.file.client.FileClient;
import com.mainsteam.stm.platform.file.dao.IFileDao;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.util.FileUtil;

/**
 * 
* <li>文件名称: FileClientImpl.java</li>
* <li>公　　司: 武汉美新翔盛科技有限公司</li>
* <li>版权所有: 版权所有(C)2019-2020</li>
* <li>修改记录: ...</li>
* <li>内容摘要: ...</li>
* <li>其他说明: ...</li>
* @version  ms.stm
* @since    2019年8月14日
* @author   wangxinghao
* @tags
 */
public class FileClientImpl implements IFileClientApi{

	private static Logger logger=Logger.getLogger(FileClientImpl.class);
	
	private IFileDao fileDao;
	private ISequence fileSequence;
	private FileClient fileClient=FileClient.getInstance();
	
    @Value("${stm.file.islocal}")
	private String islocal;
	
    @Value("${stm.file.server_ip}")
	private String serverIP;
    
    @Value("${stm.file.server_dir}")
	private String serverDir;
	
    @Value("${stm.file.root_path}")
	private String rootPath;

    @Value("${stm.file.upload_file_type}")
	private String uploadFileType;
    
	private static final String LOCAL_FILE_SERVER=System.getProperties().getProperty("catalina.home")
			+File.separatorChar+"stm_file_repository"+File.separatorChar;
	
	
	private static final String DEFAULTS="defaults";
	
	private static final String OS = System.getProperty("os.name");  
	
    /**
     * 超时时间
     */
    private final int TIME_OUT=1000*30;
	
    /**
     * 文件路径
     */
    private String rootDirPath="";
    
    @Override
    public String getRootPath(){
    	return getRootDirPath();
    }
    
    /**
     * 文件服务器路径
     * @return
     */
    public String getRootDirPath(){
    	String root=null;
		//本地文件系统
		if("on".equals(islocal.toLowerCase())){
			root=LOCAL_FILE_SERVER;
		}else{
			//windows 的IP绝对路径
			if(OS!=null && OS.toLowerCase().indexOf("windows")!=-1){
				root="\\\\"+serverIP+File.separator+serverDir;
			}
			//Linux 的远程映射路径
			if(OS!=null && OS.toLowerCase().indexOf("linux")!=-1){
				root=rootPath;
			}
		}
		//结束符
		if(!root.endsWith(File.separator)){
			root+=File.separator;
		}
		return root;
    }
	
    @PostConstruct
    protected void init() throws IOException{
    	//init root dir path
    	this.rootDirPath=getRootDirPath();
    	
    	FileConstantEnum[] allFileConstants = FileConstantEnum.values ();
    	
    	List<FileModel> savedfileModels =fileDao.getAllDefaultsFiles();

    	List<FileModel> fileModels=new ArrayList<FileModel>();
    	for(FileConstantEnum fileConstant:allFileConstants){
    		long fileId=fileConstant.getFileId();
    		File file=new File(LOCAL_FILE_SERVER+DEFAULTS+File.separator+fileConstant.getFileName());
    		if(file.isFile() && file.exists()){
    			FileModel fileModel =initFileModel(null,file,null,FileGroupEnum.STM_SYSTEM.toString());
    			fileModel.setId(fileId);
    			String filePath=rootDirPath+fileModel.getFileGroup()+File.separator+fileId;;
//    			if("on".equals(this.islocal.toLowerCase())){
//    				filePath=LOCAL_FILE_SERVER+fileModel.getFileGroup()+File.separator+fileId;;
//    			}else{
//    				filePath=rootPath+File.separator+fileModel.getFileGroup()+File.separator+fileId;;
//    			}
    			
    			fileModel.setFilePath(filePath);
    			fileModel.setLock(0);
    			
    			if(!savedfileModels.contains(fileModel)){
        			fileClient.uploadFile2Server(fileModel);
        			fileModels.add(fileModel);
    			}
    			
    		}else{
    			logger.error("Default file can't not upload,it's not on default path:"+fileConstant.getFileName());
    			continue;
    		}
    	}
    	
    	if(fileModels!=null && fileModels.size()>0){
    		fileDao.insertFiles(fileModels);
    	}
    	
    	
    	//初始化报表临时目录
    	File reportTempDir=new File(LOCAL_FILE_SERVER+"STM_REPORT_TEMP_DIR");
    	if(!reportTempDir.exists()){
    		reportTempDir.mkdir();
    	}

    }
    
	/**
	 * initFileModel 
	 * @param mfile  MultipartFile 对象
	 * @param file  File 对象
	 * @param fileGroup file组
	 * @param fileIn 文件流
	 * @return 返回 FileModel
	 * @throws IOException
	 */
	private FileModel initFileModel(MultipartFile mfile,File file,InputStream fileIn,String fileGroup) throws IOException{
		FileModel fileModel=new FileModel();
		String fileName="";
		String filePath="";
		String mimeType="";
		String fileExt="";
		long fileSize=0;
//		String MD5=null;
		Date createDatetime=new Date();
		int lock=1;
		
		InputStream in=null;
		
		if(mfile!=null){
			fileName=mfile.getOriginalFilename();
			fileSize=mfile.getSize();
			mimeType=mfile.getContentType();
			fileExt=FileUtil.getFileExt(fileName);
			in=mfile.getInputStream();
		}
		if(file!=null){
			fileName=file.getName();
			fileSize=FileUtil.getFileSizes(file);
			mimeType=FileUtil.getMimetype(file);
			fileExt=FileUtil.getFileExt(fileName);
			in=new FileInputStream(file);
		}
		// 判断上传文件的类型
		if(uploadFileType != null && !"".equals(uploadFileType) && uploadFileType.split(",") != null && uploadFileType.split(",").length > 0 && !"".equals(fileName)){
			String[] fileTypes = uploadFileType.toUpperCase().split(",");
			String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
			if(!Arrays.asList(fileTypes).contains(prefix.toUpperCase())){
				throw new IOException("上传的" + prefix + "文件类型不符合要求！");
			}
		}
		
		if(fileIn!=null){
			in=fileIn;
			fileSize=fileIn.available();
		}

		fileModel.setFileName(fileName);
		fileModel.setFileGroup(fileGroup);
		fileModel.setMimeType(mimeType);
		fileModel.setFileSize(fileSize);
		fileModel.setFileExt(fileExt);
		
		fileModel.setCreateDatetime(createDatetime);
		fileModel.setLock(lock);
		fileModel.setIn(in);
		
		return fileModel;
	}
	
	@Override
	public Long upLoadFile(File file) throws Exception {
		FileModel fileModel=initFileModel(null,file,null,FileGroupEnum.STM_PUBLIC.toString());
		return upload(fileModel);
	}


	@Override
	public Long upLoadFile(String fileGroup, File file) throws Exception {
		FileModel fileModel=initFileModel(null,file,null,fileGroup);
		return upload(fileModel);
	}


	@Override
	public Long upLoadFile(FileGroupEnum fileGroup, File file)
			throws Exception {
		FileModel fileModel=initFileModel(null,file,null,fileGroup.toString());
		return upload(fileModel);
	}


	@Override
	public Long upLoadFile(MultipartFile mfile) throws Exception {
		FileModel fileModel=initFileModel(mfile,null,null,FileGroupEnum.STM_PUBLIC.toString());
		return upload(fileModel);
	}


	@Override
	public Long upLoadFile(String fileGroup, MultipartFile mfile)
			throws Exception {
		FileModel fileModel=initFileModel(mfile,null,null,fileGroup);
		return upload(fileModel);
	}


	@Override
	public Long upLoadFile(FileGroupEnum fileGroup, MultipartFile mfile)
			throws Exception {
		FileModel fileModel=initFileModel(mfile,null,null,fileGroup.toString());
		return upload(fileModel);
	}


	@Override
	public Long upLoadFile(FileInputStream fileIn,String fileName) throws Exception {
		FileModel fileModel=initFileModel(null,null,fileIn,FileGroupEnum.STM_PUBLIC.toString());
		fileModel.setFileName(fileName);
		return upload(fileModel);
	}


	@Override
	public Long upLoadFile(String fileGroup, FileInputStream fileIn,String fileName)
			throws Exception {
		FileModel fileModel=initFileModel(null,null,fileIn,fileGroup);
		fileModel.setFileName(fileName);
		return upload(fileModel);
	}


	@Override
	public Long upLoadFile(FileGroupEnum fileGroup, FileInputStream fileIn,String fileName)
			throws Exception {
		FileModel fileModel=initFileModel(null,null,fileIn,fileGroup.toString());
		fileModel.setFileName(fileName);
		return upload(fileModel);
	}
	
	
	/**
	 * 下载文件
	 * @param fileID 文件ID
	 * @return 文件流 FileInputStream
	 * @throws IOException
	 */
	public FileInputStream getFileInputStreamByID(long fileID){
		FileInputStream fileIn=null;
		
		FileModel fileModel=fileDao.getFileById(fileID);

		try {
			fileLock(fileModel);
			fileIn=getFileInputStreamByModel(fileModel);
		} catch (IOException e) {
			logger.error("Get File InputStream By ID :"+fileID, e);
		} catch (Exception e) {
			logger.error("FileLock Exception", e);

		}
		
		return fileIn;
	}
	
	/**
	 * 下载文件
	 * @param fileID
	 * @return FileModel
	 * @throws IOException
	 */
	public FileModel getFileModelByID(long fileID) throws Exception{
		FileInputStream fileIn=null;
		
		FileModel fileModel=fileDao.getFileById(fileID);
		if(fileModel==null){
			return null;
		}
		
		fileLock(fileModel);
		fileIn=getFileInputStreamByModel(fileModel);
		fileModel.setIn(fileIn);
		
		String filePath=this.getFilePath(fileModel);
		File file=new File(filePath);
		if(file.exists()){
			fileModel.setFilePath(filePath);
		}

		return fileModel;
	}
	
	/**
	 * 下载文件
	 * @param fileID
	 * @return File
	 * @throws IOException
	 */
	public File getFileByID(long fileID) throws Exception{
		FileModel fileModel=fileDao.getFileById(fileID);
		
		fileLock(fileModel);
		
		File file=new File(getFilePath(fileModel));
		
		String tempFile=rootDirPath+FileGroupEnum.STM_TEMP_DIR+File.separator+fileModel.getFileName();
		
		File dest=new File(tempFile);
		
		FileUtil.copyFile(file, dest);
		
		return dest;
	}

	/**
	 * 获取文件流
	 * @param fileModel
	 * @return
	 * @throws IOException 
	 */
	private FileInputStream getFileInputStreamByModel(FileModel fileModel) throws IOException{
		FileInputStream fileIn=null;
		
		String filePath=rootDirPath+fileModel.getFileGroup()+File.separator+fileModel.getId();;
		
		File file=new File(filePath);
		
		if(!file.exists()){
			logger.error(filePath,new IOException("File is not on server."));
		}

//		File outFile=new File(file.getParentFile(),fileModel.getFileName());
//		FileUtil.copyFile(file, outFile);
//		
		fileIn=new FileInputStream(file);
		
		//删除临时文件
//		FileUtil.removeFile(outFile);
		return fileIn;
	}


	@Override
	public void deleteFile(long fileID) throws Exception {
		FileModel fileModel=fileDao.getFileById(fileID);
		
		if(fileModel!=null){
			String filePath=null;
			
			if("on".equals(this.islocal.toLowerCase())){
				filePath=LOCAL_FILE_SERVER+fileModel.getFileGroup()+File.separator+fileModel.getId();
			}else{
				filePath=rootDirPath+File.separator+fileModel.getFileGroup()+File.separator+fileModel.getId();
			}
			
			fileLock(fileModel);
			
			fileClient.removeFile(filePath);
		}

		fileDao.deleteFile(fileID);
	}

	@Override
	public void updateFile(long fileId, File file) throws Exception {
		FileModel fileModel=initFileModel(null,file,null,FileGroupEnum.STM_PUBLIC.toString());
		update(fileId,fileModel);
	}

	@Override
	public void updateFile(long fileId, String fileGroup, File file)
			throws Exception {
		FileModel fileModel=initFileModel(null,file,null,fileGroup);
		update(fileId,fileModel);
	}

	@Override
	public void updateFile(long fileId, FileGroupEnum fileGroup, File file)
			throws Exception {
		FileModel fileModel=initFileModel(null,file,null,fileGroup.toString());
		update(fileId,fileModel);
	}

	@Override
	public void updateFile(long fileId, MultipartFile mfile) throws Exception {
		FileModel fileModel=initFileModel(mfile,null,null,FileGroupEnum.STM_PUBLIC.toString());
		update(fileId,fileModel);

	}

	@Override
	public void updateFile(long fileId, String fileGroup, MultipartFile mfile)
			throws Exception {
		FileModel fileModel=initFileModel(mfile,null,null,fileGroup);
		update(fileId,fileModel);
	}

	@Override
	public void updateFile(long fileId, FileGroupEnum fileGroup,
			MultipartFile mfile) throws Exception {
		FileModel fileModel=initFileModel(mfile,null,null,fileGroup.toString());
		update(fileId,fileModel);

	}

	@Override
	public void updateFile(long fileId, FileInputStream fileIn, String fileName)
			throws Exception {
		FileModel fileModel=initFileModel(null,null,fileIn,FileGroupEnum.STM_PUBLIC.toString());
		fileModel.setFileName(fileName);
		update(fileId,fileModel);

	}

	@Override
	public void updateFile(long fileId, String fileGroup,
			FileInputStream fileIn, String fileName) throws Exception {
		FileModel fileModel=initFileModel(null,null,fileIn,fileGroup);
		fileModel.setFileName(fileName);
		update(fileId,fileModel);
	}

	@Override
	public void updateFile(long fileId, FileGroupEnum fileGroup,
			FileInputStream fileIn, String fileName) throws Exception {
		FileModel fileModel=initFileModel(null,null,fileIn,fileGroup.toString());
		fileModel.setFileName(fileName);
		update(fileId,fileModel);

	}
	
	
	/**
	 * upload file
	 * @param fileModel
	 * @return
	 * @throws IOException
	 */
	private long upload(FileModel fileModel) throws IOException{
		long id=fileSequence.next();
		String filePath=null;
		
		if("on".equals(this.islocal.toLowerCase())){
			filePath=LOCAL_FILE_SERVER+fileModel.getFileGroup()+File.separator+id;;
		}else{
			filePath=rootDirPath+File.separator+fileModel.getFileGroup()+File.separator+id;;
		}
		
		fileModel.setId(id);
		fileModel.setFilePath(filePath);
		
		fileClient.uploadFile2Server(fileModel);
//		String serverPath=getFileServerPath()+File.separator+fileModel.getFileGroup() +File.separator+ fileModel.getId();
//		fileModel.setFilePath(serverPath);
		fileDao.insertFile(fileModel);
		long fileId=fileModel.getId();
		//unlock file
		fileDao.updateFileLock(fileId, 0);
		return fileId;
	}
	
	/**
	 * update file
	 * @param fileId
	 * @param fileModel
	 * @throws Exception
	 */
	private void update(long fileId,FileModel fileModel) throws Exception{
		fileModel.setId(fileId);
		
		FileModel oldFileModel =getFileModelByID(fileId);
		//validate file is lock
		fileLock(oldFileModel);
		
		String filePath=getFilePath(oldFileModel);
		//update file path
		fileModel.setFilePath(filePath);
		
		//lock file
		updateLockStatus(fileId,1);
		
		//remove file from file server
		fileClient.removeFile(filePath);
		
		
		//add new file to file server
		fileClient.uploadFile2Server(fileModel);
		
		//unlock
		fileModel.setLock(0);
		//update file form DB
		fileDao.updateFile(fileModel);
	}
	
	/**
	 * update Lock Statu
	 * @param fileId
	 * @param status
	 */
	public void updateLockStatus(long fileId,int status){
		//lock file
		fileDao.updateFileLock(fileId, status);
	}
	
	
	/**
	 * 获取文件路径
	 * @param fileModel
	 * @return
	 * @throws FileNotFoundException 
	 */
	private String getFilePath(FileModel fileModel) throws FileNotFoundException{
		String filePath=null;
		if(fileModel!=null){
			filePath=this.rootDirPath+fileModel.getFileGroup()+File.separator+fileModel.getId();
			
			File file=new File(filePath);
			
			file=new File(filePath);
			//验证本地服务器文件
			if(!file.exists() || !file.isFile()){
				throw new FileNotFoundException(fileModel.getFileName()+": File is not on file server");
			}
		}

		return filePath;
	}
	
	/**
	 * 轮询锁
	 * @param fileModel
	 * @throws Exception
	 */
	private void fileLock(FileModel fileModel) throws Exception{
		long start=System.currentTimeMillis();
		if(fileModel!=null){
			while (fileModel.getLock()==1) {
				Thread.sleep(10*1000);
				
				long currnt=System.currentTimeMillis();
				if((currnt-start)>TIME_OUT){
					throw new TimeoutException("File connect time out, File has locked.");
				}

				fileModel=fileDao.getFileById(fileModel.getId());
			}	
		}
	}
	
	
	public IFileDao getFileDao() {
		return fileDao;
	}


	public void setFileDao(IFileDao fileDao) {
		this.fileDao = fileDao;
	}


	public ISequence getFileSequence() {
		return fileSequence;
	}


	public void setFileSequence(ISequence fileSequence) {
		this.fileSequence = fileSequence;
	}

	@Override
	public void upLoadFile(String fileGroup, MultipartFile mfile, long fileId)
			throws Exception {
		FileModel fileModel=initFileModel(mfile,null,null,fileGroup);
		fileModel.setId(fileId);
		
		String filePath=this.rootDirPath+fileModel.getFileGroup()+File.separator+fileId;
		
		fileModel.setFilePath(filePath);
		
		fileClient.uploadFile2Server(fileModel);

//		String serverPath=getFileServerPath()+File.separator+fileModel.getFileGroup() +File.separator+ fileModel.getId();
//		fileModel.setFilePath(serverPath);
		
		fileDao.deleteFile(fileId);
		fileDao.insertFile(fileModel);
		
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.platform.file.service.IFileClientApi#upLoadFile(java.lang.String, java.io.InputStream, java.lang.String)
	 */
	@Override
	public Long upLoadFile(String groupName, InputStream in, String fileName)throws IOException{
		FileModel fileModel=initFileModel(null,null,in,groupName);
		fileModel.setFileName(fileName);
		return upload(fileModel);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.platform.file.service.IFileClientApi#deleteFileByIds(java.util.List, java.lang.String)
	 */
	@Override
	public void deleteFiles(List<Long> fileIds) throws IOException {
		
		for(Long fileID:fileIds){
			FileModel fileModel=fileDao.getFileById(fileID);
			if(fileModel!=null){
				String filePath=null;
				if("on".equals(this.islocal.toLowerCase())){
					filePath=LOCAL_FILE_SERVER+fileModel.getFileGroup()+File.separator+fileModel.getId();
				}else{
					filePath=rootDirPath+File.separator+fileModel.getFileGroup()+File.separator+fileModel.getId();
				}		
				fileClient.removeFile(filePath);
			}
		}
		
		fileDao.deleteFileByIds(fileIds);	//删除文件信息
	
		
	}
	
	/**
	 * 重新加载默认配置文件
	 * @param fileConstant
	 */
	@Override
	public void reLoadDefaultFileConstant(FileConstantEnum fileConstant)  throws IOException{
		
		long fileId=fileConstant.getFileId();
		
		File file=new File(LOCAL_FILE_SERVER+DEFAULTS+File.separator+fileConstant.getFileName());
		if(!file.exists() || !file.isFile()){
			logger.error("reLoadDefaultFileConstant was faild,the not found default file:"+fileConstant.getFileName());
			return;
		}
		
		FileModel fileModel =initFileModel(null,file,null,DEFAULTS);
		
		try {
			update(fileId,fileModel);
		} catch (Exception e) {
			logger.error("reLoadDefaultFileConstant faild",e);
		}
		
	}

	@Override
	public boolean isExist(Long fileID){
		File file=null;
		try {
			file=this.getFileByID(fileID);
		} catch (Exception e) {
			logger.error("file is not exist:"+file.getAbsolutePath(),e);
			return false;
		}
		return file.exists();
	}
	
	
	
	@Override
	public List<FileModel> getFileModels(Page<FileModel, FileModelQuery> page)throws Exception {
		return fileDao.getFiles(page);
	}
	
}
