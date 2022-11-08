package com.mainsteam.stm.executor.util;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.executor.exception.MetricExecutorException;

/**
 * @author ziw
 */
public class EncryptUtil {

	private SecretKeySpec key;

	public void setKey(String pwd) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128, new SecureRandom(pwd.getBytes()));
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		key = new SecretKeySpec(enCodeFormat, "AES");
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 */
	public String encrypt(String content) throws MetricExecutorException {
		byte[] byteContent;
		byte[] result = null;
		try {
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			result = cipher.doFinal(byteContent);// 加密
		} catch (Exception e) {
			throw new MetricExecutorException(
					ServerErrorCodeConstant.ERR_SERVER_UNKOWN_ERROR, e);
		}
		return parseByte2HexStr(result);
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		EncryptUtil leadUtil = new EncryptUtil();
		leadUtil.setKey("adfasfesefgthyju");
		String mingWen = "password";
		String encode = leadUtil.encrypt(mingWen);
		System.out.println(encode);
		encode = leadUtil.encrypt(mingWen);
		System.out.println(encode);
	}
}
