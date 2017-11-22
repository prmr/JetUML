/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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
package ca.mcgill.cs.stg.jetuml.views.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.nodes.InterfaceNode;
import ca.mcgill.cs.stg.jetuml.views.nodes.InterfaceNodeView;

public class TestInterfaceNodeView
{
	private InterfaceNode aNode1;
	private Graphics2D aGraphics;
	
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
		MultiLineString methods = new MultiLineString();
		methods.setText("Foo");
		aNode1.setMethods(methods);
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
		MultiLineString methods = new MultiLineString();
		methods.setText("Foo");
		aNode1.setMethods(methods);
		assertEquals(new Rectangle(0,0,100,20), ((InterfaceNodeView)aNode1.view()).computeBottom());
		methods.setText("Foo\nFoo");
		assertEquals(new Rectangle(0,0,100,32), ((InterfaceNodeView)aNode1.view()).computeBottom());
		methods.setText("Foo");
		assertEquals(new Rectangle(0,0,100,20), ((InterfaceNodeView)aNode1.view()).computeBottom());
		methods.setText("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		assertEquals(new Rectangle(0,0,350,20), ((InterfaceNodeView)aNode1.view()).computeBottom());
	}
	
	@Test
	public void testComputeTop()
	{
		assertEquals(new Rectangle(0,0,100,60), ((InterfaceNodeView)aNode1.view()).computeTop());
		MultiLineString name = new MultiLineString();
		name.setText("X\nX\nX\nX");
		aNode1.setName(name);
		assertEquals(new Rectangle(0,0,100,64), ((InterfaceNodeView)aNode1.view()).computeTop());
		
		name.setText("");
		assertEquals(new Rectangle(0,0,100,60), ((InterfaceNodeView)aNode1.view()).computeTop());
		
		MultiLineString methods = new MultiLineString();
		methods.setText("X");
		aNode1.setMethods(methods);
		assertEquals(new Rectangle(0,0,100,40), ((InterfaceNodeView)aNode1.view()).computeTop());
		methods.setText("X\nX\nX");
		assertEquals(new Rectangle(0,0,100,40), ((InterfaceNodeView)aNode1.view()).computeTop());
		
		name.setText("X\nX\nX");
		assertEquals(new Rectangle(0,0,100,48), ((InterfaceNodeView)aNode1.view()).computeTop());
		name.setText("X\nX\nX\nX");
		assertEquals(new Rectangle(0,0,100,64), ((InterfaceNodeView)aNode1.view()).computeTop());
	}
}
