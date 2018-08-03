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

package ca.mcgill.cs.jetuml.diagram.builder;

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.StateDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Point;

/**
 * A builder for state diagrams.
 */
public class StateDiagramBuilder extends DiagramBuilder
{
	/**
	 * Creates a new builder for state diagrams.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null;
	 */
	public StateDiagramBuilder( Diagram pDiagram )
	{
		super( pDiagram );
		assert pDiagram instanceof StateDiagram;
	}

	// CSOFF:
	@Override
	protected boolean canConnect(Edge pEdge, Node pNode1, Optional<Node> pNode2, Point pPoint2)
	{
		if( !pNode2.isPresent() )
		{
			return false;
		}
		if( numberOfSimilarEdges(pNode1, pNode2.get()) > 1 )
		{
			return false;
		}
		if((pNode2.get() instanceof NoteNode || pNode1 instanceof NoteNode) && !(pEdge instanceof NoteEdge))
		{
			return false;
		}
		if( pEdge instanceof NoteEdge && !(pNode1 instanceof NoteNode || pNode2.get() instanceof NoteNode))
		{
			return false;
		}
		if(pNode1 != null)
		{
			if(pNode1 instanceof FinalStateNode)
			{
				if(!(pEdge instanceof NoteEdge))
				{
					return false;
				}
			}
		}
		if(pNode2.get() instanceof InitialStateNode)
		{
			if(!(pEdge instanceof NoteEdge))
			{
				return false;
			}
		}
		return true;
	} // CSON:

	private int numberOfSimilarEdges(Node pNode1, Node pNode2)
	{
		int lReturn = 0;
		for( Edge edge : aDiagram.edges() )
		{
			if( edge.getStart() == pNode1 && edge.getEnd() == pNode2 )
			{
				lReturn++;
			}
		}
		return lReturn;
	}
}
