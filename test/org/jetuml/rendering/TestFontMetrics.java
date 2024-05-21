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

import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.geom.Dimension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

public class TestFontMetrics {

	private static final FontMetrics aMetrics = new FontMetrics();
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	public void testGetDimensions()
	{
		assertEquals(new Dimension(0, 16), aMetrics.getDimension("", DEFAULT_FONT));
		assertEquals(new Dimension(95, 16), aMetrics.getDimension("Single-Line-String", DEFAULT_FONT));
		assertEquals(new Dimension(31, 48), aMetrics.getDimension("Multi\nLine\nString", DEFAULT_FONT));
	}
	
	@Test 
	@EnabledOnOs(OS.WINDOWS)
	public void testGetHeight_TwoLineString()
	{
		assertEquals(16, aMetrics.getHeight(DEFAULT_FONT));	
	}
}
