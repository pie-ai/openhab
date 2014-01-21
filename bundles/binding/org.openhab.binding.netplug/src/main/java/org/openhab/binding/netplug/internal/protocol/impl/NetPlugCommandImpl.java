package org.openhab.binding.netplug.internal.protocol.impl;

import org.openhab.binding.netplug.NetPlugCommand;
import org.openhab.binding.netplug.internal.protocol.ServiceState;

public class NetPlugCommandImpl extends NetPlugMessageImpl implements
		NetPlugCommand {
	public NetPlugCommandImpl() {
	}

	public NetPlugCommandImpl(ServiceState... services) {
		super.addServices(services);
	}

	@Override
	public String toString() {
		return "NetPlugCommand [getServices()=" + getServices()
				+ ", getHostName()=" + getHostName() + ", getPort()="
				+ getPort() + ", getHostAddress()=" + getHostAddress() + "]";
	}

}
