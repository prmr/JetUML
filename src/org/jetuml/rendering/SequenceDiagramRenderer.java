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

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.StringRenderer.Alignment;
import org.jetuml.rendering.StringRenderer.TextDecoration;
import org.jetuml.rendering.edges.CallEdgeRenderer;
import org.jetuml.rendering.edges.ReturnEdgeRenderer;
import org.jetuml.rendering.nodes.CallNodeRenderer;
import org.jetuml.rendering.nodes.ImplicitParameterNodeRenderer;

import javafx.scene.canvas.GraphicsContext;

/**
 * The renderer for sequence diagrams. The implementation of this renderer assumes
 * that any sequence diagram has either zero call nodes or a single root call node, namely, 
 * only one call node with no callers.
 */
public final class SequenceDiagramRenderer extends AbstractDiagramRenderer
{ 
	/* Initial position of the lifeline of an object if it is not the target of a constructor call.  */
	private static final int INITIAL_Y_POSITION = 80;
	
	/* Minimum number of pixels to drop a new call edge from the current position in the call sequence.
	 * Should then be adjusted based on font size. See method getDropDistance() */
	private static final int DROP_MIN = 20;
	
	/* Number of pixels to drop a constructor call node from the current position in the call sequence. 
	 * Independent of font size. */
	private static final int DROP_CONSTRUCTOR = 85;
	
	/* Number of pixels to add to a call node below the bottom of its last callee. */
	private static final int BOTTOM_PADDING = 20;
	
	/* Height, in number of pixels, of a call node without any callees. */
	private static final int LEAF_NODE_HEIGHT = 30;
	
	/* Constants to test the height of the font. */
	private static final String TEST_STRING = "|";
	private static final StringRenderer NODE_GAP_TESTER = StringRenderer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED);

	private final Map<Node, Integer> aCallNodeTopCoordinate = new IdentityHashMap<>();
	private final Map<Node, Integer> aCallNodeBottomCoordinate = new IdentityHashMap<>();
	
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
	
	/*
	 * Computes the position of the nodes in the sequence diagram, except the note nodes.
	 */
	private void layout()
	{
		computeYPositions();
	}
	
	/**
	 * @return true if no computations of nodes are found. This could be because the 
	 * diagram is empty, but also because it has been loaded from disk and before a rendering pass 
	 * has been done.
	 */
	private boolean noComputedPositionFound()
	{
		return aCallNodeTopCoordinate.isEmpty();
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
	 * @param pNode The implicit paramter node to check.
	 * @param pPoint The point to check.
	 * @return True if the top rectangle of pNode contains pPoint
	 */
	public boolean topRectangleContains(Node pNode, Point pPoint)
	{
		assert pNode != null && pNode instanceof ImplicitParameterNode;
		assert pPoint != null;
		return ((ImplicitParameterNodeRenderer)rendererFor(pNode.getClass())).
				getTopRectangle(pNode).contains(pPoint);
	}
	
	/*
	 * Returns the caller of a node, if it exists.
	 * 
	 * @param pNode The node to obtain the caller for.
	 * @return The CallNode that has a outgoing edge terminated
	 *     at pNode, if there is one.
	 * @pre pNode != null && contains(pNode)
	 */
	public Optional<CallNode> getCaller(Node pNode)
	{
		assert pNode != null && diagram().contains(pNode);
		return diagram().edges().stream()
			.filter(CallEdge.class::isInstance)
			.filter(edge -> edge.getEnd() == pNode)
			.map(Edge::getStart)
			.map(CallNode.class::cast)
			.findFirst();
	}
	
	/**
	 * @return The number of call nodes active on the same parent when
	 * this call nodes gets executed. An initial (non-nested) call 
	 * node on an implicit parameter get result 0. Nesting is generated
	 * not only by self-calls, but also by any call back to the 
	 * parent object.
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
	
	// This method assumes a single root nodes, and traces all call nodes
	// from this root node. This creates a problem when pasting implicit parameter
	// nodes that contain a dangling call node.
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
		aCallNodeTopCoordinate.put(root.get(), currentYPosition);
		for( Node callee : getCallees(root.get()))
		{
			currentYPosition = computeYPosition(callee, currentYPosition);
		}
		aCallNodeBottomCoordinate.put(root.get(), currentYPosition + BOTTOM_PADDING);
	}
	
	/**
	 * @param pNode A callnode in this sequence diagram
	 * @return The Y-coordinate of the top of the node.
	 */
	public int getY(Node pNode)
	{
		assert pNode != null;
		assert aCallNodeTopCoordinate.containsKey(pNode);
		return aCallNodeTopCoordinate.get(pNode);
	}
	
	/**
	 * @param pNode A callnode in this sequence diagram
	 * @return The Y-coordinate of the bottom of the node.
	 */
	public int getMaxY(Node pNode)
	{
		assert pNode != null;
		assert aCallNodeBottomCoordinate.containsKey(pNode);
		return aCallNodeBottomCoordinate.get(pNode);
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
		}
		else
		{
			currentPosition += getDropDistance();
		}
		aCallNodeTopCoordinate.put(pNode, currentPosition);
		List<Node> callees = getCallees(pNode);
		if( callees.isEmpty() )
		{
			currentPosition += LEAF_NODE_HEIGHT;
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
		return getIncomingCall(pNode)
					.filter(edge -> edge instanceof ConstructorEdge) // Exists and is a constructor edge
					.isPresent();
	}
	
	private Optional<Edge> getIncomingCall(Node pNode)
	{
		assert pNode.getClass() == CallNode.class;
		return diagram().edges().stream()
			.filter(edge -> edge.getEnd() == pNode)
			.findFirst();
	}
	
	/**
	 * @param pNode
	 * @return The y-coordinate of the top of this node's lifeline
	 */
	public int getLifelineTop(ImplicitParameterNode pNode)
	{
		// 20 if it's a normal call, 5 if a constructor call
		Optional<Node> topNode = diagram().allNodes().stream()
			.filter(CallNode.class::isInstance)
			.filter(node -> node.getParent() == pNode)
			.sorted(Comparator.comparing(aCallNodeTopCoordinate::get))
			.findFirst();
		if(topNode.isEmpty() || !isConstructorCall(topNode.get()))
		{
			return INITIAL_Y_POSITION - DROP_MIN;
		}
		else
		{
			return aCallNodeTopCoordinate.get(topNode.get()) - 5;
		}
	}
	
	/*
	 * The root of the call sequence is the call node without a callee
	 */
	private Optional<Node> findRoot()
	{
		Set<Node> calledNodes = diagram().edges().stream()
				.filter(edge -> CallEdge.class.isAssignableFrom(edge.getClass())) // Includes subclasses, such as constructor edges
				.map(Edge::getEnd)
				.collect(Collectors.toSet());
		List<Node> rootNode = diagram().allNodes().stream()
				.filter(node -> node.getClass() == CallNode.class)
				.filter(node -> !calledNodes.contains(node))
				.collect(Collectors.toList());
		assert rootNode.size() <= 1; 
		Optional<Node> result = Optional.empty();
		if( !rootNode.isEmpty() )
		{
			result = Optional.of(rootNode.get(0));
		}
		return result;
	}
	
	public int getCenterXCoordinate(ImplicitParameterNode pNode)
	{
		return ((ImplicitParameterNodeRenderer)rendererFor(ImplicitParameterNode.class)).getCenterXCoordinate(pNode);
	}
	
	@Override
	protected Optional<Node> deepFindNode(Node pNode, Point pPoint)
	{
		Optional<Node> result = Optional.empty();
		if( pNode.getClass() == CallNode.class )
		{
			result = getCallees(pNode).stream()
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
	
	/*
	 * @return The number of pixels to drop the call edges from the current position in the call sequence.
	 * Takes into account the size of the font to ensure labels on call edges do not overlap.
	 * 
	 */
	private int getDropDistance()
	{
		int shift = NODE_GAP_TESTER.getDimension(TEST_STRING).height() / 3;
		// Only apply shift if necessary
		if ( shift < 10 )
		{
			return DROP_MIN;
		}
		else
		{
			return DROP_MIN + 10;
		}
	}
	
	/**
	 * @return True if there is at least one call node in the diagram.
	 */
	public boolean hasEntryPoint()
	{
		return diagram().rootNodes().stream().anyMatch(SequenceDiagramRenderer::hasCallNode);
	}
	
	private static boolean hasCallNode(Node pNode)
	{
		return pNode.getClass() == ImplicitParameterNode.class &&
				!pNode.getChildren().isEmpty();
	}
	
	/**
	 * Returns the start node for pEdge if it is exclusive
	 * to pEdge, namely if it only has pEdge as an outgoing 
	 * call edge.
	 * 
	 * @param pEdge The Edge to obtain the edge start for.
	 * @return The Optional value of the start Node for pEdge.
	 * @pre pEdge != null
	 */
	public Optional<DiagramElement> getStartNodeIfExclusive(Edge pEdge)
	{
		assert pEdge != null;
		if( startNodeOnlyCalls(pEdge) )
		{
			return Optional.of(pEdge.getStart());
		}
		else if( pEdge.getClass() == ConstructorEdge.class )
		{
			// We delete the start node of pEdge if it does not have any caller and only makes calls to the 
			// object being constructed.
			if ( getCaller(pEdge.getStart()).isEmpty() && onlyCallsToASingleImplicitParameterNode(pEdge.getStart(), pEdge.getEnd().getParent()) )
			{
				return Optional.of(pEdge.getStart());
			}
		}
		return Optional.empty();
	}
	
	/* 
	 * returns true iif pEdge's start node only calls pEdge.
	 */
	private boolean startNodeOnlyCalls(Edge pEdge)
	{
		assert pEdge != null && pEdge.getStart() != null;
		List<CallEdge> calls = getCalls(pEdge.getStart());
		return  getCaller(pEdge.getStart()).isEmpty() &&
				calls.size() == 1 &&
				calls.contains(pEdge);
	}
	
	private boolean onlyCallsToASingleImplicitParameterNode(Node pCaller, Node pParentNode)
	{
		assert pCaller!= null && pParentNode != null;
		return getCalls(pCaller).stream().allMatch(edge -> edge.getEnd().getParent() == pParentNode);
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
}
