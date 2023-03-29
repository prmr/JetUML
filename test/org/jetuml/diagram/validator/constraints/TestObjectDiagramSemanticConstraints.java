package org.jetuml.diagram.validator.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.Test;

public class TestObjectDiagramSemanticConstraints
{
	private Diagram aDiagram = new Diagram(DiagramType.OBJECT);
	private ObjectNode aObject1 = new ObjectNode();
	private ObjectNode aObject2 = new ObjectNode();
	private FieldNode aField1 = new FieldNode();
	private ObjectCollaborationEdge aCollaboration1 = new ObjectCollaborationEdge();
	private ObjectReferenceEdge aReference1 = new ObjectReferenceEdge();

	private void createDiagram()
	{
		aDiagram.addRootNode(aObject1);
		aDiagram.addRootNode(aObject2);
		aObject2.moveTo(new Point(200, 200));
		aObject1.addChild(aField1);
	}

	@Test
	void testCollaborationNotCollaborationEdge()
	{
		createDiagram();
		aReference1.connect(aObject1, aObject2);
		assertTrue(ObjectDiagramSemanticConstraints.collaboration().satisfied(aReference1, aDiagram));
	}

	@Test
	void testCollaborationCollaborationNotCorrectStartNode()
	{
		createDiagram();
		aCollaboration1.connect(aField1, aObject2);
		assertFalse(ObjectDiagramSemanticConstraints.collaboration().satisfied(aCollaboration1, aDiagram));
	}

	@Test
	void testCollaborationCollaborationNotCorrectEndNode()
	{
		createDiagram();
		aCollaboration1.connect(aObject2, aField1);
		assertFalse(ObjectDiagramSemanticConstraints.collaboration().satisfied(aCollaboration1, aDiagram));
	}

	@Test
	void testCollaborationCollaborationCorrect()
	{
		createDiagram();
		aCollaboration1.connect(aObject2, aObject2);
		assertTrue(ObjectDiagramSemanticConstraints.collaboration().satisfied(aCollaboration1, aDiagram));
	}

	@Test
	void testReferenceNotReference()
	{
		createDiagram();
		aCollaboration1.connect(aField1, aObject2);
		assertTrue(ObjectDiagramSemanticConstraints.reference().satisfied(aCollaboration1, aDiagram));
	}

	@Test
	void testReferenceReferenceNotCorrectStart()
	{
		createDiagram();
		aReference1.connect(aObject1, aObject2);
		assertFalse(ObjectDiagramSemanticConstraints.reference().satisfied(aReference1, aDiagram));
	}

	@Test
	void testReferenceReferenceNotCorrectEnd()
	{
		createDiagram();
		aReference1.connect(aField1, aField1);
		assertFalse(ObjectDiagramSemanticConstraints.reference().satisfied(aReference1, aDiagram));
	}

	@Test
	void testReferenceReferenceCorrect()
	{
		createDiagram();
		aReference1.connect(aField1, aObject2);
		assertTrue(ObjectDiagramSemanticConstraints.reference().satisfied(aReference1, aDiagram));
	}
}
