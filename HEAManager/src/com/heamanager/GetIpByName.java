package com.heamanager;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetIpByName {
	InetAddress myServer = null;
	String domainName = null;
	public GetIpByName(String domainName) {
		this.domainName = domainName;
	}
	public InetAddress getServerIP() {
		try {
			myServer = InetAddress.getByName(domainName);
		} catch (UnknownHostException e) {
		}
		return (myServer);
	}
}
