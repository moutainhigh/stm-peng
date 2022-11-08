package com.mainsteam.stm.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * <li>文件名称: Util.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public class Util {

	private static final Pattern emptyPattern=Pattern.compile("\\S");
	
	/**
	 * <pre>
	 * 判断对象是否为空
	 * 对象为null、字符串没有非空白字符返回true
	 * </pre>
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj){
		return obj==null||!emptyPattern.matcher(obj.toString()).find();
	}

	/**
	 * 字符串首字母大写
	 * @param str
	 * @return
	 */
	public static String initialUpeercase(String str){
		return String.valueOf(str.charAt(0)).toUpperCase()
				+str.substring(1);
	}
	
	/**
	 * 字符串首字母小写
	 * @param str
	 * @return
	 */
	public static String initialLowercase(String str){
		return String.valueOf(str.charAt(0)).toLowerCase()
				+str.substring(1);
	}
	

	/**
	 * 根据文件路径获取文件内容
	 * @param filePath
	 */
	public static String getFileContent(String filePath,String charset){
		StringBuilder sb=new StringBuilder();
		String str;
		try {
			BufferedReader br=new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath),charset));
			while((str=br.readLine())!=null){
				sb.append(str);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 将一个浮点数转换为百分比 末位四舍五入
	 * @param val 要转换的值
	 * @param decimal 保留的小树位数 不能超过10位
	 * @return
	 */
	public static String toPercent(double val,int decimal){
		long dm=(long)Math.pow(10,decimal);
		long temp=Math.round(dm*100*val);
		//整数部分
		String result=String.valueOf(temp/dm);
		String remain=String.valueOf(temp%dm);
		if(decimal==0){
			return result+IConstant.p_per_;
		}
		int[] prefix=new int[decimal-remain.length()];
		return result+IConstant.p_dot_
				+Arrays.toString(prefix).replace(", ","").substring(1).replace("]","")
				+remain+IConstant.p_per_;
	}
	
	/**
	 * 将一个浮点数转换为百分比 保留两位小数点 末位四舍五入
	 * @param val
	 * @return
	 */
	public static String toPercent(double val){
		return toPercent(val, 2);
	}
	
	/**
	 * 将list集合中的字符转换为字符串，格式用逗号，隔开
	 * @param blank 集合为空时返回的字符
	 */
	public static String toString(Collection<String> list,String blank){
		String str=IConstant.blank_;
		for(String s:list){
			str+=s+IConstant.p_comma_;
		}
		int len=str.length();
		if(len>0){
			return str.substring(0, str.length()-1);
		}
		return blank;
	}
	
	/**
	 * 方法用于获取一个连接的内容
	 * @param url 连接地址
	 * @param charset 字符集,为null时默认为gb2312
	 */
	public static String getUrlContent(String url,String charset){
		if(charset==null)charset=IConstant.charset_gb2312;
		URL url_;
		StringBuilder sb=null;
		BufferedReader br=null;
		try {
			url_ = new URL(url);
			br=new BufferedReader(
					new InputStreamReader(
							url_.openConnection().getInputStream(),charset));
			sb=new StringBuilder(256);
			String str=null;
			while ((str=br.readLine())!=null) {
				sb.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(br!=null)br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb==null?IConstant.blank_:sb.toString();
	}
	
	/**
	 * 转换为字符串列表并用指定的符合分隔
	 * @param arr
	 * @param delimiter
	 * @return
	 */
	public static final String join(Collection<String> arr,String delimiter){
		StringBuilder sb=new StringBuilder(IConstant.blank_);
		for(String str:arr){
			sb.append(str).append(delimiter);
		}
		int len=sb.length()-1;
		if(len<0)return IConstant.blank_;
		sb.deleteCharAt(len);
		return sb.toString();
	}
	
	public static final String long2Ip(long ip) {
	    String sIp = "0.0.0.0";
	    if (!(ip > 4294967295l || ip < 0)) {
	        sIp = (ip >>> 24 & 0xff) + IConstant.p_dot_ 
	        	+ (ip >>> 16 & 0xff) + IConstant.p_dot_ 
	        	+ (ip >>> 8 & 0xff) + IConstant.p_dot_ 
	        	+ (ip & 0xFf);
	    }
	    return sIp;
	}
	
	
	/**
	 * 将IP转换为long
	 * */
	public static final long ip2Long(String ip) {
		ip = ip.trim();
		String[] ips = ip.split("\\.");
		long ipLong = 0l;
		for(int i=0;i<4;i++){
			ipLong = ipLong<<8|Integer.parseInt(ips[i]);
		}
		return ipLong;
	}
	
	/**
	 * 验证是否为正确的IP地址
	 * */
	public static final boolean isIp(String IP) {// 判断是否是一个IP
		boolean b = false;
		IP = IP.trim();
		if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
			String s[] = IP.split("\\.");
			if (Integer.parseInt(s[0]) < 255)
				if (Integer.parseInt(s[1]) < 255)
					if (Integer.parseInt(s[2]) < 255)
						if (Integer.parseInt(s[3]) < 255)
							b = true;
		}
		return b;
	}
}


