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

import static org.jetuml.testutils.CollectionAssertions.assertThat;
import static org.jetuml.testutils.CollectionAssertions.hasElementsSameAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.junit.jupiter.api.Test;

public class TestSerializationContext
{
	private SerializationContext aContext;
	private Diagram aDiagram = new Diagram(DiagramType.CLASS);
	private PackageNode aPackage1 = new PackageNode(); // Root
	private PackageNode aPackage2 = new PackageNode(); // Child of aPackage1
	private ClassNode aClassNode1= new ClassNode(); // Root
	private ClassNode aClassNode2 = new ClassNode(); // Child of aPackage1
	private ClassNode aClassNode3 = new ClassNode(); // Child of aPackage2
	private NoteNode aNoteNode = new NoteNode(); // Root
	
	private void loadNodes()
	{
		aDiagram.addRootNode(aPackage1);
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aNoteNode);
		aPackage1.addChild(aPackage2);
		aPackage1.addChild(aClassNode2);
		aPackage2.addChild(aClassNode3);
	}
	
	@Test
	void textInit()
	{
		aContext = new SerializationContext(aDiagram);
		assertEquals(0, size());
		assertSame(aDiagram, aContext.diagram());
	}
	
	@Test 
	void testMultipleInsertions()
	{
		loadNodes();
		aContext = new SerializationContext(aDiagram);
	}
	
	@Test 
	void testBasicRetrieval()
	{
		loadNodes();
		aContext = new SerializationContext(aDiagram);
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
	
	@Test
	void testMaintainNodeOrder()
	{
		loadNodes();
		aContext = new SerializationContext(aDiagram);
		assertThat(nodesInsideContext(), hasElementsSameAs, 
				aPackage1, aPackage2, aClassNode3, aClassNode2, aClassNode1, aNoteNode);
	}
	
	@Test
	void testMaintainNodeOrder2()
	{
		Node node1 = new NoteNode();
		Node node2 = new NoteNode();
		aDiagram.addRootNode(aClassNode1);
		aDiagram.addRootNode(aClassNode2);
		aDiagram.addRootNode(aClassNode3);
		aDiagram.addRootNode(aNoteNode);
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);

		aContext = new SerializationContext(aDiagram);
		assertThat(nodesInsideContext(), hasElementsSameAs, 
				aClassNode1, aClassNode2, aClassNode3, aNoteNode, node1, node2);
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
	
	private List<Node> nodesInsideContext()
	{
		List<Node> result = new ArrayList<>();
		aContext.forEach(node -> result.add(node));
		return result;
	}
}
