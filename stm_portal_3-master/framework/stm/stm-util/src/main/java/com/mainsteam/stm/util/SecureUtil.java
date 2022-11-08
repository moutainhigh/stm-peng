package com.mainsteam.stm.util;
/**
 * 加密，解密类
 * @author xiaoruqiang
 *
 */
public class SecureUtil {
	
	private static String PROP_PWD_KEY  = "prop_pwd";
	
	private static String pwdRealKey;
	
	private static String pwdKey = "password";
	
	/**
	 * md5加密
	 * @param data
	 * @return
	 */
	public static String md5Encryp(String data){
		return CipherUtil.MD5Encode(data);
	}
	/**
	 * 发现属性密码加密
	 * @param data 
	 * @return
	 */
	public static String pwdEncrypt(String data){
		if(pwdRealKey == null){
			calcPwdRealKey();
		}
		return CipherUtil.DESEncrypt(data, pwdRealKey);
	}
	/**
	 * 发现属性密码解密
	 * @param data 
	 * @return
	 */
	public static String pwdDecrypt(String data){
		if(pwdRealKey == null){
			calcPwdRealKey();
		}
		return CipherUtil.DESDecrypt(data, pwdRealKey);
	}
	
	private static void calcPwdRealKey(){
//		pwdRealKey = CipherUtil.generateDESKey(PROP_PWD_KEY);
		pwdRealKey="x0NYFX/I7/s=";
	}
	/**
	*  验证发现属性是否需要加密，属性包含password 的都需要加密解密
	*/
	public static boolean isPassswordKey(String key){
		return key != null && (key.toLowerCase().contains(pwdKey) || key.toLowerCase().equals("community"));
	}
}
