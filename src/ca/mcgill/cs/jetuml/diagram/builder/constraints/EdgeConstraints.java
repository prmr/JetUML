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
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;

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
	public static Constraint noteEdge(Edge pEdge, Node pStart, Node pEnd)
	{
		return ()->
		{
			if( pEdge.getClass() == NoteEdge.class && 
					!((pStart.getClass() == NoteNode.class && pEnd.getClass() == PointNode.class) ||
					  (pEnd.getClass() == NoteNode.class)))
			{
				return false;
			}
			return true;
		};
	}
	
	/* 
	 * An edge can only be added to or from a note node if it is a note edge
	 */
	public static Constraint noteNode(Edge pEdge, Node pStart, Node pEnd)
	{
		return ()->
		{
			if( pStart.getClass() == NoteNode.class || pEnd.getClass() == NoteNode.class )
			{
				return pEdge.getClass() == NoteEdge.class;
			}
			return true;
		};
	}
	
	/*
	 * Only one edge of a given type can be added between two nodes.
	 */
	public static Constraint existence(Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram)
	{
		return ()-> { return !existsEdge(pEdge.getClass(), pStart, pEnd, pDiagram); };
	}
	
	/*
	 * Self-edges are not allowed.
	 */
	public static Constraint noSelfEdge(Node pStart, Node pEnd)
	{
		return ()-> { return pStart != pEnd; };
	}
	
	/**
	 * Returns true iif there exists an edge of type pType between
	 * nodes pStart and pEnd. The direction matter, and the type
	 * testing is for the exact type pType, without using polymorphism.
	 * @param pType The type of edge to check for.
	 * @param pStart The start node.
	 * @param pEnd The end node.
	 * @return True if and only if there is an edge of type pType that
	 * starts at node pStart and ends at node pEnd.
	 */
	private static boolean existsEdge(Class<?> pType, Node pStart, Node pEnd, Diagram pDiagram)
	{
		assert pType != null && pStart != null && pEnd != null;
		for(Edge edge : pDiagram.edges())
		{
			if(edge.getClass() == pType && edge.getStart() == pStart && edge.getEnd() == pEnd)
			{
				return true;
			}
		}
		return false;
	}
}
