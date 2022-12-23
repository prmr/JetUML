package org.jetuml.persistence.json;

/**
 * Parses objects in JSON document according to the ECMA-404 2nd
 * edition December 2017.
 */
public final class JsonObjectParser implements JsonValueParser
{
	private static final JsonStringParser STRING_PARSER = new JsonStringParser();
	private static final JsonAnyValueParser VALUE_PARSER = new JsonAnyValueParser();
	
	private static final char CHAR_START_OBJECT = '{';
	private static final char CHAR_END_OBJECT = '}';
	private static final char CHAR_COMMA = ',';
	private static final char CHAR_COLON = ':';
	
	@Override
	public boolean isApplicable(ParsableCharacterBuffer pInput)
	{
		return pInput.isNext(CHAR_START_OBJECT);
	}

	@Override
	public JsonObject parse(ParsableCharacterBuffer pInput)
	{
		JsonObject object = new JsonObject();
        
		pInput.consume(CHAR_START_OBJECT);
			
        while(true)
		{
        	pInput.skipBlanks();
            if( pInput.isNext(CHAR_END_OBJECT))
            {
            	pInput.consume(CHAR_END_OBJECT);
            	return object;
            }
            
            if(object.entrySet().size() > 0)
            {
            	pInput.consume(CHAR_COMMA);
            	pInput.skipBlanks();
            }
       
        	String key = STRING_PARSER.parse(pInput);
        	pInput.skipBlanks();
        	pInput.consume(CHAR_COLON);
        	pInput.skipBlanks();
			Object value = VALUE_PARSER.parse(pInput);
			if( object.has(key))
			{
				throw new JsonParsingException(pInput.position());
			}
			else
			{
				object.put(key, value);
			}
		}
	}
}
