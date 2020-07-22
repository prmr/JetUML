/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package ca.mcgill.cs.jetuml.diagram;

import java.util.ArrayList;
import java.util.List;

/**
 * Broadens the interface to a diagram, to facilitate testing.
 */
public class DiagramAccessor
{
	private final Diagram aDiagram;
	
	public DiagramAccessor(Diagram pDiagram)
	{
		aDiagram = pDiagram;
	}
	
	public List<Node> getRootNodes()
	{
		ArrayList<Node> result = new ArrayList<>();
		for( Node node : aDiagram.rootNodes() )
		{
			result.add(node);
		}
		return result;
	}
	
	public List<Edge> getEdges()
	{
		ArrayList<Edge> result = new ArrayList<>();
		for( Edge edge : aDiagram.edges() )
		{
			result.add(edge);
		}
		return result;
	}
	
	public List<Edge> getEdgesConnectedTo(Node pNode)
	{
		ArrayList<Edge> result = new ArrayList<>();
		for( Edge edge : aDiagram.edgesConnectedTo(pNode))
		{
			result.add(edge);
		}
		return result;
	}
	
	public void connectAndAdd(Edge pEdge, Node pStart, Node pEnd)
	{
		pEdge.connect(pStart, pEnd, aDiagram);
		aDiagram.addEdge(pEdge);
	}
}
