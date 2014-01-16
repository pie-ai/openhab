package org.openhab.binding.netplug.internal.protocol.impl;

import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;

public class TemparatureSensor extends ServiceStateImpl {
	public TemparatureSensor() {
		super();
		setType(NetPlugServiceType.TEMPERATURE_SENSOR);
	}
}
