package org.jetuml.diagram.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.junit.jupiter.api.Test;

/**
 * Test for the rules that apply to all diagrams. These
 * rules are related to UML notes. The tests are done via 
 * a ClassDiagramValidator because we need an instance, but 
 * this class should not be used to hold test specific to class
 * diagrams.
 */
public class TestAbstractDiagramValidator
{
	private final ClassDiagramValidator aValidator =
			new ClassDiagramValidator(new Diagram(DiagramType.CLASS));
	
	private final NoteNode aNoteNode1 = new NoteNode();
	private final NoteNode aNoteNode2 = new NoteNode();
	private final ClassNode aClassNode = new ClassNode();
	private final PointNode aPointNode = new PointNode();
	private final NoteEdge aNoteEdge = new NoteEdge();
	
	private Diagram diagram()
	{
		return aValidator.diagram();
	}
	
	@Test
	void testPointNodeNotConnected()
	{
		diagram().addRootNode(new PointNode());
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromNoteToNote()
	{
		diagram().addRootNode(aNoteNode1);
		diagram().addRootNode(aNoteNode2);
		aNoteEdge.connect(aNoteNode1, aNoteNode2);
		diagram().addEdge(aNoteEdge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromClassToPoint()
	{
		diagram().addRootNode(aClassNode);
		diagram().addRootNode(aPointNode);
		aNoteEdge.connect(aClassNode, aPointNode);
		diagram().addEdge(aNoteEdge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromPointToClass()
	{
		diagram().addRootNode(aClassNode);
		diagram().addRootNode(aPointNode);
		aNoteEdge.connect(aPointNode, aClassNode );
		diagram().addEdge(aNoteEdge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromNoteToPoint()
	{
		diagram().addRootNode(aNoteNode1);
		diagram().addRootNode(aPointNode);
		aNoteEdge.connect(aNoteNode1, aPointNode );
		diagram().addEdge(aNoteEdge);
		assertTrue(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromClassNodeToNote()
	{
		diagram().addRootNode(aClassNode);
		diagram().addRootNode(aNoteNode1);
		aNoteEdge.connect(aClassNode, aNoteNode1 );
		diagram().addEdge(aNoteEdge);
		assertTrue(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromClassNodeToClass()
	{
		diagram().addRootNode(aClassNode);
		aNoteEdge.connect(aClassNode, aClassNode );
		diagram().addEdge(aNoteEdge);
		assertFalse(aValidator.isValid());
	}
}
