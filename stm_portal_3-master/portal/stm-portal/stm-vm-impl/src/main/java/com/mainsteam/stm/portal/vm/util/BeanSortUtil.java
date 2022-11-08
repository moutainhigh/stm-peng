package com.mainsteam.stm.portal.vm.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

public class BeanSortUtil<T> implements Comparator<T> {
	private static Logger logger = Logger.getLogger(BeanSortUtil.class);
	private List<T> list;
	private String sortKey;
	private String order;

	public BeanSortUtil(List<T> list, String sortKey, String order) {
		this.list = list;
		this.sortKey = sortKey;
		this.order = order;
	}

	public int compare(T sortOne, T sortTow) {
		StringBuffer methodName = new StringBuffer();
		methodName.append("get");
		if(sortKey.length() > 1 && sortKey.substring(1, 2).equals(sortKey.substring(1, 2).toUpperCase())){
			methodName.append(sortKey);
		}else{
			methodName.append(sortKey.substring(0, 1).toUpperCase());
			methodName.append(sortKey.substring(1));
		}
		Object keyOne = null;
		Object keyTow = null;
		try {
			keyOne = sortOne.getClass().getMethod(methodName.toString(), null).invoke(sortOne, null);
			keyTow = sortTow.getClass().getMethod(methodName.toString(), null).invoke(sortTow, null);
		} catch (Exception e) {
			logger.error("BeanSortUtil compare:", e);
		}
		return compareOneTow(keyOne, keyTow);
	}
	
	private int compareOneTow(Object keyOne, Object keyTwo){
		if ("ASC".equals(this.order.toUpperCase())){
			if(keyOne == null && keyTwo == null){
				return 0;
			}else if(keyOne == null && keyTwo != null){
				return -1;
			}else if(keyOne != null && keyTwo == null){
				return 1;
			}else{
				String one = keyOne.toString(), two = keyTwo.toString();
				if(keyOne instanceof Long){
					if(Long.valueOf(one) == Long.valueOf(two)){
						return 0;
					}else if(Long.valueOf(one) > Long.valueOf(two)){
						return 1;
					}else{
						return -1;
					}
				}else if(keyOne instanceof Integer){
					if(Integer.valueOf(one) == Integer.valueOf(two)){
						return 0;
					}else if(Integer.valueOf(one) > Integer.valueOf(two)){
						return 1;
					}else{
						return -1;
					}
				}else if(keyOne instanceof String){
					if(keyOne.toString().endsWith("%") && two.endsWith("%")){
						return compareNumWithUnit(one, two, "%", order);
					}else if(one.endsWith("TB") && two.endsWith("TB")){
						return compareNumWithUnit(one, two, "TB", order);
					}else if(one.endsWith("GB") && two.endsWith("GB")){
						return compareNumWithUnit(one, two, "GB", order);
					}else if(one.endsWith("GHz") && two.endsWith("GHz")){
						return compareNumWithUnit(one, two, "GHz", order);
					}else{
						if(one.compareToIgnoreCase(two) == 0){
							return 0;
						}else if(one.compareToIgnoreCase(two) > 0){
							return 1; 
						}else{
							return -1; 
						}
					}
				}
			}
		}else{
			if(keyOne == null && keyTwo == null){
				return 0;
			}else if(keyOne == null && keyTwo != null){
				return 1;
			}else if(keyOne != null && keyTwo == null){
				return -1;
			}else{
				String one = keyOne.toString().trim(), two = keyTwo.toString().trim();
				if(keyOne instanceof Long){
					if(Long.valueOf(one) == Long.valueOf(two)){
						return 0;
					}else if(Long.valueOf(one) > Long.valueOf(two)){
						return -1;
					}else{
						return 1;
					}
				}else if(keyOne instanceof Integer){
					if(Integer.valueOf(one) == Integer.valueOf(two)){
						return 0;
					}else if(Integer.valueOf(one) > Integer.valueOf(two)){
						return -1;
					}else{
						return 1;
					}
				}else if(keyOne instanceof String){
					if(one.endsWith("%") && two.endsWith("%")){
						return compareNumWithUnit(one, two, "%", order);
					}else if(one.endsWith("TB") && two.endsWith("TB")){
						return compareNumWithUnit(one, two, "TB", order);
					}else if(one.endsWith("GB") && two.endsWith("GB")){
						return compareNumWithUnit(one, two, "GB", order);
					}else if(one.endsWith("GHz") && two.endsWith("GHz")){
						return compareNumWithUnit(one, two, "GHz", order);
					}else{
						if(one.compareToIgnoreCase(two) == 0){
							return 0;
						}else if(one.compareToIgnoreCase(two) > 0){
							return -1;
						}else{
							return 1;
						}
					}
				}
			}
		}
		return 0;
	}
	
	private int compareNumWithUnit(String one, String two, String unit, String order){
		if ("ASC".equals(this.order.toUpperCase())){
			if(one.endsWith(unit) && two.endsWith(unit)
					&& one.substring(0, one.length() - unit.length()).matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")
					&& two.substring(0, two.length() - unit.length()).matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")){
				if(Float.valueOf(one.substring(0, one.length() - unit.length())) == Float.valueOf(two.substring(0, two.length()-unit.length()))){
					return 0;
				}else if(Float.valueOf(one.substring(0, one.length()-unit.length())) > Float.valueOf(two.substring(0, two.length()-unit.length()))){
					return 1;
				}else{
					return -1;
				}
			}else{
				if(one.compareToIgnoreCase(two) == 0){
					return 0;
				}else if(one.compareToIgnoreCase(two) > 0){
					return 1; 
				}else{
					return -1; 
				}
			}
		}else{
			if(one.endsWith(unit) && two.endsWith(unit)
					&& one.substring(0, one.length() - unit.length()).matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")
					&& two.substring(0, two.length() - unit.length()).matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")){
				if(Float.valueOf(one.substring(0, one.length() - unit.length())).longValue() == Float.valueOf(two.substring(0, two.length()-unit.length())).longValue()){
					return 0;
				}else if(Float.valueOf(one.substring(0, one.length()-unit.length())).longValue() > Float.valueOf(two.substring(0, two.length()-unit.length())).longValue()){
					return -1;
				}else{
					return 1;
				}
			}else{
				if(one.compareToIgnoreCase(two) == 0){
					return 0;
				}else if(one.compareToIgnoreCase(two) > 0){
					return -1; 
				}else{
					return 1; 
				}
			}
		}
	}

	public List<T> sort() {
		try {
			Collections.sort(this.list, this);
		} catch (Exception e) {
			logger.error("BeanSortUtil sort:", e);
		}
		return this.list;
	}

}
