package org.openhab.binding.netplug;


public interface NetPlugCommand extends NetPlugMessage {
	int getPort();

	String getHostAddress();

	String getHostName();
}
