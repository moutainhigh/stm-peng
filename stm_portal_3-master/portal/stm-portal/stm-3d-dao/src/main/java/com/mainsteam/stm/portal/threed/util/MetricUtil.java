package com.mainsteam.stm.portal.threed.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.mainsteam.stm.portal.threed.util.jaxb.Metrics;
import com.mainsteam.stm.util.ClassPathUtil;
/**
 * 
 * <li>文件名称: MetricUtil.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月25日
 * @author   liupeng
 */
public class MetricUtil {
	public static Metrics getMetrics() throws Exception{
		String filePath = ClassPathUtil.getCommonClasses()+"config"+File.separator+"3d-metric.xml";
		JAXBContext context = JAXBContext.newInstance(Metrics.class);
		Unmarshaller un = context.createUnmarshaller();
		File file = new File(filePath);
		if(!file.exists() || !file.isFile()) throw new Exception("未找到指标配置文件"+filePath);
		Metrics metrics = (Metrics) un.unmarshal(file);
		return metrics;
	}
	public static void main(String[] args) {
		String filePath = "D:/Servers/apache-tomcat-7.0.52/common/classes/config/metric.xml";
		try {
			Metrics metrics = MetricUtil.getMetrics();
			System.out.println(metrics);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
