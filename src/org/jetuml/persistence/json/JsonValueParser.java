package org.jetuml.persistence.json;

interface JsonValueParser
{
	boolean isApplicable(ParsableCharacterBuffer pInput);
	
	Object parse(ParsableCharacterBuffer pInput);
}
