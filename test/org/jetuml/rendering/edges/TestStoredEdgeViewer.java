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
package org.jetuml.rendering.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.AssociationEdge.Directionality;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.ArrowHead;
import org.jetuml.rendering.ClassDiagramRenderer;
import org.jetuml.rendering.EdgePath;
import org.jetuml.rendering.LineStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the StoredEdgeRenderer class.
 */
public class TestStoredEdgeViewer 
{
	private final Diagram aDiagram = new Diagram(DiagramType.CLASS);
	private final ClassDiagramRenderer aRenderer = new ClassDiagramRenderer(aDiagram);
	private StoredEdgeRenderer aStoredEdgeViewer = (StoredEdgeRenderer) aRenderer.rendererFor(AggregationEdge.class);
	private GeneralizationEdge aInheritanceEdge;
	private GeneralizationEdge aImplementationEdge;
	private AggregationEdge aAggregationEdge;
	private AggregationEdge aCompositionEdge;
	private AssociationEdge aAssociationEdge;
	private DependencyEdge aDependencyEdge;
	private ClassNode aNodeA;
	private ClassNode aNodeB;
	
	@BeforeEach
	public void setUp()
	{
		aInheritanceEdge = new GeneralizationEdge(GeneralizationEdge.Type.Inheritance);
		aImplementationEdge = new GeneralizationEdge(GeneralizationEdge.Type.Implementation);
		aAggregationEdge = new AggregationEdge(AggregationEdge.Type.Aggregation);
		aCompositionEdge = new AggregationEdge(AggregationEdge.Type.Composition);
		aAssociationEdge = new AssociationEdge();
		aDependencyEdge = new DependencyEdge();
		aNodeA = new ClassNode();
		aNodeB = new ClassNode();
		aDiagram.addRootNode(aNodeA);
		aDiagram.addRootNode(aNodeB);
		
	}

	@Test
	public void testGetLineStyle()
	{
		assertEquals(LineStyle.SOLID, getLineStyle(aInheritanceEdge));
		assertEquals(LineStyle.DOTTED, getLineStyle(aImplementationEdge));
		assertEquals(LineStyle.SOLID, getLineStyle(aAggregationEdge));
		assertEquals(LineStyle.SOLID, getLineStyle(aCompositionEdge));
		assertEquals(LineStyle.SOLID, getLineStyle(aAssociationEdge));
		assertEquals(LineStyle.DOTTED, getLineStyle(aDependencyEdge));
	}
	
	@Test
	public void testGetArrowStart_aggregation()
	{
		assertEquals(ArrowHead.DIAMOND, getArrowStart(aAggregationEdge));
		assertEquals(ArrowHead.BLACK_DIAMOND, getArrowStart(aCompositionEdge));
	}
	
	@Test
	public void testGetArrowStart_generalization()
	{
		assertEquals(ArrowHead.NONE, getArrowStart(aInheritanceEdge));
		assertEquals(ArrowHead.NONE, getArrowStart(aImplementationEdge));
	}
	
	@Test
	public void testGetArrowStart_association()
	{
		assertEquals(ArrowHead.NONE, getArrowStart(aAssociationEdge));
		aAssociationEdge.setDirectionality(Directionality.Unidirectional);
		assertEquals(ArrowHead.NONE, getArrowStart(aAssociationEdge));
		aAssociationEdge.setDirectionality(Directionality.Bidirectional);
		assertEquals(ArrowHead.V, getArrowStart(aAssociationEdge));
	}
	
	@Test
	public void testGetArrowStart_dependency()
	{
		assertEquals(ArrowHead.NONE, getArrowStart(aDependencyEdge));
		aDependencyEdge.setDirectionality(DependencyEdge.Directionality.Unidirectional);
		assertEquals(ArrowHead.NONE, getArrowStart(aDependencyEdge));
		aDependencyEdge.setDirectionality(DependencyEdge.Directionality.Bidirectional);
		assertEquals(ArrowHead.V, getArrowStart(aDependencyEdge));
	}
	
	@Test
	public void testGetArrowEnd_aggregation()
	{
		assertEquals(ArrowHead.NONE, getArrowEnd(aAggregationEdge));
		assertEquals(ArrowHead.NONE, getArrowEnd(aCompositionEdge));
	}
	
	@Test
	public void testGetArrowEnd_generalization()
	{
		assertEquals(ArrowHead.TRIANGLE, getArrowEnd(aInheritanceEdge));
		assertEquals(ArrowHead.TRIANGLE, getArrowEnd(aImplementationEdge));
	}
	
	@Test
	public void testGetArrowEnd_association()
	{
		assertEquals(ArrowHead.NONE, getArrowEnd(aAssociationEdge));
		aAssociationEdge.setDirectionality(Directionality.Unidirectional);
		assertEquals(ArrowHead.V, getArrowEnd(aAssociationEdge));
		aAssociationEdge.setDirectionality(Directionality.Bidirectional);
		assertEquals(ArrowHead.V, getArrowEnd(aAssociationEdge));
	}
	
	@Test
	public void testGetArrowEnd_dependency()
	{
		assertEquals(ArrowHead.V, getArrowEnd(aDependencyEdge));
		aDependencyEdge.setDirectionality(DependencyEdge.Directionality.Bidirectional);
		assertEquals(ArrowHead.V, getArrowEnd(aDependencyEdge));
	}
	
	@Test
	public void testgetStartLabel()
	{
		aAggregationEdge.setStartLabel("test");
		aCompositionEdge.setStartLabel("test");
		aAssociationEdge.setStartLabel("test");
		assertEquals("", getStartLabel(aInheritanceEdge));
		assertEquals("", getStartLabel(aImplementationEdge));
		assertEquals("", getStartLabel(aDependencyEdge));
		assertEquals("test", getStartLabel(aAggregationEdge));
		assertEquals("test", getStartLabel(aCompositionEdge));
		assertEquals("test", getStartLabel(aAssociationEdge));
	}

	@Test
	public void testgetMiddleLabel()
	{
		aAggregationEdge.setMiddleLabel("test");
		aCompositionEdge.setMiddleLabel("test");
		aAssociationEdge.setMiddleLabel("test");
		aDependencyEdge.setMiddleLabel("test");
		assertEquals("", getMiddleLabel(aInheritanceEdge));
		assertEquals("", getMiddleLabel(aImplementationEdge));
		assertEquals("test", getMiddleLabel(aDependencyEdge));
		assertEquals("test", getMiddleLabel(aAggregationEdge));
		assertEquals("test", getMiddleLabel(aCompositionEdge));
		assertEquals("test", getMiddleLabel(aAssociationEdge));
	}
	
	@Test
	public void testgetEndLabel()
	{
		aAssociationEdge.setEndLabel("test");
		aAggregationEdge.setEndLabel("test");
		aCompositionEdge.setEndLabel("test");
		aAssociationEdge.setEndLabel("test");
		assertEquals("", getEndLabel(aInheritanceEdge));
		assertEquals("", getEndLabel(aImplementationEdge));
		assertEquals("", getEndLabel(aDependencyEdge));
		assertEquals("test", getEndLabel(aAggregationEdge));
		assertEquals("test", getEndLabel(aCompositionEdge));
		assertEquals("test", getEndLabel(aAssociationEdge));
	}

	
	@Test
	public void testContains()
	{
		aDependencyEdge.connect(aNodeB, aNodeA);
		aDiagram.addEdge(aDependencyEdge);
		store(aDependencyEdge, new EdgePath(new Point(0, 0), new Point(0, 100)));
		assertTrue(aStoredEdgeViewer.contains(aDependencyEdge, new Point(0, 50)));
		assertTrue(aStoredEdgeViewer.contains(aDependencyEdge, new Point(1, 1)));
		assertFalse(aStoredEdgeViewer.contains(aDependencyEdge, new Point(10, 50)));
	}

	@Test
	public void testGetConnectionPoints()
	{
		aDependencyEdge.connect(aNodeB, aNodeA);
		aDiagram.addEdge(aDependencyEdge);
		store(aDependencyEdge, new EdgePath(new Point(0, 0), new Point(0, 100)));
		assertEquals(new Point(0, 0), aStoredEdgeViewer.getConnectionPoints(aDependencyEdge).point1());
		assertEquals(new Point(0, 100), aStoredEdgeViewer.getConnectionPoints(aDependencyEdge).point2());
	}
	
	@Test
	public void testGetStoredEdgePath()
	{
		aDependencyEdge.connect(aNodeB, aNodeA);
		aDiagram.addEdge(aDependencyEdge);
		store(aDependencyEdge, new EdgePath(new Point(0, 0), new Point(0, 100)));
		assertEquals(new EdgePath(new Point(0, 0), new Point(0, 100)), getStoredEdgePath(aDependencyEdge));
	}
	
	
	
	
	
	/// Private reflexive helper methods:
	
	private LineStyle getLineStyle(Edge pEdge)
	{
		try
		{
			Method method = StoredEdgeRenderer.class.getDeclaredMethod("getLineStyle", Edge.class);
			method.setAccessible(true);
			return (LineStyle) method.invoke(aStoredEdgeViewer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private ArrowHead getArrowStart(Edge pEdge)
	{
		try
		{
			Method method = StoredEdgeRenderer.class.getDeclaredMethod("getArrowStart", Edge.class);
			method.setAccessible(true);
			return (ArrowHead) method.invoke(aStoredEdgeViewer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}

	private ArrowHead getArrowEnd(Edge pEdge)
	{
		try
		{
			Method method = StoredEdgeRenderer.class.getDeclaredMethod("getArrowEnd", Edge.class);
			method.setAccessible(true);
			return (ArrowHead) method.invoke(aStoredEdgeViewer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private String getStartLabel(Edge pEdge)
	{
		try
		{
			Method method = StoredEdgeRenderer.class.getDeclaredMethod("getStartLabel", Edge.class);
			method.setAccessible(true);
			return (String) method.invoke(aStoredEdgeViewer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private String getMiddleLabel(Edge pEdge)
	{
		try
		{
			Method method = StoredEdgeRenderer.class.getDeclaredMethod("getMiddleLabel", Edge.class);
			method.setAccessible(true);
			return (String) method.invoke(aStoredEdgeViewer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private String getEndLabel(Edge pEdge)
	{
		try
		{
			Method method = StoredEdgeRenderer.class.getDeclaredMethod("getEndLabel", Edge.class);
			method.setAccessible(true);
			return (String) method.invoke(aStoredEdgeViewer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	private EdgePath getStoredEdgePath(Edge pEdge)
	{
		try
		{
			Method method = StoredEdgeRenderer.class.getDeclaredMethod("getStoredEdgePath", Edge.class);
			method.setAccessible(true);
			return (EdgePath) method.invoke(aStoredEdgeViewer, pEdge);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	/*
	 * Stores an edge path in the layouter of the active classdiagramrenderer
	 */
	private void store(Edge pEdge, EdgePath pEdgePath)
	{
		try 
		{
			Field edgeStorage = ClassDiagramRenderer.class.getDeclaredField("aEdgeStorage");
			edgeStorage.setAccessible(true);
			((EdgeStorage)edgeStorage.get(aRenderer)).store(pEdge, pEdgePath);
		}
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
}
