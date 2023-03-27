package org.jetuml.diagram.validator.constraints;

import java.util.Optional;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.PointNode;

/**
 * Constraints for sequence diagrams.
 */
public final class SequenceDiagramSemanticConstraints
{
	private SequenceDiagramSemanticConstraints() {}

	/**
	 * No edge is allowed to start in a parameter node.
	 */
	public static SemanticConstraint noEdgesFromParameterTop()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram) -> {
			return pStart.getClass() != ImplicitParameterNode.class;
		};
	}

	/**
	 * For a return edge, the end node has to be the caller, and return edges on
	 * self-calls are not allowed. - If pEdge is not ReturnEdge, then this
	 * constraint is true - If pEdge is ReturnEdge, then we need to make sure
	 * all followings hold: - pStart is CallNode - pEnd is CallNode - Caller
	 * ImplicitParameterNode has some children existing (a return can happen as
	 * long as the caller invoked it has some CallNodes) - pStart and pEnd can
	 * not have same parent node
	 */
	public static SemanticConstraint returnEdge()
	{
		// ControlFlow flow = new ControlFlow(pDiagram);
		return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram) -> {
			return !(pEdge.getClass() == ReturnEdge.class && (pStart.getClass() != CallNode.class ||
					pEnd.getClass() != CallNode.class || getCaller(pStart, pDiagram).isEmpty() ||
					pEnd != getCaller(pStart, pDiagram).get() || pStart.getParent() == pEnd.getParent()));
		};
	}

	/**
	 * Call edges that land on a parameter node must land on the lifeline part,
	 * except if it is allowed to create a constructor.
	 */
	public static SemanticConstraint callEdgeEnd()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram) -> {
			return !(pEdge.getClass() == CallEdge.class &&
					(pEnd.getClass() == ImplicitParameterNode.class || pEnd.getClass() == PointNode.class) &&
					!canCreateConstructor(pStart, pEnd));
		};
	}

	/**
	 * It's only legal to start an interaction on a parameter node if there are
	 * no existing activations in the diagram.
	 */
	public static SemanticConstraint singleEntryPoint()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram) -> {
			return !(pEdge.getClass() == CallEdge.class && pStart.getClass() == ImplicitParameterNode.class &&
					hasEntryPoint(pDiagram));
		};
	}

	private static boolean hasEntryPoint(Diagram pDiagram)
	{
		return pDiagram.rootNodes().stream().anyMatch(SequenceDiagramSemanticConstraints::hasCallNode);
	}

	private static boolean hasCallNode(Node pNode)
	{
		return pNode.getClass() == ImplicitParameterNode.class && !pNode.getChildren().isEmpty();
	}

	/**
	 * Checks whether it is permitted to create a "creates" edge between
	 * pStartNode and pEndNode.
	 *
	 * This is possible pStartNode is a CallNode or an ImplicitParameterNode,
	 * and if the end node it is an ImplicitParameterNode with no child node and
	 * the point selected is in its top rectangle.
	 *
	 * @param pStartNode The desired start node for the "creates" edge.
	 * @param pEndNode The desired end node of the "creates" edge.
	 * @return True if pStartNode is a CallNode or and ImplicitParameterNode and
	 * pEndNode is an ImplicitParameterNode with no child node and pPoint is
	 * within the top rectangular bound of pNode.
	 */

	public static boolean canCreateConstructor(Node pStartNode, Node pEndNode)
	{
		if( !(pStartNode instanceof ImplicitParameterNode || pStartNode instanceof CallNode) )
		{
			return false;
		}
		return pEndNode instanceof ImplicitParameterNode && pEndNode.getChildren().isEmpty();
	}

	/*
	 * Returns the caller of a node, if it exists.
	 *
	 * @param pNode The node to obtain the caller for.
	 * 
	 * @return The CallNode that has a outgoing edge terminated at pNode, if
	 * there is one.
	 * 
	 * @pre pNode != null && contains(pNode)
	 */
	private static Optional<CallNode> getCaller(Node pNode, Diagram pDiagram)
	{
		assert pNode != null && pDiagram.contains(pNode);
		return pDiagram.edges().stream().filter(CallEdge.class::isInstance).filter(edge -> edge.getEnd() == pNode)
				.map(Edge::getStart).map(CallNode.class::cast).findFirst();
	}

}
