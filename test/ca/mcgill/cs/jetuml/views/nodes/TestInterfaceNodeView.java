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
import org.junit.Test;

import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.nodes.InterfaceNode;

public class TestInterfaceNodeView
{
	private InterfaceNode aNode1;
	private Graphics2D aGraphics;
	private ClassDiagramGraph aGraph;
	
	@Before
	public void setup()
	{
		aNode1 = new InterfaceNode();
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
		assertEquals(new Rectangle(0,0,0,0), ((InterfaceNodeView)aNode1.view()).computeBottom());
		aNode1.setMethods("Foo");
		assertEquals(new Rectangle(0,0,100,20), ((InterfaceNodeView)aNode1.view()).computeBottom());
		aNode1.setMethods("Foo\nFoo");
		assertEquals(new Rectangle(0,0,100,32), ((InterfaceNodeView)aNode1.view()).computeBottom());
		aNode1.setMethods("Foo");
		assertEquals(new Rectangle(0,0,100,20), ((InterfaceNodeView)aNode1.view()).computeBottom());
		aNode1.setMethods("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		assertEquals(new Rectangle(0,0,350,20), ((InterfaceNodeView)aNode1.view()).computeBottom());
	}
	
	@Test
	public void testComputeTop()
	{
		assertEquals(new Rectangle(0,0,100,60), ((InterfaceNodeView)aNode1.view()).computeTop());
		aNode1.setName("X\nX\nX\nX");
		assertEquals(new Rectangle(0,0,100,64), ((InterfaceNodeView)aNode1.view()).computeTop());
		aNode1.setName("");
		assertEquals(new Rectangle(0,0,100,60), ((InterfaceNodeView)aNode1.view()).computeTop());
		
		aNode1.setMethods("X");
		assertEquals(new Rectangle(0,0,100,40), ((InterfaceNodeView)aNode1.view()).computeTop());
		aNode1.setMethods("X\nX\nX");
		assertEquals(new Rectangle(0,0,100,40), ((InterfaceNodeView)aNode1.view()).computeTop());
		
		aNode1.setName("X\nX\nX");
		assertEquals(new Rectangle(0,0,100,48), ((InterfaceNodeView)aNode1.view()).computeTop());
		aNode1.setName("X\nX\nX\nX");
		assertEquals(new Rectangle(0,0,100,64), ((InterfaceNodeView)aNode1.view()).computeTop());
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
		assertEquals(new Rectangle(10,10,100,100), aNode1.view().getBounds());
		
		aNode1.setName("X");
		aNode1.view().layout(aGraph);
		assertEquals(new Rectangle(10,10,100,80), aNode1.view().getBounds());
		
		// Test layout with snapping
		aNode1.translate(-4, -4);
		aNode1.view().layout(aGraph);
		assertEquals(new Rectangle(10,10,100,80), aNode1.view().getBounds());
	}
}
