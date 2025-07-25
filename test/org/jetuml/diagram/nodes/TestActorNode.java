/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
package org.jetuml.diagram.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Properties;
import org.jetuml.diagram.PropertyName;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestActorNode
{
	private ActorNode aNode;
	
	@BeforeEach
	public void setup()
	{
		aNode = new ActorNode();
	}
	
	@Test
	public void testGetProperties()
	{
		Properties properties = aNode.properties();
		
		assertEquals("Actor", properties.get(PropertyName.NAME).get());
		
		aNode.setName("Foo");
		aNode.translate(10, 20);
		properties = aNode.properties();
		assertEquals("Foo", properties.get(PropertyName.NAME).get());
	}
	
	@Test
	public void testProperty_Name()
	{
		aNode.setName("Foo");
		assertEquals("Foo", aNode.getName());
	}
	
	@Test
	public void testInitialPosition()
	{
		assertEquals(new Point(0,0), aNode.position());
	}
	
	@Test
	public void testMoveTo()
	{
		aNode.moveTo(new Point(10, 20));
		assertEquals(new Point(10,20), aNode.position());
	}
	
	/*
	 * The position should be a clone
	 */
	@Test
	public void testClone_OfPosition()
	{
		ActorNode clone = (ActorNode) aNode.clone();
		assertNotSame(aNode.position(), clone.position());
		clone.properties().get(PropertyName.NAME).set("Foo");
		assertNotEquals("Foo", aNode.properties().get(PropertyName.NAME));
	}
	
	/*
	 * The name should be the same object
	 */
	@Test
	public void testClone_OfName()
	{
		aNode.setName("Foo");
		ActorNode clone = (ActorNode) aNode.clone();
		assertSame(aNode.getName(), clone.getName());
	}
	
	/*
	 * The name should be the same object
	 */
	@Test
	public void testClone_OfProperties()
	{
		ActorNode clone = (ActorNode) aNode.clone();
		assertNotSame(aNode.properties(), clone.properties());
	}
	
	@Test
	public void testHasParent()
	{
		assertFalse(aNode.hasParent());
	}
	
	@Test
	public void testRequiresParent()
	{
		assertFalse(aNode.requiresParent());
	}
	
	@Test
	public void testGetChildren()
	{
		assertTrue(aNode.getChildren().isEmpty());
	}
}
