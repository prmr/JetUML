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
package ca.mcgill.cs.jetuml.viewers.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestDependencyEdgeViewer
{
	private ClassNode aNode1;
	private ClassNode aNode2;
	private DependencyEdge aEdge;
	private Diagram aDiagram;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aNode1 = new ClassNode(); // Bounds [x=0,y=0, w=100, h=60]
		aNode2 = new ClassNode(); // Bounds [x=200,y=0, w=100, h=60]
		aEdge = new DependencyEdge();
		aDiagram = new Diagram(DiagramType.CLASS);
		
		aDiagram.addRootNode(aNode1);
		aDiagram.addRootNode(aNode2);
		aEdge.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(aEdge);
		
		aNode2.translate(200, 0);
	}
	
	@Test
	public void testEdgeViewBounds()
	{
		assertEquals(new Rectangle(99,23,102,12), EdgeViewerRegistry.getBounds(aEdge));

	}
}
