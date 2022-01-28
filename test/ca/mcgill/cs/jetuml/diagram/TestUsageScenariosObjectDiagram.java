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
package ca.mcgill.cs.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.builder.ObjectDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestUsageScenariosObjectDiagram extends AbstractTestUsageScenarios
{
	private ObjectNode aObjectNode1;
	private ObjectNode aObjectNode2;
	private FieldNode aFieldNode1;
	private FieldNode aFieldNode2;
	private FieldNode aFieldNode3;
	private ObjectReferenceEdge aReferenceEdge1;
	private ObjectReferenceEdge aReferenceEdge2;
	
	@BeforeEach
	@Override
	public void setup()
	{
		super.setup();
		aDiagram = new Diagram(DiagramType.OBJECT);
		aBuilder = new ObjectDiagramBuilder(aDiagram);
		aObjectNode1 = new ObjectNode();
		aObjectNode2 = new ObjectNode();
		aFieldNode1 = new FieldNode();
		aFieldNode2 = new FieldNode();
		aFieldNode3 = new FieldNode();
		aReferenceEdge1 = new ObjectReferenceEdge();
		aReferenceEdge2 = new ObjectReferenceEdge();
	}
	
	@Test
	public void testCreateStateDiagram()
	{
		addNode(aObjectNode1, new Point(20, 20));
		setProperty(aObjectNode1.properties().get(PropertyName.NAME), "Car");
		assertEquals(1, numberOfRootNodes());
		assertEquals("Car", aObjectNode1.getName());
		
		assertFalse(aBuilder.canAdd(aFieldNode1, new Point(120, 80)));
		
		addNode(aFieldNode1, new Point(20, 40));
		addNode(aFieldNode2, new Point(30, 40));
		addNode(aFieldNode3, new Point(40, 30));
		assertEquals(3, aObjectNode1.getChildren().size());
		assertEquals(1, numberOfRootNodes());
		
	}
	
	@Test
	public void testNoteNodeWithNoteEdges()
	{
		addNode(aObjectNode1, new Point(20, 20));
		addNode(aObjectNode2, new Point(150, 20));
		addNode(aFieldNode1, new Point(20, 40));
		addNode(aFieldNode2, new Point(30, 40));
		addNode(aFieldNode3, new Point(40, 30));
		addNode(aNoteNode, new Point(80, 80));
		assertEquals(3, numberOfRootNodes());
		
		assertFalse(aBuilder.canAdd(new NoteEdge(), new Point(25, 25), new Point(55, 25)));
		assertFalse(aBuilder.canAdd(new NoteEdge(), new Point(55, 25), new Point(155, 25)));
		assertFalse(aBuilder.canAdd(new NoteEdge(), new Point(155, 25), new Point(255, 25)));
		assertFalse(aBuilder.canAdd(new NoteEdge(), new Point(155, 25), new Point(55, 25)));
		assertFalse(aBuilder.canAdd(new NoteEdge(), new Point(25, 25), new Point(255, 25)));
		assertEquals(0, numberOfEdges());
		
		// create NoteEdge from NoteNode to anywhere and from ObjectNode to NoteNode
		addEdge(new NoteEdge(), new Point(80, 80), new Point(55, 25));
		addEdge(new NoteEdge(), new Point(25, 25), new Point(80, 80));
		assertEquals(2, numberOfEdges());
		
		// create NoteEdge from FieldNode to NoteNode (not allowed)
		assertFalse(aBuilder.canAdd(new NoteEdge(), new Point(60, 80), new Point(80, 80)));
	}
	
	@Test
	public void testGeneralEdgeCreation()
	{
		addNode(aObjectNode1, new Point(20, 20));
		addNode(aObjectNode2, new Point(150, 20));
		addNode(aFieldNode1, new Point(20, 40));
		addNode(aFieldNode2, new Point(30, 40));
		addNode(aFieldNode3, new Point(40, 30));
		addNode(aNoteNode, new Point(80, 80));
		
		// create an association edge between two ObjectNode
		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		addEdge(collaborationEdge1, new Point(25, 20), new Point(165, 20));
		assertEquals(1, numberOfEdges());
		
		assertFalse(aBuilder.canAdd(new ObjectCollaborationEdge(), new Point(25, 20), new Point(80, 80)));
		assertFalse(aBuilder.canAdd(aReferenceEdge1, new Point(25, 20), new Point(80, 80)));
		
		/* create an ObjectRefEdge to an ObjectNode itself. 
		 * "value" text in field node will be erased and edge will be added.
		 */
		addEdge(aReferenceEdge1, new Point(65, 100), new Point(20, 20));
		assertEquals(2, numberOfEdges());
		assertEquals("name", aFieldNode1.getName());
		
		// create ObjectRefEdge from the other field to a different ObjectNode
		addEdge(aReferenceEdge2, new Point(65, 125), new Point(150, 20));
		assertEquals(3, numberOfEdges());
		assertEquals(aFieldNode2, aReferenceEdge2.getStart());
		assertEquals(aObjectNode2, aReferenceEdge2.getEnd());
		
		// change the property of a field
		setProperty(aFieldNode3.properties().get(PropertyName.NAME), "Car");
		assertEquals("Car", aFieldNode3.getName());
	}
	
	@Test
	public void testIndividualNodeMovement()
	{
		addNode(aObjectNode1, new Point(20, 20));
		addNode(aFieldNode1, new Point(20, 40));
		addNode(aNoteNode, new Point(80, 80));

		moveNode(aObjectNode1, 3, 12);
		moveNode(aNoteNode, 40, 20);

		assertEquals(new Point(23, 32), aObjectNode1.position());
		assertEquals(new Point(120, 100), aNoteNode.position());
	}
	
	@Test
	public void testNodesAndEdgesMovement()
	{
		addNode(aObjectNode1, new Point(20, 20));
		addNode(aObjectNode2, new Point(150, 20));
		addNode(aFieldNode1, new Point(20, 40));
		addNode(aFieldNode2, new Point(30, 40));
		addNode(aNoteNode, new Point(80, 80));

		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		addEdge(collaborationEdge1, new Point(25, 20), new Point(165, 20));
		addEdge(aReferenceEdge1, new Point(65, 100), new Point(20, 20));
		addEdge(aReferenceEdge2, new Point(65, 125), new Point(150, 20));
		
		selectAll();
		moveSelection(26, 37);
		
		assertEquals(new Point(46, 57), aObjectNode1.position());
		assertEquals(new Point(106, 117), aNoteNode.position());
	}
	
	@Test
	public void testDeleteObjectNodeAndNoteNode()
	{
		
		addNode(aObjectNode1, new Point(20, 20));
		select(aObjectNode1);
		deleteSelected();
		assertEquals(0, numberOfRootNodes());

		undo();
		assertEquals(1, numberOfRootNodes());
		
		addNode(aNoteNode, new Point(75, 75));
		select(aNoteNode);
		deleteSelected();
		assertEquals(1, numberOfRootNodes());

		undo();
		assertEquals(2, numberOfRootNodes());
	}
	
	@Test
	public void testDeleteFieldNode()
	{
		addNode(aObjectNode1, new Point(20, 20));
		addNode(aFieldNode1, new Point(20, 40));

		select(aFieldNode1);
		deleteSelected();
		assertEquals(0, aObjectNode1.getChildren().size());

		undo();
		assertEquals(1, aObjectNode1.getChildren().size());
	}
	
	@Test
	public void testDeleteEdge()
	{
		addNode(aObjectNode1, new Point(20, 20));
		addNode(aObjectNode2, new Point(150, 20));
		addNode(aFieldNode1, new Point(20, 40));
		addNode(aFieldNode2, new Point(30, 40));
		ObjectCollaborationEdge collaborationEdge1 = new ObjectCollaborationEdge();
		addEdge(collaborationEdge1, new Point(25, 20), new Point(165, 20));
		addEdge(aReferenceEdge1, new Point(65, 125), new Point(25, 20));
		addEdge(aReferenceEdge2, new Point(65, 125), new Point(150, 20));

		// delete aReferenceEdge2 and collaborationEdge1
		select(aReferenceEdge2);
		deleteSelected();
		assertEquals(2, numberOfEdges());
		
		select(collaborationEdge1);
		deleteSelected();
		assertEquals(1, numberOfEdges());
		
		undo();
		assertEquals(2, numberOfEdges());
		undo();
		assertEquals(3, numberOfEdges());
	}
	
	@Test
	public void testDeleteCombinationNodeAndEdge()
	{
		addNode(aObjectNode1, new Point(20, 20));
		addNode(aObjectNode2, new Point(150, 20));
		addNode(aFieldNode1, new Point(20, 40));
		addNode(aFieldNode2, new Point(30, 40));
		
		ObjectCollaborationEdge assoEdge1 = new ObjectCollaborationEdge();
		addEdge(assoEdge1, new Point(25, 20), new Point(165, 20));
		addEdge(aReferenceEdge1, new Point(65, 125), new Point(20, 20));
		addEdge(aReferenceEdge2, new Point(65, 125), new Point(150, 20));

		// delete aObjectNode1 and all 3 edges
		select(aObjectNode1, assoEdge1, aReferenceEdge1, aReferenceEdge2);
		
		assertEquals(2, numberOfRootNodes());
		assertEquals(3, numberOfEdges());
		
		deleteSelected();
		assertEquals(1, numberOfRootNodes());
		assertEquals(0, numberOfEdges());
		
		undo();
		assertEquals(2, numberOfRootNodes());
		assertEquals(3, numberOfEdges());
		
		// now delete aFieldNode2 and as a result the reference edges
		// connected to it: aReferenceEdge1 and aReferenceEdge2
		select(aFieldNode2);
		deleteSelected();
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, aObjectNode1.getChildren().size());
		assertEquals(1, numberOfEdges());
		
		undo();
		assertEquals(2, numberOfRootNodes());
		assertEquals(2, aObjectNode1.getChildren().size());
		assertEquals(3, numberOfEdges());
	}
	
	@Test
	public void testDeleteNodeWithLinkedEdge()
	{
		addNode(aObjectNode1, new Point(0,0));
		addNode(aObjectNode2, new Point(100,0));
		addNode(aFieldNode1, new Point(10,10));
		aReferenceEdge1.connect(aFieldNode1, aObjectNode2, aDiagram);
		aDiagram.addEdge(aReferenceEdge1);
		select(aObjectNode1);
		deleteSelected();
		assertEquals(0, numberOfEdges());
		assertEquals(1, numberOfRootNodes());
	}

	@Test
	public void testCopyNode()
	{
		addNode(aObjectNode1, new Point(20, 20));
		addNode(aFieldNode1, new Point(20, 40));
		select(aObjectNode1);
		copy();
		paste();
		
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, ((ObjectNode) getRootNode(1)).getChildren().size());
		assertEquals(new Point(20, 20), 
				((ObjectNode) getRootNode(1)).position());
		
		// paste a FieldNode itself is not allowed
		select(aFieldNode1);
		copy();
		paste();
		assertEquals(2, numberOfRootNodes());
	}
	
	@Test
	public void testCutObjectNodeWithFieldNode()
	{
		addNode(aObjectNode1, new Point(20, 20));
		addNode(aFieldNode1, new Point(20, 40));
		select(aObjectNode1);
		cut();
		paste();
		assertEquals(1, numberOfRootNodes());
		assertEquals(1, ((ObjectNode) getRootNode(0)).getChildren().size());
		assertEquals(new Point(20, 20), 
				((ObjectNode) getRootNode(0)).position());
	}
	
	@Test
	public void testCutFieldNode()
	{
		addNode(aObjectNode1, new Point(20, 20));
		addNode(aFieldNode1, new Point(20, 40));
		select(aFieldNode1);
		cut();
		
		assertEquals(1, numberOfRootNodes());
		assertEquals(0, ((ObjectNode) getRootNode(0)).getChildren().size());
		assertEquals(new Point(20, 20), aObjectNode1.position());
		
		// a FieldNode will not be pasted
		paste();
		assertEquals(1, numberOfRootNodes());
		assertEquals(0, ((ObjectNode) getRootNode(0)).getChildren().size());
	}
	
	@Test
	public void testCopyNodesWithEdge()
	{
		addNode(aObjectNode1, new Point(50, 20));
		addNode(aObjectNode2, new Point(150, 20));
		addEdge(new ObjectCollaborationEdge(), new Point(55, 25), new Point(155, 25));
		selectAll();
		copy();
		paste();

		assertEquals(4, numberOfRootNodes());
		assertEquals(2, numberOfEdges());
		assertEquals(new Point(50, 20), ((ObjectNode) getRootNode(2)).position());
	}
	
	@Test
	public void testCutNodesWithEdge()
	{
		addNode(aObjectNode1, new Point(50, 20));
		addNode(aObjectNode2, new Point(150, 20));
		addEdge(new ObjectCollaborationEdge(), new Point(55, 25), new Point(155, 25));
		
		selectAll();
		cut();
		assertEquals(0, numberOfRootNodes());
		assertEquals(0, numberOfEdges());

		paste();
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertEquals(new Point(50, 20), ((ObjectNode) getRootNode(0)).position());
	}
}
