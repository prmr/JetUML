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

package org.jetuml.diagram.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramAccessor;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestObjectDiagramBuilder
{
	private Diagram aDiagram = new Diagram(DiagramType.OBJECT);
	private ObjectDiagramBuilder aBuilder = new ObjectDiagramBuilder(aDiagram);
	private DiagramAccessor aAccessor = new DiagramAccessor(aDiagram);
	private ObjectNode aObjectNode1 = new ObjectNode();
	private ObjectNode aObjectNode2 = new ObjectNode();
	private NoteNode aNote = new NoteNode();
	private FieldNode aFieldNode1 = new FieldNode();
	private FieldNode aFieldNode2 = new FieldNode();
	private ObjectReferenceEdge aReference1 = new ObjectReferenceEdge();
	
	@BeforeEach
	public void setUp()
	{
		aFieldNode1.setName("Field1");
		aFieldNode1.setValue("value");
		aFieldNode1.setName("Field2");
		aNote.translate(300, 300);
		aObjectNode2.translate(100, 100);
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
	public void testCreateAddEdgeOperation_ObjectToNote()
	{
		aDiagram.addRootNode(aObjectNode1);
		aDiagram.addRootNode(aNote);
		Edge edge = new NoteEdge();
		DiagramOperation operation = aBuilder.createAddEdgeOperation(edge, new Point(20,20), new Point(310,310));
		operation.execute();
		assertEquals(1, aAccessor.getEdges().size());
		assertSame(edge, aAccessor.getEdges().get(0));
		assertSame(aObjectNode1, edge.start());
		assertSame(aNote, edge.end());
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
		assertSame(aFieldNode1, aReference1.start());
		assertSame(aObjectNode2, aReference1.end());
		
		operation.undo();
		assertEquals(0, aAccessor.getEdges().size());
		assertEquals("value", aFieldNode1.getValue());
	}
}
