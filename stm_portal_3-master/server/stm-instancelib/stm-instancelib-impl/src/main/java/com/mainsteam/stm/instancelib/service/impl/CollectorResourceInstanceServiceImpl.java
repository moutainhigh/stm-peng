package com.mainsteam.stm.instancelib.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.BatchResourceInstanceResult;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.obj.ResourceInstanceResult;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.instancelib.remote.ResourceInstanceSyncMBean;
import com.mainsteam.stm.instancelib.service.bean.ComparerResult;
import com.mainsteam.stm.instancelib.util.CollectorResourceInstanceCache;
import com.mainsteam.stm.instancelib.util.ResourceComparer;
import com.mainsteam.stm.instancelib.util.UniqueInstanceCache;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.persister.ObjectFileDatabase;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.transfer.MetricDataTransferSender;
import com.mainsteam.stm.transfer.obj.TransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;

/**
 * 采集器资源实例实现类
 * 
 * @author xiaoruqiang
 */
public class CollectorResourceInstanceServiceImpl implements
		ResourceInstanceService {

	private static final Log logger = LogFactory
			.getLog(ResourceInstanceService.class);

	CollectorResourceInstanceCache cache;

	private LocaleNodeService localeNodeService;

	public void setCache(CollectorResourceInstanceCache cache) {
		this.cache = cache;
	}

	private OCRPCClient client;

	private Node currentNode;
	
	private ObjectFileDatabase<ResourceInstance> instancePersister;
	
	private ResourceComparer resourceComparer;
	
	private ISequence instanceSeq;
	
	private MetricDataTransferSender metricDataTransferSender;
	
	private CapacityService capacityService;
	
	public void setClient(OCRPCClient client) {
		this.client = client;
	}

	/**
	 * 采集器资源库数据来自处理器 接收到数据放入到采集器缓存中
	 * 
	 * @throws InstancelibException
	 */
	public void init() throws InstancelibException {
		if (System.getProperty("testCase") != null) {
			return;
		}
		try {
			currentNode = localeNodeService.getCurrentNode();
		} catch (NodeException e1) {
			if (logger.isErrorEnabled()) {
				logger.error("Collector method -- localeNodeService.getCurrentNode() is null", e1);
			}
		}
		if (currentNode == null) {
			if (logger.isErrorEnabled()) {
				logger.error("sync to collector data not found currentNode.");
			}
			throw new InstancelibException(1211,"error:node load is null");
		}
		loadResourceInstance();
	}

	private void loadResourceInstance() throws InstancelibException {
		long allStartTime = 0;
		long allEndTime = 0;
		if (logger.isInfoEnabled()) {
			allStartTime = System.currentTimeMillis();
			logger.info("collector load instance start = " + allStartTime);
		}
		try {
			if(currentNode.isIsolated()){
				//隔离墙DCS资源从文件中获取
				List<ResourceInstance> allInstance = loadResourceInstanceFromFile();
				//添加唯一标识到缓存中
				if(CollectionUtils.isNotEmpty(allInstance)){
					for (ResourceInstance resourceInstance : allInstance) {
						UniqueInstanceCache.getInstanceCache().addUnique(resourceInstance);
					}
				}
			}else{
				//非隔离墙DCS资源从DHS远程获取
				loadResourceInstanceFromDHS();
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("collector load data is failed", e);
			}
			throw new InstancelibException(1211,"load data is failed");
		}
		if (logger.isInfoEnabled()) {
			allEndTime = System.currentTimeMillis();
			long diff = allEndTime - allStartTime;
			StringBuilder b = new StringBuilder(100);
			b.append("collector load instance end = ");
			b.append(allEndTime);
			b.append("(");
			b.append(diff / 1000.0);
			b.append(" seconds)");
			logger.info(b);
		}
	}

	private void loadResourceInstanceFromDHS() throws Exception {
		long startTime = 0;
		long endTime = 0;
		if (logger.isDebugEnabled()) {
			startTime = System.currentTimeMillis();
			logger.debug("get ResourceInstanceSyncMBean start = "
					+ startTime);
		}
		System.out.println("malachi the NodeFunc for loadResourceInstanceFromDHS = " + NodeFunc.processer.toString());

		ResourceInstanceSyncMBean resourceInstanceSyncMBean  = client.getParentRemoteSerivce(
				NodeFunc.processer, ResourceInstanceSyncMBean.class);
		if (logger.isDebugEnabled()) {
			endTime = System.currentTimeMillis();
			logger.debug("get ResourceInstanceSyncMBean end = " + endTime);
			long diff = endTime - startTime;
			logger.debug("get ResourceInstanceSyncMBean time = " + diff/ 1000.0 + " second");
		}
		System.out.println("malachi the resourceInstanceSyncMBean for loadResourceInstanceFromDHS = " + resourceInstanceSyncMBean);
		if (resourceInstanceSyncMBean != null) {
			if (logger.isDebugEnabled()) {
				startTime = System.currentTimeMillis();
				logger.debug("collector get data from process start = "+ startTime);
			}
			System.out.println("malachi the currentNode for loadResourceInstanceFromDHS = " + currentNode.getIp() + ":" + currentNode.getPort());
			List<ResourceInstance> processerResourceInstances = resourceInstanceSyncMBean
					.dataSyncToCollector(currentNode.getGroupId());
			System.out.println("malachi in loadResourceInstanceFromDHS 1");
			if (CollectionUtils.isEmpty(processerResourceInstances)) {
				if (logger.isDebugEnabled()) {
					logger.debug("No data is synchronized to the collector!");
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("sync resource instance total size = "
							+ processerResourceInstances.size());
					endTime = System.currentTimeMillis();
					logger.debug("collector get data from process end = "
							+ endTime);
					long diff = endTime - startTime;
					logger.debug("collector get data from process time = "
							+ diff / 1000.0 + " second");
				}

				// 计算缓存话费时间
				if (logger.isDebugEnabled()) {
					startTime = System.currentTimeMillis();
					logger.debug("collector data add to cache start = " + startTime);
				}
				for (ResourceInstance resourceInstance : processerResourceInstances) {
					addToCache(resourceInstance);
				}
				if (logger.isDebugEnabled()) {
					endTime = System.currentTimeMillis();
					logger.debug("collector data add to cache end = "
							+ endTime);
					long diff = endTime - startTime;
					logger.debug("collector data add to cache time = "+ diff / 1000.0 + " second");
				}
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("remote processer MBen not found!");
				throw new InstancelibException(1211,
						"remote processer MBen not found!");
			}
		}
	}

	private List<ResourceInstance> loadResourceInstanceFromFile() throws IOException{
		return instancePersister.loadAllObjects();
	}
	
	// 添加到采集器缓存中
	private void addToCache(ResourceInstance resourceInstance) {
		cache.add(resourceInstance.getId(), resourceInstance);
		if (logger.isInfoEnabled()) {
			logger.info("add to collector cache resourceinstance: id="
					+ resourceInstance.getId() + " name="
					+ resourceInstance.getName() + " discoverWay="
					+ resourceInstance.getDiscoverWay());
			List<DiscoverProp> discoverProps = resourceInstance
					.getDiscoverProps();
			List<ModuleProp> moduleProps = resourceInstance.getModuleProps();
			if (CollectionUtils.isNotEmpty(moduleProps)) {
				for (ModuleProp moduleProp : moduleProps) {
					StringBuilder b = new StringBuilder();
					b.append("ModuleProp: key=").append(moduleProp.getKey());
					b.append(" value=[");
					for (String value : moduleProp.getValues()) {
						b.append(value);
					}
					b.append("]");
					logger.info(b);
				}
			}
			if (CollectionUtils.isNotEmpty(discoverProps)) {
				for (DiscoverProp discoverProp : discoverProps) {
					StringBuilder b = new StringBuilder();
					b.append("DiscoverProp: key=").append(discoverProp.getKey());
					b.append(" value=[");
					for (String value : discoverProp.getValues()) {
						b.append(value);
					}
					b.append("]");
					logger.info(b);
				}
			}
		}
		List<ResourceInstance> childResourceInstances = resourceInstance
				.getChildren();
		if (CollectionUtils.isNotEmpty(childResourceInstances)) {
			for (ResourceInstance child : childResourceInstances) {
				child.setParentInstance(resourceInstance);
				cache.add(child.getId(), child);
			}
		}
	}

	private List<ResourceInstance> loadChildInstanceByParentId(long parentId,boolean isContainDeleted) throws InstancelibException{
		/**
		 * 从缓存获取数据
		 */
		ResourceInstance resourceInstance= getResourceInstance(parentId);
		List<ResourceInstance> result = null;
		if(resourceInstance != null){
			result = resourceInstance.getChildren();
		}
		return result;
	}
	
	private ComparerResult addCalcRepeat(ResourceInstance resourceInstance) throws InstancelibException{
		long start = System.currentTimeMillis();
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("repeat start:");
			b.append(resourceInstance.getName());
			logger.info(b);
		}
		ComparerResult comparerResult = null;
		// 子资源实例验证
		if (resourceInstance.getParentInstance() != null || resourceInstance.getParentId() > 0) {
			/**
			 * 从缓存获取数据,包含已经删除的资源实例
			 */
			List<ResourceInstance> children = loadChildInstanceByParentId(resourceInstance.getParentId(), true);
			if (CollectionUtils.isNotEmpty(children)) {
				// 某个父实例下所有的子资源实例比较
				comparerResult = resourceComparer.childResourceInstanceSameDevice(children, resourceInstance);
			}
		} else {
			CategoryDef category = capacityService
					.getCategoryById(resourceInstance.getCategoryId());
			if(category.getParentCategory() != null){
				resourceInstance.setParentCategoryId(category.getParentCategory().getId());
			}
			HashSet<Long> uniqueResult = UniqueInstanceCache.getInstanceCache().getUnique(resourceInstance);
			if(uniqueResult != null){
				//如果是已经删除的，不需要告知重复
				comparerResult = new ComparerResult();
				List<Long> repeatIds = new ArrayList<>();
				for (long id : uniqueResult) {
					ResourceInstance tempInstance = getResourceInstance(id);
					if(tempInstance == null){
						continue;
					}
					comparerResult.setInstanceId(id);
					comparerResult.setLifeState(tempInstance.getLifeState());
					if (InstanceLifeStateEnum.NOT_MONITORED == tempInstance.getLifeState()
							|| InstanceLifeStateEnum.MONITORED == tempInstance.getLifeState()) {
						repeatIds.add(tempInstance.getId());
					}
					if (logger.isInfoEnabled()) {
						StringBuilder b = new StringBuilder(100);
						b.append("find instance ");
						b.append("oldInstanceId:").append(tempInstance.getId());
						b.append(" oldshowIP:").append(tempInstance.getShowIP());
					    b.append(" oldInstanceName:").append(tempInstance.getName());
						b.append(" newShowIP:").append(resourceInstance.getShowIP());
					    b.append(" newInstanceName:").append(resourceInstance.getName());
					    logger.info(b);
					}
				}
				comparerResult.setRepeatIds(repeatIds);
				return comparerResult;
			}
			// 验证父实例
			comparerResult = parentInstanceValidate(resourceInstance);
		}
		long end = System.currentTimeMillis();
		long diff = end - start;
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("repeat end:");
			b.append(resourceInstance.getName());
			b.append("time:").append(diff / 1000.0).append(" second");
			logger.info(b);
		}
		return comparerResult;
	}
	
	
	private ComparerResult parentInstanceValidate(ResourceInstance validateInstance) throws InstancelibException {
		ComparerResult comparerResult = resourceComparer.isSameDevice(validateInstance);
		if(comparerResult != null){
			List<Long> repeatIds = new ArrayList<>();
			for (long tempInstanceId : comparerResult.getRepeatIds()) {
				ResourceInstance tempInstance = getResourceInstance(tempInstanceId);
				if(tempInstance == null){
					continue;
				}
				comparerResult.setInstanceId(tempInstanceId);
				comparerResult.setLifeState(tempInstance.getLifeState());
				if (InstanceLifeStateEnum.NOT_MONITORED == tempInstance.getLifeState()
						|| InstanceLifeStateEnum.MONITORED == tempInstance.getLifeState()) {
					repeatIds.add(tempInstance.getId());
				}
			}
			comparerResult.setRepeatIds(repeatIds);
		}
		if(comparerResult == null){
			comparerResult = new ComparerResult();
		}
		return comparerResult;
	}
	

	/**
	 * 隔离墙DCS使用
	 * @throws Exception 
	 */
	private ResourceInstanceResult addResourceInstanceToFile(ResourceInstance resourceInstance) throws Exception{
		/*
		 * 1：判断重复
		 * 2：资源实例上次到DHS
		 */
		/*
		 * 验证资源实例是否重复
		 */
		ResourceInstanceResult result = null;
		if (resourceInstance.isRepeatValidate()) {
			ComparerResult comparerResult = null;
			try {
				comparerResult = addCalcRepeat(resourceInstance);
			} catch (InstancelibException e) {
				if(logger.isErrorEnabled()){
					logger.error("",e);
					throw e;
				}
			} catch(Exception e){
				if(logger.isErrorEnabled()){
					logger.error("",e);
				}
			}
			if(comparerResult != null){
				if (comparerResult.getInstanceId() != 0) {
					//再次发现时，使用刷新操作。
					if (comparerResult.getLifeState() == InstanceLifeStateEnum.DELETED || comparerResult.getLifeState() == InstanceLifeStateEnum.INITIALIZE) {
						result = new ResourceInstanceResult(comparerResult.getInstanceId(), false,comparerResult.getRepeatIds());
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("add resourceInstance repeat:"
									+ resourceInstance.getName());
						}
						result = new ResourceInstanceResult(comparerResult.getInstanceId(), true,comparerResult.getRepeatIds());
					}
					resourceInstance.setId(comparerResult.getInstanceId());
					refreshResourceInstance(resourceInstance);
					return result;
				}
			}
		}
		long parentId = instanceSeq.next();
		resourceInstance.setId(parentId);
		//生成Id
		List<ResourceInstance> tempChildren = resourceInstance.getChildren();
		if(CollectionUtils.isNotEmpty(tempChildren)){
			for (ResourceInstance child : tempChildren) {
				child.setParentId(parentId);
				child.setId(instanceSeq.next());
			}
		}
		try {
			if (resourceInstance.getParentId() <= 0) {
				/**
				 * 父资源
				 */
				//写入文件,添加到缓存
				instancePersister.saveObject(String.valueOf(parentId), resourceInstance);
				cache.add(parentId, resourceInstance);
				//添加唯一标识
				UniqueInstanceCache.getInstanceCache().addUnique(resourceInstance);
				if (logger.isDebugEnabled()) {
					logger.debug("add to collector instanceId="
							+ resourceInstance.getId() + " instanceName "
							+ resourceInstance.getName());
				}
				//添加子实例
				List<ResourceInstance> children = resourceInstance.getChildren();
				if (CollectionUtils.isNotEmpty(children)) {
					for (ResourceInstance child : children) {
						child.setParentId(parentId);
						child.setParentInstance(resourceInstance);
						cache.add(child.getId(), child);
					}
				} 
			}else{
				/**
				 * 子资源
				 */
				ResourceInstance parentInstance = cache.get(resourceInstance.getParentId());
				if (parentInstance != null) {
					boolean find = false;
					List<ResourceInstance> children = parentInstance.getChildren();
					if (children != null) {
						for (ResourceInstance ch : children) {
							if (ch.getId() == resourceInstance.getId()) {
								find = true;
								break;
							}
						}
					} else {
						//创建空集合
						parentInstance.setChildren(new ArrayList<ResourceInstance>());
					}
					if (find) {
						if (logger.isWarnEnabled()) {
							logger.warn("addResourceInstance instance has exist.id="
									+ resourceInstance.getId());
						}
					} else {
						//父添加子,加入缓存(跟缓存同一对象，不需要单独添加一次)
						parentInstance.getChildren().add(resourceInstance);
						resourceInstance.setParentInstance(parentInstance);
						//写入父资源文件
						instancePersister.saveObject(String.valueOf(resourceInstance.getParentId()), parentInstance);
						//子资源加入缓存
						cache.add(resourceInstance.getId(), resourceInstance);
						if (logger.isDebugEnabled()) {
							logger.debug("add to collector instanceId="
									+ resourceInstance.getId()
									+ " instanceName "
									+ resourceInstance.getName());
						}
					}
				} else {
					if (logger.isWarnEnabled()) {
						StringBuilder b = new StringBuilder(
								"child instance has no parent was found.");
						b.append(" instanceId=").append(
								resourceInstance.getId());
						b.append(" parentid=").append(
								resourceInstance.getParentId());
						logger.warn(b.toString());
					}
				}
			}
			//数据上传
			TransferData data = new TransferData();
			//资源实例
			data.setData(resourceInstance);
			//需要添加 资源类型枚举
			data.setDataType(TransferDataType.ResourceInstance);
			//
			metricDataTransferSender.sendData(data);
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("addResourceInstance", e);
			}
		}
		return result;
	}
	
	/**
	 * 非隔离墙DCS使用
	 */
	private void addResourceInstanceFromDHS(ResourceInstance resourceInstance){
		if (resourceInstance.getParentId() <= 0) {
			cache.add(resourceInstance.getId(), resourceInstance);
			if (logger.isDebugEnabled()) {
				StringBuilder str = new StringBuilder(100);
				str.append("add to collector instanceId=");
				str.append(resourceInstance.getId());
				str.append(" instanceName ");
				str.append(resourceInstance.getName());
				logger.debug(str);
			}
		}
		// 如果是父实例，添加子实例
		List<ResourceInstance> children = resourceInstance.getChildren();
		if (CollectionUtils.isNotEmpty(children)) {
			for (ResourceInstance child : children) {
				child.setParentId(resourceInstance.getId());
				child.setParentInstance(resourceInstance);
				cache.add(child.getId(), child);
			}
		} else if (resourceInstance.getParentId() != 0) {
			// 子实例
			ResourceInstance parentInstance = cache.get(resourceInstance
					.getParentId());
			if (parentInstance != null) {
				boolean find = false;
				children = parentInstance.getChildren();
				if (children != null) {
					for (ResourceInstance ch : children) {
						if (ch.getId() == resourceInstance.getId()) {
							find = true;
							break;
						}
					}
				} else {
					parentInstance
							.setChildren(new ArrayList<ResourceInstance>());
				}
				if (find) {
					if (logger.isWarnEnabled()) {
						logger.warn("addResourceInstance instance has exist.id="
								+ resourceInstance.getId());
					}
				} else {
					parentInstance.getChildren().add(resourceInstance);
					resourceInstance.setParentInstance(parentInstance);
					cache.add(resourceInstance.getId(), resourceInstance);
					if (logger.isDebugEnabled()) {
						logger.debug("add to collector instanceId="
								+ resourceInstance.getId()
								+ " instanceName "
								+ resourceInstance.getName());
					}
				}
			} else {
				if (logger.isWarnEnabled()) {
					StringBuilder b = new StringBuilder(
							"child instance has no parent was found.");
					b.append(" instanceId=").append(
							resourceInstance.getId());
					b.append(" parentid=").append(
							resourceInstance.getParentId());
					logger.warn(b.toString());
				}
			}
		}
	}
	
	
	@Override
	public ResourceInstanceResult addResourceInstance(ResourceInstance resourceInstance)
			throws InstancelibException {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("collector addResourceInstance start instanceId=").append(
					resourceInstance.getId());
			b.append(" instanceName=").append(resourceInstance.getName());
			b.append(" parentid=").append(resourceInstance.getParentId());
			logger.info(b);
		}
		ResourceInstanceResult result = null;
		try {
			if(currentNode.isIsolated()){
				result = addResourceInstanceToFile(resourceInstance);
			}else{
				addResourceInstanceFromDHS(resourceInstance);
			}
			if(result == null){
				result = new ResourceInstanceResult(resourceInstance.getId(),false,null);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addResourceInstance is failed", e);
			}
			throw new InstancelibException(1211,"addResourceInstance failed");
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("collector addResourceInstance end instanceId=")
					.append(resourceInstance.getId());
			b.append(" instanceName ").append(resourceInstance.getName());
			logger.info(b);
		}
		return result;
	}

	@Override
	public Map<InstanceLifeStateEnum,List<Long>> refreshResourceInstance(ResourceInstance resourceInstance)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("refreshResourceInstance start instanceId="
					+ resourceInstance.getId());
		}
		refresh(resourceInstance);
		if (logger.isTraceEnabled()) {
			logger.trace("refreshResourceInstance  end");
		}
		return null;
	}
	
	@Override
	public Map<InstanceLifeStateEnum,List<Long>> refreshResourceInstance(ResourceInstance resourceInstance,boolean isAutoDeleteChildInstance)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("refreshResourceInstance start instanceId="
					+ resourceInstance.getId());
		}
		refresh(resourceInstance);
		if (logger.isTraceEnabled()) {
			logger.trace("refreshResourceInstance  end");
		}
		return null;
	}

	private void refresh(ResourceInstance newResourceInstance) throws InstancelibException{
		ResourceInstance old = cache.get(newResourceInstance.getId());
		if(old != null){
			if(old.getParentId() <= 0){
				//父
				List<ResourceInstance> newChildren = newResourceInstance.getChildren();
				HashSet<Long> newChildId = new HashSet<>(200);
				if(CollectionUtils.isNotEmpty(newChildren)){
					for (ResourceInstance childResourceInstance : newChildren) {
						newChildId.add(childResourceInstance.getId());
					}
				}
				List<ResourceInstance> oldChildren = old.getChildren();
				HashSet<Long> oldChildId = new HashSet<>(200);
				if(CollectionUtils.isNotEmpty(oldChildren)){
					for (ResourceInstance childResourceInstance : oldChildren) {
						oldChildId.add(childResourceInstance.getId());
					}
				}
				//取交集
				Collection<?> intersection = CollectionUtils.intersection(newChildId, oldChildId);
				
				//取现在缓存中的子资源在新的资源中没有的
				oldChildId.removeAll(intersection);
				
				//缓存中有的子资添加到新的资源资源的子列表中
				for (long oldInstancrId : oldChildId) {
					ResourceInstance oldR = cache.get(oldInstancrId);
					if(oldR != null && newChildren != null){
						newChildren.add(oldR);
					}
				}
			}
			// 先删除再放入到缓存
			cache.remove(newResourceInstance.getId());
		}
		addResourceInstance(newResourceInstance);
	}
	
	@Override
	public void updateResourceInstanceState(
			Map<Long, InstanceLifeStateEnum> states)
			throws InstancelibException {
		// if (logger.isTraceEnabled()) {
		// logger.trace("updateResourceInstanceState start ");
		// }
		// // 更新缓存
		// for (Entry<Long, InstanceLifeStateEnum> state : states.entrySet()) {
		// ResourceInstance cacheInstance = cache.get(state.getKey());
		// if (cacheInstance != null) {
		// cacheInstance.setLifeState(state.getValue());
		// }
		// }
		// if (logger.isTraceEnabled()) {
		// logger.trace("updateResourceInstanceState end");
		// }
	}
	
	@Override
	public void updateResourceInstanceDomain(Map<Long, Long> domainIds)
			throws InstancelibException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
	}
	
	@Override
	public void updateResourceInstanceName(long instanceId, String name)
			throws InstancelibException {
//		if (logger.isTraceEnabled()) {
//			logger.trace("updateResourceInstanceName start name=" + name);
//		}
//		// 更新缓存
//		ResourceInstance cacheInstance = cache.get(instanceId);
//		if (cacheInstance != null) {
//			cacheInstance.setName(name);
//		}
//		if (logger.isTraceEnabled()) {
//			logger.trace("updateResourceInstanceState end ");
//		}
		
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
	}

	@Override
	public void updateResourceInstanceShowIP(long instanceId,
			String showIP) throws InstancelibException {
//		if (logger.isTraceEnabled()) {
//			logger.trace("updateResourceInstanceName start showIP="
//					+ showIP);
//		}
//		// 更新缓存
//		ResourceInstance cacheInstance = cache.get(instanceId);
//		if (cacheInstance != null) {
//			cacheInstance.setShowIP(showIP);
//		}
//		if (logger.isTraceEnabled()) {
//			logger.trace("updateResourceInstanceState end showIP="
//					+ showIP);
//		}
		
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
	}

	@Override
	public void updateResourceInstanceDiscoverNode(long instanceId,
			String discoverNode) throws InstancelibException {
		// if (logger.isTraceEnabled()) {
		// logger.trace("updateResourceInstanceName start discoverNode="
		// + discoverNode);
		// }
		// // 更新缓存
		// ResourceInstance cacheInstance = cache.get(instanceId);
		// if (cacheInstance != null) {
		// cacheInstance.setDiscoverNode(discoverNode);
		// }
		// if (logger.isTraceEnabled()) {
		// logger.trace("updateResourceInstanceState end discoverNode="
		// + discoverNode);
		// }
		
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
	}

	@Override
	public void removeResourceInstance(long instanceId)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeResourceInstance start instanceId="
					+ instanceId);
		}
		ResourceInstance deletedInstance = cache.get(instanceId);
		cache.remove(instanceId);
		if(deletedInstance != null){
			UniqueInstanceCache.getInstanceCache().removeUnique(deletedInstance);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removeResourceInstance end");
		}
	}

	@Override
	public void removeResourceInstances(List<Long> instanceIds)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeResourceInstances start instanceId="
					+ instanceIds);
		}
		for (long instanceId : instanceIds) {
			ResourceInstance deletedInstance = cache.get(instanceId);
			cache.remove(instanceId);
			if(deletedInstance != null){
				UniqueInstanceCache.getInstanceCache().removeUnique(deletedInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removeResourceInstances end");
		}
	}

	@Override
	public ResourceInstance getResourceInstance(long instanceId)
			throws InstancelibException {
		if (logger.isDebugEnabled()) {
			logger.debug("getResourceInstance start instanceId=" + instanceId);
		}
		// 获取缓存
		ResourceInstance resourceInstance = cache.get(instanceId);
		if (logger.isDebugEnabled()) {
			logger.debug("getResourceInstance end");
		}
		return resourceInstance;
	}

	@Override
	public List<ResourceInstance> getAllParentInstance()
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getAllParentInstance start ");
		}
		List<ResourceInstance> result = cache.getParentInstances();
		if (logger.isTraceEnabled()) {
			logger.trace("getAllParentInstance end");
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getParentInstanceByCategoryId(
			String categoryId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentInstanceByCategoryId start categoryId="
					+ categoryId);
		}
		List<ResourceInstance> result = new ArrayList<>();
		List<ResourceInstance> allIdList = cache.getParentInstances();
		for (ResourceInstance resourceInstance : allIdList) {
			if (categoryId.equals(resourceInstance.getCategoryId())) {
				result.add(resourceInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentInstanceByCategoryId end");
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getParentInstanceByCategoryIds(
			HashSet<String> categoryIds) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentInstanceByCategoryIds start categoryId="
					+ categoryIds);
		}
		List<ResourceInstance> result = new ArrayList<>();
		List<ResourceInstance> allIdList = cache.getParentInstances();
		for (ResourceInstance resourceInstance : allIdList) {
			if (categoryIds.contains(resourceInstance.getCategoryId())) {
				result.add(resourceInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentInstanceByCategoryIds end");
		}
		return result;
	}
	
	@Override
	public List<ResourceInstance> getResourceInstanceByResourceId(
			String resourceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentInstanceByResourceId start resourceId"
					+ resourceId);
		}
		List<ResourceInstance> result = new ArrayList<>();
		// 查询所有的父实例
		List<ResourceInstance> allIdList = cache.getParentInstances();
		for (ResourceInstance resourceInstance : allIdList) {
			if (resourceId.equals(resourceInstance.getResourceId())) {
				result.add(resourceInstance);
			}
			// 查询所有的子实例
			List<ResourceInstance> children = resourceInstance.getChildren();
			if (children != null && !children.isEmpty()) {
				for (ResourceInstance child : children) {
					if (resourceId.equals(child.getResourceId())) {
						result.add(resourceInstance);
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentInstanceByResourceId end");
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getResourceInstanceByResourceId(
			HashSet<String> resourceIds) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceByResourceId start resourceIds"
					+ resourceIds);
		}
		List<ResourceInstance> result = new ArrayList<ResourceInstance>();
		// 查询所有的父实例
		List<ResourceInstance> allIdList = cache.getParentInstances();
		for (ResourceInstance resourceInstance : allIdList) {
			if (resourceIds.contains(resourceInstance.getResourceId())) {
				result.add(resourceInstance);
			}
			// 查询所有的子实例
			List<ResourceInstance> children = resourceInstance.getChildren();
			if (children != null) {
				for (ResourceInstance child : children) {
					if (resourceIds.contains(child.getResourceId())) {
						result.add(child);
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceByResourceId end");
		}
		return result;
	}
	
	@Override
	public List<ResourceInstance> getChildInstanceByParentId(long parentId)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getChildInstanceByParentId start parentId" + parentId);
		}
		List<ResourceInstance> result = null;
		List<ResourceInstance> allIdList = cache.getParentInstances();
		for (ResourceInstance resourceInstance : allIdList) {
			if (parentId == resourceInstance.getParentId()) {
				result = resourceInstance.getChildren();
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getChildInstanceByParentId end");
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getParentResourceInstanceByLifeState(
			InstanceLifeStateEnum lifeState) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByLifeState start lifeState"
					+ lifeState.toString());
		}

		List<ResourceInstance> result = new ArrayList<>();

		List<ResourceInstance> parentResourceInstances = cache
				.getParentInstances();
		for (ResourceInstance resourceInstance : parentResourceInstances) {
			if (lifeState == resourceInstance.getLifeState()) {
				result.add(resourceInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByLifeState end lifeState"
					+ lifeState.toString());
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getParentResourceInstanceByNode(
			String groudNodeId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByNode start groudNodeId"
					+ groudNodeId);
		}
		List<ResourceInstance> result = new ArrayList<>();
		List<ResourceInstance> parentResourceInstances = cache
				.getParentInstances();
		for (ResourceInstance resourceInstance : parentResourceInstances) {
			if (groudNodeId.equals(resourceInstance.getDiscoverNode())) {
				result.add(resourceInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByNode end groudNodeId"
					+ groudNodeId);
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getParentResourceInstanceByDomainIds(
			Set<Long> domainIds) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByDomainIds start domainId"
					+ domainIds);
		}
		List<ResourceInstance> result = new ArrayList<>();
		List<ResourceInstance> parentResourceInstances = cache
				.getParentInstances();
		for (ResourceInstance resourceInstance : parentResourceInstances) {
			if (domainIds.contains(resourceInstance.getDomainId())) {
				result.add(resourceInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByDomain end domainId"
					+ domainIds);
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getParentResourceInstanceByDiscoverWay(
			DiscoverWayEnum way) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByDomainIds start discoverWayEnum"
					+ way);
		}
		List<ResourceInstance> result = new ArrayList<>();
		List<ResourceInstance> parentResourceInstances = cache
				.getParentInstances();
		for (ResourceInstance resourceInstance : parentResourceInstances) {
			if (way == resourceInstance.getDiscoverWay()) {
				result.add(resourceInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByDomainIds end discoverWayEnum"
					+ way);
		}
		return result;
	}
	
	@Override
	public List<ResourceInstance> getResourceInstances(List<Long> instanceIds)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstance start instanceId=" + instanceIds);
		}
		List<ResourceInstance> result = new ArrayList<ResourceInstance>(
				instanceIds.size());
		for (long instanceId : instanceIds) {
			// 获取缓存
			ResourceInstance resourceInstance = cache.get(instanceId);
			if (resourceInstance != null) {
				result.add(resourceInstance);
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("ResourceInstance not found,instanceId="
							+ instanceId);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstance end");
		}
		return result;
	}

	public void setLocaleNodeService(LocaleNodeService localeNodeService) {
		this.localeNodeService = localeNodeService;
	}

	/*
	 * 链路的数据只是传输到DHS. DCS不做链路资源入库，只是发现完成后展示.
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#addResourceInstanceForLink(java.util.List, boolean)
	 */
	@Override
	public void addResourceInstanceForLink(
			List<ResourceInstance> resourceInstances, boolean isAll)
			throws InstancelibException {
		if (logger.isInfoEnabled()) {
			logger.info("addResourceInstance for link start");
		}
		if(resourceInstances == null  || resourceInstances.isEmpty()){
			if(logger.isErrorEnabled()){
				logger.error("addResourceInstance for link null");
			}
			return;
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("addResourceInstance for link start ");
			b.append(" size=").append(resourceInstances.size());
			logger.info(b);
			logger.info("data transfer......");
		}
		TransferData data = new TransferData();
		//资源实例
		data.setData(resourceInstances);
		//需要添加 资源类型枚举
		data.setDataType(TransferDataType.ResourceInstancesForLink);
		//发送数据到DHS
		metricDataTransferSender.sendData(data);
		if (logger.isInfoEnabled()) {
			logger.info("addResourceInstance for link end");
		}
	}

	@Override
	public void removeResourceInstanceByLinks(List<Long> deleteIds)
			throws InstancelibException {
		//do nothing
	}

	@Override
	public List<Long> getAllChildrenInstanceIdbyParentId(Set<Long> parentIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getAllChildrenInstanceIdbyParentId(Set<Long> parentIds,
			InstanceLifeStateEnum state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResourceInstance> getAllResourceInstancesForLink() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeChildResourceInstance(long arg0) throws InstancelibException {
		removeResourceInstance(arg0);
		
	}

	@Override
	public void removeChildResourceInstance(List<Long> arg0) throws InstancelibException {
		removeResourceInstances(arg0);
		//do nothing
	}

	@Override
	public List<ResourceInstance> getParentInstanceByCategoryIdsFordb(Set<String> categoryIds)
			throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkLicense(ResourceInstance resourceInstance) throws InstancelibException {
		//do nothing
		return false;
	}

	@Override
	public List<ResourceInstance> getChildInstanceByParentId(long parentId, boolean isAll) throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceInstance getResourceInstanceWithDeleted(long instanceId) throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;
	}

	public void setInstancePersister(
			ObjectFileDatabase<ResourceInstance> instancePersister) {
		this.instancePersister = instancePersister;
	}

	public void setResourceComparer(ResourceComparer resourceComparer) {
		this.resourceComparer = resourceComparer;
	}

	public void setInstanceSeq(ISequence instanceSeq) {
		this.instanceSeq = instanceSeq;
	}

	public void setMetricDataTransferSender(
			MetricDataTransferSender metricDataTransferSender) {
		this.metricDataTransferSender = metricDataTransferSender;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	@Override
	public BatchResourceInstanceResult addResourceInstances(
			List<ResourceInstance> resourceInstances)
			throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}
}
