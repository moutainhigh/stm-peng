package com.mainsteam.stm.instancelib.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.license.LicenseModelEnum;

/**
 * <li>文件名称: LicenseUtil</li>
 * <li>公 司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2016年3月18日 上午10:45:08
 * @author zhangjunfeng
 */
public class LicenseUtil {
	private static final Log logger = LogFactory.getLog(LicenseUtil.class);

	private static LicenseUtil licenseUtil = null;
	private Map<LicenseModelEnum, HashSet<String>> licenseKeyCategorys;
	private HashSet<String> categoryIds;
	private Integer storageNum = 0;
	private Lock lock = new ReentrantLock();// 锁对象

	private LicenseUtil() {
		storageNum = 0;
		categoryIds = new HashSet<String>();
		licenseKeyCategorys = new HashMap<LicenseModelEnum, HashSet<String>>();
		HashSet<String> vmHost = new HashSet<String>(3);
		vmHost.add("VirtualHost");
		vmHost.add("XenHosts");
		vmHost.add("FusionComputeHosts");
		licenseKeyCategorys.put(LicenseModelEnum.stmMonitorVmHost, vmHost);
		categoryIds.addAll(vmHost);
		HashSet<String> vmvm = new HashSet<String>(3);
		vmvm.add("VirtualVM");
		vmvm.add("XenVMs");
		vmvm.add("FusionComputeVMs");
		licenseKeyCategorys.put(LicenseModelEnum.stmMonitorVmVm, vmvm);
		categoryIds.addAll(vmvm);
		HashSet<String> hd = new HashSet<String>(3);
		hd.add("Hardware");
		hd.add(CapacityConst.SNMPOTHERS);
		hd.add(CapacityConst.NETWORK_DEVICE);
		licenseKeyCategorys.put(LicenseModelEnum.stmMonitorSh, hd);//stmMonitorHd
		categoryIds.addAll(hd);
		HashSet<String> app = new HashSet<String>(1);
		app.add(CapacityConst.STANDARDSERVICE);
		licenseKeyCategorys.put(LicenseModelEnum.stmMonitorApp, app);
		categoryIds.addAll(app);
		HashSet<String> storage = new HashSet<String>(1);
		storage.add(CapacityConst.STORAGE);
		licenseKeyCategorys.put(LicenseModelEnum.stmMonitorStor, storage);
		categoryIds.addAll(storage);
	}

	public static synchronized LicenseUtil getLicenseUtil() {
		if (licenseUtil == null) {
			licenseUtil = new LicenseUtil();
		}
		return licenseUtil;
	}

	/**
	 * @Title: getCategorysByLicenseKey @Description:通过License
	 * key获取对应的category @param key @return List<String> @throws
	 */
	public HashSet<String> getCategorysByLicenseKey(LicenseModelEnum key) {
		return licenseKeyCategorys.get(key);
	}

	/**
	 * @Title: getUsedCategoryIds @Description: TODO(这里用一句话描述这个方法的作用) @return
	 * List<String> @throws
	 */
	public HashSet<String> getUsedCategoryIds() {
		return this.categoryIds;
	}

	/**
	 * 查询License占用数量
	 * 
	 * @return
	 */
	public int getLicenseStorageNum() {
		int result = 0;
		try {
			lock.lock();
			result = this.storageNum;
		} finally {
			lock.unlock();
		}
		return result;
	}

	/**
	 * 占用License数量
	 * //malachi lic
	 */
	public void addLicenseStorageNum() {
		try {
			lock.lock();
			if (logger.isInfoEnabled()) {
				logger.info("资源入库，开始占用License数量！");
			}
			storageNum++;
		} catch (Exception e) {
			lock.unlock();
		} finally {
			lock.unlock();
		}

	}

	/**
	 * 解除License占用
	 */
	public void deleteLicenseStorageNum() {
		try {
			lock.lock();
			if (logger.isInfoEnabled()) {
				logger.info("资源入库完成，解除License数量占用！");
			}
			storageNum--;
		} catch (Exception e) {
			lock.unlock();
		} finally {
			lock.unlock();
		}
	}

	public void deleleResourceInstanceUpdateLicense(long resourceInstanceId){
		try {
			lock.lock();
			storageNum--;
			if(storageNum < 0){
				storageNum = 0;
			}else{
				if (logger.isInfoEnabled()) {
					logger.info("删除资源 instanceId=" + resourceInstanceId+" 解除License数量占用！");
				}
			}
		} catch (Exception e) {
			lock.unlock();
		} finally {
			lock.unlock();
		}
	}
	
	public void cleanLicense(){
		try {
			lock.lock();
			storageNum = 0;
		} catch (Exception e) {
			lock.unlock();
		} finally {
			lock.unlock();
		}
	}
}
