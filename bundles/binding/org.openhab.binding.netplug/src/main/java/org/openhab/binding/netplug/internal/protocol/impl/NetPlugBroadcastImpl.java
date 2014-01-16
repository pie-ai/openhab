package org.openhab.binding.netplug.internal.protocol.impl;

import org.openhab.binding.netplug.NetPlugBroadcast;

public class NetPlugBroadcastImpl extends NetPlugMessageImpl implements
		NetPlugBroadcast {

	@Override
	public String toString() {
		return "NetPlugBroadcast [getServices()=" + getServices()
				+ ", getHostName()=" + getHostName() + ", getPort()="
				+ getPort() + ", getHostAddress()=" + getHostAddress() + "]";
	}

}
