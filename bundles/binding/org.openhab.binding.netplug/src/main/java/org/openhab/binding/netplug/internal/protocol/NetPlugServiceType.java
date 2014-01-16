package org.openhab.binding.netplug.internal.protocol;

public enum NetPlugServiceType {
	RELAIS("R"), TEMPERATURE_SENSOR("T"), DIGITAL_OUTPUT("D"), INFRARED("I");
	private String identifier;

	private NetPlugServiceType(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public static NetPlugServiceType forIdentifier(String idenntifier) {
		for (NetPlugServiceType type : values()) {
			if (type.identifier.equals(idenntifier)) {
				return type;
			}

		}
		return null;
	}

}
