package com.mainsteam.stm.capbase;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

/*
 * AES对称加密算法
 * 
 * @see 
 *      ==========================================================================
 * @see 这里演示的是其Java6.0的实现,理所当然的BouncyCastle也支持AES对称加密算法
 * @see 另外,我们也可以以AES算法实现为参考,完成RC2,RC4和Blowfish算法的实现
 * @see 
 *      ==========================================================================
 * @see 由于DES的不安全性以及DESede算法的低效,于是催生了AES算法(Advanced Encryption Standard)
 * @see 该算法比DES要快,安全性高,密钥建立时间短,灵敏性好,内存需求低,在各个领域应用广泛
 * @see 目前,AES算法通常用于移动通信系统以及一些软件的安全外壳,还有一些无线路由器中也是用AES算法构建加密协议
 * @see 
 *      ==========================================================================
 * @see 由于Java6.0支持大部分的算法,但受到出口限制,其密钥长度不能满足需求
 * @see 所以特别需要注意的是:如果使用256位的密钥,则需要无政策限制文件(Unlimited Strength Jurisdiction Policy
 *      Files)
 * @see 不过Sun是通过权限文件local_poblicy.jar和US_export_policy.jar做的相应限制
 *      ,我们可以在Sun官网下载替换文件,减少相关限制
 * @see 网址为http://www.oracle.com/technetwork/java/javase/downloads/index.html
 * @see 在该页面的最下方找到Java Cryptography Extension (JCE) Unlimited Strength
 *      Jurisdiction Policy Files 6,点击下载
 * @see http://download.oracle.com/otn-pub/java/jce_policy/6/jce_policy-6.zip
 * @see http://download.oracle.com/otn-pub/java/jce/7/UnlimitedJCEPolicyJDK7.zip
 * @see 然后覆盖本地JDK目录和JRE目录下的security目录下的文件即可
 * @see 
 *      ==========================================================================
 * @see 关于AES的更多详细介绍
 *      ,可以参考此爷的博客http://blog.csdn.net/kongqz/article/category/800296
 * @create Jul 17, 2012 6:35:36 PM
 * @author 玄玉(http://blog.csdn/net/jadyer)
 */
public class TextUtil {
	// 密钥算法
	public static final String KEY_ALGORITHM = "AES";
	private static final String ENDECODER_KEY = "Si0F0Vqb3AS/3xnF5OtrgQ==";

	// 加解密算法/工作模式/填充方式,Java6.0支持PKCS5Padding填充方式,BouncyCastle支持PKCS7Padding填充方式
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
	
	public static final String ISO_CHARSET = "ISO-8859-1";
	
	public static final String UTF_CHARSET = "UTF-8";

	/**
	 * 生成密钥
	 */
	public static String initkey() {
		try {
			KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM); // 实例化密钥生成器
			kg.init(128); // 初始化密钥生成器:AES要求密钥长度为128,192,256位
			SecretKey secretKey = kg.generateKey(); // 生成密钥
			return Base64.encodeBase64String(secretKey.getEncoded()); // 获取二进制密钥编码形式
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return ENDECODER_KEY;
	}

	/**
	 * 转换密钥
	 */
	public static Key toKey(byte[] key) {
		return new SecretKeySpec(key, KEY_ALGORITHM);
	}

	/**
	 * 加密数据
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return 加密后的数据
	 * */
	public static String encrypt1(String data, String key) {
		try {
			Key k = toKey(Base64.decodeBase64(key)); // 还原密钥
			// 使用PKCS7Padding填充方式,这里就得这么写了(即调用BouncyCastle组件实现)
			// Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM); // 实例化Cipher对象，它用于完成实际的加密操作
			cipher.init(Cipher.ENCRYPT_MODE, k); // 初始化Cipher对象，设置为加密模式
			return Base64.encodeBase64String(cipher.doFinal(data.getBytes())); // 执行加密操作。加密后的结果通常都会用Base64编码进行传输
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	

	/**
	 * 解密数据
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @return 解密后的数据
	 * */
	public static String decrypt1(String data, String key) {
		try {
			Key k = toKey(Base64.decodeBase64(key));
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, k); // 初始化Cipher对象，设置为解密模式
			return new String(cipher.doFinal(Base64.decodeBase64(data))); // 执行解密操作
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt2(String key) {
		if(StringUtils.isEmpty(key)){
			return "";
		}
		Base64 b64 = new Base64();
		byte[] bytes = null;
		try {
			bytes = b64.decode(key.getBytes(ISO_CHARSET));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			bytes = b64.decode(key.getBytes());
			e.printStackTrace();
		}
		try {
			return new String(bytes, UTF_CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(bytes);
	}

	/**
	 * BASE64加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt2(String key) {
		Base64 b64 = new Base64();
		byte[] bytes;
		try {
			bytes = b64.encode(key.getBytes(ISO_CHARSET));
		} catch (UnsupportedEncodingException e) {
			bytes = b64.encode(key.getBytes());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return new String(bytes, UTF_CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(bytes);
	}

	public static String encrypt(String source, String key) {
		return encrypt2(encrypt1(source, key));
	}

	public static String decrypt(String source, String key) {
		return decrypt1(decrypt2(source), key);
	}

	public static void main(String[] args) {
		String source = "*只为做那读懂0和1的人^-_-^?[]望着通往世界另一头的那扇窗&";
		System.out.println("原文：" + source);

		String key = "Si0F0Vqb3AS/3xnF5OtrgQ==";// initkey();
		System.out.println("密钥：" + key);
		String encryptData = encrypt1(source, key);
		System.out.println("加密：" + encryptData);

		// 二次加密和解密
		String encryptData2 = encrypt2(encryptData);
		System.out.println("加密2：" + encryptData2);
		String decryptData0 = decrypt2("L3JmVlVLemx1bHJVUVE5UWRQRXZuNXlPUTU5TUliU0FVMUZHV1dYeEwxVT0=");
		System.out.println("解密0: " + decryptData0);

		String decryptData = decrypt1(decryptData0, key);
		System.out.println("解密: " + decryptData);
	}
}
