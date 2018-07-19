/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.views.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestPackageNodeView
{
	private PackageNode aPackageNode1;
	private Graphics2D aGraphics;
	private Method aGetTopBoundsMethod;
	private Method aGetBottomBoundsMethod;
	
	public TestPackageNodeView() throws ReflectiveOperationException
	{
		aGetTopBoundsMethod = PackageNodeView.class.getDeclaredMethod("getTopBounds");
		aGetTopBoundsMethod.setAccessible(true);
		aGetBottomBoundsMethod = PackageNodeView.class.getDeclaredMethod("getBottomBounds");
		aGetBottomBoundsMethod.setAccessible(true);
	}
	
	private Rectangle getTopBounds(PackageNode pNode)
	{
		try
		{
			return (Rectangle) aGetTopBoundsMethod.invoke(pNode.view());
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
			return (Rectangle) aGetBottomBoundsMethod.invoke(pNode.view());
		}
		catch( ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	/**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
	
	@Before
	public void setup()
	{
		aPackageNode1 = new PackageNode();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
	}
	
	@After
	public void teardown()
	{
		aGraphics.dispose();
	}
	
	@Test
	public void testGetBoundsDefault()
	{
		assertEqualRectangles(0,0,100,80, aPackageNode1.view().getBounds());
	}
	
	@Test
	public void testGetBoundsOffset()
	{
		aPackageNode1.moveTo(new Point(25,25));
		assertEqualRectangles(25,25,100,80, aPackageNode1.view().getBounds());
	}
	
	@Test
	public void testGetBoundsNameNoContent()
	{
		aPackageNode1.setName("Package");
		assertEqualRectangles(0,0,100,80, aPackageNode1.view().getBounds());
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
		assertEqualRectangles(0,0,60,20, getTopBounds(aPackageNode1));
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
