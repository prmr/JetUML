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
 ******************************************************************************/
package org.jetuml.rendering;

import static java.util.stream.Collectors.toList;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetuml.diagram.ControlFlow;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.edges.CallEdgeRenderer;
import org.jetuml.rendering.edges.ReturnEdgeRenderer;
import org.jetuml.rendering.nodes.CallNodeRenderer;
import org.jetuml.rendering.nodes.ImplicitParameterNodeRenderer;

import javafx.scene.canvas.GraphicsContext;

/**
 * The renderer for sequence diagrams.
 */
public final class SequenceDiagramRenderer extends AbstractDiagramRenderer
{
	private static final int INITIAL_Y_POSITION = 80;
	private static final int DROP_NORMAL = 20;
	private static final int DROP_CONSTRUCTOR = 85;
	private static final int DROP_LIFELINE = 80;
	private static final int BOTTOM_PADDING = 20;
	private static final int LEAF_NODE_LENGTH = 30;
	private static final int NESTING_SHIFT_DISTANCE = 10;

	private final Map<Node, Integer> aLifelineXPositions = new IdentityHashMap<>();
	private final Map<Node, Integer> aLifelineYPositions = new IdentityHashMap<>();
	private final Map<Node, Integer> aCallNodeTopCoordinate = new IdentityHashMap<>();
	private final Map<Node, Integer> aCallNodeBottomCoordinate = new IdentityHashMap<>();
	private final Map<Node, Integer> aCallNodeXCoordinate = new IdentityHashMap<>();
	
	public SequenceDiagramRenderer(Diagram pDiagram)
	{
		super(pDiagram);
		addElementRenderer(CallNode.class, new CallNodeRenderer(this));
		addElementRenderer(ImplicitParameterNode.class, new ImplicitParameterNodeRenderer(this));
		addElementRenderer(ReturnEdge.class, new ReturnEdgeRenderer(this));
		addElementRenderer(CallEdge.class, new CallEdgeRenderer(this));
		addElementRenderer(ConstructorEdge.class, new CallEdgeRenderer(this));
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		layout();
		super.draw(pGraphics); 
	}
	
	/**
	 * @return true if no computations of nodes are found. This could be because the 
	 * diagram is empty, but also because it has been loaded from disk and before a rendering pass 
	 * has been done.
	 */
	private boolean noComputedPositionFound()
	{
		return aLifelineXPositions.isEmpty();
	}
	
	@Override
	public final Rectangle getBounds()
	{
		//When getBounds(pDiagram) is called to open an existing class diagram file,
		//the positions have not yet been computed and need to be.
		if(noComputedPositionFound())
		{
			layout();
		}
		return super.getBounds();
	}
	
	/**
	 * Precomputes the position of the nodes.
	 */
	private void layout()
	{
		computeLifelinePositions();
		computeYPositions();
		computeCallNodeXPositions();
	}
	
	private void computeLifelinePositions()
	{
		aLifelineXPositions.clear();
		aLifelineYPositions.clear();
		for( Node node : diagram().rootNodes() )
		{
			if(node.getClass() == ImplicitParameterNode.class)
			{
				aLifelineXPositions.put(node, implicitParameterNodeRenderer().getCenterXCoordinate(node));
				aLifelineYPositions.put(node, 0);
			}
		}
	}
	
	/**
	 * Returns the caller of a node, if it exists.
	 * 
	 * @param pNode The node to obtain the caller for.
	 * @return The CallNode that has a outgoing edge terminated
	 *     at pNode, if there is one.
	 * @pre pNode != null && contains(pNode)
	 */
	private Optional<CallNode> getCaller(Node pNode)
	{
		assert pNode != null && diagram().contains(pNode);
		return diagram().edges().stream()
			.filter(CallEdge.class::isInstance)
			.filter(edge -> edge.getEnd() == pNode)
			.map(Edge::getStart)
			.map(CallNode.class::cast)
			.findFirst();
	}
	
	private int getNestingDepth(CallNode pNode)
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
	
	private void computeYPositions()
	{
		aCallNodeTopCoordinate.clear();
		aCallNodeBottomCoordinate.clear();
		Optional<Node> root = findRoot();
		if( root.isEmpty() )
		{
			return; // Empty call graph, normal case when creating a new diagram
		}
		int currentYPosition = INITIAL_Y_POSITION;
		// Position root node
		aLifelineYPositions.put(root.get().getParent(), 0);
		aCallNodeTopCoordinate.put(root.get(), currentYPosition);
		for( Node callee : getCallees(root.get()))
		{
			currentYPosition = computeYPosition(callee, currentYPosition);
		}
		aCallNodeBottomCoordinate.put(root.get(), currentYPosition + BOTTOM_PADDING);
	}
	
	private void computeCallNodeXPositions()
	{
		aCallNodeXCoordinate.clear();
		for( Node node : diagram().allNodes() )
		{
			if( node instanceof CallNode )
			{
				final int nestingDepth = getNestingDepth((CallNode)node);
				final int lifelineXCoordinate = aLifelineXPositions.get(node.getParent());
				aCallNodeXCoordinate.put(node, lifelineXCoordinate - NESTING_SHIFT_DISTANCE + (NESTING_SHIFT_DISTANCE * nestingDepth));
			}
		}
	}
	
	/*
	 * Computes the y position of the pNode call node, and all its callees,
	 * through recursive descent. Also adjust the parent in case it's a constructor call.
	 */
	private int computeYPosition(Node pNode, int pCurrentPosition)
	{
		int currentPosition = pCurrentPosition;
		// If this is a constructor call, also adjust the parent.
		if( isConstructorCall(pNode) )
		{
			currentPosition += DROP_CONSTRUCTOR;
			aLifelineYPositions.put(pNode.getParent(), pCurrentPosition + DROP_LIFELINE);
		}
		else
		{
			currentPosition += DROP_NORMAL;
		}
		aCallNodeTopCoordinate.put(pNode, currentPosition);
		List<Node> callees = getCallees(pNode);
		if( callees.isEmpty() )
		{
			currentPosition += LEAF_NODE_LENGTH;
		}
		else
		{
			for( Node callee : callees)
			{
				currentPosition = computeYPosition(callee, currentPosition);
			}
			currentPosition += BOTTOM_PADDING;
		} 
		aCallNodeBottomCoordinate.put(pNode, currentPosition);
		return currentPosition;
	}
	
	private boolean isConstructorCall(Node pNode)
	{
		assert pNode.getClass() == CallNode.class;
		return getIncomingCall(pNode) instanceof ConstructorEdge;
	}
	
	private CallEdge getIncomingCall(Node pNode)
	{
		assert pNode.getClass() == CallNode.class;
		return (CallEdge) diagram().edges().stream()
			.filter(edge -> edge.getEnd() == pNode)
			.findFirst()
			.get();
	}
	
	
	
	/*
	 * The root of the call sequence is the call node without a callee
	 */
	private Optional<Node> findRoot()
	{
		Set<Node> calledNodes = diagram().edges().stream()
				.filter(edge -> edge.getClass().isAssignableFrom(CallEdge.class)) // Includes subclasses, such as constructor edges
				.map(Edge::getEnd)
				.collect(Collectors.toSet());
		return diagram().allNodes().stream()
			.filter(node -> node.getClass() == CallNode.class)
			.filter(node -> !calledNodes.contains(node))
			.findFirst();
	}
	
	private ImplicitParameterNodeRenderer implicitParameterNodeRenderer()
	{
		return (ImplicitParameterNodeRenderer)rendererFor(ImplicitParameterNode.class);
	}
	
	@Override
	protected Optional<Node> deepFindNode(Node pNode, Point pPoint)
	{
		Optional<Node> result = Optional.empty();
		if( pNode.getClass() == CallNode.class )
		{
			result = new ControlFlow(diagram()).getCallees(pNode).stream()
				.map(node -> deepFindNode(node, pPoint))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
		}
		return result.or(() -> super.deepFindNode(pNode, pPoint));
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
	private List<Node> getCallees(Node pNode)
	{
		assert pNode != null && diagram().contains(pNode);
		return diagram().edges().stream()
				.filter(CallEdge.class::isInstance)
				.filter(edge -> edge.getStart() == pNode)
				.map(Edge::getEnd)
				.collect(toList());
	}
	
	/*
	 * This specialized version supports selecting implicit parameter nodes only by 
	 * selecting their top rectangle.
	 */
	@Override
	public Optional<Node> selectableNodeAt(Point pPoint)
	{
		Optional<Node> topRectangleSelected = diagram().rootNodes().stream()
			.filter(node -> node.getClass() == ImplicitParameterNode.class)
			.filter(node -> ((ImplicitParameterNodeRenderer)rendererFor(ImplicitParameterNode.class)).getTopRectangle(node).contains(pPoint))
			.findFirst();
		return topRectangleSelected.or(() -> super.selectableNodeAt(pPoint));				
	}
}
