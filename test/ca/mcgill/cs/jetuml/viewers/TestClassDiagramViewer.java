/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
package ca.mcgill.cs.jetuml.viewers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.geom.EdgePath;
import ca.mcgill.cs.jetuml.geom.Point;

/**
 * Tests for ClassDiagramViewer 
 */
public class TestClassDiagramViewer 
{
	private final Diagram aDiagram = new Diagram(DiagramType.CLASS);
	private ClassDiagramViewer aClassDiagramViewer;
	private GeneralizationEdge aImplementationEdge;
	private AggregationEdge aAggregationEdge;
	private DependencyEdge aDependencyEdge;
	private ClassNode aNodeA;
	private ClassNode aNodeB;
	
	/**
	 * Connects aDependencyEdge from aNodeB to aNodeA and stores its EdgePath in storage.
	 */
	private void setUpAndStoreDependencyEdge()
	{
		aDependencyEdge.connect(aNodeB, aNodeA, aDiagram);
		aDiagram.addEdge(aDependencyEdge);
		aClassDiagramViewer.store(aDependencyEdge, new EdgePath(new Point(0, 0), new Point(0, 100)));
	}
	@BeforeEach
	public void setUp()
	{
		aClassDiagramViewer = (ClassDiagramViewer) DiagramType.viewerFor(aDiagram);
		aImplementationEdge = new GeneralizationEdge(GeneralizationEdge.Type.Implementation);
		aAggregationEdge = new AggregationEdge(AggregationEdge.Type.Aggregation);
		aDependencyEdge = new DependencyEdge();
		aNodeA = new ClassNode();
		aNodeB = new ClassNode();
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
	}
	
	@Test
	public void testConnectionPointAvailableInStorage()
	{
		setUpAndStoreDependencyEdge();
		assertFalse(aClassDiagramViewer.connectionPointAvailableInStorage(new Point(0, 0)));
		assertFalse(aClassDiagramViewer.connectionPointAvailableInStorage(new Point(0, 100)));
		assertTrue(aClassDiagramViewer.connectionPointAvailableInStorage(new Point(0, 50)));
	}
	
	@Test
	public void testStoredEdgesWithSameNodes()
	{
		aImplementationEdge.connect(aNodeB, aNodeA, aDiagram);
		aAggregationEdge.connect(aNodeB, aNodeA, aDiagram);
		aDiagram.addEdge(aImplementationEdge);
		aDiagram.addEdge(aAggregationEdge);
		aClassDiagramViewer.store(aImplementationEdge, new EdgePath(new Point(0, 0), new Point(0, 100), new Point(100, 100), new Point(200, 100)));
		List<Edge> result = aClassDiagramViewer.storedEdgesWithSameNodes(aAggregationEdge);
		assertTrue(result.size() == 1);
		assertTrue(result.contains(aImplementationEdge));
	}
	
	@Test
	public void testStoredEdgesConnctedTo()
	{
		setUpAndStoreDependencyEdge();
		List<Edge> result = aClassDiagramViewer.storedEdgesConnectedTo(aNodeA);
		assertTrue(result.size() == 1);
		assertEquals(aDependencyEdge, result.get(0));
	}
	
	@Test
	public void testStorageContains()
	{
		setUpAndStoreDependencyEdge();
		assertTrue(aClassDiagramViewer.storageContains(aDependencyEdge));
		assertFalse(aClassDiagramViewer.storageContains(aImplementationEdge));
		
	}
	
	@Test
	public void testStoredEdgePath()
	{
		setUpAndStoreDependencyEdge();
		assertEquals(new EdgePath(new Point(0, 0), new Point(0, 100)), aClassDiagramViewer.storedEdgePath(aDependencyEdge));
	}
	
}
