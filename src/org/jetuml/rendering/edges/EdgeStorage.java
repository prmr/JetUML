/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
package org.jetuml.rendering.edges;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.geom.Point;
import org.jetuml.rendering.EdgePath;

/**
 * Stores the EdgePaths of Edges for class diagrams.
 */
public class EdgeStorage
{
	private Map<Edge, EdgePath> aEdgePaths = new IdentityHashMap<>();
 	
 	/**
 	 * Adds pEdge and pEdgePath into storage.
 	 * If pEdge is already in storage, then its EdgePath is updated to pEdgePath.
 	 * @param pEdge the edge to store
 	 * @pre pEdge!=null
 	 * @pre pEdgePath!=null
 	 */
 	public void store(Edge pEdge, EdgePath pEdgePath)
 	{
 		assert pEdge!=null && pEdgePath!=null;
 		aEdgePaths.put(pEdge, pEdgePath);
 	}
 
 	
 	/**
 	 * Returns whether storage is empty.  
 	 * @return true if aEdgePaths is empty, false otherwise.
 	 */
 	public boolean isEmpty()
 	{
 		return aEdgePaths.isEmpty();
 	}
 	
 	/**
 	 * Returns pEdge's EdgePath from storage.
 	 * @param pEdge the stored edge of interest
 	 * @return the EdgePath for pEdge from storage 
 	 * @pre pEdge!=null
 	 * @pre this.contains(pEdge)
 	 */
 	public EdgePath getEdgePath(Edge pEdge)
 	{
 		assert pEdge!=null;
 		assert this.contains(pEdge);
 		return aEdgePaths.get(pEdge);
	
 	}
 	
 	/**
 	 * Returns whether pEdge is in storage.
 	 * @param pEdge the edge of interest
 	 * @return true if pEdge is in storage, false otherwise
 	 * @pre pEdge!=null
 	 */
 	public boolean contains(Edge pEdge)
 	{
 		assert pEdge!=null;
 		return aEdgePaths.containsKey(pEdge);
 	}

 	/**
 	 * Returns a list of edges in storage which are connected to pNode.
	 * @param pNode The node of interest
	 * @return All the edges connected to pNode
	 * @pre pNode != null
	 */
	public List<Edge> edgesConnectedTo(Node pNode)
	{
		assert pNode != null;
		List<Edge> result = new ArrayList<>();
		for( Edge edge : aEdgePaths.keySet() )
		{
			if( edge.start() == pNode || edge.end() == pNode )
			{
				result.add(edge);
			}
		}
		return result;
	}
	
	/**
	 * Returns whether pConnectionPoint is available.
	 * @param pConnectionPoint a Point in the diagram
	 * @return false if pPoint is a start or end connection point for an edge in storage, true otherwise
	 * @pre pConnectionPoint !=null;
	 */
	public boolean connectionPointIsAvailable(Point pConnectionPoint)
	{
		assert pConnectionPoint !=null;
		for( EdgePath path : aEdgePaths.values() )
		{
			if(path.getStartPoint().equals(pConnectionPoint) || path.getEndPoint().equals(pConnectionPoint))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns a list of edges which share the same two attached nodes as pEdge, reffered to as "shared-node edges".
	 * Based on diagram constraints, this list will typically either be empty or contain a maximum of 1 edge. 
	 * @param pEdge the edge of interest
	 * @return a list of edges from storage which are also attached to pEdge.getStart() and pEdge.getEnd(). 	 
	 */
	public List<Edge> getEdgesWithSameNodes(Edge pEdge)
	{
		return aEdgePaths.keySet().stream()
				.filter(edge -> edge.start() == pEdge.start() || edge.start() == pEdge.end())
				.filter(edge -> edge.end() == pEdge.start() || edge.end() == pEdge.end())
				.filter(edge -> !edge.equals(pEdge))
				.collect(Collectors.toList());
	}
	
	/**
	 * Clears edge storage.
	 */
	public void clearStorage()
	{
		aEdgePaths.clear();
	}
}
