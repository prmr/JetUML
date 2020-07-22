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
package ca.mcgill.cs.jetuml.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;

public class TestDeserializationContext
{
	private Diagram aGraph;
	private DeserializationContext aContext;
	private ClassNode aClassNode1; 
	private ClassNode aClassNode2; 
	private ClassNode aClassNode3; 

	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aGraph = new Diagram(DiagramType.CLASS);
		aClassNode1 = new ClassNode();
		aClassNode2 = new ClassNode();
		aClassNode3 = new ClassNode();
	}
	
	@Test
	public void textInit()
	{
		aContext = new DeserializationContext(aGraph);
		assertEquals(0, size());
		assertSame(aGraph, aContext.pDiagram());
	}
	
	@Test
	public void testAddGet()
	{
		aContext = new DeserializationContext(aGraph);
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
