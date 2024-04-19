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
import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.geom.Dimension;
import org.jetuml.rendering.StringRenderer.Alignment;
import org.jetuml.rendering.StringRenderer.TextDecoration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.Test;

public class TestStringViewer 
{

	private static String userDefinedFontName;
	private static int userDefinedFontSize;
	
	private StringRenderer topCenter;
	private StringRenderer topCenterPadded;
	private StringRenderer topCenterBold;
	private StringRenderer bottomCenterPadded;
	private StringRenderer topCenterItalics;
	private StringRenderer topCenterBoldItalics;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontName = UserPreferences.instance().getString(UserPreferences.StringPreference.fontName);
		UserPreferences.instance().setString(StringPreference.fontName, DEFAULT_FONT_NAME);
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
	}
	
	@BeforeEach
	public void setup()
	{
		topCenter = StringRenderer.get(Alignment.TOP_CENTER);
		topCenterPadded = StringRenderer.get(Alignment.TOP_CENTER, TextDecoration.PADDED);
		topCenterBold = StringRenderer.get(Alignment.TOP_CENTER, TextDecoration.BOLD);
		bottomCenterPadded = StringRenderer.get(Alignment.BOTTOM_CENTER, TextDecoration.PADDED);
		topCenterItalics = StringRenderer.get(Alignment.TOP_LEFT, TextDecoration.ITALIC);
		topCenterBoldItalics = StringRenderer.get(Alignment.TOP_LEFT, TextDecoration.BOLD, TextDecoration.ITALIC);
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setString(StringPreference.fontName, userDefinedFontName);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@Test
	public void testFlyweightProperty()
	{
		StringRenderer stringViewer = StringRenderer.get(Alignment.TOP_CENTER);
		
		assertNotSame(topCenterPadded, stringViewer);
		assertNotSame(bottomCenterPadded, stringViewer);
		assertSame(topCenter, stringViewer);
	}
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	public void testDimensionDefaultFont()
	{
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(0, 0), topCenterPadded.getDimension(""));
		assertEquals(new Dimension(73, 16), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(79, 16), topCenterBold.getDimension("Display String"));
		assertEquals(new Dimension(87, 28), topCenterPadded.getDimension("Display String"));
	}
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	public void testDimension8ptFont()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, 8);
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(0, 0), topCenterPadded.getDimension(""));
		assertEquals(new Dimension(49, 11), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(53, 11), topCenterBold.getDimension("Display String"));
		assertEquals(new Dimension(63, 23), topCenterPadded.getDimension("Display String"));
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
	}

	@Test
	@EnabledOnOs(OS.WINDOWS)
	public void testDimension24ptFont()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, 24);
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(0, 0), topCenterPadded.getDimension(""));
		assertEquals(new Dimension(146, 32), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(158, 32), topCenterBold.getDimension("Display String"));
		assertEquals(new Dimension(160, 44), topCenterPadded.getDimension("Display String"));
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
	}
	
	@Test
	public void testWrapString()
	{
		assertEquals("Display String", StringRenderer.wrapString("Display String", 15));
		assertEquals("A really long\nstring that\nshould probably\nbe wrapped", 
				StringRenderer.wrapString("A really long string that should probably be wrapped", 15));
		assertEquals("Display\nString", StringRenderer.wrapString("Display String", 1));
		assertEquals("A\nreally\nlong\nstring\nthat\nshould\nprobably\nbe\nwrapped", 
				StringRenderer.wrapString("A really long string that should probably be wrapped", 1));
	}
	
	/**
	 * getHeight delegates the actual computation of the height to FontMetrics, which is tested in TestFontMetrics.
	 * Instead, we need to test whether the correct style (bold, italics) is applied to the font we want to get the height of.
	 */
	@Test
	public void testGetHeight()
	{
		try 
		{
			Field boldField = StringRenderer.class.getDeclaredField("aBold");
			Field italicsField = StringRenderer.class.getDeclaredField("aItalic");
			boldField.setAccessible(true);
			italicsField.setAccessible(true);
			
			// No bold, no italics
			assertFalse(boldField.getBoolean(topCenter));
			assertFalse(italicsField.getBoolean(topCenter));
			
			// Yes bold, no italics
			assertTrue(boldField.getBoolean(topCenterBold));
			assertFalse(italicsField.getBoolean(topCenterBold));
			
			// No bold, yes italics
			assertFalse(boldField.getBoolean(topCenterItalics));
			assertTrue(italicsField.getBoolean(topCenterItalics));
			
			// Yes bold, yes italics
			assertTrue(boldField.getBoolean(topCenterBoldItalics));
			assertTrue(italicsField.getBoolean(topCenterBoldItalics));
		} 
		catch (ReflectiveOperationException e) 
		{
			e.printStackTrace();
		}
	}
}
