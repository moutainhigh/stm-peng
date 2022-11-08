package com.mainsteam.stm.topo.web.action;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.topo.api.ITopoImageApi;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.TopoIconBo;


/**
 * <li>拓扑管理所有的图片处理action</li>
 * <li>文件名称: ImageAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年10月9日
 * @author zwx
 */
@Controller
@RequestMapping(value="topo/image",produces="text/html;charset=UTF-8")
public class TopoImageAction extends BaseAction{
	
	private final Logger logger = LoggerFactory.getLogger(TopoImageAction.class);
	@Autowired
	private ITopoImageApi topoImageApi;
	
	/**
	 * 上传图片，支持多文件上传
	 * @param icon 		图片信息
	 * @param images 	图片文件
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public JSONObject save(TopoIconBo icon,MultipartFile image){
		try {
			MultipartFile[] images = new MultipartFile[1];
			images[0]=image;
			if(StringUtils.isNotBlank(icon.getType()) && images!=null && images.length > 0){
				topoImageApi.saveImages(icon, images); 
				return toSuccess("图片上传成功！");
			}else {
				return toJsonObject(700, "请选择图片后再上传");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("图片上传失败!",e);
			return toJsonObject(700, "图片上传失败");
		}
	}
	
	/**
	 * 分类型获取拓扑图片
	 * @param type 类型：icon,cmd等，用来标注此图标干什么的
	 * @return
	 */
	@RequestMapping(value="/getByType/{type}", method=RequestMethod.GET)
	public String getImagesByType(@PathVariable(value="type") String type){
		List<TopoIconBo> iconBos = topoImageApi.getTopoImagesByType(type);
		if(null != iconBos){
			return JSON.toJSONString(iconBos);
		}
		return null;
	}
	
	/**
	 * 删除图片
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="del", method=RequestMethod.POST)
	public String deleteImagesByIds(Long[] ids) {
		try {
			topoImageApi.deleteImagesByIds(ids);
			return toSuccess("删除成功").toJSONString();
		} catch (Exception e) {
			logger.error("删除图片失败!",e);
			return toJsonObject(700, "删除图片失败").toJSONString();
		}
	}
	
	/**
	 * 通过原地址获取指定类型的图片
	 * @param type 图片类型
	 * @param srcPath 源地址	
	 * @param resp 响应
	 */
	@RequestMapping(value="change", method=RequestMethod.GET)
	public void getImage(String path,String type,HttpServletResponse resp,HttpServletRequest req){
		BufferedImage image = null;
		try {
			Long imgId = Long.parseLong(path);
			image = topoImageApi.getImage(type,imgId);
		} catch (NumberFormatException e1) {
			if(path.indexOf("resource")<0){
				path="resource/"+path;
			}
			String realPath = req.getSession().getServletContext().getRealPath(path);
			image = topoImageApi.getImage(type,realPath);
		} catch (Exception e) {
			logger.error(path+" 文件下载异常",e);
		}
		try {
			ImageIO.write(image, "png",resp.getOutputStream());
		} catch (IOException e) {
			logger.error("下载图片失败-src="+path+",type="+type,e);
		}
	}
	@RequestMapping(value="{type}/default", method=RequestMethod.GET)
	public String defaultIcon(@PathVariable(value="type") String type){
		NodeBo nb = new NodeBo();
		nb.setType(type);
		nb.mapAttr();
		return nb.getIcon();
	}
}
