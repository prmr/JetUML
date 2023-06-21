package org.jetuml.diagram.validator.constraints;

import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;

/**
 * Constraints for sequence diagrams.
 */
public final class SequenceDiagramSemanticConstraints
{
	private SequenceDiagramSemanticConstraints() {}

	/**
	 * For a return edge, the end node has to be the caller, and return edges on
	 * self-calls are not allowed. 
	 * 	- If pEdge is not ReturnEdge, then this constraint is true 
	 *  - If pEdge is ReturnEdge, then we need to make sure all followings hold: 
	 *      - pStart is CallNode - pEnd is CallNode 
	 *      - Caller's ImplicitParameterNode has some children existing (a return can happen as
	 * 		  long as the caller invoked it has some CallNodes) 
	 *      - pStart and pEnd cannot have same parent node
	 */
	public static EdgeConstraint returnEdge()
	{
		// CSOFF:
		return (Edge pEdge, Diagram pDiagram) -> {
			return !(pEdge.getClass() == ReturnEdge.class && (pEdge.start().getClass() != CallNode.class ||
					pEdge.end().getClass() != CallNode.class || getCaller(pEdge.start(), pDiagram).isEmpty() ||
					pEdge.end() != getCaller(pEdge.start(), pDiagram).get() || 
					pEdge.start().getParent() == pEdge.end().getParent()));
		}; // CSON:
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
		return pDiagram.edges().stream().filter(CallEdge.class::isInstance).filter(edge -> edge.end() == pNode)
				.map(Edge::start).map(CallNode.class::cast).findFirst();
	}

}
