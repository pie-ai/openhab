package org.openhab.binding.netplug.internal.protocol.impl;

import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;
import org.openhab.binding.netplug.internal.protocol.ServiceState;

public abstract class ServiceStateImpl implements ServiceState {
	private NetPlugServiceType type = null;
	private int id;
	private int state;

	public ServiceStateImpl(NetPlugServiceType type, int id) {
		super();
		this.type = type;
		this.id = id;
	}

	public ServiceStateImpl(NetPlugServiceType type, int id, int state) {
		super();
		this.type = type;
		this.id = id;
		this.state = state;
	}

	public ServiceStateImpl() {
		super();
	}

	protected void setType(NetPlugServiceType type) {
		this.type = type;
	}

	public void setId(int id) {
		this.id = id;
	}

	public NetPlugServiceType getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public int getState() {
		return state;
	}

	protected void setState(int state) {
		this.state = state;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceStateImpl other = (ServiceStateImpl) obj;
		if (id != other.id)
			return false;
		if (state != other.state)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
