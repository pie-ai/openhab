package org.openhab.binding.netplug.internal;

import org.openhab.binding.netplug.internal.protocol.NetPlugServiceType;
import org.openhab.core.binding.BindingConfig;
import org.openhab.model.item.binding.BindingConfigParseException;

public class NetPlugBindingConfig implements BindingConfig {
	/**
	 * udp: ip
	 * tcp: ip/host
	 */
	private String location;
	private int port = 80;
	private String itemName;
	private int id;
	private NetPlugServiceType type;
	private String secret;
	private String netPlugId;
	private ConnectionType connectionType = null;

	public NetPlugBindingConfig(String itemName, ConnectionType connectionType,
			String location, int port, NetPlugServiceType type, int id) {
		super();
		this.itemName = itemName;
		this.connectionType = connectionType;
		this.location = location;
		this.port = port;
		this.id = id;
		this.type = type;
	}
	
	public NetPlugBindingConfig(String itemName, ConnectionType connectionType,
			String location, String netPlugId, String secret, NetPlugServiceType type, int id) {
		super();
		this.itemName = itemName;
		this.connectionType = connectionType;
		this.location = location;
		this.netPlugId = netPlugId;
		this.secret = secret;
		this.id = id;
		this.type = type;
	}

	public String getLocation() {
		return location;
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

	public String getSecret() {
		return secret;
	}

	public String getNetPlugId() {
		return netPlugId;
	}

	@Override
	public String toString() {
		return "NetPlugBindingConfig [ip=" + location + ", port=" + port
				+ ", itemName=" + itemName + ", id=" + id + ", type=" + type
				+ ", connectionType=" + connectionType + "]";
	}

	private static final String ERROR_MESSAGE_PARSING_GENERAL = "bindingConfig is not legal: use udp://ip:port/TypeId, ip://RouterId/ or usb://device/TypeId";
	
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
						ERROR_MESSAGE_PARSING_GENERAL);
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
						ERROR_MESSAGE_PARSING_GENERAL);
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
		} else if (bindingConfig.startsWith("http")) {
			// http://host[:port]/location|NetPlugId:Secret:TypeId
			
			String protocol = bindingConfig.startsWith("https://")?"https://":"http://";
			if (!bindingConfig.startsWith(protocol))
			{
				throw new BindingConfigParseException("tcp protocol should start with http:// or https://");
			}
			
			String[] parts = bindingConfig.split("\\|");
			if (parts.length != 2)
			{
				throw new BindingConfigParseException("tcp protocol should look like http://host/netplug|NetPlugId:Secret:TypeId");
			}
			String location = parts[0];
			String netPlugSecretTypeId = parts[1];
			
			parts = netPlugSecretTypeId.split(":");
			
			if (parts.length != 3)
			{
				throw new BindingConfigParseException("tcp protocol should look like http://host/netplug|NetPlugId:Secret:TypeId");
			}
			
			
			String netPlugId = parts[0];
			String secret = parts[1];
			
			
			
			String typeString = parts[2].substring(0, 1);
			String idString = parts[2].substring(1,
					parts[2].length());

			NetPlugServiceType type = NetPlugServiceType
					.forIdentifier(typeString);
			int id = Integer.parseInt(idString);

			return new NetPlugBindingConfig(itemName, ConnectionType.TCP, location, netPlugId, secret,
					type, id);

		}
		
		
		else {
			throw new BindingConfigParseException(
					ERROR_MESSAGE_PARSING_GENERAL);
		}
	}
}
