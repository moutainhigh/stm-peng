package com.mainsteam.stm.portal.netflow.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class ChinaWiservEncrypt {
	public interface CEncryptLib extends Library {

		CEncryptLib INSTANCE = (CEncryptLib) Native.loadLibrary("mutil",
				CEncryptLib.class);

		int enc_dec_3des_c(byte[] inputBuf, int inputLen,
				PointerByReference outputBuf, IntByReference outputLen,
				int isEnc);

		void mutil_free(Pointer p);
	}

	public interface Libc extends Library {
		Libc INSTANCE = (Libc) Native.loadLibrary("c", Libc.class);

		void free(Pointer p);
	}

	/*
	 * return: null:error; not null:success
	 */
	public static byte[] TriDesDecrypt(byte[] inputByte) {
		return TriDesEncDec(inputByte, 0);
	}

	public static byte[] TriDesEncrypt(byte[] inputByte) {
		return TriDesEncDec(inputByte, 1);
	}

	private static byte[] TriDesEncDec(byte[] inputByte, int isEnc) {
		String osName = System.getProperty("os.name", "");
		PointerByReference pref = new PointerByReference();
		IntByReference iref = new IntByReference();
		int in_len = inputByte.length;
		int ret_value = 0;
		byte[] ret_buf = null;
		CEncryptLib cencryptLib = get("mutil", CEncryptLib.class);
		try {
			ret_value = cencryptLib.enc_dec_3des_c(inputByte, in_len, pref,
					iref, isEnc);
			if (ret_value != 1)
				return null;
			ret_buf = pref.getValue().getByteArray(0, iref.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (osName.startsWith("Windows")) {
			cencryptLib.mutil_free(pref.getValue());
		} else if (osName.startsWith("Linux")) {
			Libc.INSTANCE.free(pref.getValue());
		}
		return ret_buf;
	}

	@SuppressWarnings("unchecked")
	private static <T> T get(String name, Class<T> classz) {
		String os = System.getProperty("os.name", "");
		String path = ChinaWiservEncrypt.class.getClassLoader().getResource("")
				.getFile().substring(1);
		if (path.startsWith("/") && os.startsWith("Windows")) {
			path = path.substring(1);
		}
		if (os.startsWith("Windows")) {
			String arch = System.getProperty("os.arch", "");
			switch (arch) {
			case "amd64":
				path += "windows/64/";
				break;
			default:
				path += "windows/32/";
				break;
			}
		} else {
			path += "linux/";
		}
		return (T) Native.loadLibrary(path + name, classz);
	}

	public static void main(String[] args) {
		byte[] input = new byte[] { 24, 13, 2, -12 };
		byte[] result = ChinaWiservEncrypt.TriDesEncrypt(input);
		System.out.println(result.length);
		// System.out.println(System.getProperty("java.library.path"));
		// System.out.println(System.getProperty("user.dir"));
		// String oString
		// =ChinaWiservEncrypt.class.getClassLoader().getResource("").toString();
		// System.out.println(oString);

		// ChinaWiservEncrypt.get("mutil", CEncryptLib.class);
		// System.out.println(ChinaWiservEncrypt.class.getClass()
		// .getProtectionDomain().getCodeSource().getLocation().getFile());
	}
}
