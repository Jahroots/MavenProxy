package com.socgen.maven.proxy.utils;

import java.util.TimerTask;

public class CookieTimeTask extends TimerTask{

	public void run() {
		final long lastRead = CookieReader.getLastRead();
		final long lastModified = CookieReader.FILE.lastModified();
		if (lastRead < lastModified){
			CookieReader.readCookies();
		}
	}

}
