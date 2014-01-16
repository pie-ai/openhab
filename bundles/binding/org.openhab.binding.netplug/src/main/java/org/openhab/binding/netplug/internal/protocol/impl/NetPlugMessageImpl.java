package org.openhab.binding.netplug.internal.protocol.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openhab.binding.netplug.NetPlugMessage;
import org.openhab.binding.netplug.internal.protocol.ServiceState;

public abstract class NetPlugMessageImpl implements NetPlugMessage {
	private List<ServiceState> services = new ArrayList<ServiceState>();
	private String hostName;
	private String hostAddress;
	private int port;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NetPlugMessageImpl)) {
			return false;
		}

		NetPlugMessageImpl other = (NetPlugMessageImpl) obj;
		if (!this.hostName.equals(other.getHostName())
				|| !this.hostAddress.equals(other.getHostAddress())

		) {
			return false;
		}

		for (int i = 0; i < this.services.size(); i++) {
			if (!services.get(i).equals(other.getServices().get(i))) {
				return false;
			}
		}
		return true;
	}

	public NetPlugMessageImpl() {
	}

	public NetPlugMessageImpl(ServiceState... services) {
		this.services.addAll(Arrays.asList(services));
	}

	public void addServices(ServiceState... services) {
		this.services.addAll(Arrays.asList(services));
	}

	public List<ServiceState> getServices() {
		return services;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}
}
