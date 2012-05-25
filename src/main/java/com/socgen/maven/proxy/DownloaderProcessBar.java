package com.socgen.maven.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
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
import java.util.Map;
import java.util.logging.Logger;

import net.sf.jcprogress.ConsoleProgressBarThread;
import net.sf.jcprogress.ConsoleProgressThreadBase;
import net.sf.jcprogress.ProgressStatusProvider;

import com.socgen.maven.proxy.utils.CookieReader;
import com.socgen.maven.proxy.utils.Utils;

public class DownloaderProcessBar implements ProgressStatusProvider, Runnable{
	private static final Logger LOGGER = Logger.getLogger(DownloaderProcessBar.class.getName());
	
	private String progressStatusText = "";
	private int currentProgressCount = 0;
	private int wholeProcessCount = 0;

	private Socket socket;
	private ConsoleProgressThreadBase progress = null;
	private String fileName;
	private boolean useCookie;
	public DownloaderProcessBar(final Socket socket, final boolean useCookie) {
		this.socket = socket;
		this.useCookie = useCookie;
	}
	
	@Override
	public void run() {
		try{
			final BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			final String request = socketIn.readLine();
			final Map<String, String> requestMap = Utils.getRequestMap(request);
			
			final String url = requestMap.get(Utils.URI);
//			final String url = "http://search.maven.org/remotecontent?filepath=com/google/guava/guava/12.0/guava-12.0.jar";
			fileName = Utils.getFileName(url);
			
			URL urlConnection = new URL(url);
			HttpURLConnection con = null;
			if (!useCookie){
				con = (HttpURLConnection) urlConnection.openConnection();
			}else{
				SocketAddress addr = new InetSocketAddress("sogetoile.arpege.socgen", 8080);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
				con = (HttpURLConnection) urlConnection.openConnection(proxy);
				//"BCSI-AC-1411779732B63501=1EB79ABD00000105Ho7eVGJIWA3v4Kim1rr9xBpgKIchAAAABQEAABbWVwFwYgAATQEAAFJrBAA="
				con.addRequestProperty("Cookie", CookieReader.VALUE);
			}
			wholeProcessCount = con.getContentLength();
			
			progress = new ConsoleProgressBarThread(System.err, this);
//			progress.setShowCounter(false);
//			progress.setShowEndDate(false);
			progress.setShowCounter(true);
			progress.setShowPercentage(true);
			progress.setShowRemainingTime(true);
			this.progressStatusText = fileName;//+ " : " + Utils.convertToStringRepresentation(wholeProcessCount);
//			progress.start();
			
//			System.out.println("\n" + fileName + " size:" + Utils.convertToStringRepresentation(wholeProcessCount));
			BufferedInputStream inStream = new BufferedInputStream(con.getInputStream());
			final FileOutputStream fos = new FileOutputStream(fileName);
			final BufferedOutputStream bOut = new BufferedOutputStream(fos,1024);
			final byte[] data = new byte[1024];
			int len = 0;
			LOGGER.info(fileName + " " + wholeProcessCount);
			while ((len = inStream.read(data, 0, 1024)) >= 0) {
				bOut.write(data, 0, len);
				currentProgressCount += len;
				System.out.println("\t\t" + currentProgressCount);
			}
			
			bOut.close();
			inStream.close();
			
//			progress.waitToStop();
			Utils.sendResponseToSocket(socket.getOutputStream(), fileName, Utils.ResponseType.HTTP_200);
		}catch(FileNotFoundException fe){
			if (null!=socket){
				try {
					Utils.sendResponseToSocket(socket.getOutputStream(), fileName, Utils.ResponseType.HTTP_404);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			fe.printStackTrace();
		}catch(IOException io){
			io.printStackTrace();
		}finally{
			try {
				if (null!=socket) socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public String getProgressStatusText() {
		return "[" + this.progressStatusText + "]";
	}

	@Override
	public int getCurrentProgressCount() {
		return this.currentProgressCount;
	}

	@Override
	public int getWholeProcessCount() {
		return this.wholeProcessCount;
	}

}
