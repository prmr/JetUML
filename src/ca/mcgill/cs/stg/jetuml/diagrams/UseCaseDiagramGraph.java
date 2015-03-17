/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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

package ca.mcgill.cs.stg.jetuml.diagrams;

import java.util.ResourceBundle;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;
import ca.mcgill.cs.stg.jetuml.graph.ActorNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.CommunicationEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.ExtendRelationEdge;
import ca.mcgill.cs.stg.jetuml.graph.GeneralizationEdge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.IncludeRelationEdge;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
import ca.mcgill.cs.stg.jetuml.graph.UseCaseNode;

/**
 *   A UML use case diagram.
 */
public class UseCaseDiagramGraph extends Graph
{
	private static final Node[] NODE_PROTOTYPES = new Node[]{new ActorNode(), new UseCaseNode(), new NoteNode()};	
	private static final Edge[] EDGE_PROTOTYPES = new Edge[5];

	static
	{
		EDGE_PROTOTYPES[0] = new CommunicationEdge();

		EDGE_PROTOTYPES[1] = new ExtendRelationEdge();
		
		EDGE_PROTOTYPES[2] = new IncludeRelationEdge();;
	    
	    EDGE_PROTOTYPES[3] = new GeneralizationEdge();

	    EDGE_PROTOTYPES[4] = new NoteEdge();
	}
	
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
		return ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.UMLEditorStrings").getString("usecase.extension");
	}

	@Override
	public String getDescription() 
	{
		return ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.UMLEditorStrings").getString("usecase.name");
	}
}





