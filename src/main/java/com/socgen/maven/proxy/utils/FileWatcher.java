package com.socgen.maven.proxy.utils;

import java.util.Timer;
import java.util.TimerTask;

public class FileWatcher extends Thread {
	
	public void run() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				final long lastRead = CookieReader.getLastRead();
				final long lastModified = CookieReader.FILE.lastModified();
				if (lastRead < lastModified){
					CookieReader.readCookies();
				}
			}
		}, 0, 1 * 1000);
	}

}
