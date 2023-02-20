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
