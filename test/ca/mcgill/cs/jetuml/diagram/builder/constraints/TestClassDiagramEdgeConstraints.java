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

package ca.mcgill.cs.jetuml.diagram.builder.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestClassDiagramEdgeConstraints
{
	private Diagram aDiagram;
	private ClassNode aNode1;
	private ClassNode aNode2;
	private DependencyEdge aEdge1;
	private GeneralizationEdge aGen1;
	private Point aPoint;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.CLASS);
		aNode1 = new ClassNode();
		aNode2 = new ClassNode();
		aEdge1 = new DependencyEdge();
		aGen1 = new GeneralizationEdge();
		aPoint = new Point(0,0);
	}
	
	private void createDiagram()
	{
		aNode2.moveTo(new Point(0,100));
		aDiagram.addRootNode(aNode1);
		aDiagram.addRootNode(aNode2);
	}
	
	@Test
	public void testNoSelfGeneralizationNotAGeneralizationEdge()
	{
		createDiagram();
		assertTrue(ClassDiagramEdgeConstraints.noSelfGeneralization().satisfied(aEdge1, aNode1, new ClassNode(), aPoint, aPoint, aDiagram));
		assertTrue(ClassDiagramEdgeConstraints.noSelfGeneralization().satisfied(aEdge1, aNode1, aNode1, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoSelfGeneralizationGeneralizationEdge()
	{
		createDiagram();
		assertTrue(ClassDiagramEdgeConstraints.noSelfGeneralization().satisfied(aGen1, aNode1, aNode2,aPoint, aPoint, aDiagram));
		assertFalse(ClassDiagramEdgeConstraints.noSelfGeneralization().satisfied(aGen1, aNode1, aNode2,aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoDirectCycles_NotADependency()
	{
		createDiagram();
		assertTrue(ClassDiagramEdgeConstraints.noDirectCycles(DependencyEdge.class).satisfied(new GeneralizationEdge(), aNode1, aNode2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoDirectCycles_NoExistingEdge()
	{
		createDiagram();
		assertTrue(ClassDiagramEdgeConstraints.noDirectCycles(DependencyEdge.class).satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoDirectCycles_NoExistingDependencyEdge()
	{
		createDiagram();
		GeneralizationEdge edge = new GeneralizationEdge();
		edge.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(edge);
		assertTrue(ClassDiagramEdgeConstraints.noDirectCycles(DependencyEdge.class).satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testNoDirectCycles_False()
	{
		createDiagram();
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode2, aDiagram);
		aDiagram.addEdge(edge);
		assertFalse(ClassDiagramEdgeConstraints.noDirectCycles(DependencyEdge.class).satisfied(aEdge1, aNode1, aNode2, aPoint, aPoint, aDiagram));
	}
}
