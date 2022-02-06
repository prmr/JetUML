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
package ca.mcgill.cs.jetuml.views;

import static ca.mcgill.cs.jetuml.testutils.GeometryUtils.osDependent;
import static ca.mcgill.cs.jetuml.viewers.FontMetrics.DEFAULT_FONT_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;

public class TestStringViewer 
{
	private static int userDefinedFontSize;
	private StringViewer topCenter;
	private StringViewer topCenterPadded;
	private StringViewer topCenterBold;
	private StringViewer bottomCenterPadded;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
	}
	
	@BeforeEach
	public void setup()
	{
		topCenter = StringViewer.get(Alignment.TOP_CENTER);
		topCenterPadded = StringViewer.get(Alignment.TOP_CENTER, TextDecoration.PADDED);
		topCenterBold = StringViewer.get(Alignment.TOP_CENTER, TextDecoration.BOLD);
		bottomCenterPadded = StringViewer.get(Alignment.BOTTOM_CENTER, TextDecoration.PADDED);
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@Test
	public void testFlyweightProperty()
	{
		StringViewer stringViewer = StringViewer.get(Alignment.TOP_CENTER);
		
		assertNotSame(topCenterPadded, stringViewer);
		assertNotSame(bottomCenterPadded, stringViewer);
		assertSame(topCenter, stringViewer);
	}
	
	@Test
	public void testDimensionDefaultFont()
	{
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(0, 0), topCenterPadded.getDimension(""));
		assertEquals(new Dimension(osDependent(73, 69, 69), osDependent(13, 12, 12)), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(osDependent(79, 69, 69), osDependent(13, 12, 12)), topCenterBold.getDimension("Display String"));
		assertEquals(new Dimension(osDependent(87, 83, 83), osDependent(27, 26, 26)), topCenterPadded.getDimension("Display String"));
	}
	
	@Test
	public void testDimension8ptFont()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, 8);
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(0, 0), topCenterPadded.getDimension(""));
		assertEquals(new Dimension(osDependent(49, 46, 46), osDependent(9, 8, 8)), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(osDependent(53, 46, 46), osDependent(9, 8, 8)), topCenterBold.getDimension("Display String"));
		assertEquals(new Dimension(osDependent(63, 60, 60), osDependent(23, 22, 22)), topCenterPadded.getDimension("Display String"));
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
	}

	@Test
	public void testDimension24ptFont()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, 24);
		assertEquals(new Dimension(0, 0), topCenter.getDimension(""));
		assertEquals(new Dimension(0, 0), topCenterPadded.getDimension(""));
		assertEquals(new Dimension(osDependent(146, 139, 139), osDependent(26, 23, 23)), topCenter.getDimension("Display String"));
		assertEquals(new Dimension(osDependent(158, 139, 139), osDependent(26, 23, 23)), topCenterBold.getDimension("Display String"));
		assertEquals(new Dimension(osDependent(160, 153, 153), osDependent(40, 37, 37)), topCenterPadded.getDimension("Display String"));
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
	}
	
	@Test
	public void testWrapString()
	{
		assertEquals("Display String", StringViewer.wrapString("Display String", 15));
		assertEquals("A really long\nstring that\nshould probably\nbe wrapped", 
				StringViewer.wrapString("A really long string that should probably be wrapped", 15));
		assertEquals("Display\nString", StringViewer.wrapString("Display String", 1));
		assertEquals("A\nreally\nlong\nstring\nthat\nshould\nprobably\nbe\nwrapped", 
				StringViewer.wrapString("A really long string that should probably be wrapped", 1));
	}
}
