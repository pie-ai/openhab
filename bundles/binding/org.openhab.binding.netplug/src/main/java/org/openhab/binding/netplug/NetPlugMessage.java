package org.openhab.binding.netplug;

import java.util.List;

import org.openhab.binding.netplug.internal.protocol.ServiceState;

public interface NetPlugMessage {
	List<ServiceState> getServices();
}
