package com.socgen.maven.proxy;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
	private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
	private transient ServerSocket socketServer;

	public Server() {
		try {
			socketServer = new ServerSocket(2511);
			LOGGER.info("Maven Server Proxy Started !");
			startListen();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	private void startListen() throws IOException {
		while(true){
			new Thread(new ServerSocketRequest(socketServer.accept())).start();
		}
	}

	public static void main(final String[] args) {
		new Server();
	}
}
