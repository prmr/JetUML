/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
		if(!pInput.hasMore() )
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
	
    private static int parseInt(String pNumber, int pPosition)
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
