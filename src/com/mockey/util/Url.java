package com.mockey.util;

public class Url {
	String scheme;
	int port = 80;
	String host;
	String path;

	public Url(String url) {
		parse(url);

	}

	private void parse(String url) {
		// extract the scheme
		if (url.matches("(?i)^https?://.*")) {
			this.scheme = url.substring(0, url.indexOf(":"));
			url = url.substring(url.indexOf("://") + 3, url.length());
		} else {
			this.scheme = "http";
		}

		// extract the host and port
		String hostAndPort;
		if (url.indexOf("/") > 0) {
			hostAndPort = url.substring(0, url.indexOf("/"));
		} else {
			hostAndPort = url;
		}
		if (hostAndPort.indexOf(":") > 0) {
			this.host = hostAndPort.substring(0, hostAndPort.indexOf(":"));
			this.port = Integer.valueOf(hostAndPort.substring(hostAndPort.indexOf(":") + 1, hostAndPort.length()));
		} else {
			this.host = hostAndPort;
			if (this.scheme.equalsIgnoreCase("https")) {
				this.port = 443;
			} else {
				this.port = 80;
			}
		}

		if (url.indexOf("/") > 0) {
			this.path = url.substring(url.indexOf("/"), url.length());
		} else {
			this.path = "";
		}

		// scheme and port are not case sensitive so normalize to lowercase
		this.scheme = this.scheme.toLowerCase();
		this.host = this.host.toLowerCase();
	}

	public String getScheme() {
		return scheme;
	}

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

	public String getPath() {
		return path;
	}

	private boolean isDefaultPort() {
		return ("https".equals(scheme) && 443 == port) || ("http".equals(scheme) && 80 == port);
	}

	public String getFullUrl() {
		StringBuilder builder = new StringBuilder();
		if (scheme != null && host != null && host.trim().length()>0) {
			builder.append(scheme).append("://").append(host);
			if (!isDefaultPort()) {
				builder.append(":").append(port);
			}
		}
		if(path!=null){
			builder.append(path);
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return getFullUrl();
	}

	/**
	 * 
	 * @param uri
	 *            -
	 * @param req
	 *            - Context path, from HttpServletRequest.getContextPath()
	 * @return - returns path relative to context. For example, if uri = home,
	 *         then will return /Mockey/home or /home, depending on context
	 *         path.
	 */
	public static String getContextAwarePath(String uri, String contextRoot) {
		String relativePath = "";
		if (contextRoot != null) {
			if (!contextRoot.startsWith("/")) {
				contextRoot = "/" + contextRoot;
			}
		} else {
			contextRoot = "/";
		}

		if (!contextRoot.endsWith("/") && !uri.startsWith("/")) {

			relativePath = contextRoot + "/" + uri;
		} else if (contextRoot.endsWith("/") && !uri.startsWith("/")) {
			relativePath = contextRoot + uri;
		} else if (contextRoot.trim().equals("/") && uri.startsWith("/")) {

			relativePath = uri;
		} else if (contextRoot.endsWith("/") && uri.startsWith("/")) {
			contextRoot = contextRoot.substring(0, contextRoot.length());
			relativePath = contextRoot + uri;
		} else {
			relativePath = contextRoot + uri;
		}
		return relativePath;
	}

	public static void main(String[] args) {
		System.out.println("/mockey/home =? " + Url.getContextAwarePath("/home", "/mockey"));
		System.out.println("/mockey/home =? " + Url.getContextAwarePath("/", "/mockey"));
		System.out.println("/mockey/home =? " + Url.getContextAwarePath("/home/", "mockey"));
		System.out.println("/mockey/home =? " + Url.getContextAwarePath("/home/", "/mockey"));
		System.out.println("/home/ =? " + Url.getContextAwarePath("/home/", "/"));
		System.out.println("/home =? " + Url.getContextAwarePath("home", "/"));
		System.out.println("/home =? " + Url.getContextAwarePath("home", ""));
	}
}
