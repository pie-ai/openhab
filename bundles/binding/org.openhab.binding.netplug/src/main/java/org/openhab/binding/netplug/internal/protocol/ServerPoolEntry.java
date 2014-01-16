package org.openhab.binding.netplug.internal.protocol;

import java.util.Date;
import java.util.UUID;

import org.openhab.binding.netplug.NetPlugBroadcast;

public class ServerPoolEntry {
	private String id = UUID.randomUUID().toString();

	private NetPlugBroadcast server;
	private Date registeredAt = new Date();
	private Date lastContactAt = new Date();

	public ServerPoolEntry(NetPlugBroadcast server) {
		super();
		this.server = server;
	}

	public NetPlugBroadcast getServer() {
		return server;
	}

	public void setServer(NetPlugBroadcast server) {
		this.server = server;
	}

	public Date getRegisteredAt() {
		return registeredAt;
	}

	public void setRegisteredAt(Date registeredAt) {
		this.registeredAt = registeredAt;
	}

	public Date getLastContactAt() {
		return lastContactAt;
	}

	public void setLastContactAt(Date lastContactAt) {
		this.lastContactAt = lastContactAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
