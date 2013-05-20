package com.stormpath.sdk.client;

public class Proxy {
	
	public final static Proxy NO_PROXY = new Proxy(null, -1);
	
	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final boolean withAuthentication;
	
	public Proxy(String host, int port)
	{
		this(host, port, null, null, false);
	}
	
	public Proxy(String host, int port, String username, String password)
	{
		this(host, port, username, password, true);
	}

	private Proxy(String host, int port, String username, String password, boolean withAuthentication)
	{
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.withAuthentication = withAuthentication;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isWithAuthentication() {
		return withAuthentication;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + port;
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		result = prime * result + (withAuthentication ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Proxy other = (Proxy) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (port != other.port)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (withAuthentication != other.withAuthentication)
			return false;
		return true;
	}
}
