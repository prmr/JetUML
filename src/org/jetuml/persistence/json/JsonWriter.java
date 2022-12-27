package org.jetuml.persistence.json;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * An object able to write a JSON value to its String representation.
 */
public final class JsonWriter
{
	private static final char QUOTE = '"';
	
	private static final Map<Class<?>, Function<Object, String>> WRITERS = new IdentityHashMap<>();
	
	static
	{
		WRITERS.put(Boolean.class, Object::toString);
		WRITERS.put(Integer.class, Object::toString);
		WRITERS.put(String.class, JsonWriter::writeJsonString);
	}
	
	public static String write(Object pJsonValue)
	{
		JsonValueValidator.validateType(pJsonValue);
		return WRITERS.get(pJsonValue.getClass()).apply(pJsonValue);
	}
	
	private static String writeJsonString(Object pString)
	{
		StringBuilder result = new StringBuilder();
		result.append(QUOTE);
		for( char character : JsonValueValidator.asString(pString).toCharArray())
		{
			
		}
		result.append(QUOTE);
		return result.toString();
	}
}
