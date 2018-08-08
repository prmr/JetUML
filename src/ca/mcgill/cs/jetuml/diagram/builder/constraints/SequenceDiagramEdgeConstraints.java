/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.diagram.builder.constraints;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.views.nodes.ImplicitParameterNodeView;

/**
 * Methods to create edge addition constraints that only apply to
 * state diagrams. CSOFF:
 */
public final class SequenceDiagramEdgeConstraints
{
	private SequenceDiagramEdgeConstraints() {}
	
	/*
	 * No edge is allowed to start in a parameter node.
	 */
	public static Constraint noEdgesFromParameter(Node pStart)
	{
		return ()->
		{
			return pStart.getClass() != ImplicitParameterNode.class;
		};
	}
	
	/*
	 * For a return edge, the end node has to be the caller, and return
	 * edges on self-calls are not allowed.
	 */
	public static Constraint returnEdge(Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram)
	{
		return ()->
		{
			return !(pEdge.getClass() == ReturnEdge.class && 
					(pStart.getClass() != CallNode.class ||
					 pEnd.getClass() != CallNode.class ||
					 !((SequenceDiagram)pDiagram).getCaller(pStart).isPresent() ||
					 pEnd != ((SequenceDiagram)pDiagram).getCaller(pStart).get() ||
					 ((CallNode)pStart).getParent() == ((CallNode)pEnd).getParent()));
		};
	}
	
	/*
	 * Call edges that land on a parameter node must land on the lifeline part.
	 */
	public static Constraint callEdgeEnd(Edge pEdge, Node pEndNode, Point pEndPoint)
	{
		return ()->
		{
			return !(pEdge.getClass() == CallEdge.class && 
					 pEndNode.getClass() == ImplicitParameterNode.class &&
					 ((ImplicitParameterNodeView)((ImplicitParameterNode)pEndNode).view()).getTopRectangle().contains(pEndPoint));
		};
	}
}
