package com.socgen.maven.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.socgen.maven.proxy.utils.Utils;

public class Downloader implements Runnable {
	private Socket socket;
	private String cookie;
	private static final Logger LOGGER = Logger.getLogger(Downloader.class.getName());
	
	public Downloader(final Socket socket, final String cookie) {
		this.socket = socket;
		this.cookie = cookie;
	}
	
	@Override
	public void run() {
		try{
			final BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			final String request = socketIn.readLine();
			
			Map<String, String> requestMap = Utils.getRequestMap(request);
			LOGGER.info(requestMap.toString());
			
			final String url = requestMap.get(Utils.URI);
			final StringTokenizer tokenizer = new StringTokenizer(url, "/");
			String fileName = null;
			while(tokenizer.hasMoreElements()){
				fileName = (String) tokenizer.nextElement();
			}
			URL urlConnection = new URL(url);
			SocketAddress addr = new InetSocketAddress("sogetoile.arpege.socgen", 8080);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
			HttpURLConnection con = (HttpURLConnection) urlConnection.openConnection(proxy);
			//"BCSI-AC-1411779732B63501=1EB79ABD00000105Ho7eVGJIWA3v4Kim1rr9xBpgKIchAAAABQEAABbWVwFwYgAATQEAAFJrBAA="
			con.addRequestProperty("Cookie", cookie);
			
			
			BufferedInputStream inStream = new BufferedInputStream(con.getInputStream());
			final FileOutputStream fos = new FileOutputStream(fileName);
			final BufferedOutputStream bOut = new BufferedOutputStream(fos,1024);
			final byte[] data = new byte[1024];
			int len = 0;
			int realSize = 0;
			while ((len = inStream.read(data, 0, 1024)) >= 0) {
				bOut.write(data, 0, len);
				realSize += len;
				System.out.println("\t " + realSize);
			}
			LOGGER.info("\t " + realSize + " bytes\n");
			
			bOut.close();
			inStream.close();
			
			Thread.sleep(500);
			
			File file = new File(fileName);
			System.out.println(file.exists() + " " + file.getAbsolutePath());
			byte fileContent[] = new byte[(int) file.length()];
			FileInputStream fin = new FileInputStream(file);
			fin.read(fileContent);
			fin.close();
			file.delete();
			
			Calendar c = Calendar.getInstance();
		    final String date = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
		    socket.getOutputStream().write(("HTTP/1.1 200 OK\r\nContent-length : " + fileContent.length + "\r\nDate : " + date + "\r\nContent-Type : text/html\r\n\r\n").getBytes());
			socket.getOutputStream().write(fileContent);
			socket.getOutputStream().flush();
			
	
		}catch(FileNotFoundException e){
			e.printStackTrace();
			try {
				if (null!=socket){
					Calendar c = Calendar.getInstance();
				    final String date = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
				    socket.getOutputStream().write(("HTTP/1.1 404 OK\r\nDate : " + date + "\r\nContent-Type : text/html\r\n\r\n").getBytes());
					socket.getOutputStream().flush();
				}
			} catch (IOException io) {
				io.printStackTrace();
			}
		}catch(IOException e){
			e.printStackTrace();
		}catch(InterruptedException e){
			e.printStackTrace();
		}finally{
			try {
				if (null!=socket) socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
//			try {
//				if (null!=socket){
//					Calendar c = Calendar.getInstance();
//				    final String date = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
//				    socket.getOutputStream().write(("HTTP/1.1 404 OK\r\nDate : " + date + "\r\nContent-Type : text/html\r\n\r\n").getBytes());
//					socket.getOutputStream().flush();
//					socket.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}

}
