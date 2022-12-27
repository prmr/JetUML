package org.jetuml.persistence.json;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * An object able to write a JSON value to its String representation.
 */
public final class JsonWriter
{
	private static final Map<Class<?>, Function<Object, String>> WRITERS = new IdentityHashMap<>();
	
	static
	{
		WRITERS.put(Boolean.class, Object::toString);
		WRITERS.put(Integer.class, Object::toString);
		WRITERS.put(String.class, JsonStringParser::writeJsonString);
		WRITERS.put(JsonObject.class, JsonObjectParser::writeJsonObject);
		WRITERS.put(JsonArray.class, JsonArrayParser::writeJsonArray);
	}
	
	public static String write(Object pJsonValue)
	{
		JsonValueValidator.validateType(pJsonValue);
		return WRITERS.get(pJsonValue.getClass()).apply(pJsonValue);
	}
}
