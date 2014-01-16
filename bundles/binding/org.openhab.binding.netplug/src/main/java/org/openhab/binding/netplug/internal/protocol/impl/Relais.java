package org.openhab.binding.netplug.internal.protocol.impl;

import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;


public class Relais extends ServiceStateImpl {
	public Relais()
	{
		setType(NetPlugServiceType.RELAIS);
	}
	
	public void setState(int state) {
		if (state == 1 || state == 0) {
			super.setState(state);
		} else {
			throw new IllegalArgumentException("only 1 and 0 are valid states");
		}
	}
}
