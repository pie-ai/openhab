package org.openhab.binding.netplug.internal.protocol.impl;

import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;

public class ButtonPush extends ServiceStateImpl {
	public ButtonPush(int id){
		super.setId(id);
		setType(NetPlugServiceType.RELAIS_AS_PUSH_BUTTON);
		setState(1);
	}

}
