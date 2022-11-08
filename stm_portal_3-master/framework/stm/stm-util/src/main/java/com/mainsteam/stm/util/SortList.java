package com.mainsteam.stm.util;
/**
 * 
 */

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * <li>文件名称: SortList.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月16日
 * @author   caoyong
 */
public final class SortList {
//	private final static String GET = "get";
	private final static String DESC = "desc";
	private final static String ASC = "asc";
	private final static String COMMA = ",";
	private final static String IP = "ip";
	/**
	 * 
	 * @Method: sort 通用排序：按多个字段，多种方式排序
	 * @Description: TODO
	 * @param @param <T>
	 * @param @param list 要排序的对象
	 * @param @param keys 要排序的字段
	 * @param @param sorts要排序的方式（与key成对出现如：key:"key1,key2";sorts:"sort1,sort2"）
	 * @return void
	 * @throws
	 */
	public static <T> void sort(List<T> list,String keys,String sorts) { 
		final List<Comparator<T>> comparatorList =new ArrayList<Comparator<T>>();
		String[] keysArray = new String[1];
		String[] sortsArray = new String[1];
		if(keys.contains(COMMA)) keysArray = keys.split(COMMA);else keysArray[0] = keys;
		if(sorts.contains(COMMA)) sortsArray = sorts.split(COMMA);else sortsArray[0] = sorts;
		for(int i=0;i<keysArray.length;i++){
			Comparator<T> e = generateAComparator(keysArray[i], sortsArray[i]);
			comparatorList.add(e);
		}
		if (comparatorList.isEmpty()) {
			 throw new IllegalArgumentException("comparatorList is empty"); 
		}
		Comparator<T> comparator = new Comparator<T>() { 
			public int compare(T o1, T o2) {  
				for (Comparator<T> c:comparatorList) { 
					if (c.compare(o1, o2) > 0) {  
						return 1; 
					} else if (c.compare(o1,o2) < 0) { 
						return -1;
					}
				}
				return 0;  
				}  
			};
		Collections.sort(list, comparator);
	}  
	/**
	 * 
	 * @Method: generateAComparator 生成根据某个字段排序的比较器
	 * @Description: TODO
	 * @param @param <T>
	 * @param @param key 要排序的字段
	 * @param @param sort 要排序的方式desc倒序，其它正序
	 * @param @return
	 * @return Comparator<T>
	 * @throws
	 */
	public static<T> Comparator<T> generateAComparator(final String key,final String sort){
		Comparator<T> comparator = new Comparator<T>() {
					
			public int compare(T o1, T o2) {
				int ret = 0;
				double d1 = 0,d2 = 0;
				String s1 = null,s2 = null;Date date1,date2;
				//按字符排序
				try {
					Field f1 = o1.getClass().getDeclaredField(key);
					f1.setAccessible(true);
					Object object1 = f1.get(o1);
					Field f2 = o2.getClass().getDeclaredField(key);
					f2.setAccessible(true);
					Object object2 = f2.get(o2);
					
					if(f1.getType()==String.class && f2.getType()==String.class){//String类型
						if(key.toLowerCase().contains(IP)){
							 if(object1 == null && object2 == null){
	                    		   return 0;
							 }else if(object1 == null && object2 != null){
								 return DESC.equals(sort)?1:-1;
							 }else if(object1 != null && object2 == null){
								 return DESC.equals(sort)?-1:1;
							 }else{
								 if(Util.ip2Long(object1.toString()) == Util.ip2Long(object2.toString())){
									 return 0;
								 }else if(Util.ip2Long(object1.toString()) > Util.ip2Long(object2.toString())){
									 return DESC.equals(sort)?-1:1; 
								 }else{
									 return DESC.equals(sort)?1:-1; 
								 }
                    	   }
						}else{
							//先转换为小写再比较
							s1 = object1.toString().toLowerCase();
							s2 = object2.toString().toLowerCase();
							if(DESC.equals(sort)){//倒序
								ret = s2.compareTo(s1); 
							}else if(ASC.equals(sort)){//正序
								ret = s1.compareTo(s2); 
							}
						}
					}else if((f1.getType()==long.class && f2.getType()==long.class)//long类型
							|| (f1.getType()==Long.class && f2.getType()==Long.class) //Long类型
							|| (f1.getType()==double.class && f2.getType()==double.class) //Double类型
							|| (f1.getType()==Double.class && f2.getType()==Double.class) //Double类型
							|| (f1.getType()==int.class && f2.getType()==int.class) //int类型
							|| (f1.getType()==Integer.class && f2.getType()==Integer.class) //Integer类型
							|| (f1.getType()==float.class && f2.getType()==float.class) //int类型
							|| (f1.getType()==Float.class && f2.getType()==Float.class) //Float类型
							|| (f1.getType()==short.class && f2.getType()==short.class) //short类型
							|| (f1.getType()==Short.class && f2.getType()==Short.class) //Short类型
							){
						d1 = Double.parseDouble(object1.toString());
						d2 = Double.parseDouble(object2.toString());
						if(DESC.equals(sort)){//倒序
							ret = (int)d2 - (int)d1; 
						}else if(ASC.equals(sort)){//正序
							ret = (int)d1 - (int)d2;
						}
					}else if((f1.getType()==java.util.Date.class && f2.getType()==java.util.Date.class)//java.util.Date类型
							|| (f1.getType()==java.sql.Date.class && f2.getType()==java.sql.Date.class)){//java.sql.Date类型
						date1 = (Date) object1;
						date2 = (Date) object2;
						if(DESC.equals(sort)){//倒序
							if(date1.after(date2)){  
								return -1;  
							}else{  
								return 1;  
							}  
						}else if(ASC.equals(sort)){//正序
							if(date2.after(date1)){  
								return -1;  
							}else{  
								return 1;  
							}  
						}
					}
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return ret;
			}
		};
		return comparator;
	} 
}
