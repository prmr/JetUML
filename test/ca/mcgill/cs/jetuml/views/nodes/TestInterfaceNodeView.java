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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.geom.Dimension;

public class TestInterfaceNodeView
{
	private InterfaceNode aNode1;
	private Graphics2D aGraphics;
	
	@BeforeClass
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@Before
	public void setup()
	{
		aNode1 = new InterfaceNode();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
	}
	
	@After
	public void teardown()
	{
		aGraphics.dispose();
	}
	
	@Test
	public void testNeedsMiddle()
	{
		assertFalse(((InterfaceNodeView)aNode1.view()).needsMiddleCompartment());
	}
	
	@Test
	public void testNeedsBottom()
	{
		assertFalse(((InterfaceNodeView)aNode1.view()).needsBottomCompartment());
		aNode1.setMethods("Foo");
		assertTrue(((InterfaceNodeView)aNode1.view()).needsBottomCompartment());
	}
	
	@Test
	public void testComputeMiddle()
	{
		assertEquals(0, ((InterfaceNodeView)aNode1.view()).middleWidth());
		assertEquals(0, ((InterfaceNodeView)aNode1.view()).middleHeight());
	}
	
	@Test
	public void testComputeBottom()
	{
		assertEquals(new Dimension(0,0), ((InterfaceNodeView)aNode1.view()).computeBottom());
		aNode1.setMethods("Foo");
		assertTrue(new Dimension(100,23).equals(((InterfaceNodeView)aNode1.view()).computeBottom())||
				new Dimension(100,24).equals(((InterfaceNodeView)aNode1.view()).computeBottom()));
		aNode1.setMethods("Foo\nFoo");
		assertTrue(new Dimension(100,39).equals(((InterfaceNodeView)aNode1.view()).computeBottom())||
				new Dimension(100,40).equals(((InterfaceNodeView)aNode1.view()).computeBottom()));
		aNode1.setMethods("Foo");
		assertTrue(new Dimension(100,23).equals(((InterfaceNodeView)aNode1.view()).computeBottom())||
				new Dimension(100,24).equals(((InterfaceNodeView)aNode1.view()).computeBottom()));
		aNode1.setMethods("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		assertTrue(new Dimension(310,22).equals(((InterfaceNodeView)aNode1.view()).computeBottom())||
				new Dimension(310,24).equals(((InterfaceNodeView)aNode1.view()).computeBottom()));
	}
	
	@Test
	public void testComputeTop()
	{
		assertEquals(new Dimension(100,60), ((InterfaceNodeView)aNode1.view()).computeTop());
		aNode1.setName("X\nX\nX\nX");
		assertTrue(new Dimension(100,70).equals(((InterfaceNodeView)aNode1.view()).computeTop()) ||
					new Dimension(100,72).equals(((InterfaceNodeView)aNode1.view()).computeTop()));
		aNode1.setName("");
		assertEquals(new Dimension(100,60), ((InterfaceNodeView)aNode1.view()).computeTop());
		
		aNode1.setMethods("X");
		assertEquals(new Dimension(100,40), ((InterfaceNodeView)aNode1.view()).computeTop());
		aNode1.setMethods("X\nX\nX");
		assertEquals(new Dimension(100,40), ((InterfaceNodeView)aNode1.view()).computeTop());
		
		aNode1.setName("X\nX\nX");
		assertTrue(new Dimension(100,54).equals(((InterfaceNodeView)aNode1.view()).computeTop()) ||
					new Dimension(100,56).equals(((InterfaceNodeView)aNode1.view()).computeTop()));
		aNode1.setName("X\nX\nX\nX");
		assertTrue(new Dimension(100,70).equals(((InterfaceNodeView)aNode1.view()).computeTop()) ||
				new Dimension(100,72).equals(((InterfaceNodeView)aNode1.view()).computeTop()));
	}
}
