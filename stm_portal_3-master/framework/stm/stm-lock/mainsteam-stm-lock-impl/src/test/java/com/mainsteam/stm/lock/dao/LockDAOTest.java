package com.mainsteam.stm.lock.dao;

import java.util.Calendar;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.lock.dao.LockDAO;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class LockDAOTest {

	private static final String lock_name = "123222";
	@Autowired LockDAO lockDao;
	
	@Test
	public void testHeabertLock(){
//		lockDao.updateLockHeartbeatTime(lock_name);
	}
}
