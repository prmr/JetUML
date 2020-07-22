/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package ca.mcgill.cs.jetuml.diagram.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Properties;

public class TestCallNode
{
	private CallNode aNode;
	
	@BeforeEach
	public void setup()
	{
		aNode = new CallNode();
	}
	
	@Test
	public void testGetProperties()
	{
		Properties properties = aNode.properties();
		
		assertEquals(false, properties.get("openBottom").get());
		assertEquals(0, properties.get("x").get());
		assertEquals(0, properties.get("y").get());
		
		aNode.setOpenBottom(true);
		aNode.translate(10, 20);
		properties = aNode.properties();
		assertEquals(true, properties.get("openBottom").get());
		assertEquals(10, properties.get("x").get());
		assertEquals(20, properties.get("y").get());
	}
	
	@Test
	public void testOpenBottom()
	{
		assertFalse(aNode.isOpenBottom());
		aNode.setOpenBottom(true);
		assertTrue(aNode.isOpenBottom());
	}
	
	@Test
	public void testClone_OpenBottom()
	{
		assertFalse(aNode.clone().isOpenBottom());
		aNode.setOpenBottom(true);
		assertTrue(aNode.clone().isOpenBottom());
	}
	
	@Test
	public void testClone_Parent()
	{
		ImplicitParameterNode parent = new ImplicitParameterNode();
		aNode.link(parent);
		assertSame(parent, aNode.clone().getParent());
	}
	
	@Test
	public void testClone_Diagram()
	{
		Diagram diagram = new Diagram(DiagramType.SEQUENCE);
		aNode.attach(diagram);
		assertSame(diagram, aNode.clone().getDiagram().get());
	}
	
	@Test
	public void testRequiresParent()
	{
		assertTrue(aNode.requiresParent());
	}
	
	@Test
	public void testParent()
	{
		ImplicitParameterNode parent = new ImplicitParameterNode();
		assertFalse(aNode.hasParent());
		aNode.link(parent);
		assertTrue(aNode.hasParent());
		assertSame(parent, aNode.getParent());
		aNode.unlink();
		assertFalse(aNode.hasParent());
	}
	
	@Test
	public void testAllowsChildren()
	{
		assertFalse(aNode.allowsChildren());
	}
}
