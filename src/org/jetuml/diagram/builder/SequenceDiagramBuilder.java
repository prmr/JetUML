/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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

package org.jetuml.diagram.builder;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.builder.constraints.ConstraintSet;
import org.jetuml.diagram.builder.constraints.EdgeConstraints;
import org.jetuml.diagram.builder.constraints.SequenceDiagramEdgeConstraints;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.SequenceDiagramRenderer;
import org.jetuml.rendering.nodes.ImplicitParameterNodeRenderer;

/**
 * A builder for sequence diagrams.
 */
public class SequenceDiagramBuilder extends DiagramBuilder
{
	private static final int CALL_NODE_YGAP = 5;
	private static final ConstraintSet CONSTRAINTS = new ConstraintSet(
			EdgeConstraints.noteEdge(),
			EdgeConstraints.noteNode(),
			EdgeConstraints.maxEdges(1),
			SequenceDiagramEdgeConstraints.noEdgesFromParameterTop(),
			SequenceDiagramEdgeConstraints.returnEdge(),
			SequenceDiagramEdgeConstraints.singleEntryPoint(),
			SequenceDiagramEdgeConstraints.callEdgeEnd()
	);
	
	/**
	 * Creates a new builder for sequence diagrams.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null;
	 */
	public SequenceDiagramBuilder( Diagram pDiagram )
	{
		super( pDiagram );
		assert pDiagram.getType() == DiagramType.SEQUENCE;
	}
	
	@Override
	protected ConstraintSet getEdgeConstraints()
	{

		return CONSTRAINTS;
	}
	
	/**
	 * Check if the start node is either a CallNode or ImplicitParameterNode, and the end node is an ImplicitParameterNode
	 * with no child nodes. The end point of the edge should land on the top rectangle of the end Node.
	 * @param pStart the start position of the mouse.
	 * @param pEnd the end position of the mouse.
	 * @return True if the start node and the end node of the edge satisfy the conditions to create the constructor call.
	 * @pre pStart!= null && pEnd != null
	 */
	public boolean canCreateConstructorCall(Point pStart, Point pEnd)
	{
		assert pStart!= null && pEnd != null;
		Optional<Node> end = aDiagramRenderer.nodeAt(pEnd);
		Optional<Node> start = aDiagramRenderer.nodeAt(pStart);
		if(start.isPresent() && end.isPresent())
		{
			Node startNode = start.get();
			Node endNode = end.get();
			return 	SequenceDiagramEdgeConstraints.canCreateConstructor(startNode, endNode, pEnd, renderer());
		}
		return false;
	}
	
	@Override
	protected List<DiagramElement> getCoRemovals(DiagramElement pElement)
	{
		List<DiagramElement> result = super.getCoRemovals(pElement);
		if(pElement instanceof Node)
		{
			result.addAll(getNodeUpstreams((Node)pElement));
			result.addAll(getNodeDownStreams((Node)pElement));
		}
		else if(pElement instanceof Edge)
		{
			Optional<DiagramElement> edgeStart = ((SequenceDiagramRenderer)renderer()).getStartNodeIfExclusive((Edge)pElement);
			if(edgeStart.isPresent())
			{
				result.add(edgeStart.get());
			}
			result.addAll(getEdgeDownStreams((Edge)pElement));
		}	
		result.addAll(getCorrespondingReturnEdges(result));
		//Implicit parameter nodes downstream of constructor calls should not be removed
		if (pElement instanceof ConstructorEdge) 
		{
			result.removeIf(element -> element instanceof ImplicitParameterNode);
		}
		return result;
	}
	
	@Override
	public boolean canAdd(Node pNode, Point pRequestedPosition)
	{
		boolean result = true;
		if(pNode instanceof CallNode && insideTargetArea(pRequestedPosition) == null)
		{
			result = false;
		}
		return result;
	}
	
	@Override
	protected void completeEdgeAdditionOperation( CompoundOperation pOperation, Edge pEdge, Node pStartNode, Node pEndNode,
			Point pStartPoint, Point pEndPoint)
	{
		if( !(pEdge instanceof CallEdge) )
		{
			super.completeEdgeAdditionOperation(pOperation, pEdge, pStartNode, pEndNode, pStartPoint, pEndPoint);
			return;
		}
		Node start = pStartNode;
		if( start.getClass() == ImplicitParameterNode.class )
		{
			CallNode newCallNode = new CallNode();
			ImplicitParameterNode parent = (ImplicitParameterNode) pStartNode;
			pOperation.add(new SimpleOperation(() -> 
			{
				newCallNode.attach(aDiagramRenderer.diagram());
				parent.addChild(newCallNode);
			}, () -> 
			{
				newCallNode.detach();
				parent.removeChild(newCallNode);
			}));
			start = newCallNode;
		}
		ImplicitParameterNode endParent = null;
		if( pEndNode.getClass() == ImplicitParameterNode.class )
		{
			endParent = (ImplicitParameterNode) pEndNode;
		}
		else
		{
			assert pEndNode.getClass() == CallNode.class;
			endParent = (ImplicitParameterNode)((CallNode)pEndNode).getParent();
		}
		CallNode end = new CallNode();
		final ImplicitParameterNode parent = endParent;
		pOperation.add(new SimpleOperation(()-> 
		{
			end.attach(aDiagramRenderer.diagram());
			parent.addChild(end);
		},
		()-> 
		{
			end.detach();
			parent.removeChild(end);
		}
		));
		int insertionIndex = computeInsertionIndex(start, pStartPoint.getY());
		
		// CSOFF: Needed for assigning to the final variable
		final Edge edge = canCreateConstructorCall(pStartPoint, pEndPoint)?new ConstructorEdge():pEdge; // CSON:
		edge.connect(start, end, aDiagramRenderer.diagram());
		pOperation.add(new SimpleOperation(()-> aDiagramRenderer.diagram().addEdge(insertionIndex, edge),
				()-> aDiagramRenderer.diagram().removeEdge(edge)));
	}
	
	private int computeInsertionIndex( Node pCaller, int pY)
	{
		for( CallEdge callee : getCalls(pCaller))
		{
			if( renderer().getConnectionPoints(callee).getY1() > pY )
			{
				return aDiagramRenderer.diagram().indexOf(callee);
			}
		}
		return aDiagramRenderer.diagram().edges().size();
	}
	
	@Override
	public DiagramOperation createAddNodeOperation(Node pNode, Point pRequestedPosition)
	{
		DiagramOperation result = null;
		if(pNode instanceof CallNode) 
		{
			Optional<ImplicitParameterNode> target = insideTargetArea(pRequestedPosition);
			if( target.isPresent() )
			{
				result = new SimpleOperation(()-> 
				{ 
					pNode.attach(aDiagramRenderer.diagram());
					target.get().addChild(pNode); 
				},
				()-> 
				{
					pNode.detach();
					target.get().removeChild(pNode);
				});
			}
		}
		if( result == null )
		{
			result = super.createAddNodeOperation(pNode, pRequestedPosition);
		}
		return result;
	}
	
	/*
	 * If pPoint is inside an ImplicitParameterNode but below its top
	 * rectangle, returns the Optional value of that node. Otherwise, returns Optional.empty().
	 */
	private Optional<ImplicitParameterNode> insideTargetArea(Point pPoint)
	{
		for( Node node : aDiagramRenderer.diagram().rootNodes() )
		{
			if( node instanceof ImplicitParameterNode && aDiagramRenderer.contains(node, pPoint) )
			{
				if( !(pPoint.getY() < implicitParameterRenderer().getTopRectangle(node).getMaxY() + CALL_NODE_YGAP) )
				{
					return Optional.of( (ImplicitParameterNode)node );
				}
			}
		}
		return Optional.empty();
	}
	
	private ImplicitParameterNodeRenderer implicitParameterRenderer()
	{
		return (ImplicitParameterNodeRenderer)aDiagramRenderer.rendererFor(ImplicitParameterNode.class);
	}
	
	/**
	 * @param pElements The DiagramElements to obtain the corresponding ReturnEdges for.
	 * @return The Collection of corresponding ReturnEdges for pElements.
	 */
	private Collection<DiagramElement> getCorrespondingReturnEdges(List<DiagramElement> pElements)
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
		return renderer().diagram().edges().stream()
			.filter(ReturnEdge.class::isInstance)
			.filter(edge -> edge.getStart() == pEdge.getEnd())
			.filter(edge -> edge.getEnd() == pEdge.getStart())
			.findFirst();
	}
	
	/**
	 * @param pNode The node to check.
	 * @return True if pNode is a CallNode and is at the end of a ConstructorEdge.
	 */
	private boolean isConstructorExecution(Node pNode)
	{
		assert pNode != null;
		if( pNode.getClass() != CallNode.class )
		{
			return false;
		}
		for( Edge edge : diagram().edges() )
		{
			if ( edge.getEnd() == pNode && edge.getClass() == ConstructorEdge.class )
			{
				return true;
			}
		}
		return false;	
	}
	
	/**
	 * @param pNode The Node to obtain the caller and upstream DiagramElements for.
	 * @return The Collection of DiagramElements in the upstream of pNode.
	 *     Excludes pNode and elements returned by getCoRemovals method in the DiagramBuilder class.
	 * @pre pNode != null
	 */
	private Collection<DiagramElement> getNodeUpstreams(Node pNode)
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
					if( onlyCallsToASingleImplicitParameterNode(callerNode, pNode.getParent()) )
					{
						elements.add(callerNode);
					}
				}
			}
		}
		else if( pNode.getClass() == ImplicitParameterNode.class && pNode.getChildren().size() > 0 )
		{
			Optional<CallNode> caller = getCaller(firstChildOf(pNode));
			if( caller.isPresent() && getCaller(caller.get()).isEmpty() && onlyCallsToASingleImplicitParameterNode(caller.get(), pNode) )
			{
				elements.add(caller.get());
			}
		}
		return elements;
	}
	
	private Optional<CallNode> getCaller(Node pNode)
	{
		return ((SequenceDiagramRenderer)renderer()).getCaller(pNode);
	}
	
	private boolean onlyCallsToASingleImplicitParameterNode(Node pCaller, Node pParentNode)
	{
		assert pCaller!= null && pParentNode != null;
		return getCalls(pCaller).stream().allMatch(edge -> edge.getEnd().getParent() == pParentNode);
	}
	
	private static Node firstChildOf(Node pNode)
	{
		assert pNode.getChildren().size() > 0;
		return pNode.getChildren().get(0);
	}
	
	/**
	 * @param pCaller The caller node.
	 * @return The list of call edges starting at pCaller
	 * @pre pCaller != null
	 */
	private List<CallEdge> getCalls(Node pCaller)
	{
		assert pCaller != null;
		return diagram().edges().stream()
				.filter(CallEdge.class::isInstance)
				.map(CallEdge.class::cast)
				.filter(edge -> edge.getStart() == pCaller)
				.collect(toList());
	}
	
	// Migrated from ControlFlow
	
	/*
	 * Returns true if pNode is the ImplicitParameterNode that gets created in constructor call
	 */
	private boolean isConstructedObject(Node pNode)
	{
		assert pNode != null;
		return pNode.getClass() == ImplicitParameterNode.class && pNode.getChildren().size() > 0 &&
				isConstructorExecution(firstChildOf(pNode));
	}
	
	private Optional<Edge> getConstructorEdge(Node pNode)
	{
		assert pNode != null && isConstructorExecution(pNode);
		if( pNode.getClass() != CallNode.class )
		{
			return Optional.empty();	
		}
		for( Edge edge : diagram().edges() )
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
				for( Edge edge: diagram().edges() )
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
