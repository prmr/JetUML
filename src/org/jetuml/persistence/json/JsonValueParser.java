package org.jetuml.persistence.json;

public interface JsonValueParser
{
	boolean isApplicable(ParsableCharacterBuffer pInput);
	
	Object parse(ParsableCharacterBuffer pInput);
}
