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
import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;

public class TestClassViewNode
{
	private ClassNode aNode1;
	private Graphics2D aGraphics;
	private ClassDiagramGraph aGraph;
	
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
		aNode1 = new ClassNode();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		aGraph= new ClassDiagramGraph();
	}
	
	@After
	public void teardown()
	{
		aGraphics.dispose();
	}
	
	@Test
	public void testNeedsMiddle()
	{
		assertFalse(((ClassNodeView)aNode1.view()).needsMiddleCompartment());
		aNode1.setAttributes("Foo");
		assertTrue(((ClassNodeView)aNode1.view()).needsMiddleCompartment());
	}
	
	@Test
	public void testNeedsBottom()
	{
		assertFalse(((ClassNodeView)aNode1.view()).needsBottomCompartment());
		aNode1.setMethods("Foo");
		assertTrue(((ClassNodeView)aNode1.view()).needsBottomCompartment());
	}
	
	@Test
	public void testComputeMiddle()
	{
		assertEquals(0, ((ClassNodeView)aNode1.view()).middleWidth());
		assertEquals(0, ((ClassNodeView)aNode1.view()).middleHeight());
		aNode1.setAttributes("Foo");
		assertEquals(100, ((ClassNodeView)aNode1.view()).middleWidth());
		assertTrue(24 == ((ClassNodeView)aNode1.view()).middleHeight() || 23 == ((ClassNodeView)aNode1.view()).middleHeight());
		aNode1.setAttributes("Foo\nFoo");
		assertEquals(100, ((ClassNodeView)aNode1.view()).middleWidth());
		assertTrue(40 == ((ClassNodeView)aNode1.view()).middleHeight() || 39 == ((ClassNodeView)aNode1.view()).middleHeight());
		aNode1.setAttributes("Foo");
		assertEquals(100, ((ClassNodeView)aNode1.view()).middleWidth());
		assertTrue(24 == ((ClassNodeView)aNode1.view()).middleHeight() || 23 == ((ClassNodeView)aNode1.view()).middleHeight());
		aNode1.setAttributes("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		assertEquals(310, ((ClassNodeView)aNode1.view()).middleWidth());
		assertTrue(24 == ((ClassNodeView)aNode1.view()).middleHeight() || 22 == ((ClassNodeView)aNode1.view()).middleHeight());
	}
	
	@Test
	public void testComputeBottom()
	{
		assertEquals(new Rectangle(0,0,0,0), ((ClassNodeView)aNode1.view()).computeBottom());
		aNode1.setMethods("Foo");
		assertTrue(new Rectangle(0,0,100,23).equals(((ClassNodeView)aNode1.view()).computeBottom()) || new Rectangle(0,0,100,24).equals(((ClassNodeView)aNode1.view()).computeBottom()));
		aNode1.setMethods("Foo\nFoo");
		assertTrue(new Rectangle(0,0,100,39).equals(((ClassNodeView)aNode1.view()).computeBottom()) || new Rectangle(0,0,100,40).equals(((ClassNodeView)aNode1.view()).computeBottom()));
		aNode1.setMethods("Foo");
		assertTrue(new Rectangle(0,0,100,23).equals(((ClassNodeView)aNode1.view()).computeBottom()) || new Rectangle(0,0,100,24).equals(((ClassNodeView)aNode1.view()).computeBottom()));
		aNode1.setMethods("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		assertTrue(new Rectangle(0,0,310, 24).equals(((ClassNodeView)aNode1.view()).computeBottom()) || new Rectangle(0,0,310,22).equals(((ClassNodeView)aNode1.view()).computeBottom()));
	}
	
	@Test
	public void testComputeTop()
	{
		assertEquals(new Rectangle(0,0,100,60), ((ClassNodeView)aNode1.view()).computeTop());
		aNode1.setName("X\nX\nX\nX");
		assertTrue(new Rectangle(0,0,100,72).equals(((ClassNodeView)aNode1.view()).computeTop()) || new Rectangle(0,0,100,70).equals(((ClassNodeView)aNode1.view()).computeTop()));
		
		aNode1.setName("");
		assertEquals(new Rectangle(0,0,100,60), ((ClassNodeView)aNode1.view()).computeTop());
		
		aNode1.setMethods("X");
		assertEquals(new Rectangle(0,0,100,40), ((ClassNodeView)aNode1.view()).computeTop());
		aNode1.setMethods("X\nX\nX");
		assertEquals(new Rectangle(0,0,100,40), ((ClassNodeView)aNode1.view()).computeTop());
		
		aNode1.setName("X\nX\nX");
		assertTrue(new Rectangle(0,0,100,56).equals(((ClassNodeView)aNode1.view()).computeTop()) || new Rectangle(0,0,100,54).equals(((ClassNodeView)aNode1.view()).computeTop()));
		aNode1.setName("X\nX\nX\nX");
		assertTrue(new Rectangle(0,0,100,72).equals(((ClassNodeView)aNode1.view()).computeTop()) || new Rectangle(0,0,100,70).equals(((ClassNodeView)aNode1.view()).computeTop()));
		
		aNode1.setName("X");
		aNode1.setMethods("X");
		aNode1.setAttributes("X");
		assertTrue(new Rectangle(0,0,100,24).equals(((ClassNodeView)aNode1.view()).computeTop()) || new Rectangle(0,0,100,22).equals(((ClassNodeView)aNode1.view()).computeTop()));
		
		aNode1.setMethods("");
		assertEquals(new Rectangle(0,0,100,40), ((ClassNodeView)aNode1.view()).computeTop());
	}
	
	@Test
	public void testLayout()
	{
		// Test layout with no snapping (grid size is 10)
		aNode1.translate(10, 10);
		aNode1.view().layout(aGraph);
		assertEquals(new Rectangle(10,10,100,60), aNode1.view().getBounds());
		
		aNode1.setName("X\nX\nX\nX");
		aNode1.view().layout(aGraph);
		assertEquals(new Rectangle(10,10,100,80), aNode1.view().getBounds());
		
		aNode1.setMethods("X\nX");
		aNode1.view().layout(aGraph);
		assertEquals(new Rectangle(10,10,100,120), aNode1.view().getBounds());
		
		aNode1.setName("X");
		aNode1.setMethods("X");
		aNode1.view().layout(aGraph);
		assertEquals(new Rectangle(10,10,100,80), aNode1.view().getBounds());
		
		// Test layout with snapping
		aNode1.translate(-4, -4);
		aNode1.view().layout(aGraph);
		assertEquals(new Rectangle(10,10,100,80), aNode1.view().getBounds());
	}
}
