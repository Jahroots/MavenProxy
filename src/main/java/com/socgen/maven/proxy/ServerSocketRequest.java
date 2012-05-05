package com.socgen.maven.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerSocketRequest implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerSocketRequest.class);
	private static final String METHOD = "method";
	private static final String URI = "uri";
	private static final String PROTOCOL = "protocol";
	private final Socket socket;
//	private BufferedWriter out;
//	private HttpClient httpClient;

//	static {
//		ConsoleAppender consoleAppender = new ConsoleAppender(new SimpleLayout());
//		BasicConfigurator.configure(consoleAppender);
//	}

	public ServerSocketRequest(final Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			final BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			final String request = socketIn.readLine();
			if (null != request){
				LOGGER.info("New Request: " + request);
				final Map<String, String> requestMap = getRequestMap(request);
				downloadFile(requestMap.get(URI));
			}
			
			socketIn.close();
//			out.close();
			socket.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private void downloadFile(final String uri) {
		BufferedInputStream inStream;
		try {
			inStream = new BufferedInputStream(new URL(uri).openStream());
			final BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream(),1024);
			final byte[] data = new byte[1024];
			int x = 0;
			while((x=inStream.read(data,0,1024))>=0){
				bout.write(data,0,x);
			}
			bout.close();
			inStream.close();
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	private Map<String, String> getRequestMap(final String request) {
		final Map<String, String> mapRequest = new Hashtable<String, String>();
		final StringTokenizer tokenizer = new StringTokenizer(request, " ");
		if (tokenizer.countTokens() == 3) {
			final String method = (String) tokenizer.nextElement();
			final String uri = (String) tokenizer.nextElement();
			final String protocol = (String) tokenizer.nextElement();

			mapRequest.put(METHOD, method);
			mapRequest.put(URI, uri);
			mapRequest.put(PROTOCOL, protocol);
		}
		return mapRequest;
	}
}
