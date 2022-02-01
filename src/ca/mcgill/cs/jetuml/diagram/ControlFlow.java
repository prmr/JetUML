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

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import ca.mcgill.cs.jetuml.annotations.Immutable;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ConstructorEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;

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
	 * Returns the list of nodes directly called by pNode,
	 * in the order of the call sequence.
	 * 
	 * @param pNode The node to obtain the callees for.
	 * @return All Nodes pointed to by an outgoing edge starting
	 *     at pNode, or the empty list if there are none.
	 * @pre pNode != null && contains(pNode)
	 */
	public List<Node> getCallees(Node pNode)
	{
		assert pNode != null && aDiagram.contains(pNode);
		return aDiagram.edges().stream()
				.filter(CallEdge.class::isInstance)
				.filter(edge -> edge.getStart() == pNode)
				.map(Edge::getEnd)
				.collect(toList());
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
	 * Returns the caller of a node, if it exists.
	 * 
	 * @param pNode The node to obtain the caller for.
	 * @return The CallNode that has a outgoing edge terminated
	 *     at pNode, if there is one.
	 * @pre pNode != null && contains(pNode)
	 */
	public Optional<CallNode> getCaller(Node pNode)
	{
		assert pNode != null && aDiagram.contains(pNode);
		return aDiagram.edges().stream()
			.filter(CallEdge.class::isInstance)
			.filter(edge -> edge.getEnd() == pNode)
			.map(Edge::getStart)
			.map(CallNode.class::cast)
			.findFirst();
	}
	
	/**
	 * Returns whether pNode is the first callee in the 
	 * call sequence of its caller.
	 * 
	 * @param pNode The node to test. Must have a caller. 
	 * @return true iif pNode is the first callee of its parent
	 * @pre pNode != null && getCaller(pNode).isPresent() && contains(pNode)
	 */
	public boolean isFirstCallee(CallNode pNode)
	{
		assert pNode != null;
		Optional<CallNode> caller = getCaller(pNode);
		assert caller.isPresent();
		List<Node> callees = getCallees(caller.get());
		return callees.get(0) == pNode;
	}
	
	/**
	 * @param pNode The node to check.
	 * @return The node called before pNode by the parent. 
	 * @pre pNode !=null
	 * @pre getCaller(pNode).isPresent()
	 * @pre !isFirstCallee(pNode)
	 * @pre contains(pNode)
	 */
	public CallNode getPreviousCallee(CallNode pNode)
	{
		assert pNode != null;
		Optional<CallNode> caller = getCaller(pNode);
		assert caller.isPresent();
		assert !isFirstCallee(pNode);
		List<Node> callees = getCallees(caller.get());
		int index = callees.indexOf(pNode);
		assert index >= 1;
		return (CallNode) callees.get(index-1);
	}
	
	/**
	 * Checks whether a call node represents a nested (recursive) call.
	 * 
	 * @param pNode The node to test.
	 * @return True if pNode has a caller on the same implicit parameter node, false otherwise.
	 * @pre pNode != null && contains(pNode) && pNode.getParent() != null
	 */
	public boolean isNested(CallNode pNode)
	{
		assert pNode != null;
		Optional<CallNode> caller = getCaller(pNode);
		if( !caller.isPresent() )
		{
			return false;
		}
		return caller.get().getParent() == pNode.getParent();
	}
	
	/**
	 * @param pNode The node to check.
	 * @return The number of call nodes upstream in the control-flow
	 *     that are on the same implicit parameter node.
	 */
	public int getNestingDepth(CallNode pNode)
	{
		assert pNode != null;
		int result = 0;
		Optional<CallNode> node = getCaller(pNode);
		while( node.isPresent() )
		{
			if( node.get().getParent() == pNode.getParent() )
			{
				result++;
			}
			node = getCaller(node.get());
		}
		return result;
	}
	
	/**
	 * @return True if there is at least one call node in the diagram.
	 */
	public boolean hasEntryPoint()
	{
		return aDiagram.rootNodes().stream().anyMatch(ControlFlow::hasCallNode);
	}
	
	private static boolean hasCallNode(Node pNode)
	{
		return pNode.getClass() == ImplicitParameterNode.class &&
				!pNode.getChildren().isEmpty();
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
	
	private boolean onlyCallsOneObject(Node pCaller, Node pParentNode)
	{
		assert pCaller!= null && pParentNode != null;
		return getCalls(pCaller).stream().allMatch(edge -> edge.getEnd().getParent() == pParentNode);
	}
	
	private boolean onlyConnectedToOneEdge(Node pNode, Edge pEdge)
	{
		assert pNode != null && pEdge != null;
		List<CallEdge> calls = getCalls(pNode);
		return  getCaller(pNode).isEmpty() &&
				calls.size() == 1 &&
				calls.contains(pEdge);
	}
	
	/**
	 * @param pNode The Node to obtain the caller and upstream DiagramElements for.
	 * @return The Collection of DiagramElements in the upstream of pNode.
	 *     Excludes pNode and elements returned by getCoRemovals method in the DiagramBuilder class.
	 * @pre pNode != null
	 */
	public Collection<DiagramElement> getNodeUpstreams(Node pNode)
	{
		assert pNode != null;
		Set<DiagramElement> elements = new HashSet<>();
		if( pNode.getClass() == CallNode.class )
		{
			Optional<CallNode> caller = getCaller(pNode);
			if( caller.isPresent() && getCaller(caller.get()).isEmpty() )
			{
				CallNode callerNode = caller.get();
				// If the caller is only connected to one call
				if( getCalls(callerNode).size() == 1 && getCalls(callerNode).get(0).getEnd() == pNode )
				{
					elements.add(callerNode);
				}
				else if( isConstructorExecution(pNode) )
				{
					getCalls(callerNode).stream().filter(e -> e.getEnd().getParent() == pNode.getParent())
												 .forEach(e -> elements.add(e));
					if( onlyCallsOneObject(callerNode, pNode.getParent()) )
					{
						elements.add(callerNode);
					}
				}
			}
		}
		else if( pNode.getClass() == ImplicitParameterNode.class && pNode.getChildren().size() > 0 )
		{
			Optional<CallNode> caller = getCaller(firstChildOf(pNode));
			if( caller.isPresent() && getCaller(caller.get()).isEmpty() && onlyCallsOneObject(caller.get(), pNode) )
			{
				elements.add(caller.get());
			}
		}
		return elements;
	}
	
	/**
	 * @param pEdge The Edge to obtain the edge start for.
	 * @return The Optional value of the start Node for pEdge.
	 * @pre pEdge != null
	 */
	public Optional<DiagramElement> getEdgeStart(Edge pEdge)
	{
		assert pEdge != null;
		Node start = pEdge.getStart();
		if( onlyConnectedToOneEdge(start, pEdge) )
		{
			return Optional.of(start);
		}
		else if( pEdge.getClass() == ConstructorEdge.class )
		{
			if ( getCaller(start).isEmpty() && onlyCallsOneObject(start, pEdge.getEnd().getParent()) )
			{
				return Optional.of(start);
			}
		}
		return Optional.empty();
	}	
	
	/**
	 * @param pElements The DiagramElements to obtain the corresponding ReturnEdges for.
	 * @return The Collection of corresponding ReturnEdges for pElements.
	 */
	public Collection<DiagramElement> getCorrespondingReturnEdges(List<DiagramElement> pElements)
	{
		assert pElements != null;
		Set<DiagramElement> returnEdges = new HashSet<>();
		for( DiagramElement element: pElements )
		{
			if( element instanceof CallEdge )
			{
				Optional<Edge> returnEdge = getReturnEdge((Edge) element);
				if( returnEdge.isPresent() )
				{
					returnEdges.add(returnEdge.get());
				}
			}
		}
		return returnEdges;
	}
	
	private Optional<Edge> getReturnEdge(Edge pEdge)
	{
		return aDiagram.edges().stream()
			.filter(ReturnEdge.class::isInstance)
			.filter(edge -> edge.getStart() == pEdge.getEnd())
			.filter(edge -> edge.getEnd() == pEdge.getStart())
			.findFirst();
	}
}
