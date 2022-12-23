package org.jetuml.persistence.json;

import java.util.List;

/**
 * Parses any value in a JSON document according to the ECMA-404 2nd
 * edition December 2017.
 */
final class JsonAnyValueParser implements JsonValueParser
{
	private static final List<JsonValueParser> PARSERS = List.of(
			new JsonStringParser(),
			new JsonBooleanParser(),
			new JsonIntegerParser(),
			new JsonObjectParser(),
			new JsonArrayParser()
			);
	
	@Override
	public boolean isApplicable(ParsableCharacterBuffer pInput)
	{
		return PARSERS.stream()
			.anyMatch(parser -> parser.isApplicable(pInput));
    }
    
	@Override
	public Object parse(ParsableCharacterBuffer pInput)
	{
		for( JsonValueParser parser : PARSERS )
		{
			if( parser.isApplicable(pInput))
			{
				return parser.parse(pInput);
			}
		}
		throw new JsonParsingException(pInput.position());
	}
}
