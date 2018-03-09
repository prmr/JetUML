/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2017 by the contributors of the JetUML project.
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
import ca.mcgill.cs.jetuml.graph.Edge2;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.Node2;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge2;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseGeneralizationEdge;
import ca.mcgill.cs.jetuml.graph.nodes.ActorNode;
import ca.mcgill.cs.jetuml.graph.nodes.ActorNode2;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode2;
import ca.mcgill.cs.jetuml.graph.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.graph.nodes.UseCaseNode2;

/**
 *   A UML use case diagram.
 */
public class UseCaseDiagramGraph2 extends Graph2
{
	private static final Node2[] NODE_PROTOTYPES = new Node2[]{new ActorNode2(), new UseCaseNode2(), new NoteNode2()};
	private static final Edge2[] EDGE_PROTOTYPES = new Edge2[]{new UseCaseAssociationEdge(),
															 new UseCaseDependencyEdge(UseCaseDependencyEdge.Type.Extend),
															 new UseCaseDependencyEdge(UseCaseDependencyEdge.Type.Include),
															 new UseCaseGeneralizationEdge(),
															 new NoteEdge2()};

	@Override
	public Node2[] getNodePrototypes()
	{
		return NODE_PROTOTYPES;
	}

	@Override
	public Edge2[] getEdgePrototypes()
	{
		return EDGE_PROTOTYPES;
	}   
	
	@Override
	public String getFileExtension() 
	{
		return ResourceBundle.getBundle("ca.mcgill.cs.jetuml.UMLEditorStrings").getString("usecase.extension");
	}

	@Override
	public String getDescription() 
	{
		return ResourceBundle.getBundle("ca.mcgill.cs.jetuml.UMLEditorStrings").getString("usecase.name");
	}
	
	@Override
	public boolean canConnect(Edge2 pEdge, Node2 pNode1, Node2 pNode2, Point pPoint2)
	{
		if( !super.canConnect(pEdge, pNode1, pNode2, pPoint2) )
		{
			return false;
		}
		if( pNode1 == pNode2 )
		{	// Self-edges are not allowed in use case diagrams
			return false;
		}
		
		return true;
	}
}





