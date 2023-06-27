/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020-2023 by McGill University.
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
package org.jetuml.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ClassNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestDeserializationContext
{
	private Diagram aDiagram;
	private DeserializationContext aContext;
	private ClassNode aClassNode1; 
	private ClassNode aClassNode2; 
	private ClassNode aClassNode3; 

	@BeforeEach
	void setup()
	{
		aDiagram = new Diagram(DiagramType.CLASS);
		aClassNode1 = new ClassNode();
		aClassNode2 = new ClassNode();
		aClassNode3 = new ClassNode();
	}
	
	@Test
	void testIdExists()
	{
		aContext = new DeserializationContext(aDiagram);
		aContext.addNode(aClassNode1, 1);
		aContext.addNode(aClassNode2, 2);
		assertTrue(aContext.idExists(1));
		assertTrue(aContext.idExists(2));
		assertFalse(aContext.idExists(3));
		assertFalse(aContext.idExists(4));
	}
	
	@Test
	void textInit()
	{
		aContext = new DeserializationContext(aDiagram);
		assertEquals(0, size());
		assertSame(aDiagram, aContext.diagram());
	}
	
	@Test
	void testAddGet()
	{
		aContext = new DeserializationContext(aDiagram);
		aContext.addNode(aClassNode1, 0);
		assertEquals(1, size());
		assertSame(aClassNode1, aContext.getNode(0));
		aContext.addNode(aClassNode2, 1);
		assertEquals(2, size());
		assertSame(aClassNode1, aContext.getNode(0));
		assertSame(aClassNode2, aContext.getNode(1));
		aContext.addNode(aClassNode3, 2);
		assertEquals(3, size());
		assertSame(aClassNode1, aContext.getNode(0));
		assertSame(aClassNode2, aContext.getNode(1));
		assertSame(aClassNode3, aContext.getNode(2));
		
		// Add the same node again, with the same id.
		aContext.addNode(aClassNode1, 0);
		assertEquals(3, size());
		assertSame(aClassNode1, aContext.getNode(0));
		assertSame(aClassNode2, aContext.getNode(1));
		assertSame(aClassNode3, aContext.getNode(2));
		
		// Add the same node again, with a different id
		aContext.addNode(aClassNode1, 4);
		assertEquals(3, size());
		assertSame(aClassNode1, aContext.getNode(4));
		assertSame(aClassNode2, aContext.getNode(1));
		assertSame(aClassNode3, aContext.getNode(2));
	}
	
	private int size()
	{
		int size = 0;
		for( @SuppressWarnings("unused") Node n : aContext )
		{
			size++;
		}
		return size;
	}
}
