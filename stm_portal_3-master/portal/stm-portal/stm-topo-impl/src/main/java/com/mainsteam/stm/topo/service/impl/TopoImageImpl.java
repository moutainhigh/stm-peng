package com.mainsteam.stm.topo.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.topo.api.ITopoImageApi;
import com.mainsteam.stm.topo.bo.TopoIconBo;
import com.mainsteam.stm.topo.dao.IIconDao;
import com.mainsteam.stm.topo.enums.IconState;
import com.mainsteam.stm.topo.util.FileUtilEx;

/**
 * <li>图片处理实现</li>
 * <li>文件名称: ImageImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年10月9日
 * @author zwx
 */
public class TopoImageImpl implements ITopoImageApi{
	private IIconDao iconDao;
	private Logger logger = Logger.getLogger(TopoImageImpl.class);
	//图片上传服务
	@Resource(name="fileClient")
	private IFileClientApi fileClient;

	@Override
	public int deleteImagesByIds(Long[] ids) throws Exception {
		//1.查询出要删除的图片信息
		List<TopoIconBo> iconBos = iconDao.getIiconsByIds(ids);
		
		//2.图片信息
		int rows = iconDao.deleteImgesByIds(ids);
		
		//3.删除图片
//		String subSavePath = "images";	//拓扑图标、图片放置路径（如：webapps/topoUpload/images）
		for(TopoIconBo icon:iconBos){
			fileClient.deleteFile(icon.getFileId());
//			String imagPath = FileUtilEx.getFileUploadUrl(subSavePath)+File.separator+icon.getPath();
//			File image = new File(imagPath);
//			if (null != image && image.exists()) {
//				image.delete();
//			}
		}
		
		return rows;
	}
	
	@Override
	public List<TopoIconBo> getTopoImagesByType(String type) {
		return iconDao.getIicons(type);
	}

	@Override
	public void saveImages(TopoIconBo icon, MultipartFile[] images) throws Exception {
		for(MultipartFile image : images){
			Long fileId = fileClient.upLoadFile(image);
			icon.setFileId(fileId);
			
		    iconDao.save(icon);
		}  
	}

	public void setIconDao(IIconDao iconDao) {
		this.iconDao = iconDao;
	}
	
	@Override
	@Deprecated
	public void downLoadImage(String name,HttpServletResponse rsp) throws IOException {
		String subSavePath = "images";	//拓扑图标、图片放置路径（如：webapps/topoUpload/images）
        String imagPath = FileUtilEx.getFileUploadUrl(subSavePath)+File.separator+name;
        try {
        	IOUtils.copy(new FileInputStream(imagPath), rsp.getOutputStream());
		} catch (FileNotFoundException e) {
			//如果是状态灯图片
			if(name.contains("_")){
				//获取原始图片
				String[] parts = name.split("\\.");
				String[] names = parts[0].split("\\_");
				String rawPath = FileUtilEx.getFileUploadUrl(subSavePath)+File.separator+names[0]+"."+parts[1];
				File rawFile = new File(rawPath);
				if(rawFile.exists()){
					BufferedImage img = ImageIO.read(rawFile);
					BufferedImage newImg = FileUtilEx.setState(IconState.nameOf(names[1]), img);
					ImageIO.write(newImg, parts[1],rsp.getOutputStream());
				}
			}else{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public BufferedImage getImage(String type, String srcPath) {
		IconState state = IconState.nameOf(type);
		File srcFile = new File(srcPath);
		if(srcFile.exists()){
			try {
				BufferedImage srcImage = ImageIO.read(srcFile);
				return FileUtilEx.setState(state, srcImage);
			} catch (IOException e) {
				logger.error(e);
			}
		}else{
			logger.warn(srcPath+"文件不存在");
		}
		return null;
	}

	@Override
	public BufferedImage getImage(String type, Long fileId) throws Exception {
		IconState state = IconState.nameOf(type);
		File file = fileClient.getFileByID(fileId);
		if(null != file){
			BufferedImage srcImage = ImageIO.read(file);
			return FileUtilEx.setState(state, srcImage);
		}else {
			logger.warn("文件["+fileId+"]不存在");
			return null;
		}
	}
}
