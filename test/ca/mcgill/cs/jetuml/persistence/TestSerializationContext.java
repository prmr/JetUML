/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.persistence;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.PackageNode;

import static org.junit.Assert.*;

public class TestSerializationContext
{
	private SerializationContext aContext;
	private ClassDiagramGraph aGraph;
	private PackageNode aPackage1; // Root
	private PackageNode aPackage2; // Child of aPackage1
	private ClassNode aClassNode1; // Root
	private ClassNode aClassNode2; // Child of aPackage1
	private ClassNode aClassNode3; // Child of aPackage2
	private NoteNode aNoteNode; // Root
	
	@Before
	public void setup()
	{
		aGraph = new ClassDiagramGraph();
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
		aGraph.restoreRootNode(aPackage1);
		aGraph.restoreRootNode(aClassNode1);
		aGraph.restoreRootNode(aNoteNode);
	}
	
	@Test
	public void textInit()
	{
		aContext = new SerializationContext(aGraph);
		assertEquals(0, size());
		assertSame(aGraph, aContext.getGraph());
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
