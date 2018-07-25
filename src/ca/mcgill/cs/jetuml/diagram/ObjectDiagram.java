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

package ca.mcgill.cs.jetuml.diagram;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;

/**
 *  An UML-style object diagram that shows object references.
 */
public final class ObjectDiagram extends Diagram
{
	private static final Node[] NODE_PROTOTYPES = new Node[3];
	private static final Edge[] EDGE_PROTOTYPES = new Edge[3];
	
	static
	{
		NODE_PROTOTYPES[0] = new ObjectNode();
	      
		FieldNode fieldNode = new FieldNode();
	    fieldNode.setName("name");
	    fieldNode.setValue("value");
	    
	    NODE_PROTOTYPES[1] = fieldNode;
	    NODE_PROTOTYPES[2] = new NoteNode();
	    
	    EDGE_PROTOTYPES[0] = new ObjectReferenceEdge();
	    EDGE_PROTOTYPES[1] = new ObjectCollaborationEdge();
	    EDGE_PROTOTYPES[2] = new NoteEdge();
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
		return RESOURCES.getString("objectdiagram.file.extension");
	}

	@Override
	public String getDescription() 
	{
		return RESOURCES.getString("objectdiagram.file.name");
	}
}
