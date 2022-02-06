/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
package ca.mcgill.cs.jetuml.viewers.nodes;

import static ca.mcgill.cs.jetuml.testutils.GeometryUtils.osDependent;
import static ca.mcgill.cs.jetuml.viewers.FontMetrics.DEFAULT_FONT_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.diagram.nodes.AbstractPackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestPackageNodeViewer
{
	private static int userDefinedFontSize;
	private PackageNodeViewer aViewer = new PackageNodeViewer();
	private PackageNode aPackageNode1;
	private Graphics2D aGraphics;
	private Method aGetTopBoundsMethod;
	private Method aGetBottomBoundsMethod;
	
	public TestPackageNodeViewer() throws ReflectiveOperationException
	{
		aGetTopBoundsMethod = AbstractPackageNodeViewer.class.getDeclaredMethod("getTopBounds", AbstractPackageNode.class);
		aGetTopBoundsMethod.setAccessible(true);
		aGetBottomBoundsMethod = AbstractPackageNodeViewer.class.getDeclaredMethod("getBottomBounds", AbstractPackageNode.class);
		aGetBottomBoundsMethod.setAccessible(true);
	}
	
	private Rectangle getTopBounds(PackageNode pNode)
	{
		try
		{
			return (Rectangle) aGetTopBoundsMethod.invoke(aViewer, pNode);
		}
		catch( ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private Rectangle getBottomBounds(PackageNode pNode)
	{
		try
		{
			return (Rectangle) aGetBottomBoundsMethod.invoke(aViewer, pNode);
		}
		catch( ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aPackageNode1 = new PackageNode();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
	}
	
	@AfterEach
	public void teardown()
	{
		aGraphics.dispose();
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@Test
	public void testGetBoundsDefault()
	{
		assertEqualRectangles(0,0,100,80, NodeViewerRegistry.getBounds(aPackageNode1));
	}
	
	@Test
	public void testGetBoundsOffset()
	{
		aPackageNode1.moveTo(new Point(25,25));
		assertEqualRectangles(25,25,100,80, NodeViewerRegistry.getBounds(aPackageNode1));
	}
	
	@Test
	public void testGetBoundsNameNoContent()
	{
		aPackageNode1.setName("Package");
		assertEqualRectangles(0,0, osDependent(104,104,101), osDependent(81, 80, 80), NodeViewerRegistry.getBounds(aPackageNode1));
	}
	
	@Test
	public void testGetTopBoundsDefault()
	{
		assertEqualRectangles(0,0,60,20, getTopBounds(aPackageNode1));
	}
	
	@Test
	public void testGetTopBoundsName()
	{
		aPackageNode1.setName("Package");
		assertEqualRectangles(0,0,osDependent(64, 64, 61), osDependent(21, 20, 20), getTopBounds(aPackageNode1));
	}
	
	@Test
	public void testGetBottomBoundsDefault()
	{
		assertEqualRectangles(0,20,100,60, getBottomBounds(aPackageNode1));
	}
	
	private static final void assertEqualRectangles(int pExpectedX, int pExpectedY, int pExpectedWidth, int pExpectedHeight, Rectangle pRectangle)
	{
		assertEquals( pExpectedX, pRectangle.getX());
		assertEquals( pExpectedY, pRectangle.getY());
		assertEquals( pExpectedWidth, pRectangle.getWidth());
		assertEquals( pExpectedHeight, pRectangle.getHeight());
	}
}
