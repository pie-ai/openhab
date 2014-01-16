package org.openhab.binding.netplug.internal;

import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;
import org.openhab.core.binding.BindingConfig;
import org.openhab.model.item.binding.BindingConfigParseException;

public class NetPlugBindingConfig implements BindingConfig {
	private String ip;
	private int port = 80;
	private String itemName;
	private int id;
	private NetPlugServiceType type;
	private ConnectionType connectionType = null;

	public NetPlugBindingConfig(String itemName, ConnectionType connectionType,
			String ip, int port, NetPlugServiceType type, int id) {
		super();
		this.itemName = itemName;
		this.connectionType = connectionType;
		this.ip = ip;
		this.port = port;
		this.id = id;
		this.type = type;
	}

	public String getIP() {
		return ip;
	}

	public int getId() {
		return id;
	}

	public String getItemName() {
		return itemName;
	}

	public int getPort() {
		return port;
	}

	public NetPlugServiceType getType() {
		return type;
	}

	public ConnectionType getConnectionType() {
		return connectionType;
	}

	@Override
	public String toString() {
		return "NetPlugBindingConfig [ip=" + ip + ", port=" + port
				+ ", itemName=" + itemName + ", id=" + id + ", type=" + type
				+ ", connectionType=" + connectionType + "]";
	}

	public static NetPlugBindingConfig parse(String itemName,
			String bindingConfig) throws BindingConfigParseException {
		if (bindingConfig == null) {
			throw new IllegalArgumentException("bindingConfig must not be null");
		}

		if (bindingConfig.startsWith("udp://")) {
			// udp://ip:port/TypeId
			String udpBindingConfig = bindingConfig
					.substring("udp://".length());

			String[] ipPortAndTypeId = udpBindingConfig.split("/");
			if (ipPortAndTypeId.length != 2) {
				throw new BindingConfigParseException(
						"bindingConfig is not legal: use udp://ip:port/TypeId or usb://device/TypeId");
			}

			ConnectionType connectionType = ConnectionType.UDP;
			int port = 10666;
			String ip = null;
			if (ipPortAndTypeId[0].contains(":")) {
				ip = ipPortAndTypeId[0].split(":")[0];
				port = Integer.parseInt(ipPortAndTypeId[0].split(":")[1]);
			} else {
				ip = ipPortAndTypeId[0];
			}

			if (ipPortAndTypeId[1].length() < 2) {
				throw new BindingConfigParseException(
						"bindingConfig is not legal: use udp://ip:port/TypeId or usb://device/TypeId");
			}
			String typeString = ipPortAndTypeId[1].substring(0, 1);
			String idString = ipPortAndTypeId[1].substring(1,
					ipPortAndTypeId[1].length());

			NetPlugServiceType type = NetPlugServiceType
					.forIdentifier(typeString);
			int id = Integer.parseInt(idString);

			return new NetPlugBindingConfig(itemName, connectionType, ip, port,
					type, id);

		} else if (bindingConfig.startsWith("usb://")) {
			throw new BindingConfigParseException(
					"usb binding is not supported yet");
		} else {
			throw new BindingConfigParseException(
					"bindingConfig is not legal: use udp://ip:port/TypeId or usb://device/TypeId");
		}
	}
}
