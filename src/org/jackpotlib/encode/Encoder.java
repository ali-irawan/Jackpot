package org.jackpotlib.encode;

import java.io.IOException;

import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.Base64OutputStream;

public class Encoder {

	public static String encode64(byte[] data) throws IOException {
		return Base64OutputStream.encodeAsString(data, 0, data.length, false,
				false);
	}

	public static String decode64(byte[] data) throws IOException {
		return new String(Base64InputStream.decode(data,0,data.length));
	}
}
