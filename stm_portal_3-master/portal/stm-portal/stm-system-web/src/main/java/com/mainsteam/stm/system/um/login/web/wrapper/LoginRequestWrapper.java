package com.mainsteam.stm.system.um.login.web.wrapper;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.mainsteam.stm.util.Util;

public class LoginRequestWrapper extends HttpServletRequestWrapper {

	private Logger logger = Logger.getLogger(LoginRequestWrapper.class);
	
	private final String ACCOUNT_U="u";
	private final String PASSWORD_P="p";
	private final String PASSWORD_OP="op";
	private final String CODE_C="c";
	
	private final String ACCOUNT="account";
	private final String PASSWORD="password";
	private final String OLDPASSWORD="oldPassword";
	private final String CODE="code";
	
	private final String AES_KEY="X2B4C6D8E0F2G4H6";
	
	public LoginRequestWrapper(HttpServletRequest request) {
		super(request);
	}
	@Override
	public String getParameter(String name){
		if(ACCOUNT.equals(name) && !Util.isEmpty(super.getParameter(ACCOUNT_U))){
			return AESDecrypt(super.getParameter(ACCOUNT_U));
		}
		if(PASSWORD.equals(name) && !Util.isEmpty(super.getParameter(PASSWORD_P))){
			return super.getParameter(PASSWORD_P);
		}
		if(OLDPASSWORD.equals(name) && !Util.isEmpty(super.getParameter(PASSWORD_OP))){
			return super.getParameter(PASSWORD_OP);
		}
		if(CODE.equals(name) && !Util.isEmpty(super.getParameter(CODE_C))){
			return AESDecrypt(super.getParameter(CODE_C));
		}
		return super.getParameter(name);
	}
	
    /**
     * AES 解密
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     */
	private String AESDecrypt(String sSrc) {
        try {
            byte[] raw = AES_KEY.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
            	logger.error("AESDecrypt", e);
                return null;
            }
        } catch (Exception ex) {
        	logger.error("AESDecrypt", ex);
            return null;
        }
    }
	
	/**
	 * 解密
	 * @param hexStr
	 * @return
	 */
	private String loginEncrypt(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		String result = hexStr;
		try {
			result = new String(bytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("loginEncrypt", e);
		}
		return result;
	}
	
}
