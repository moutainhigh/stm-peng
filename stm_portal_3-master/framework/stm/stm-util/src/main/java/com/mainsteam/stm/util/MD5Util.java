package com.mainsteam.stm.util;

import java.security.MessageDigest;

/**
 * <li>文件名称: MD5Util.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月1日
 */
public class MD5Util {
	private MD5Util(){}
	static final char [] HEX_DIGITS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String MD5(String text) {
        try {
            byte[] bytesText = text.toUpperCase().getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(bytesText);
            // 获得密文
            byte[] md = messageDigest.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char results[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                results[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
                results[k++] = HEX_DIGITS[byte0 & 0xf];
            }
            return new String(results);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
