package org.openhab.binding.netplug.internal.protocol.impl;

import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;

public class DigitalOutput extends ServiceStateImpl {
	public DigitalOutput() {
		setType(NetPlugServiceType.DIGITAL_OUTPUT);
	}

	public void setState(int state) {
		if (state >= 0 || state <= 255) {
			super.setState(state);
		} else {
			throw new IllegalArgumentException(
					"only values between 0 and 255 are valid states");
		}
	}
}
