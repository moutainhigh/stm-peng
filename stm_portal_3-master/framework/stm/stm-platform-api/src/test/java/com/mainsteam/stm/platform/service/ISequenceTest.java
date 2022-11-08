package com.mainsteam.stm.platform.service;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * <li>文件名称: ISequenceTest.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月26日
 * @author   ziwenwen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:META-INF/services/public-*-beans.xml"})
public class ISequenceTest {

	@Resource(name="platformSequence")
	private ISequence seq;
	
	@Resource(name="fileClient")
	private IFileClientApi fileClient;
	
	
	@Test 
	public void testUpload() throws IOException{
		final String filePath="F:\\wiserv\\资源实例.doc";
		File file=new File(filePath);
		
		try {
			long id=fileClient.upLoadFile(file);
			
			System.out.println(id);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		fileClient.deleteFile(7001);
		
//		File f=fileClient.getFileByID(10001);
//		FileModel fm=fileClient.getFileModelByID(10001);

//		fileClient.downloadFile(4001L);
		
//		FileModel fileModel=new FileModel(FileGroupEnum.STM_PUBLIC.toString(), file);
		
//		fileClient.uploadFile(fileModel);
		
	}
	
	@Test
	public void testDownload() throws IOException{
//		String fileID="LedW878nXjZ5bhFX5V/PnSoFvabsVWKVX8M9tghIEOu68KIl7r1WGzDfR1JAh8dEcKTBFA5BZME=";
//		FileInputStream fin=fileClient.downloadFile(fileID);
//		System.out.println(fin.available());
		
	}
	
	
	@Test
	public void testInit(){
		System.out.println(seq.next());
		System.out.println(seq.current());
		System.out.println(seq);
	}
	
	@Test
	public void testNextString() {
		System.out.println(seq.next());
		System.out.println(seq.next());
		System.out.println(seq.next());
	}
}


