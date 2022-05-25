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

import org.jetuml.JavaFXLoader;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestObjectDiagramEdgeConstraints
{
	private Diagram aDiagram;
	private ObjectNode aObject1;
	private ObjectNode aObject2;
	private FieldNode aField1;
	private ObjectCollaborationEdge aCollaboration1;
	private ObjectReferenceEdge aReference1;
	private Point aPoint;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.OBJECT);
		aObject1 = new ObjectNode();
		aObject2 = new ObjectNode();
		aField1 = new FieldNode();
		aCollaboration1 = new ObjectCollaborationEdge();
		aReference1 = new ObjectReferenceEdge();
		aPoint = new Point(0,0);
	}
	
	private void createDiagram()
	{
		aDiagram.addRootNode(aObject1);
		aDiagram.addRootNode(aObject2);
		aObject2.moveTo(new Point(200,200));
		aObject1.addChild(aField1);
	}
	
	@Test
	public void testCollaborationNotCollaborationEdge()
	{
		createDiagram();
		assertTrue(ObjectDiagramEdgeConstraints.collaboration().satisfied(aReference1, aObject1, aObject2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testCollaborationCollaborationNotCorrectStartNode()
	{
		createDiagram();
		assertFalse(ObjectDiagramEdgeConstraints.collaboration().satisfied(aCollaboration1, aField1, aObject2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testCollaborationCollaborationNotCorrectEndNode()
	{
		createDiagram();
		assertFalse(ObjectDiagramEdgeConstraints.collaboration().satisfied(aCollaboration1, aObject2, aField1, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testCollaborationCollaborationCorrect()
	{
		createDiagram();
		assertTrue(ObjectDiagramEdgeConstraints.collaboration().satisfied(aCollaboration1, aObject2, aObject2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testReferenceNotReference()
	{
		createDiagram();
		assertTrue(ObjectDiagramEdgeConstraints.reference().satisfied(aCollaboration1, aField1, aObject2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testReferenceReferenceNotCorrectStart()
	{
		createDiagram();
		assertFalse(ObjectDiagramEdgeConstraints.reference().satisfied(aReference1, aObject1, aObject2, aPoint, aPoint, aDiagram));
	}
	
	@Test
	public void testReferenceReferenceNotCorrectEnd()
	{
		createDiagram();
		assertFalse(ObjectDiagramEdgeConstraints.reference().satisfied(aReference1, aField1, aField1, aPoint, aPoint, aDiagram));
	}

	@Test
	public void testReferenceReferenceCorrect()
	{
		createDiagram();
		assertTrue(ObjectDiagramEdgeConstraints.reference().satisfied(aReference1, aField1, aObject2, aPoint, aPoint, aDiagram));
	}


}
