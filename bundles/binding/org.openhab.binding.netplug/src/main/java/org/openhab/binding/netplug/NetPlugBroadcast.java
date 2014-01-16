package org.openhab.binding.netplug;


public interface NetPlugBroadcast extends NetPlugMessage {
	int getPort();

	String getHostAddress();

	String getHostName();
}
