package com.mainsteam.stm.topo.api;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.topo.bo.TopoIconBo;

/**
 * <li>拓扑图片操作接口定义</li>
 * <li>文件名称: IImageApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年10月9日
 * @author zwx
 */
public interface ITopoImageApi {
	//监控和费监控两个状态图标位置
	public static final String MONITORED_ICON="resource/themes/blue/images/topo/monitored.png";
	public static final String NOT_MONITORED_ICON="resource/themes/blue/images/topo/not_monitored.png";
	
	/**
	 * 上传图片（支持批量上传）
	 * @param icon		(type	类型：icon,cmd等，用来标注此图标干什么的)
	 * @param images	图片文件
	 * @throws IOException 
	 */
	public void saveImages(TopoIconBo icon, MultipartFile[] images) throws Exception;
	
	/**
	 * 分类型获取拓扑图片
	 * @param type 类型：icon,cmd等，用来标注此图标干什么的
	 * @return List<TopoIconBo>
	 */
	public List<TopoIconBo> getTopoImagesByType(String type);
	
	/**
	 * 根据ids删除拓扑图片
	 * @param ids
	 * @return
	 */
	public int deleteImagesByIds(Long[] ids) throws Exception;

	/**
	 * 返回下载流
	 * @param name
	 * @return
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	@Deprecated
	public void downLoadImage(String name,HttpServletResponse rsp) throws IOException;
	/**
	 * 通过原地址获取指定类型的图片
	 * @param type 图片类型
	 * @param srcPath 源地址
	 * @return
	 */
	public BufferedImage getImage(String type, String srcPath);
	
	/**
	 * 通过文件ID取指定类型的图片
	 * @param type	图片类型
	 * @param fileId 文件id
	 * @return
	 */
	public BufferedImage getImage(String type,Long fileId) throws Exception;
}
