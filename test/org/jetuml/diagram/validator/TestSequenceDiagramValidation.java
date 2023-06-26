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
import org.jetuml.diagram.edges.ConstructorEdge;
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
	private final CallEdge aCallEdge = new CallEdge();
	private final CallEdge aConstructor = new ConstructorEdge();

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
	void testCallNodeToNoteNode()
	{
		diagram().addRootNode(aImplicitParameterNode);
		aImplicitParameterNode.addChild(aCallNode);
		diagram().addRootNode(aNoteNode);
		aCallEdge.connect(aCallNode, aNoteNode);
		diagram().addEdge(aCallEdge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testConstructorNodeToNoteNode()
	{
		diagram().addRootNode(aImplicitParameterNode);
		aImplicitParameterNode.addChild(aCallNode);
		diagram().addRootNode(aNoteNode);
		aConstructor.connect(aCallNode, aNoteNode);
		diagram().addEdge(aConstructor);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testImplicitParameterToCall()
	{
		diagram().addRootNode(aImplicitParameterNode);
		ImplicitParameterNode node2 = new ImplicitParameterNode();
		diagram().addRootNode(node2);
		node2.addChild(aCallNode);
		aCallEdge.connect(aImplicitParameterNode, aCallNode);
		diagram().addEdge(aCallEdge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testImplicitParameterToCall2()
	{
		diagram().addRootNode(aImplicitParameterNode);
		ImplicitParameterNode node2 = new ImplicitParameterNode();
		diagram().addRootNode(node2);
		node2.addChild(aCallNode);
		aConstructor.connect(aImplicitParameterNode, aCallNode);
		diagram().addEdge(aConstructor);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCallToImplicitParameter()
	{
		diagram().addRootNode(aImplicitParameterNode);
		ImplicitParameterNode node2 = new ImplicitParameterNode();
		diagram().addRootNode(node2);
		node2.addChild(aCallNode);
		aCallEdge.connect(aCallNode, aImplicitParameterNode);
		diagram().addEdge(aCallEdge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCallToImplicitParameter2()
	{
		diagram().addRootNode(aImplicitParameterNode);
		ImplicitParameterNode node2 = new ImplicitParameterNode();
		diagram().addRootNode(node2);
		node2.addChild(aCallNode);
		aConstructor.connect(aCallNode, aImplicitParameterNode);
		diagram().addEdge(aConstructor);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testMultipleCallers()
	{
		ImplicitParameterNode object1 = new ImplicitParameterNode();
		CallNode call1 = new CallNode();
		diagram().addRootNode(object1);
		object1.addChild(call1);
		
		ImplicitParameterNode object2 = new ImplicitParameterNode();
		CallNode call2 = new CallNode();
		diagram().addRootNode(object2);
		object1.addChild(call2);
		
		ImplicitParameterNode object3 = new ImplicitParameterNode();
		CallNode call3 = new CallNode();
		diagram().addRootNode(object3);
		object1.addChild(call3);
		
		Edge callEdge1 = new CallEdge();
		callEdge1.connect(call1, call2);
		diagram().addEdge(callEdge1);
		
		Edge callEdge2 = new CallEdge();
		callEdge2.connect(call2, call3);
		diagram().addEdge(callEdge2);
		
		Edge callEdge3 = new CallEdge();
		callEdge3.connect(call1, call3);
		diagram().addEdge(callEdge3);
		
		assertFalse(aValidator.isValid());
	}
}