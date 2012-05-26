package com.socgen.maven.proxy;

import com.socgen.maven.proxy.utils.FirefoxCookieWatcher.Cookie;

public class Context {

	public Cookie cookie;

	public Cookie getCookie() {
		return cookie;
	}

	public void setCookie(Cookie cookie) {
		this.cookie = cookie;
	}
	
}
