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

package ca.mcgill.cs.jetuml.diagram.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramAccessor;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestObjectDiagramBuilder
{
	private Diagram aDiagram;
	private ObjectDiagramBuilder aBuilder;
	private DiagramAccessor aAccessor;
	private ObjectNode aObjectNode1;
	private ObjectNode aObjectNode2;
	private NoteNode aNote;
	private FieldNode aFieldNode1;
	private FieldNode aFieldNode2;
	private ObjectCollaborationEdge aCollaboration1;
	private ObjectReferenceEdge aReference1;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.OBJECT);
		aAccessor = new DiagramAccessor(aDiagram);
		aBuilder = new ObjectDiagramBuilder(aDiagram);
		aObjectNode1 = new ObjectNode();
		aFieldNode1 = new FieldNode();
		aFieldNode1.setName("Field1");
		aFieldNode1.setValue("value");
		aFieldNode2 = new FieldNode();
		aFieldNode1.setName("Field2");
		aNote = new NoteNode();
		aNote.translate(300, 300);
		aObjectNode2 = new ObjectNode();
		aObjectNode2.translate(100, 100);
		aCollaboration1 = new ObjectCollaborationEdge();
		aReference1 = new ObjectReferenceEdge();
	}		
		
	@Test
	public void testCreateRemoveElementsOperationFirstOfTwo()
	{
		aDiagram.addRootNode(aObjectNode1);
		aObjectNode1.addChild(aFieldNode1);
		aObjectNode1.addChild(aFieldNode2);
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));

		DiagramOperation operation = aBuilder.createRemoveElementsOperation(Arrays.asList(aFieldNode1));
		operation.execute();
		
		assertEquals(1, aObjectNode1.getChildren().size());
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(0));
		
		operation.undo();
		
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));
	}
	
	@Test
	public void testCreateRemoveElementsOperationSecondOfTwo()
	{
		aDiagram.addRootNode(aObjectNode1);
		aObjectNode1.addChild(aFieldNode1);
		aObjectNode1.addChild(aFieldNode2);
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));

		DiagramOperation operation = aBuilder.createRemoveElementsOperation(Arrays.asList(aFieldNode2));
		operation.execute();
		
		assertEquals(1, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		
		operation.undo();
		
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));
	}
	
	@Test
	public void testCreateRemoveElementsOperationTwoOfTwoInOrder()
	{
		aDiagram.addRootNode(aObjectNode1);
		aObjectNode1.addChild(aFieldNode1);
		aObjectNode1.addChild(aFieldNode2);
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));

		DiagramOperation operation = aBuilder.createRemoveElementsOperation(Arrays.asList(aFieldNode1, aFieldNode2));
		operation.execute();
		
		assertEquals(0, aObjectNode1.getChildren().size());
		
		operation.undo();
		
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));
	}
	
	@Test
	public void testCreateRemoveElementsOperationTwoOfTwoInReverseOrder()
	{
		aDiagram.addRootNode(aObjectNode1);
		aObjectNode1.addChild(aFieldNode1);
		aObjectNode1.addChild(aFieldNode2);
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));

		DiagramOperation operation = aBuilder.createRemoveElementsOperation(Arrays.asList(aFieldNode2, aFieldNode1));
		operation.execute();
		
		assertEquals(0, aObjectNode1.getChildren().size());
		
		operation.undo();
		
		assertEquals(2, aObjectNode1.getChildren().size());
		assertSame(aFieldNode1, aObjectNode1.getChildren().get(0));
		assertSame(aFieldNode2, aObjectNode1.getChildren().get(1));
	}
	
	@Test
	public void testCanAdd_Constraint_MaxEdges()
	{
		aDiagram.addRootNode(aObjectNode1);
		aDiagram.addRootNode(aObjectNode2);
		assertTrue(aBuilder.canAdd(aCollaboration1, new Point(10,10), new Point(110,110)));
		aCollaboration1.connect(aObjectNode1, aObjectNode2, aDiagram);
		aDiagram.addEdge(aCollaboration1);
		ObjectCollaborationEdge edge = new ObjectCollaborationEdge();
		assertFalse(aBuilder.canAdd(edge, new Point(10,10), new Point(110,110)));
	}
	
	@Test
	public void testCreateAddEdgeOperation_ObjectToNote()
	{
		aDiagram.addRootNode(aObjectNode1);
		aDiagram.addRootNode(aNote);
		Edge edge = new NoteEdge();
		DiagramOperation operation = aBuilder.createAddEdgeOperation(edge, new Point(20,20), new Point(310,310));
		operation.execute();
		assertEquals(1, aAccessor.getEdges().size());
		assertSame(edge, aAccessor.getEdges().get(0));
		assertSame(aObjectNode1, edge.getStart());
		assertSame(aNote, edge.getEnd());
	}
	
	@Test
	public void testCreateAddEdgeOperation_FieldToObject()
	{
		aDiagram.addRootNode(aObjectNode1);
		aDiagram.addRootNode(aObjectNode2);
		aObjectNode1.addChild(aFieldNode1);
		DiagramOperation operation = aBuilder.createAddEdgeOperation(aReference1, new Point(10,75), new Point(110,110));
		operation.execute();
		assertEquals(1, aAccessor.getEdges().size());
		assertSame(aReference1, aAccessor.getEdges().get(0));
		assertEquals("", aFieldNode1.getValue());
		assertSame(aFieldNode1, aReference1.getStart());
		assertSame(aObjectNode2, aReference1.getEnd());
		
		operation.undo();
		assertEquals(0, aAccessor.getEdges().size());
		assertEquals("value", aFieldNode1.getValue());
	}
}
