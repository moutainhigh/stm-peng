package com.mainsteam.stm.state.ext.process.bean;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/*
缓存告警状态对象，实现了自定义序列化过程有问题，因为缓存是set一次，多次读取，那么就会有WriteObject()一次，readObject()多次的情况发生，
最终将导致OptionalDataException 异常，参见JDK ObjectInputStream说明
 */
public class AlarmStateQueue {

	private static final Log logger = LogFactory.getLog(AlarmStateQueue.class);

	public static final String NON_STRING = "null";
	public static final String FIELD_SEPARATOR = ",";

	private TreeSet<MetricStateEnum> queue; //按告警状态顺序保存状态索引
	private Map<MetricStateEnum, Set<String>> entry; //保存告警状态索引及其对应的id

	public AlarmStateQueue() {
		if(this.queue == null) {
			this.queue = new TreeSet<>(new Comparator<MetricStateEnum>() {
				@Override
				public int compare(MetricStateEnum o1, MetricStateEnum o2) {
					return o1.getStateVal() > o2.getStateVal() ? -1 :(o1.getStateVal() < o2.getStateVal() ? 1 : 0);
				}
			});
		}
		if(this.entry == null) {
			entry = new HashMap<>(3);
		}
	}

	public void add(CompareInstanceState compareInstanceState) {
		MetricStateEnum key = compareInstanceState.getAlarmState();
		this.queue.add(key);
		Set<String> strings = this.entry.get(key);
		if(strings == null){
			strings = new HashSet<>();
			strings.add(compareInstanceState.getId());
			this.entry.put(key, strings);
		}else{
			strings.add(compareInstanceState.getId());
		}
	}

	public CompareInstanceState peek() {
		if(this.queue.isEmpty())
			return null;
		else {
			MetricStateEnum key = this.queue.first();
			Set<String> strings = this.entry.get(key);
			Iterator<String> iterator = strings.iterator();
			String str = null;
			while (iterator.hasNext()) {
				str = iterator.next();
				break;
			}
			CompareInstanceState compareInstanceState = new CompareInstanceState(str, key);
			return compareInstanceState;
		}
	}

	public List<CompareInstanceState> pollAll() {
		if(this.queue.isEmpty())
			return null;
		else {
			List<CompareInstanceState> result = new ArrayList<>();
			while (!this.queue.isEmpty()) {
				MetricStateEnum key = this.queue.pollFirst();
				Set<String> set = this.entry.get(key);
				if(set !=null && !set.isEmpty()){
					Iterator<String> iterator = set.iterator();
					while (iterator.hasNext()) {
						result.add(new CompareInstanceState(iterator.next(), key));
					}
					this.entry.remove(key);
				}
			}
			return result;
		}
	}

	public void replace(CompareInstanceState pre, CompareInstanceState cur) {
		if(pre != null){
			remove(pre);
		}
		if(cur != null) {
			add(cur);
		}
	}

	public boolean remove(CompareInstanceState compareInstanceState) {
		if(!this.queue.isEmpty() && compareInstanceState !=null) {
			MetricStateEnum key = compareInstanceState.getAlarmState();
			Set<String> strings = this.entry.get(key);
			if(strings !=null) {
				strings.remove(compareInstanceState.getId());
				if(strings.isEmpty()){
					this.entry.remove(key);
					return this.queue.remove(key);
				}
			}else{
				return this.queue.remove(key);
			}
		}
		return false;
	}

	public boolean removeBatch(Set<String> keys) {
		if(keys !=null && !keys.isEmpty()) {
			if(this.queue.isEmpty())
				return false;
			Set<Map.Entry<MetricStateEnum, Set<String>>> entries = this.entry.entrySet();
			Iterator<Map.Entry<MetricStateEnum, Set<String>>> iterator = entries.iterator();
			boolean isClear = false;
			while (iterator.hasNext()) {
				Map.Entry<MetricStateEnum, Set<String>> next = iterator.next();
				Set<String> value = next.getValue();
				Iterator<String> keyIterator = value.iterator();
				MetricStateEnum entryKey = next.getKey();
				while (keyIterator.hasNext()) {
					String key = keyIterator.next();
					if(keys.contains(key)) {
						keyIterator.remove();
						if(!isClear)
							isClear = true;
						if(value.isEmpty()) {
							iterator.remove();
							this.queue.remove(entryKey);
							break;
						}
					}
				}
			}
			return isClear;
		}
		return false;
	}

	public void removeAll() {
		this.queue.clear();
		this.entry.clear();
	}


	public String serialize() {
		if(this.queue.isEmpty()) {
			return NON_STRING;
		}else {
			StringBuilder stringBuilder = new StringBuilder();
			Iterator<MetricStateEnum> iterator = this.queue.iterator();
			while (iterator.hasNext()) {
				MetricStateEnum next = iterator.next();
				Set<String> strings = this.entry.get(next);
				if(!strings.isEmpty()) {
					Iterator<String> iterator1 = strings.iterator();
					while (iterator1.hasNext()) {
						String str = iterator1.next();
						stringBuilder.append(str);
						stringBuilder.append(FIELD_SEPARATOR);
						stringBuilder.append(next.getStateVal());
						stringBuilder.append(FIELD_SEPARATOR);
					}
				}
			}
			return stringBuilder.toString();
		}
	}

	/**
	 * 反序列化告警队列
	 * @param priorityQueue
	 * @return
	 */
	public AlarmStateQueue deserialize(String priorityQueue) {
		if(StringUtils.isNotBlank(priorityQueue)) {
			try{
				if(StringUtils.equals(priorityQueue, NON_STRING))
					return this;
				int endIndex = 0;
				int fromIndex = 0;
				int count = 0;
				CompareInstanceState compareInstanceState = null;
				while ((endIndex+1) < priorityQueue.length()) {
					endIndex = priorityQueue.indexOf(FIELD_SEPARATOR, fromIndex);
					String str = priorityQueue.substring(fromIndex, endIndex);
					count++;
					if(StringUtils.isNotBlank(str)) {
						switch (count){
							case 1:
								compareInstanceState = new CompareInstanceState();
								compareInstanceState.setId(str);
								break;
							case 2:
								compareInstanceState.setAlarmState(MetricStateEnum.valueIt(Integer.parseInt(str)));
								break;
						}
					}
					fromIndex = endIndex+1;
					if(count == 2) {
						this.add(compareInstanceState);
						count = 0;
					}
				}
			}catch (Exception e){
				if(logger.isErrorEnabled()){
					logger.error("deserialize alarmStateQueue str error:("+priorityQueue+")" + e.getMessage(), e);
				}
				this.removeAll();
				return this;
			}
		}
		return this;
	}

	public boolean isEmpty() {
		return this.queue.isEmpty();
	}

	@Override
	public String toString() {
		return "AlarmStateQueue{" +
				"queue=" + queue +
				", entry=" + entry +
				'}';
	}
}
