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

import org.jetuml.geom.Dimension;
import org.junit.jupiter.api.Test;

import javafx.scene.text.Font;

public class TestFontMetrics {

	private static final Font DEFAULT_FONT = new Font("System", DEFAULT_FONT_SIZE);
	private static final FontMetrics aMetrics = new FontMetrics(DEFAULT_FONT);
	private static final String SINGLE_LINE_STRING = "One";
	
	@Test
	public void testGetDimensions()
	{
		assertEquals(new Dimension(0, osDependent(13,12,12)), aMetrics.getDimension(""));
		assertEquals(new Dimension(osDependent(95, 92, 92), osDependent(13, 12, 12)), aMetrics.getDimension("Single-Line-String"));
		assertEquals(new Dimension(osDependent(31, 30, 30), osDependent(45, 40, 45)), aMetrics.getDimension("Multi\nLine\nString"));
	}
	
	/**
	 * Note that due to the rounding performed in getHeight, testing the height of strings 
	 * with a certain number of lines will result in the test failing.
	 * E.g. A string with 13 lines will fail this test.
	 */
	@Test 
	public void testGetHeight_TwoLineString()
	{
		assertEquals(aMetrics.getHeight(SINGLE_LINE_STRING) * 2, aMetrics.getHeight("One\nTwo"));	
	}
	
	@Test 
	public void testGetHeight_TenLineString()
	{
		assertEquals(aMetrics.getHeight(SINGLE_LINE_STRING) * 10, 
				aMetrics.getHeight("One\nTwo\nThree\nFour\nFive\nSix\nSeven\nEight\nNine\nTen"));	
	}
}
