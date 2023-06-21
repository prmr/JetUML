package org.jetuml.diagram.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.nodes.StateNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests the complete structural and semantic validation of sequence diagrams.
 * Tests should focus on invalid diagrams. False positives (valid semantics detected as invalid)
 * are easy to detect just from using the tool.
 */
public class TestSequenceDiagramValidation
{
	private final SequenceDiagramValidator aValidator = new SequenceDiagramValidator(
			new Diagram(DiagramType.SEQUENCE));
	private final ImplicitParameterNode aImplicitParameterNode = new ImplicitParameterNode();
	private final CallNode aCallNode = new CallNode();
	private final NoteNode aNoteNode = new NoteNode();
	private final PointNode aPointNode = new PointNode();
	private final CallEdge aCallEdge = new CallEdge();

	private Diagram diagram()
	{
		return aValidator.diagram();
	}
	
	private static List<Node> provideInvalidNodes()
	{
		return List.of(new ActorNode(), new ClassNode(), new FieldNode(), new FinalStateNode(), 
				new InitialStateNode(), new InterfaceNode(), new ObjectNode(), new PackageDescriptionNode(), 
				new PackageNode(), new StateNode(), new UseCaseNode());
	}
	
	private static List<Edge> provideInvalidEdges()
	{
		return List.of(new AggregationEdge(), new AssociationEdge(), new DependencyEdge(),
				new GeneralizationEdge(), new ObjectCollaborationEdge(), new ObjectReferenceEdge(), 
				new ReturnEdge(), new StateTransitionEdge(), new UseCaseAssociationEdge(),
				new UseCaseDependencyEdge(), new UseCaseGeneralizationEdge());
	}
	
	@ParameterizedTest
	@MethodSource("provideInvalidNodes")
	void testInvalidElement_Node(Node pNode)
	{
		diagram().addRootNode(pNode);
		assertFalse(aValidator.isValid());
	}
	
	@ParameterizedTest
	@MethodSource("provideInvalidEdges")
	void testInvalidElement_Edge(Edge pEdge)
	{
		pEdge.connect(aNoteNode, aNoteNode);
		diagram().addEdge(pEdge);
		diagram().addRootNode(aNoteNode);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeConnectsTwoNoteNodes()
	{
		Node node2 = new NoteNode();
		Edge edge = new NoteEdge();
		edge.connect(aNoteNode, node2);
		diagram().addRootNode(aNoteNode);
		diagram().addRootNode(node2);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeConnectstAtLeastOneNoteNode()
	{
		Edge edge = new NoteEdge();
		edge.connect(aImplicitParameterNode, new ImplicitParameterNode());
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCallNodeHasParent()
	{
		diagram().addRootNode(aCallNode);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testPointNodeNotConnected()
	{
		diagram().addRootNode(new PointNode());
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void callEdgeToPointNode()
	{
		diagram().addRootNode(aImplicitParameterNode);
		aImplicitParameterNode.addChild(aCallNode);
		diagram().addRootNode(aPointNode);
		aCallEdge.connect(aCallNode, aPointNode);
		diagram().addEdge(aCallEdge);
		assertFalse(aValidator.isValid());
	}
}