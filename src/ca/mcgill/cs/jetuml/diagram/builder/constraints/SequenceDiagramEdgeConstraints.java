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

package ca.mcgill.cs.jetuml.diagram.builder.constraints;

import ca.mcgill.cs.jetuml.diagram.ControlFlow;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.viewers.nodes.ImplicitParameterNodeViewer;

/**
 * Methods to create edge addition constraints that only apply to
 * state diagrams. CSOFF:
 */
public final class SequenceDiagramEdgeConstraints
{
	private static final ImplicitParameterNodeViewer IMPLICIT_PARAMETER_NODE_VIEWER = new ImplicitParameterNodeViewer();
	
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
							 	!canCreateConstructor(pStart, pEnd, pDiagram, pEndPoint));
		};
	}
	
	private static boolean canCreateConstructor(Node pStartNode, Node pEndNode, Diagram pDiagram, Point pEndPoint)
	{
		return 	(pStartNode instanceof ImplicitParameterNode || pStartNode instanceof CallNode) && 
				new ControlFlow(pDiagram).canCreateConstructedObject(pEndNode, pEndPoint);
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
