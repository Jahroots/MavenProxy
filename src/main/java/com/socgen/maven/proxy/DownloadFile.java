package com.socgen.maven.proxy;

import java.io.*;
import java.net.*;

public abstract class DownloadFile {
	public static void main(final String args[]) throws IOException {
		final String uri = args[0];
		final String fileName = args[1];
		final BufferedInputStream inStream = new BufferedInputStream(new URL(uri).openStream());
		final FileOutputStream fos = new FileOutputStream(fileName);
		final BufferedOutputStream bOut = new BufferedOutputStream(fos, 1024);
		final byte[] data = new byte[1024];
		int len = 0;
		while ((len = inStream.read(data, 0, 1024)) >= 0) {
			bOut.write(data, 0, len);
		}
		bOut.close();
		inStream.close();
	}
}
