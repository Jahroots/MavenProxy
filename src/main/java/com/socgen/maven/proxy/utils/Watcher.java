package com.socgen.maven.proxy.utils;

import java.util.Timer;
import java.util.TimerTask;

public class Watcher extends Thread {
	
	private TimerTask timerTask;
	private long delay = 1 * 1000;
	
	public Watcher(final TimerTask tTask) {
		this.timerTask = tTask;
	}
	
	public Watcher(final TimerTask tTask, final long delay) {
		this.timerTask = tTask;
		this.delay = delay;
	}
	
	public void run() {
		Timer timer = new Timer();
		timer.schedule(timerTask, 0, delay);
	}

}
