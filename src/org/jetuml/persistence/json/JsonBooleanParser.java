package org.jetuml.persistence.json;

/**
 * Parses boolean values in JSON document according to the ECMA-404 2nd
 * edition December 2017.
 */
final class JsonBooleanParser implements JsonValueParser
{
	@Override
	public boolean isApplicable(ParsableCharacterBuffer pInput)
	{
		return pInput.isNext(Boolean.TRUE.toString().charAt(0)) ||
				pInput.isNext(Boolean.FALSE.toString().charAt(0));
	}

	@Override
	public Boolean parse(ParsableCharacterBuffer pInput)
	{
		char next = pInput.next();
    	String valueString = "";

    	if( next == Boolean.TRUE.toString().charAt(0) )
    	{
    		valueString = Boolean.TRUE.toString();
    	}
    	else
    	{
    		valueString = Boolean.FALSE.toString();
    	}
    	pInput.backUp();
    	if(pInput.next(valueString.length()).equals(valueString))
    	{
    		return Boolean.valueOf(valueString);
    	}
    	else
    	{
    		throw new JsonParsingException(pInput.position());
    	}
	}
}
