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

package org.jetuml.diagram.builder.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramRenderer;
import org.junit.jupiter.api.Test;

public class TestClassDiagramEdgeConstraints
{
	private Diagram aDiagram = new Diagram(DiagramType.CLASS);
	private DiagramRenderer aRenderer = DiagramType.newRendererInstanceFor(aDiagram);
	private ClassNode aNode1 = new ClassNode();;
	private ClassNode aNode2 = new ClassNode();;
	private DependencyEdge aEdge1 = new DependencyEdge();
	private GeneralizationEdge aGen1 = new GeneralizationEdge();
	private Point aPoint = new Point(0,0);
	
	private void createDiagram()
	{
		aNode2.moveTo(new Point(0,100));
		aDiagram.addRootNode(aNode1);
		aDiagram.addRootNode(aNode2);
	}
	
	@Test
	void testNoSelfGeneralizationNotAGeneralizationEdge()
	{
		createDiagram();
		assertTrue(ClassDiagramEdgeConstraints.noSelfGeneralization().satisfied(aEdge1, aNode1, new ClassNode(), aPoint, aPoint, aRenderer));
		assertTrue(ClassDiagramEdgeConstraints.noSelfGeneralization().satisfied(aEdge1, aNode1, aNode1, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoSelfGeneralizationGeneralizationEdge()
	{
		createDiagram();
		assertTrue(ClassDiagramEdgeConstraints.noSelfGeneralization().satisfied(aGen1, aNode1, aNode2,aPoint, aPoint, aRenderer));
		assertFalse(ClassDiagramEdgeConstraints.noSelfGeneralization().satisfied(aGen1, aNode1, aNode1,aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoDirectCycles_NotADependency()
	{
		createDiagram();
		assertTrue(ClassDiagramEdgeConstraints.noDirectCycles(DependencyEdge.class).satisfied(new GeneralizationEdge(), aNode1, aNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoDirectCycles_NoExistingEdge()
	{
		createDiagram();
		assertTrue(ClassDiagramEdgeConstraints.noDirectCycles(DependencyEdge.class).satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoDirectCycles_NoExistingDependencyEdge()
	{
		createDiagram();
		GeneralizationEdge edge = new GeneralizationEdge();
		edge.connect(aNode1, aNode2);
		aDiagram.addEdge(edge);
		assertTrue(ClassDiagramEdgeConstraints.noDirectCycles(DependencyEdge.class).satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoDirectCycles_False()
	{
		createDiagram();
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode2);
		aDiagram.addEdge(edge);
		assertFalse(ClassDiagramEdgeConstraints.noDirectCycles(DependencyEdge.class).satisfied(aEdge1, aNode2, aNode1, aPoint, aPoint, aRenderer));
	}
}