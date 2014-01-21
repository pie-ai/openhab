package org.openhab.binding.netplug.internal.protocol;

public enum NetPlugServiceType {

	/**
	 * switch on and off, knows and publishes it's state
	 */
	RELAIS("R"), 
	
	/**
	 * digital pwm value between 0 and 255, knows and publishes it's state
	 */
	DIGITAL_OUTPUT("D"), 
	
	/**
	 * 
	 */
	REMOTE_CONTROL("S"),
	
	/**
	 * relais used as a push button, switches on temporarily
	 */
	RELAIS_AS_PUSH_BUTTON("P"),
	
	/**
	 * temperature sensor, publishes it's value
	 */
	TEMPERATURE_SENSOR("T"), 
	
	/**
	 * analog input, publishes it's value
	 */
	ANALOG_INPUT("A"),
	
	/**
	 * infrared sensor, publishes it's value if received
	 */
	INFRARED_SENSOR("I");
	
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
