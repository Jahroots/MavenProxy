package com.socgen.maven.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DownloadFile implements Runnable{
	private static final Log LOGGER = LogFactory.getLog(DownloadFile.class);
	private transient final String uri;
	private transient final OutputStream out;
	public DownloadFile(final String uri, final OutputStream out) {
		this.uri = uri;
		this.out = out;
	}
	
	public void run(){
		BufferedInputStream inStream = null;
		BufferedOutputStream bOut = null;
		
		try {
			URL url = new URL(uri);
			
//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
//			"mydomain\\MYUSER:MYPASSWORD"
//			StringBuilder userPassword = new StringBuilder(System.getProperty(Server.HTTP_PROXY_USER));
//			userPassword.append("\\");
//			userPassword.append(System.getProperty(Server.HTTP_PROXY_PASSWORD));
//			String encodedUserPwd = encoder.encode(userPassword.toString().getBytes());
//			con.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPwd);
//			inStream = new BufferedInputStream(con.getInputStream());
			
			
//			String encoded = new String
//				      (Base64.base64Encode(new String("username:password").getBytes()));
//				uc.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
//				uc.connect();
				
			inStream = new BufferedInputStream(url.openStream());
			
			bOut = new BufferedOutputStream(out,1024);
			final byte[] data = new byte[1024];
			int len = 0;
			while((len=inStream.read(data,0,1024))>=0){
				bOut.write(data,0,len);
			}
		} catch (MalformedURLException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} finally{
			if (null != bOut){
				try {
					bOut.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
			if (null != inStream){
				try {
					inStream.close();
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
	}
	
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
