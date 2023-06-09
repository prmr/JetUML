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
package org.jetuml.diagram.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Properties;
import org.jetuml.diagram.PropertyName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
		
		assertEquals(false, properties.get(PropertyName.OPEN_BOTTOM).get());
		
		aNode.setOpenBottom(true);
		aNode.translate(10, 20);
		properties = aNode.properties();
		assertEquals(true, properties.get(PropertyName.OPEN_BOTTOM).get());
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
}
