package com.socgen.maven.proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
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
				LOGGER.info(request);
				final Map<String, String> requestMap = getRequestMap(request);
				new DownloadFile(requestMap.get(URI), socket.getOutputStream()).run();
			}
			
			socketIn.close();
//			out.close();
			socket.close();
		} catch (Exception e) {
			LOGGER.error("Exception:", e.getMessage());
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
