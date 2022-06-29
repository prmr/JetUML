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

package org.jetuml.diagram.builder.constraints;

import org.jetuml.diagram.ControlFlow;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Point;
import org.jetuml.viewers.nodes.ImplicitParameterNodeViewer;

/**
 * Methods to create edge addition constraints that only apply to
 * state diagrams. CSOFF:
 */
public final class SequenceDiagramEdgeConstraints
{
	private static final ImplicitParameterNodeViewer IMPLICIT_PARAMETER_NODE_VIEWER = 
			new ImplicitParameterNodeViewer(DiagramType.newRendererInstanceFor(new Diagram(DiagramType.SEQUENCE)));
	
	private SequenceDiagramEdgeConstraints() {}
	
	/*
	 * No edge is allowed to start in a parameter node.
	 */
	public static Constraint noEdgesFromParameterTop()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram)->
		{
			return !(pStart.getClass() == ImplicitParameterNode.class && 
					IMPLICIT_PARAMETER_NODE_VIEWER.getTopRectangle(pStart).contains(pStartPoint));
		};
	}
	
	/*
	 * For a return edge, the end node has to be the caller, and return
	 * edges on self-calls are not allowed.
	 */
	public static Constraint returnEdge()
	{
		//ControlFlow flow = new ControlFlow(pDiagram);
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram)->
		{
			ControlFlow flow = new ControlFlow(pDiagram);
			return !(pEdge.getClass() == ReturnEdge.class && 
					(pStart.getClass() != CallNode.class ||
					 pEnd.getClass() != CallNode.class ||
					 !flow.getCaller(pStart).isPresent() ||
					 pEnd != flow.getCaller(pStart).get() ||
					 pStart.getParent() == pEnd.getParent()));
		};
	}
	
	/*
	 * Call edges that land on a parameter node must land on the lifeline part,
	 * except if it is allowed to create a constructor.
	 */
	public static Constraint callEdgeEnd()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram)->
		{
			return !(pEdge.getClass() == CallEdge.class && 
					 pEnd.getClass() == ImplicitParameterNode.class &&
							 IMPLICIT_PARAMETER_NODE_VIEWER.getTopRectangle(pEnd).contains(pEndPoint) && 
							 	!canCreateConstructor(pStart, pEnd, pEndPoint));
		};
	}
	
	/**
	 * Checks whether it is permitted to create a "creates" edge between
	 * pStartNode and pEndNode.
	 * 
	 * This is possible pStartNode is a CallNode or an ImplicitParameterNode, and if 
	 * the end node it is an ImplicitParameterNode with no child node and the point selected
	 * is in its top rectangle.
	 * 
	 * @param pStartNode The desired start node for the "creates" edge.
	 * @param pEndNode The desired end node of the "creates" edge.
	 * @param pPoint The point on the canvas selected by the user.
	 * @return True if pStartNode is a CallNode or and ImplicitParameterNode and pEndNode
	 *     is an ImplicitParameterNode with no child node and pPoint is within the top rectangular bound of pNode.
	 * @pre pNode != null && pPoint != null
	 */
	public static boolean canCreateConstructor(Node pStartNode, Node pEndNode, Point pEndPoint)
	{
		if( !(pStartNode instanceof ImplicitParameterNode || pStartNode instanceof CallNode) )
		{
			return false;
		}
		return pEndNode instanceof ImplicitParameterNode && 
				IMPLICIT_PARAMETER_NODE_VIEWER.getTopRectangle(pEndNode).contains(pEndPoint) && 
				pEndNode.getChildren().isEmpty();
	}
	
	/*
	 * It's only legal to start an interaction on a parameter node if there are no existing activations
	 * in the diagram.
	 */
	public static Constraint singleEntryPoint()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram)->
		{
			return !(pEdge.getClass() == CallEdge.class && 
					pStart.getClass() == ImplicitParameterNode.class &&
					new ControlFlow(pDiagram).hasEntryPoint());
		};
	}
}
