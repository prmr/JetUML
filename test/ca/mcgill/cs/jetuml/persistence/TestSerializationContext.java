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
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;

public class TestSerializationContext
{
	private SerializationContext aContext;
	private Diagram aGraph;
	private PackageNode aPackage1; // Root
	private PackageNode aPackage2; // Child of aPackage1
	private ClassNode aClassNode1; // Root
	private ClassNode aClassNode2; // Child of aPackage1
	private ClassNode aClassNode3; // Child of aPackage2
	private NoteNode aNoteNode; // Root
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aGraph = new Diagram(DiagramType.CLASS);
		aPackage1 = new PackageNode();
		aPackage2 = new PackageNode();
		aClassNode1 = new ClassNode();
		aClassNode2 = new ClassNode();
		aClassNode3 = new ClassNode();
		aNoteNode = new NoteNode();
		aPackage1.addChild(aPackage2);
		aPackage1.addChild(aClassNode2);
		aPackage2.addChild(aClassNode3);
	}
	
	private void loadNodes()
	{
		aGraph.addRootNode(aPackage1);
		aGraph.addRootNode(aClassNode1);
		aGraph.addRootNode(aNoteNode);
	}
	
	@Test
	public void textInit()
	{
		aContext = new SerializationContext(aGraph);
		assertEquals(0, size());
		assertSame(aGraph, aContext.pDiagram());
	}
	
	@Test 
	public void testMultipleInsertions()
	{
		loadNodes();
		aContext = new SerializationContext(aGraph);
	}
	
	@Test 
	public void testBasicRetrieval()
	{
		loadNodes();
		aContext = new SerializationContext(aGraph);
		assertEquals(6, size());
		boolean[] slots = new boolean[6];
		for( Node node : aContext )
		{
			int index = aContext.getId(node);
			if( slots[index] )
			{
				fail("Reused id");
			}
			else
			{
				slots[index] = true;
			}
		}
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
