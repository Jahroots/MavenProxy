package com.socgen.maven.proxy.utils;

import com.socgen.maven.proxy.Context;

public class ContextThreadLocal {
	public static final ThreadLocal<Context> threadLocal = new ThreadLocal<Context>();
	
	public static void set(final Context context) {
		threadLocal.set(context);
	}

	public static void unset() {
		threadLocal.remove();
	}

	public static Context get() {
		return threadLocal.get();
	}
}
