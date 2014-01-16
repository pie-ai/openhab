package org.openhab.binding.netplug.internal.protocol.impl;

import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;
import org.openhab.binding.netplug.internal.protocol.ServiceState;

public class ServiceStateFactory extends ServiceStateImpl {
	public static ServiceState create(NetPlugServiceType type, int id, int state) {
		switch (type) {
		case RELAIS:
			Relais r = new Relais();
			r.setId(id);
			r.setState(state);
			return r;
		case DIGITAL_OUTPUT:
			DigitalOutput digitalOut = new DigitalOutput();
			digitalOut.setId(id);
			digitalOut.setState(state);
			return digitalOut;
		case TEMPERATURE_SENSOR:
			TemparatureSensor sensor = new TemparatureSensor();
			sensor.setId(id);
			sensor.setState(state);
			return sensor;
		default:
			throw new IllegalArgumentException("illegal type:" + type);
		}
	}
}
