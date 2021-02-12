package ca.mcgill.cs.jetuml.views;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.stream.Stream;

import static ca.mcgill.cs.jetuml.views.StringViewer.FONT;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ca.mcgill.cs.jetuml.geom.Dimension;

public class TestFontMetrics {

	private static final FontMetrics aMetrics = new FontMetrics(FONT);
	// Ensures there is no caching of sorts when reusing the same Text object
	@ParameterizedTest
	@MethodSource("stringPairParameters")
	public void testStateNotPreserved(String firstString, String secondString)
	{
		
		assertNotEquals(aMetrics.getDimension(firstString), aMetrics.getDimension(secondString));
	}
	
	private static Stream<Arguments> stringPairParameters() {
	    return Stream.of(
	            Arguments.of("X", "XX"),
	            Arguments.of("XX", "XXX"),
	            Arguments.of("XXX", "XXXX"),
	            Arguments.of("XXXX", "XXXXX"),
	            Arguments.of("XXXXX", "XXXXXX")
	    );
	}
	
	@Test
	public void testGetDimensions()
	{
		assertEquals(new Dimension(0, 11), aMetrics.getDimension(""));
		assertEquals(new Dimension(92, 11), aMetrics.getDimension("Single-Line-String"));
		assertEquals(new Dimension(29, 39), aMetrics.getDimension("Multi\nLine\nString"));
	}
}
