/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2021 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.rendering;

import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_SIZE;
import static org.jetuml.testutils.GeometryUtils.osDependent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.stream.Stream;

import org.jetuml.geom.Dimension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TestFontMetrics {

	private static final FontMetrics aMetrics = new FontMetrics(Font.font("System", DEFAULT_FONT_SIZE));
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
		assertEquals(new Dimension(0, osDependent(13,12,12)), aMetrics.getDimension(""));
		assertEquals(new Dimension(osDependent(95, 92, 92), osDependent(13, 12, 12)), aMetrics.getDimension("Single-Line-String"));
		assertEquals(new Dimension(osDependent(31, 30, 30), osDependent(45, 40, 45)), aMetrics.getDimension("Multi\nLine\nString"));
	}
	
	@Test
	public void testGetHeight_EmptyText()
	{
		Text emptyText = new Text("");
		int height = (int) Math.round(emptyText.getLayoutBounds().getHeight());
		assertEquals( height, aMetrics.getHeight(""));	
	}
	
	@Test 
	public void testGetHeight_SingleLineText()
	{
		Text singleLineText = new Text("Single-Line-String");
		int height = (int) Math.round(singleLineText.getLayoutBounds().getHeight());
		assertEquals( height, aMetrics.getHeight("Single-Line-String"));	
	}
	
	@Test 
	public void testGetHeight_MultiLineText()
	{
		Text multiLineText = new Text("Multi\nLine\nString");
		int height = (int) Math.round(multiLineText.getLayoutBounds().getHeight());
		assertEquals( height, aMetrics.getHeight("Multi\nLine\nString"));	
	}
}
