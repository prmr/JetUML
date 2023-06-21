package org.jetuml.diagram.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestSequenceDiagramValidator
{
	private Diagram aDiagram;
	private SequenceDiagramValidator aSequenceDiagramValidator;
	private ImplicitParameterNode aImplicitParameterNode;
	private CallNode aCallNode;
	private NoteNode aNoteNode;

	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.SEQUENCE);
		aImplicitParameterNode = new ImplicitParameterNode();
		aCallNode = new CallNode();
		aNoteNode = new NoteNode();
		aSequenceDiagramValidator = new SequenceDiagramValidator(aDiagram);
	}

	@Test
	public void testValidNodeHierarchy_True()
	{
		aImplicitParameterNode.addChild(aCallNode);
		aDiagram.addRootNode(aImplicitParameterNode);
		assertTrue(aSequenceDiagramValidator.isValid());
	}

	@Test
	public void testValidNodeHierarchy_False()
	{
		try
		{
			Field aCallNodes = ImplicitParameterNode.class.getDeclaredField("aCallNodes");
			aCallNodes.setAccessible(true);
			ArrayList<Node> parameterNodeChildren = new ArrayList<>();
			parameterNodeChildren.add(aNoteNode);
			aCallNodes.set(aImplicitParameterNode, parameterNodeChildren);
			aDiagram.addRootNode(aImplicitParameterNode);
			assertFalse(aSequenceDiagramValidator.isValid());
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testValidElementName_True()
	{
		aDiagram.addRootNode(aImplicitParameterNode);
		aDiagram.addRootNode(aNoteNode);
		assertTrue(aSequenceDiagramValidator.isValid());
	}

	@Test
	public void testValidElementName_False()
	{
		ObjectNode aObjectNode = new ObjectNode();
		aDiagram.addRootNode(aObjectNode);
		assertFalse(aSequenceDiagramValidator.isValid());
	}
}