package de.pa2.automation.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Serializer {
	public String serialize(Object object) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(object);
		return json;
	}
}
