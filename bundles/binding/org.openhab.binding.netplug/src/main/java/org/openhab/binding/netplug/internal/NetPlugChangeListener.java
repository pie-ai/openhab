package org.openhab.binding.netplug.internal;

import org.openhab.binding.netplug.NetPlugBroadcast;
import org.openhab.binding.netplug.internal.protocol.ServiceState;

public interface NetPlugChangeListener {
	void notifyChange(NetPlugBroadcast server, ServiceState service);
}
