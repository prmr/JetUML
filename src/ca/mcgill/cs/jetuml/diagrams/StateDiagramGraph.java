/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagrams;

import java.util.ResourceBundle;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.graph.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.graph.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.StateNode;

/**
 * A UML state diagram.
 */
public class StateDiagramGraph extends Graph
{
	private static final Node[] NODE_PROTOTYPES = new Node[]{new StateNode(), new InitialStateNode(), new FinalStateNode(), new NoteNode()};
	private static final Edge[] EDGE_PROTOTYPES = new Edge[]{new StateTransitionEdge(), new NoteEdge()};
	
	@Override
	public Node[] getNodePrototypes()
	{
		return NODE_PROTOTYPES;
	}

	@Override
	public Edge[] getEdgePrototypes()
	{
		return EDGE_PROTOTYPES;
	}
	
	@Override
	public String getFileExtension() 
	{
		return ResourceBundle.getBundle("ca.mcgill.cs.jetuml.UMLEditorStrings").getString("state.extension");
	}

	@Override
	public String getDescription() 
	{
		return ResourceBundle.getBundle("ca.mcgill.cs.jetuml.UMLEditorStrings").getString("state.name");
	}
	
	// CSOFF:
	@Override
	public boolean canConnect(Edge pEdge, Node pNode1, Node pNode2, Point pPoint2)
	{
		if( pNode2 == null )
		{
			return false;
		}
		if( numberOfSimilarEdges(pNode1, pNode2) > 1 )
		{
			return false;
		}
		if((pNode2 instanceof NoteNode || pNode1 instanceof NoteNode) && !(pEdge instanceof NoteEdge))
		{
			return false;
		}
		if( pEdge instanceof NoteEdge && !(pNode1 instanceof NoteNode || pNode2 instanceof NoteNode))
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
		if(pNode2 instanceof InitialStateNode)
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
		for( Edge edge : getEdges() )
		{
			if( edge.getStart() == pNode1 && edge.getEnd() == pNode2 )
			{
				lReturn++;
			}
		}
		return lReturn;
	}
}





