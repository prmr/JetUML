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

import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;

/**
 * A UML sequence diagram.
 */
public final class SequenceDiagram extends Diagram
{
	private static final Node[] NODE_PROTOTYPES = new Node[]{new ImplicitParameterNode(), new NoteNode()};
	private static final Edge[] EDGE_PROTOTYPES = new Edge[]{new CallEdge(), new ReturnEdge(), new NoteEdge()};
	
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
		return RESOURCES.getString("sequencediagram.file.extension");
	}

	@Override
	public String getDescription() 
	{
		return RESOURCES.getString("sequencediagram.file.name");
	}
}
