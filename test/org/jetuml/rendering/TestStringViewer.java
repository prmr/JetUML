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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Alignment;
import org.jetuml.rendering.StringRenderer.Decoration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

public class TestStringViewer 
{

	private static String userDefinedFontName;
	private static int userDefinedFontSize;
	
	private StringRenderer topCenter;
	private StringRenderer topCenterBold;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontName = UserPreferences.instance().getString(UserPreferences.StringPreference.fontName);
		UserPreferences.instance().setString(StringPreference.fontName, UserPreferences.DEFAULT_FONT_NAME);
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, UserPreferences.DEFAULT_FONT_SIZE);
	}
	
	@BeforeEach
	public void setup()
	{
		topCenter = new StringRenderer(Alignment.CENTER);
		topCenterBold = new StringRenderer(Alignment.CENTER, Decoration.BOLD);
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setString(StringPreference.fontName, userDefinedFontName);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	void testDimensionDefaultFont()
	{
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(73, 16), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(79, 16), topCenterBold.getDimension("Display String"));
	}
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	void testDimension8ptFont()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, 8);
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(49, 11), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(53, 11), topCenterBold.getDimension("Display String"));
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, UserPreferences.DEFAULT_FONT_SIZE);
	}

	@Test
	@EnabledOnOs(OS.WINDOWS)
	void testDimension24ptFont()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, 24);
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(146, 32), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(158, 32), topCenterBold.getDimension("Display String"));
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, UserPreferences.DEFAULT_FONT_SIZE);
	}
}
