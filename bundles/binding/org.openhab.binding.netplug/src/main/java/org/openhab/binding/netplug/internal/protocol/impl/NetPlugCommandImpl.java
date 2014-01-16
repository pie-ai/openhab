package org.openhab.binding.netplug.internal.protocol.impl;

import org.openhab.binding.netplug.NetPlugCommand;

public class NetPlugCommandImpl extends NetPlugMessageImpl implements
		NetPlugCommand {

	@Override
	public String toString() {
		return "NetPlugCommand [getServices()=" + getServices()
				+ ", getHostName()=" + getHostName() + ", getPort()="
				+ getPort() + ", getHostAddress()=" + getHostAddress() + "]";
	}

}
