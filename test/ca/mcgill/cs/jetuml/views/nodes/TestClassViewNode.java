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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.geom.Dimension;

public class TestClassViewNode
{
	private ClassNode aNode1;
	private Graphics2D aGraphics;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aNode1 = new ClassNode();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
	}
	
	@AfterEach
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
		assertEquals(new Dimension(0,0), ((ClassNodeView)aNode1.view()).computeBottom());
		aNode1.setMethods("Foo");
		assertTrue(new Dimension(100,23).equals(((ClassNodeView)aNode1.view()).computeBottom()) || new Dimension(100,24).equals(((ClassNodeView)aNode1.view()).computeBottom()));
		aNode1.setMethods("Foo\nFoo");
		assertTrue(new Dimension(100,39).equals(((ClassNodeView)aNode1.view()).computeBottom()) || new Dimension(100,40).equals(((ClassNodeView)aNode1.view()).computeBottom()));
		aNode1.setMethods("Foo");
		assertTrue(new Dimension(100,23).equals(((ClassNodeView)aNode1.view()).computeBottom()) || new Dimension(100,24).equals(((ClassNodeView)aNode1.view()).computeBottom()));
		aNode1.setMethods("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		assertTrue(new Dimension(310, 24).equals(((ClassNodeView)aNode1.view()).computeBottom()) || new Dimension(310,22).equals(((ClassNodeView)aNode1.view()).computeBottom()));
	}
	
	@Test
	public void testComputeTop()
	{
		assertEquals(new Dimension(100,60), ((ClassNodeView)aNode1.view()).computeTop());
		aNode1.setName("X\nX\nX\nX");
		assertTrue(new Dimension(100,72).equals(((ClassNodeView)aNode1.view()).computeTop()) || new Dimension(100,70).equals(((ClassNodeView)aNode1.view()).computeTop()));
		
		aNode1.setName("");
		assertEquals(new Dimension(100,60), ((ClassNodeView)aNode1.view()).computeTop());
		
		aNode1.setMethods("X");
		assertEquals(new Dimension(100,40), ((ClassNodeView)aNode1.view()).computeTop());
		aNode1.setMethods("X\nX\nX");
		assertEquals(new Dimension(100,40), ((ClassNodeView)aNode1.view()).computeTop());
		
		aNode1.setName("X\nX\nX");
		assertTrue(new Dimension(100,56).equals(((ClassNodeView)aNode1.view()).computeTop()) || new Dimension(100,54).equals(((ClassNodeView)aNode1.view()).computeTop()));
		aNode1.setName("X\nX\nX\nX");
		assertTrue(new Dimension(100,72).equals(((ClassNodeView)aNode1.view()).computeTop()) || new Dimension(100,70).equals(((ClassNodeView)aNode1.view()).computeTop()));
		
		aNode1.setName("X");
		aNode1.setMethods("X");
		aNode1.setAttributes("X");
		assertTrue(new Dimension(100,24).equals(((ClassNodeView)aNode1.view()).computeTop()) || new Dimension(100,22).equals(((ClassNodeView)aNode1.view()).computeTop()));
		
		aNode1.setMethods("");
		assertEquals(new Dimension(100,40), ((ClassNodeView)aNode1.view()).computeTop());
	}
}
