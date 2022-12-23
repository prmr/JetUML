package org.jetuml.persistence.json;

/**
 * Parses integer values in JSON document according to the ECMA-404 2nd
 * edition December 2017.
 */
final class JsonIntegerParser implements JsonValueParser
{
	private static final char CHAR_MINUS = '-';
	private static final char CHAR_ZERO = '0';
	private static final char CHAR_NINE = '9';
	
	@Override
	public boolean isApplicable(ParsableCharacterBuffer pInput)
	{
		if (!pInput.hasMore() )
    	{
    		return false;
    	}
    	char next = pInput.next();
    	pInput.backUp();
		return next == CHAR_MINUS || isDigit(next);
    }
    
    /*
     * This is different from Character.isDigit, which 
     * also returns true for non-JSON digits.
     */
    private static boolean isDigit(char pCharacter)
    {
    	return pCharacter >= CHAR_ZERO && pCharacter <= CHAR_NINE;
    }

	@Override
	public Integer parse(ParsableCharacterBuffer pInput)
	{
		StringBuffer numberAsString = new StringBuffer();
		if( pInput.isNext(CHAR_MINUS))
		{
			numberAsString.append(pInput.next());
		}
    	while( pInput.hasMore() )
    	{
    		char next = pInput.next();
    		if( isDigit(next) )
    		{
    			numberAsString.append(next);
    		}
    		else
    		{
    			pInput.backUp();
    			break;
    		}
    	}
    	return parseInt(numberAsString.toString(), pInput.position());
	}
	
    private int parseInt(String pNumber, int pPosition)
    {
    	try
    	{
    		if( illegalNumber(pNumber) )
    		{
    			throw new JsonParsingException(pPosition);
    		}
    		return Integer.parseInt(pNumber);
    	}
    	catch(NumberFormatException exception)
    	{
    		throw new JsonParsingException(pPosition);
    	}
    }
    
    private static boolean illegalNumber(String pNumber)
    {
    	if(pNumber.isBlank())
    	{
    		return true;
    	}
    	if(pNumber.startsWith("-0"))
    	{
    		return true;
    	}
    	if( pNumber.length() >=2 && pNumber.charAt(0) == CHAR_ZERO && isDigit(pNumber.charAt(1)))
    	{
    		return true;
    	}
    	return false;
    }
}
