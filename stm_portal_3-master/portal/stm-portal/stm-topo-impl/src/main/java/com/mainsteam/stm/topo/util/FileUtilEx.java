package com.mainsteam.stm.topo.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.topo.enums.IconState;

/**
 * 文件工具类
 * @author zwxiao
 */
@Component
public class FileUtilEx {
	
	/**
	 * 上传文件
	 * @param file			文件
	 * @param subSavePath	保存子目录，根目录是tomcat的webapps
	 * @return				保存的文件名称
	 */
	@Deprecated
	public static String uploadFile(MultipartFile file,String subSavePath) {
		//获取唯一名字
		String vname = null;
		if(null != file){
			vname = getUniqueFileName(file.getOriginalFilename());
			//获取上传文件地址
			String uploadUrl = getFileUploadUrl(subSavePath);
			File savefile = new File(new File(uploadUrl), vname);
			
			if (!savefile.getParentFile().exists())
				savefile.getParentFile().mkdirs();
			
			try {
				//保存文件
				FileUtils.copyInputStreamToFile(file.getInputStream(), savefile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return vname;
	}
	
	
	/**
	 * 
	 * @param image 图片数据
	 * @param prefix 文件名前缀-除后缀名部分
	 * @param suffix 文件名后缀
	 * @param path 路径
	 */
	@Deprecated
	public static void uploadFile(BufferedImage image, String prefix,String suffix, String path) {
		//获取上传文件地址
		String uploadUrl = getFileUploadUrl(path);
		File savefile = new File(new File(uploadUrl), prefix+"."+suffix);
		if (!savefile.getParentFile().exists())
			savefile.getParentFile().mkdirs();
		try {
			//保存文件
			ImageIO.write(image, suffix, savefile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取上传文件地址
	 * @param subSavePath	子目录
	 * @return 				文件存储地址
	 */
	@Deprecated
	public static String getFileUploadUrl(String subSavePath) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String tomcatRoot = request.getSession().getServletContext().getRealPath("");
		File furl = new File(tomcatRoot);
		String url=furl.getParent()+File.separator+"topoUpload";
		if(StringUtils.isNotBlank(subSavePath)){
			url += File.separator + subSavePath;
		}
		return url;
	}
	
	/**
	 * 产生唯一的文件名
	 * @param filename	文件原始名称
	 * @return
	 */
	@Deprecated
	public static synchronized String getUniqueFileName(String filename) {
		int dotIndex = filename.lastIndexOf(".");
		String vfileName = System.nanoTime() + filename.substring(dotIndex);	// 纳秒
		return vfileName;
	}

	/**
	 * 设置设备状态
	 * @param state
	 * @param bimage
	 * @return
	 */
	public static BufferedImage setState(IconState state, BufferedImage bimage) {
		int w=bimage.getWidth(),h=bimage.getHeight();
		BufferedImage retnImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for(int i=0;i<w;++i){
			for(int j=0;j<h;++j){
				Color color = new Color(bimage.getRGB(i, j),true);
				switch(state){
					case DANGER:
						retnImage.setRGB(i, j,new Color(255,color.getRed(),color.getRed(),color.getAlpha()).getRGB());
						break;
					case WARNING:{
							int g = (int)Math.round(color.getGreen()*0.6+190);
							if(g>=255) g=255;
							retnImage.setRGB(i, j,new Color(245,g,color.getRed(),color.getAlpha()).getRGB());
						}
						break;
					case DISABLED:
						retnImage.setRGB(i, j,new Color(color.getRed(),color.getRed(),color.getRed(),color.getAlpha()).getRGB());
						break;
					case SEVERITY:{
							int g = (int)Math.round(color.getRed()*0.48+130);
							if(g>=255) g=255;
							retnImage.setRGB(i, j,new Color(255,g,color.getRed(),color.getAlpha()).getRGB());
						}
					break;
					case MONITORED:
						retnImage.setRGB(i, j,new Color(color.getRed(),color.getRed(),color.getRed(),color.getAlpha()).getRGB());
						break;
					case NOT_MONITORED:
						retnImage.setRGB(i, j,new Color(color.getRed(),color.getRed(),color.getRed(),color.getAlpha()).getRGB());
						break;
					default:
						retnImage.setRGB(i, j,color.getRGB());
						break;
				}
			}
		}
		return retnImage;
	}
}
