package org.openhab.binding.netplug.internal.protocol;

public interface ServiceState {
	NetPlugServiceType getType();

	int getId();

	int getState();
}
