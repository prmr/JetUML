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

import ca.mcgill.cs.stg.jetuml.graph.ActorNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
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
	private static final int EDGE_PROTOTYPE_SIZE = 5;
	private static final Edge[] EDGE_PROTOTYPES = new Edge[EDGE_PROTOTYPE_SIZE];

	static
	{
		EDGE_PROTOTYPES[0] = ClassRelationshipEdge.createCommunicationEdge();

		EDGE_PROTOTYPES[1] = ClassRelationshipEdge.createExtendRelationEdge();
		
		EDGE_PROTOTYPES[2] = ClassRelationshipEdge.createIncludeRelationEdge();;
	    
	    EDGE_PROTOTYPES[3] = ClassRelationshipEdge.createGeneralizationEdge();

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





