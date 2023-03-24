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

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramRenderer;

/**
 * Methods to create edge addition constraints that apply to
 * all diagrams. CSOFF:
 */
public final class EdgeConstraints
{
	private EdgeConstraints() {}
	
	/* 
	 * A note edge can only be added between:
	 * - Any node and a note node.
	 * - A note node and a point node. 
	 */
	public static Constraint noteEdge()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, DiagramRenderer pRenderer)->
				!( pEdge.getClass() == NoteEdge.class && 
						!((pStart.getClass() == NoteNode.class && pEnd.getClass() == PointNode.class) ||
								(pEnd.getClass() == NoteNode.class)));
		
	}
	
	/* 
	 * An edge can only be added to or from a note node if it is a note edge
	 */
	public static Constraint noteNode()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, DiagramRenderer pRenderer)->
		{
			if( pStart.getClass() == NoteNode.class || pEnd.getClass() == NoteNode.class )
			{
				return pEdge.getClass() == NoteEdge.class;
			}
			return true;
		};
	}
	
	/*
	 * Only pNumber of edges of the same type are allowed in one direction between two nodes
	 */
	public static Constraint maxEdges(int pNumber)
	{
		assert pNumber > 0;
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, DiagramRenderer pRenderer)->
		{
			return numberOfEdges(pEdge.getClass(), pStart, pEnd, pRenderer.diagram()) <= pNumber-1;
		};
	}
	
	/*
	 * Self-edges are not allowed.
	 */
	public static Constraint noSelfEdge()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, DiagramRenderer pRenderer)-> 
			{ return pStart != pEnd; };
	}

	/*
	 * Returns the number of edges of type pType between pStart and pEnd
	 */
	private static int numberOfEdges(Class<? extends Edge> pType, Node pStart, Node pEnd, Diagram pDiagram)
	{
		assert pType != null && pStart != null && pEnd != null && pDiagram != null;
		int result = 0;
		for(Edge edge : pDiagram.edges())
		{
			if(edge.getClass() == pType && edge.getStart() == pStart && edge.getEnd() == pEnd)
			{
				result++;
			}
		}
		return result;
	}
}