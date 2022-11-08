package com.mainsteam.stm.portal.netflow.web.action.common;

import java.io.BufferedOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class DownUtil {

	public static void down(HttpServletResponse response, String filename,
			byte[] data) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setContentType("application/x-msdownload;");
		response.setHeader("Content-disposition", "attachment; filename="
				+ new String(filename.getBytes(), "ISO8859-1"));
		BufferedOutputStream bos = new BufferedOutputStream(
				response.getOutputStream());
		bos.write(data);
		bos.flush();
		bos.close();
	}
}
