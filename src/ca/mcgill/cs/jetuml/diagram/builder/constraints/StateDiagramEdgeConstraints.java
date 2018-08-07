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

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;

/**
 * Methods to create edge addition constraints that only apply to
 * state diagrams. CSOFF:
 */
public final class StateDiagramEdgeConstraints
{
	private StateDiagramEdgeConstraints() {}
	
	/*
	 * No edges are allowed into an Initial Node
	 */
	public static Constraint noEdgeToInitialNode(Node pEnd)
	{
		return ()->
		{
			return pEnd.getClass() != InitialStateNode.class;
		};
	}
	
	/*
	 * The only edge allowed out of a FinalNode is a NoteEdge
	 */
	public static Constraint noEdgeFromFinalNode(Edge pEdge, Node pStart)
	{
		return ()->
		{
			return !(pStart.getClass() == FinalStateNode.class && pEdge.getClass() != NoteEdge.class );
		};
	}
}
