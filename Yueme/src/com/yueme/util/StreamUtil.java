package com.yueme.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
	private StreamUtil(){};
	/**
	 * 根据输入流得到字符串
	 * @param inputStream
	 * @return
	 */
	public static String getString(InputStream inputStream) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while((len=inputStream.read(buffer))>0) {
				baos.write(buffer,0,len);
			}
			baos.flush();
			baos.close();
			return baos.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
