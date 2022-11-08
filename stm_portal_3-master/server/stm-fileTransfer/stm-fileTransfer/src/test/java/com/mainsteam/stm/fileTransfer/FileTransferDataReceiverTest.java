package com.mainsteam.stm.fileTransfer;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class FileTransferDataReceiverTest {

	@Autowired FileTransferDataReceiver fileTransferDataReceiver;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	@Test
	public void testDispatch() throws IOException{
		
		byte[] bts="234123423452454".getBytes();
		
		InnerTransferData data=new InnerTransferData();
		data.setCharset("utf-8");
		data.setData(bts);
		data.setFileName("testRecevier.txt");
		data.setFinish(true);
		data.setPoint(0);
		data.setSize(bts.length);
		
		fileTransferDataReceiver.dispatch(data);
	}
}
