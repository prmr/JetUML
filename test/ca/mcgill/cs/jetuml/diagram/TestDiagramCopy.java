/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2019 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;

/**
 * For testing method copy of class Diagram.
 */
public class TestDiagramCopy
{
	private Diagram aClassDiagram;
	
	@BeforeEach
	public void setUp()
	{
		aClassDiagram = new Diagram(DiagramType.CLASS);
	}
	
	@Test
	public void test_empty()
	{
		assertEquals(0, aClassDiagram.copy().edges().size());
		assertEquals(0, aClassDiagram.copy().rootNodes().size());
	}
	
	@Test
	public void test_NoEdges()
	{
		aClassDiagram.addRootNode(new ClassNode());
		Diagram copy = aClassDiagram.copy();
		assertEquals(0, aClassDiagram.copy().edges().size());
		assertEquals(1, copy.rootNodes().size());
		Node node = copy.rootNodes().get(0);
		assertNotSame(aClassDiagram.rootNodes().get(0), node);
	}
	
	@Test
	public void test_oneEdge()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		aClassDiagram.addRootNode(node1);
		aClassDiagram.addRootNode(node2);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(node1, node2, aClassDiagram);
		aClassDiagram.addEdge(edge);
		Diagram copy = aClassDiagram.copy();
		assertNotSame(aClassDiagram.rootNodes().get(0), copy.rootNodes().get(0));
		assertNotSame(aClassDiagram.rootNodes().get(1), copy.rootNodes().get(1));
		assertNotSame(aClassDiagram.edges().get(0), copy.edges().get(0));
		assertSame(copy.rootNodes().get(0), copy.edges().get(0).getStart());
		assertSame(copy.rootNodes().get(1), copy.edges().get(0).getEnd());
	}
}
