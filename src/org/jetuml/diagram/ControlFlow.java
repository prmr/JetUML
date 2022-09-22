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

package org.jetuml.diagram;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetuml.annotations.Immutable;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;

/**
 * An immutable wrapper around a sequence Diagram that can answer
 * various queries about the control-flow represented by 
 * the wrapped sequence diagram.
 */
@Immutable
public final class ControlFlow
{
	private final Diagram aDiagram;
	
	/**
	 * Creates a new ControlFlow to query pDiagram.
	 * 
	 * @param pDiagram The diagram to wrap.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.SEQUENCE
	 */
	public ControlFlow(Diagram pDiagram)
	{
		assert pDiagram != null;
		assert pDiagram.getType() == DiagramType.SEQUENCE;
		aDiagram = pDiagram;
	}
	
	/**
	 * @param pCaller The caller node.
	 * @return The list of call edges starting at pCaller
	 * @pre pCaller != null
	 */
	public List<CallEdge> getCalls(Node pCaller)
	{
		assert pCaller != null;
		return aDiagram.edges().stream()
				.filter(CallEdge.class::isInstance)
				.map(CallEdge.class::cast)
				.filter(edge -> edge.getStart() == pCaller)
				.collect(toList());
	}
	
	/**
	 * @param pNode The node to check.
	 * @return True if pNode is a CallNode and is at the end of a ConstructorEdge.
	 */
	public boolean isConstructorExecution(Node pNode)
	{
		assert pNode != null;
		if( pNode.getClass() != CallNode.class )
		{
			return false;
		}
		for( Edge edge : aDiagram.edges() )
		{
			if ( edge.getEnd() == pNode && edge.getClass() == ConstructorEdge.class )
			{
				return true;
			}
		}
		return false;	
	}
	
	/*
	 * Returns true if pNode is the ImplicitParameterNode that gets created in constructor call
	 */
	private boolean isConstructedObject(Node pNode)
	{
		assert pNode != null;
		return pNode.getClass() == ImplicitParameterNode.class && pNode.getChildren().size() > 0 &&
				isConstructorExecution(firstChildOf(pNode));
	}
	
	private static Node firstChildOf(Node pNode)
	{
		assert pNode.getChildren().size() > 0;
		return pNode.getChildren().get(0);
	}
	
	private Optional<Edge> getConstructorEdge(Node pNode)
	{
		assert pNode != null && isConstructorExecution(pNode);
		if( pNode.getClass() != CallNode.class )
		{
			return Optional.empty();	
		}
		for( Edge edge : aDiagram.edges() )
		{
			if ( edge.getEnd() == pNode && edge.getClass() == ConstructorEdge.class )
			{
				return Optional.of(edge);
			}
		}
		return Optional.empty();	
	}

	/**
	 * @param pEdge The Edge to get downstream elements from.
	 * @return The downstream DiagramElements of pEdge.
	 * @pre pEdge != null
	 */
	public Collection<DiagramElement> getEdgeDownStreams(Edge pEdge)
	{
		assert pEdge != null;
		Set<DiagramElement> downstreamElements = new HashSet<>();
		
		// The edge addition here is necessary for recursive calls
		downstreamElements.add(pEdge);
		if( pEdge.getClass() == ConstructorEdge.class )
		{
			Node endParent = pEdge.getEnd().getParent();
			downstreamElements.add(endParent);
			downstreamElements.addAll(endParent.getChildren());
			
			// Recursively add downstream elements of the child nodes
			for( Node child: endParent.getChildren() )
			{
				for( Edge edge: getCalls(child) )
				{
					downstreamElements.addAll(getEdgeDownStreams(edge));
				}
				
				// Add upstream edges of the child nodes
				for( Edge edge: aDiagram.edges() )
				{
					if( edge.getEnd() == child )
					{
						downstreamElements.add(edge);
					}
				}
			}
		}
		else if( pEdge.getClass() == CallEdge.class )
		{
			CallNode endNode = (CallNode)pEdge.getEnd();
			downstreamElements.add(endNode);
			for( Edge e: getCalls(endNode) )
			{
				downstreamElements.addAll(getEdgeDownStreams(e));
			}
		}
		return downstreamElements;
	}

	/** 
	 * @param pNode The Node to obtain downstream elements from.
	 * @return The downstream DiagramElements of pNode.
	 * @pre pNode!=null
	 */
	public Collection<DiagramElement> getNodeDownStreams(Node pNode)
	{
		assert pNode!=null;
		Set<DiagramElement> downstreamElements = new HashSet<>();
		if( isConstructorExecution(pNode) )
		{
			Optional<Edge> constructorEdge = getConstructorEdge(pNode);
			if( constructorEdge.isPresent() )
			{
				downstreamElements.addAll(getEdgeDownStreams(constructorEdge.get()));
			}
		}
		else if( isConstructedObject(pNode) )
		{
			Optional<Edge> constructorEdge = getConstructorEdge(firstChildOf(pNode));
			if( constructorEdge.isPresent() )
			{
				downstreamElements.addAll(getEdgeDownStreams(constructorEdge.get()));
			}
		}
		else if( pNode.getClass() == CallNode.class )
		{
			for( Edge edge: getCalls(pNode) )
			{
				downstreamElements.addAll(getEdgeDownStreams(edge));
			}
		}
		else if ( pNode.getClass() == ImplicitParameterNode.class )
		{
			downstreamElements.addAll(pNode.getChildren());
			for( Node child: pNode.getChildren() )
			{
				for( Edge edge: getCalls(child) )
				{
					downstreamElements.addAll(getEdgeDownStreams(edge));
				}
			}
		}
		return downstreamElements;
	}
}
