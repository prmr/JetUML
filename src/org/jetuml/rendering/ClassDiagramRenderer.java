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
import static org.jetuml.rendering.EdgePriority.priorityOf;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.edges.ThreeLabelEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.edges.EdgeStorage;
import org.jetuml.rendering.edges.NodeIndex;
import org.jetuml.rendering.edges.StoredEdgeRenderer;
import org.jetuml.rendering.nodes.InterfaceNodeRenderer;
import org.jetuml.rendering.nodes.NodeRenderer;
import org.jetuml.rendering.nodes.PackageDescriptionNodeRenderer;
import org.jetuml.rendering.nodes.PackageNodeRenderer;
import org.jetuml.rendering.nodes.TypeNodeRenderer;

import javafx.scene.canvas.GraphicsContext;

/**
 * The renderer for class diagrams.
 */
public final class ClassDiagramRenderer extends AbstractDiagramRenderer
{
	private static final int TWENTY_PIXELS = 20;
	private static final int TEN_PIXELS = 10;
	
	private final EdgeStorage aEdgeStorage = new EdgeStorage();
	
	/**
	 * @param pDiagram The diagram being rendered.
	 */
	public ClassDiagramRenderer(Diagram pDiagram)
	{
		super(pDiagram);
		addElementRenderer(ClassNode.class, new TypeNodeRenderer(this));
		addElementRenderer(InterfaceNode.class, new InterfaceNodeRenderer(this));
		addElementRenderer(PackageNode.class, new PackageNodeRenderer(this));
		addElementRenderer(PackageDescriptionNode.class, new PackageDescriptionNodeRenderer(this));
		
		StoredEdgeRenderer storedEdgeViewer = new StoredEdgeRenderer(this);
		addElementRenderer(DependencyEdge.class, storedEdgeViewer);
		addElementRenderer(AssociationEdge.class, storedEdgeViewer);
		addElementRenderer(DependencyEdge.class, storedEdgeViewer);
		addElementRenderer(GeneralizationEdge.class, storedEdgeViewer);
		addElementRenderer(AggregationEdge.class, storedEdgeViewer);
	}

	/**
	 * Draws pDiagram onto pGraphics.
	 * 
	 * @param pGraphics the graphics context where the diagram should be drawn.
	 * @param pDiagram the diagram to draw.
	 * @pre pDiagram != null && pGraphics != null.
	 */
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		//draw and store nodes 
		activateNodeStorages();
		diagram().rootNodes().forEach(node -> drawNode(node, pGraphics));
		
		//plan edge paths using Layouter
		layout();
		
		//draw edges using plan from EdgeStorage
		diagram().edges().forEach(edge -> draw(edge, pGraphics));
		deactivateAndClearNodeStorages();
	}
	
	@Override
	public Rectangle getBounds()
	{
		// When getBounds(pDiagram) is called to open an existing class diagram
		// file, aEdgeStorage is initially empty and needs to be filled to
		// compute the diagram bounds.
		if( aEdgeStorage.isEmpty() )
		{
			layout();
		}
		return super.getBounds();
	}

	/**
	 * Uses positional information of nodes and stored edges to layout and 
	 * store the EdgePaths of edges in pDiagram.
	 * @param pDiagram the diagram of interest
	 * @pre pDiagram.getType() == DiagramType.CLASS
	 */
	public void layout()
	{
		assert diagram().getType() == DiagramType.CLASS;
		aEdgeStorage.clearStorage();
		layoutSegmentedEdges(EdgePriority.INHERITANCE);	
		layoutSegmentedEdges(EdgePriority.IMPLEMENTATION);
		layoutSegmentedEdges(EdgePriority.AGGREGATION);
		layoutSegmentedEdges(EdgePriority.COMPOSITION);
		layoutSegmentedEdges(EdgePriority.ASSOCIATION);
		layoutDependencyEdges();
		layoutSelfEdges();
	}
	
	/**
	 * Plans the EdgePaths for all segmented edges with EdgePriority 
	 * pEdgePriority.
	 * @param pEdgePriority the edge priority level 
	 * @pre pDiagram.getType() == DiagramType.CLASS
	 * @pre EdgePriority.isSegmented(pEdgePriority)
	 */
	private void layoutSegmentedEdges(EdgePriority pEdgePriority)
	{
		assert diagram().getType() == DiagramType.CLASS;
		assert EdgePriority.isSegmented(pEdgePriority);
		List<Edge> edgesToProcess = diagram().edges().stream()
				.filter(edge -> priorityOf(edge) == pEdgePriority)
				.sorted(Comparator.comparing(edge -> edge.start().position().x()))
				.collect(toList());
				
		while( !edgesToProcess.isEmpty() )
		{
			Edge currentEdge = edgesToProcess.get(0);
			Side edgeDirection = attachedSide(currentEdge, currentEdge.start());
			//Get all the edges which will merge with the start or end of currentEdge
			List<Edge> edgesToMergeStart = getEdgesToMergeStart(currentEdge, edgesToProcess);
			List<Edge> edgesToMergeEnd = getEdgesToMergeEnd(currentEdge, edgesToProcess);	
			//Determine if currendEdge should merge with other edges at its start node or end node
			if( !edgesToMergeStart.isEmpty() )
			{ 	
				edgesToMergeStart.add(currentEdge);
				edgesToProcess.removeAll(edgesToMergeStart);
				storeMergedStartEdges(edgeDirection, edgesToMergeStart);
			}
			else
			{
				edgesToMergeEnd.add(currentEdge);
				edgesToProcess.removeAll(edgesToMergeEnd);
				storeMergedEndEdges(edgeDirection, edgesToMergeEnd);
			}
		}
	}
	
	
	/**
	 * Plans the EdgePaths for Dependency Edges.
	 */
	private void layoutDependencyEdges()
	{
		assert diagram().getType() == DiagramType.CLASS;
		for(Edge edge : diagram().edges())
		{
			if(priorityOf(edge)==EdgePriority.DEPENDENCY)
			{   //Determine the start and end connection points
				Side attachedEndSide = attachedSide(edge, edge.end());
				Point startPoint = getConnectionPoint(edge.start(), edge, attachedEndSide.mirrored());
				Point endPoint = getConnectionPoint(edge.end(), edge, attachedEndSide);
				//Store an EdgePath from startPoint to endPoint
				aEdgeStorage.store(edge, new EdgePath(startPoint, endPoint));
			}
		}	
	}
	
	/**
	 * Plans the EdgePaths for self-edges in pDiagram.
	 */
	private void layoutSelfEdges()
	{
		List<Edge> selfEdges = diagram().edges().stream()
			.filter(edge -> priorityOf(edge) == EdgePriority.SELF_EDGE)
			.collect(toList());
		for(Edge edge : selfEdges)
		{
			//Determine the corner where the self-edge should be placed
			NodeCorner corner = getSelfEdgeCorner(edge);
			//Build a self-edge EdgePath at the corner and store it
			EdgePath path = buildSelfEdge(edge, corner);
			aEdgeStorage.store(edge, path);
		}
	}
	
	/**
	 * Gets the node corner where the self-edge pEdge should be placed. If no corners are available, returns TOP_RIGHT. 
	 * @param pEdge the self-edge of interest
	 * @return the first available corner on the node, starting with TOP_RIGHT and moving counter-clockwise.
	 * @pre priorityOf(pEdge) == EdgePriority.SELF_EDGE
	 */
	private NodeCorner getSelfEdgeCorner(Edge pEdge)
	{
		assert priorityOf(pEdge) == EdgePriority.SELF_EDGE;
		for(NodeCorner corner : NodeCorner.values())
		{	//Get a 2D array of [startPoint, endPoint] for a self edge at the corner
			Point[] points = toPoints(corner, pEdge.end());
			//Return the first corner with available start and end points
			if(aEdgeStorage.connectionPointIsAvailable(points[0]) && 
					aEdgeStorage.connectionPointIsAvailable(points[1]))
			{
				return corner;
			}
		}
		//if no corners are available, the top right corner will be used.
		return NodeCorner.TOP_RIGHT;
	}
	
	/**
	 * Builds an EdgePath for a self-edge pEdge, on the pCorner of pEdge's node.
	 * @param pEdge the edge of interest
	 * @param pCorner the corner of a node where the self edge should be
	 * @return the EdgePath for pEdge, beginning on the North or South side of a node, ending on the East or West side of the node. 
	 * @pre EdgePriority.priorityOf(pEdge) == EdgePriority.SELF_EDGE
	 * @pre pCorner != null
	 */
	private EdgePath buildSelfEdge(Edge pEdge, NodeCorner pCorner)
	{
		assert priorityOf(pEdge) == EdgePriority.SELF_EDGE;
		assert pCorner != null;
		Point[] connectionPoints = toPoints(pCorner, pEdge.end());
		Point startPoint = connectionPoints[0];
		Point endPoint = connectionPoints[1];
		Point firstBend;
		Point middleBend;
		Point lastBend;
		//determine location of first bend: either 20px above or 20px below the start point
		if(NodeCorner.horizontalSide(pCorner) == Direction.NORTH)
		{
			firstBend = new Point(startPoint.x(), startPoint.y() - (2 * TEN_PIXELS)); 
		}
		else
		{
			firstBend = new Point(startPoint.x(), startPoint.y() + (2 * TEN_PIXELS)); 
		}
		//determine location of middle and last bends
		if(NodeCorner.verticalSide(pCorner) == Direction.EAST)
		{
			middleBend = new Point(firstBend.x() + (4 * TEN_PIXELS), firstBend.y());
			lastBend = new Point(endPoint.x() + (2 * TEN_PIXELS), endPoint.y());
		}
		else
		{
			middleBend = new Point(firstBend.x() - (4 * TEN_PIXELS), firstBend.y());
			lastBend = new Point(endPoint.x() - (2 * TEN_PIXELS), endPoint.y());
		}
		return new EdgePath(startPoint, firstBend, middleBend, lastBend, endPoint);
	}

	
	/**
	 * Builds and stores the EdgePaths for edges in pEdgesToMergeEnd, so they merge at a common end point.
	 * @param pDirection the trajectory of the edges in pEdgesToMergeEnd (the direction of the first segment of the edges)
	 * @param pEdgesToMergeEnd a list of edges whose ends should be merged
	 * @param pDiagram the diagram
	 * @pre pEdgesToMergeEnd.size() > 0
	 * @pre pDiagram.getType() == DiagramType.CLASS
	 */
	private void storeMergedEndEdges(Side pDirection, List<Edge> pEdgesToMergeEnd)
	{
		assert pEdgesToMergeEnd.size() > 0;
		//Merged edges will share a common end point
		Point sharedEndPoint = getConnectionPoint(pEdgesToMergeEnd.get(0).end(), pEdgesToMergeEnd.get(0), pDirection.mirrored());
		//get the individual start points for each edge
		Map<Edge, Point> startPoints = new HashMap<>();
		for(Edge e : pEdgesToMergeEnd)
		{
			startPoints.put(e, getConnectionPoint(e.start(), e, pDirection));
		}
		//Determine the position of the shared middle segment
		Point closestStartPoint = getClosestPoint(startPoints.values(), pDirection);
		int midLineCoordinate;
		if(pDirection.isHorizontal())
		{
			midLineCoordinate = getHorizontalMidLine(closestStartPoint, sharedEndPoint, pDirection, pEdgesToMergeEnd.get(0));
		}
		else
		{
			midLineCoordinate = getVerticalMidLine(closestStartPoint, sharedEndPoint, pDirection, pEdgesToMergeEnd.get(0));
		}
		//Build and store each edge's EdgePath
		for(Edge edge : pEdgesToMergeEnd)
		{
			EdgePath path = buildSegmentedEdgePath(pDirection, startPoints.get(edge), midLineCoordinate, sharedEndPoint);
			aEdgeStorage.store(edge, path);
		}
	}
	
	
	/**
	 * Builds and stores the EdgePaths for edges in pEdgesToMergeStart so that they share a common start point.
	 * @param pDirection the trajectory of the edges in pEdgesToMmergeStart (the direction of the first segment of the edges)
	 * @param pEdgesToMergeStart a list of edges which should me merged at their start points
	 * @pre pEdgesToMergeStart.size() > 0
	 */
	private void storeMergedStartEdges(Side pDirection, List<Edge> pEdgesToMergeStart)
	{
		assert pEdgesToMergeStart.size() > 0;
		//Get the shared start point for all pEdgesToMerge
		Point startPoint = getConnectionPoint(pEdgesToMergeStart.get(0).start(), pEdgesToMergeStart.get(0), pDirection);
		//Get the individual end points for each edge
		Map<Edge, Point> endPoints = new HashMap<>();
		for(Edge edge : pEdgesToMergeStart)
		{
			endPoints.put(edge, getConnectionPoint(edge.end(), edge, pDirection.mirrored()));
		}
		//Determine the X or Y coordinate of the middle segment for the edges:
		//The default position of the merged middle segment is halfway between startPoint and closestEndPoint:
		Point closestEndPoint = getClosestPoint(endPoints.values(), pDirection.mirrored());
		int midLineCoordinate;
		if(pDirection.isHorizontal())
		{
			midLineCoordinate = getHorizontalMidLine(closestEndPoint, startPoint, pDirection, 
					pEdgesToMergeStart.get(0));
		}
		else
		{
			midLineCoordinate = getVerticalMidLine(closestEndPoint, startPoint, pDirection, 
					pEdgesToMergeStart.get(0));
		}
		//Build and store each edge's EdgePath
		for(Edge edge : pEdgesToMergeStart)
		{
			EdgePath path = buildSegmentedEdgePath(pDirection, startPoint, midLineCoordinate, endPoints.get(edge));
			aEdgeStorage.store(edge, path);
		}
	}
	
	
	/**
	 * Creates a segmented EdgePath using pStart and pEnd as start and end points (respectively)
	 * and pMidLine as an X or Y coordinate of the middle segment. 
	 * @param pEdgeDirection the direction describing the trajectory of pEdge
	 * @param pStart the start point of pEdge
	 * @param pMidLine an integer representing an X or Y coordinate of the middle segment
	 * @param pEnd the end point of pEdge
	 * @return a EdgePath consisting of pStart, 2 middle points connecting the middle segment, and pEnd
	 * @pre pStart != null && pEnd != null
	 * @pre pMidLine >= 0
	 */
	private static EdgePath buildSegmentedEdgePath(Side pEdgeDirection, Point pStart, int pMidLine, Point pEnd)
	{
		assert pStart != null && pEnd != null;
		assert pMidLine >= 0;
		Point firstMiddlePoint;
		Point secondMiddlePoint;
		if(pEdgeDirection.isHorizontal())
		{
			//Then the mid-point coordinate is a Y-coordinate
			firstMiddlePoint = new Point(pStart.x(), pMidLine);
			secondMiddlePoint = new Point(pEnd.x(), pMidLine);
		}
		else //East or West
		{	//Then the mid-point coordinate is a X-coordinate
			firstMiddlePoint = new Point(pMidLine, pStart.y());
			secondMiddlePoint = new Point(pMidLine, pEnd.y());
		}
		return new EdgePath(pStart, firstMiddlePoint, secondMiddlePoint, pEnd);
	}
	
	
	/**
	 * Gets the edges which should merge to share a common end point with pEdge.
	 * @param pEdge the edge of interest
	 * @param pEdges a list of edges in the diagram
	 * @return a list containing the edges which should merge with pEdge (not including pEdge itself).
	 * @pre pEdge != null
	 * @pre pEdges != null
	 */
	private List<Edge> getEdgesToMergeEnd(Edge pEdge, List<Edge> pEdges)
	{
		assert pEdge != null && pEdges != null;
		return pEdges.stream()
				.filter(edge -> edge.end() == pEdge.end())
				.filter(edge -> priorityOf(edge) == priorityOf(pEdge))
				.filter(edge -> attachedSide(edge, edge.end()) == attachedSide(pEdge, pEdge.end())) 
				.filter(edge -> noConflictingEndLabels(edge, pEdge))
				.filter(edge -> noOtherEdgesBetween(edge, pEdge, pEdge.end()))
				.filter(edge -> !edge.equals(pEdge))
				.collect(toList());		
	}
	
	/**
	 * Gets the edges which should merge to share a common start point with pEdge.
	 * @param pEdge the edge of interest
	 * @param pEdges a list of edges in the diagram
	 * @return a list containing the edges which should merge with pEdge (not including pEdge itself).
	 * @pre pEdge != null
	 * @pre pEdges != null
	 */
	private List<Edge> getEdgesToMergeStart(Edge pEdge, List<Edge> pEdges)
	{
		assert pEdge != null && pEdges != null;
		return pEdges.stream()
			.filter(edge -> edge.start().equals(pEdge.start()))
			.filter(edge -> priorityOf(edge) == priorityOf(pEdge))
			.filter(edge -> attachedSide(edge, edge.start()) == attachedSide(pEdge, pEdge.start()))
			.filter(edge -> noOtherEdgesBetween(edge, pEdge, pEdge.start()))
			.filter(edge -> noConflictingStartLabels(edge, pEdge))
			.filter(edge -> !edge.equals(pEdge))
			.collect(toList());
	}
	
	/**
	 * Gets the y-coordinate of the horizontal middle segment of pEdge.
	 * @param pStart the start point for pEdge
	 * @param pEnd the end point for pEdge
	 * @param pEdgeDirection the trajectory of pEdge, either North or South
	 * @param pEdge the segmented edge of interest
	 * @return an integer representing a y-coordinate of the middle segment of pEdge
	 * @pre pDirection == Direction.NORTH || pDirection == Direction.SOUTH
	 * @pre EdgePriority.isSegmented(pEdge)
	 * @pre pStart != null && pEnd != null
	 */
	private int getHorizontalMidLine(Point pStart, Point pEnd, Side pEdgeDirection, Edge pEdge)
	{
		assert pEdgeDirection.isHorizontal();
		assert EdgePriority.isSegmented(pEdge);
		assert pStart != null && pEnd != null;
		//Check for any edge in storage which is attached to pEdge's start and end nodes
		// "Shared-node edges" require a different layout strategy:
		List<Edge> storedEdgesWithSameNodes = aEdgeStorage.getEdgesWithSameNodes(pEdge);
		if(!storedEdgesWithSameNodes.isEmpty())
		{
			return horizontalMidlineForSharedNodeEdges(storedEdgesWithSameNodes.get(0), pEdge, pEdgeDirection);
		}
		//Otherwise, find the closest edge which conflicts with pEdge
		Optional<Edge> closestStoredEdge = closestConflictingHorizontalSegment(pEdgeDirection, pEdge);
		if(closestStoredEdge.isEmpty())
		{
			//If there are no conflicting segments, return the y-coordinate equidistant between the start and end points
			return pEnd.y() + ((pStart.y() - pEnd.y()) / 2);
		}
		else
		{	//Return a y-coordinate either 10px above or 10px below the horizontal middle segment of closestStoredEdge
			return adjacentHorizontalMidLine(closestStoredEdge.get(), pEdge, pEdgeDirection);	
		}
	}
	
	/**
	 * Gets the x-coordinate of the vertical middle segment of pEdge.
	 * @param pStart the start point of pEdge
	 * @param pEnd the end point of pEdge
	 * @param pEdgeDirection the direction of pEdge (the direction of the first segment of pEdge).
	 * @param pEdge the edge of interest
	 * @return an integer representing the x-coordinate for the vertical middle segment of pEdge.
	 * @pre pEdgeDirection == Direction.EAST || pEdgeDirection == Direction.WEST
	 * @pre EdgePriority.isSegmented(pEdge)
	 * @pre pStart != null && pEnd != null
	 */
	private int getVerticalMidLine(Point pStart, Point pEnd, Side pEdgeDirection, Edge pEdge)
	{
		assert pEdgeDirection.isVertical();
		assert EdgePriority.isSegmented(pEdge);
		assert pStart != null && pEnd != null;
		//Check for any edge in storage which shares the same 2 nodes as pEdge: 
		List<Edge> storedEdgesWithSameNodes = aEdgeStorage.getEdgesWithSameNodes(pEdge);
		if(!storedEdgesWithSameNodes.isEmpty())
		{
			//"Shared-node edges" require a different layout strategy
			return verticalMidlineForSharedNodeEdges(storedEdgesWithSameNodes.get(0), pEdge, pEdgeDirection);
		}
		//Otherwise, find the closest edge which conflicts with pEdge
		Optional<Edge> closestStoredEdge = closestConflictingVerticalSegment(pEdgeDirection, pEdge);
		if(closestStoredEdge.isEmpty())
		{
			//If no stored edges conflict with pEdge then return the x-coordinate in between the start and end points
			return pEnd.x() + ((pStart.x() - pEnd.x()) / 2);
		}
		else
		{	//Return an x-coordinate either 10px to the left or 10px to the right of closestStoredEdge
			return adjacentVerticalMidLine(closestStoredEdge.get(), pEdge, pEdgeDirection);
		}
	}
	
	/**
	 * Gets the y-coordinate of the horizontal middle segment of pNewEdge, so that it avoids overlapping with 
	 * pEdgeWithSameNodes, which is already in storage.
	 * @param pEdgeWithSameNodes an edge in storage which shares the same 2 connected nodes as pNewEdge. 
	 * @param pNewEdge the segmented edge whose middle segment we want
	 * @param pEdgeDirection the direction of pEdge (the direction of the first segment of pEdge). 
	 * @return an integer describing the y-coordinate of the middle segment of pEdge, so that pEdge and pEdgeWithSameNodes do not overlap.
	 * @pre pEdgeWithSameNodes is present in storage
	 * @pre pEdgeWithSameNodes and pEdge both share the same 2 attached nodes.
	 * @pre pEdgeDirection == Direction.NORTH || pEdgeDirection == Direction.SOUTH
	 */
	private int horizontalMidlineForSharedNodeEdges(Edge pEdgeWithSameNodes, Edge pNewEdge, Side pEdgeDirection)
	{
		assert aEdgeStorage.contains(pEdgeWithSameNodes);
		assert pEdgeWithSameNodes.start() == pNewEdge.start() || pEdgeWithSameNodes.start() == pNewEdge.end();
		assert pEdgeWithSameNodes.end() == pNewEdge.start() || pEdgeWithSameNodes.end() == pNewEdge.end();
		assert pEdgeDirection.isHorizontal();
		if(pEdgeDirection == Side.TOP)
		{	
			return getEdgePath(pEdgeWithSameNodes).getPointByIndex(1).y() - TEN_PIXELS;
		}
		else //pEdgeDirection == Direction.SOUTH
		{
			return getEdgePath(pEdgeWithSameNodes).getPointByIndex(1).y() + TEN_PIXELS;
		}
	}
	
	/**
	 * Gets the x-coordinate of the vertical middle segment of pNewEdge, so that it avoids overlapping with 
	 * pEdgeWithSameNodes, which is already in storage.
	 * @param pEdgeWithSameNodes an edge in storage which shares the same 2 connected nodes as pNewEdge. 
	 * @param pNewEdge the edge whose EdgePath we want to plan
	 * @param pEdgeDirection the direction of pEdge (the direction of the first segment of pEdge). 
	 * @return The x-coordinate of the middle segment of pEdge, so that pEdge and pEdgeWithSameNodes do not overlap.
	 * @pre pEdgeWithSameNodes is present in storage
	 * @pre pEdgeWithSameNodes and pEdge both share the same 2 attached nodes.
	 * @pre pEdgeDirection == Direction.WEST || pEdgeDirection == Direction.EAST
	 */
	private int verticalMidlineForSharedNodeEdges(Edge pEdgeWithSameNodes, Edge pNewEdge, Side pEdgeDirection) 
	{
		assert aEdgeStorage.contains(pEdgeWithSameNodes);
		assert pEdgeWithSameNodes.start() == pNewEdge.start() || pEdgeWithSameNodes.start() == pNewEdge.end();
		assert pEdgeWithSameNodes.end() == pNewEdge.start() || pEdgeWithSameNodes.end() == pNewEdge.end();
		assert pEdgeDirection.isVertical();
		if(pEdgeDirection == Side.LEFT)
		{
			return getEdgePath(pEdgeWithSameNodes).getPointByIndex(1).x() - TEN_PIXELS;
		}
		else
		{
			return getEdgePath(pEdgeWithSameNodes).getPointByIndex(1).x() + TEN_PIXELS;
		}
	}
	
	
	/**
	 * Gets the closest Edge to pEdge.getEnd() (or pEdge.getStart() for AggregationEdges) which might interfere with the position 
	 * of the horizontal middle segment of pEdge. Considers all stored, segmented edges which are attached to either of pEdge's nodes and 
	 * could conflict with pEdge. 
	 * @param pEdgeDirection the direction describing the trajectory of pEdge (the direction of the first segment of pEdge)
	 * @param pEdge the edge of interest
	 * @return the closest Edge conflicting with the horizontal middle segment of pEdge, or Optional.empty() if there are no conflicting edges.
	 * @pre pEdgeDirection == Direction.NORTH || pEdgeDirection == Direction.SOUTH
	 */
	private Optional<Edge> closestConflictingHorizontalSegment(Side pEdgeDirection, Edge pEdge)
	{
		assert pEdgeDirection.isHorizontal();
		//Consider all edges connected to pEdge.getEnd() which are in the way of pEdge
		List<Edge> conflictingEdges = storedConflictingEdges(pEdgeDirection.mirrored(), pEdge.end(), pEdge);	
		//also consider edges which are connected to pEdge.getStart() which are in the way of pEdge
		conflictingEdges.addAll(storedConflictingEdges(pEdgeDirection, pEdge.start(), pEdge));
		if(conflictingEdges.isEmpty())
		{
			return Optional.empty();
		}
		else 
		{	//For Aggregation/Composition edges: return the Edge with the middle segment which is closest to pEdge's start node
			if(pEdge instanceof AggregationEdge)
			{
				return conflictingEdges.stream()
						.min(Comparator.comparing(edge -> verticalDistanceToNode(pEdge.start(), edge, pEdgeDirection)));
			}
			else
			{  //For all other segmented edges: return the Edge with the middle segment which is closest to pEdge's end node
				return conflictingEdges.stream()
						.min(Comparator.comparing(edge -> verticalDistanceToNode(pEdge.end(), edge, pEdgeDirection)));
			}
		}
	}
	
	/**
	 * Gets the closest Edge to pEdge.getEnd() (pEdge.getStart() for AggregationEdges) which might interfere with the position of 
	 * the vertical middle segment of pEdge. Considers all segmented edges which are attached to both pEdge.getStart() and pEdge.getEnd(), 
	 * and could conflict with pEdge. 
	 * @param pEdgeDirection the direction describing the trajectory of pEdge (the direction of the first segment of pEdge)
	 * @param pEdge the edge of interest
	 * @return the closest Edge conflicting with the vertical middle segment of pEdge, or Optional.empty() if there are no conflicting edges.
	 * @pre pEdgeDirection == Direction.EAST || pEdgeDirection == Direction.WEST
	 * @pre EdgePriority.isSegmented(pEdge)
	 */
	private Optional<Edge> closestConflictingVerticalSegment(Side pEdgeDirection, Edge pEdge) 
	{
		assert pEdgeDirection.isVertical();
		assert EdgePriority.isSegmented(pEdge);
		//Get all edges connected to pEdge's end node which could conflict with pEdge's middle segment position
		List<Edge> conflictingEdges = storedConflictingEdges(pEdgeDirection.mirrored(), pEdge.end(), pEdge);	
		//Also consider edges connected to pEdge's start node which could conflict with pEdge's middle segment
		conflictingEdges.addAll(storedConflictingEdges(pEdgeDirection, pEdge.start(), pEdge));
		if(conflictingEdges.isEmpty())
		{
			return Optional.empty();
		}
		else 
		{	//for AggregationEdges: return the Edge with the middle segment which is closest to pEdge.getStart()
			if(pEdge instanceof AggregationEdge)
			{
				return conflictingEdges.stream()
						.min(Comparator.comparing(edge -> horizontalDistanceToNode(pEdge.start(), edge, pEdgeDirection)));
			}
			else
			{	//For all other edges: return the Edge with the middle segment which is closest to pEdge.getEnd()
				return conflictingEdges.stream()
						.min(Comparator.comparing(edge -> horizontalDistanceToNode(pEdge.end(), edge, pEdgeDirection)));
			}
		}
	}
	
	
	/**
	 * Gets the y-coordinate for the horizontal middle segment of pEdge based on the position of pClosestStoredEdge. 
	 * @param pClosestStoredEdge the closest edge which conflicts with pEdge. 
	 * @param pEdge the edge of interest
	 * @param pEdgeDirection the direction describing the trajectory of pEdge (the direction of the first segment of pEdge)
	 * @return the y-coordinate for the middle segment of pEdge: either 10px below pClosestStoredEdge or 10px above pClosestStoredEdge 
	 * @pre classDiagramViewerFor(pEdge).storageContains(pClosestStoredEdge)
	 * @pre EdgePriority.isSegmented(pEdge)
	 * @pre pEdgeDirection == Direction.NORTH || pEdgeDirection == Direction.SOUTH
	 */
	private int adjacentHorizontalMidLine(Edge pClosestStoredEdge, Edge pEdge, Side pEdgeDirection)
	{
		assert aEdgeStorage.contains(pClosestStoredEdge);
		assert EdgePriority.isSegmented(pEdge);
		assert pEdgeDirection.isHorizontal();
		Node commonNode = getSharedNode(pClosestStoredEdge, pEdge);
		if(pEdgeDirection == Side.TOP)
		{
			if(isOutgoingEdge(pEdge, commonNode))
			{
				return getEdgePath(pClosestStoredEdge).getPointByIndex(1).y() + TEN_PIXELS;
			}
			else
			{
				return getEdgePath(pClosestStoredEdge).getPointByIndex(1).y() - TEN_PIXELS;
			}
		}
		else //Direction is BOTTOM
		{
			if(isOutgoingEdge(pEdge, commonNode))
			{
				return getEdgePath(pClosestStoredEdge).getPointByIndex(1).y() - TEN_PIXELS;
			}
			else
			{
				return getEdgePath(pClosestStoredEdge).getPointByIndex(1).y() + TEN_PIXELS;
			}
		}
	}
	
	
	/**
	 * Gets the x-coordinate for the vertical middle segment of pEdge based on the position of pClosestStoredEdge. 
	 * @param pClosestStoredEdge the closest edge to pNode which conflicts with pEdge. 
	 * @param pEdge the edge of interest
	 * @param pEdgeDirection the direction describing the trajectory of pEdge (the direction of the first segment of pEdge).
	 * @return the x-coordinate for the middle segment of pEdge, 
	 *     so that it is either 10px to the right of pClosestStoredEdge, or 10px to the left.
	 * @pre pClosestStoredEdge is present in the diagram's EdgeStorage
	 * @pre EdgePriority.isSegmented(pEdge)
	 * @pre pEdgeDirection == Direction.WEST || pEdgeDirection == Direction.EAST
	 */
	private int adjacentVerticalMidLine(Edge pClosestStoredEdge, Edge pEdge, Side pEdgeDirection) 
	{
		assert aEdgeStorage.contains(pClosestStoredEdge);
		assert EdgePriority.isSegmented(pEdge);
		assert pEdgeDirection.isVertical();
		Node commonNode = getSharedNode(pClosestStoredEdge, pEdge);
		if(pEdgeDirection == Side.LEFT)
		{
			if(isOutgoingEdge(pEdge, commonNode))
			{
				return getEdgePath(pClosestStoredEdge).getPointByIndex(1).x() + TEN_PIXELS;
			}
			else
			{
				return getEdgePath(pClosestStoredEdge).getPointByIndex(1).x() - TEN_PIXELS;
			}
		}
		else //Direction is RIGHT
		{
			if(isOutgoingEdge(pEdge, commonNode))
			{
				return getEdgePath(pClosestStoredEdge).getPointByIndex(1).x() - TEN_PIXELS;
			}
			else
			{
				return getEdgePath(pClosestStoredEdge).getPointByIndex(1).x() + TEN_PIXELS;
			}
		}
	}

	
	/**
	 * Gets the node which pEdgeA and pEdgeB are both attached to.
	 * @param pEdgeA an edge in the diagram
	 * @param pEdgeB another edge in the diagram
	 * @return the Node which pEdgeA and pEdgeB are both connected to.
	 * @pre pEdgeA and pEdgeB have an attached node in common
	 */
	private static Node getSharedNode(Edge pEdgeA, Edge pEdgeB)
	{
		assert pEdgeA.start() == pEdgeB.start() || pEdgeA.start() == pEdgeB.end() ||
				pEdgeA.end() == pEdgeB.start() || pEdgeA.end() == pEdgeB.end();
		if(pEdgeA.start().equals(pEdgeB.start()) || pEdgeA.start().equals(pEdgeB.end()))
		{
			return pEdgeA.start();
		}
		else
		{			
			return pEdgeA.end();
		}
	}
	
	/**
	 * Gets all edges currently in storage which are connected to the pNodeFace side of pNode
	 * with the same index sign (-1 or +1) as pEdge. These edges may conflict with the default placement 
	 * of pEdge, so their EdgePaths need to be considered when planning the EdgePath of pEdge. 
	 * @param pNodeSide the side of pNode on which pEdge is attached
	 * @param pNode the node of interest which pEdge is attached to
	 * @param pEdge the edge of interest
	 * @return a list of edges which could conflict with the default EdgePath of pEdge.
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode
	 */
	private List<Edge> storedConflictingEdges(Side pNodeSide, Node pNode, Edge pEdge)
	{
		assert pEdge.start() == pNode || pEdge.end() == pNode;
		return aEdgeStorage.edgesConnectedTo(pNode).stream()
			.filter(edge -> attachedSideFromStorage(edge, pNode) == pNodeSide)
			.filter(edge -> EdgePriority.isSegmented(edge))
			.filter(edge -> getIndexSign(edge, pNode, pNodeSide) == getIndexSign(pEdge, pNode, pNodeSide))
			.filter(edge -> !edge.equals(pEdge))
			.collect(toList());
	}
	
	/**
	 * Uses EdgeStorage to get the side describing the face of pNode on which a stored edge pEdge is attached.
	 * @param pEdge the edge of interest
	 * @param pNode the node of interest which PEdge is attached to
	 * @return the side of pNode on which pEdge is attached
	 * @pre storageContains(pEdge)
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode
	 */
	private Side attachedSideFromStorage(Edge pEdge, Node pNode)
	{
		assert aEdgeStorage.contains(pEdge);
		assert pEdge.start() == pNode || pEdge.end() == pNode;
		//Get the connection point of pEdge onto pNode
		Point connectionPoint = getEdgePath(pEdge).getStartPoint();
		if(!isOutgoingEdge(pEdge, pNode))
		{
			connectionPoint = getEdgePath(pEdge).getEndPoint();
		}
		//Iterate over each side of pNode. Return the side which contains connectionPoint
		for(Side side : Side.values())
		{
			if(getFace(pNode, side).spanning().contains(connectionPoint))
			{
				return side;
			}
		}
		return Side.TOP;
	}
	
	private Line getFace(Node pNode, Side pSide)
	{
		return ((NodeRenderer)rendererFor(pNode.getClass())).getFace(pNode, pSide);
	}
	
	/**
	 * Gets the vertical distance in pixels between the North side of pEndNode and the horizontal middle segment of pEdge.
	 * @param pEndNode the end node of interest
	 * @param pEdge the segmented edge of interest
	 * @param pEdgeDirection the trajectory of pEdge
	 * @return the absolute value of the distance between the North side of pEndNode and the middle segment of pEdge in pixels.
	 * @pre pEdgeDirection == Direction.NORTH || pEdgeDirection == Direction.SOUTH	
	 * @pre pEdge.getEnd() == pEndNode
	 * @pre EdgePriority.isSegmented(priorityOf(pEdge));
	 */
	private int verticalDistanceToNode(Node pEndNode, Edge pEdge, Side pEdgeDirection) 
	{
		assert pEdgeDirection.isHorizontal();	
		assert EdgePriority.isSegmented(priorityOf(pEdge));
		assert aEdgeStorage.contains(pEdge);
		return Math.abs(getEdgePath(pEdge).getPointByIndex(1).y() - pEndNode.position().y());
	}
	
	/**
	 * Gets the horizontal distance between the West side of pNode and the vertical middle segment of pEdge.
	 * @param pEndNode the node of interest. The end node for pEdge. 
	 * @param pEdge the segmented edge of interest.
	 * @param pEdgeDirection the trajectory of pEdge
	 * @return the absolute value of the distance between the West side of pNode and the middle segment of pEdge
	 * @pre pEdgeDirection == Direction.EAST || PEdgeDirection == Direction.WEST
	 * @pre EdgePriority.isSegmented(priorityOf(pEdge));
	 */
	private int horizontalDistanceToNode(Node pEndNode, Edge pEdge, Side pEdgeDirection) 
	{
		assert pEdgeDirection.isVertical();
		assert EdgePriority.isSegmented(priorityOf(pEdge));
		assert aEdgeStorage.contains(pEdge);
		return Math.abs(getEdgePath(pEdge).getPointByIndex(1).x() - pEndNode.position().x());
	}
	
	/**
	 * Gets the point from pPoints which is farthest in the direction pEdgeDirection.
	 * For example, if pDirection is North, it will return the Northern-most point from pPoints.
	 * @param pPoints the list of edge start points
	 * @param pDirection the direction used to compare pPoints
	 * @return the Point which which maximizes pDirection
	 * @pre pPoints.size() > 0
	 */
	private static Point getClosestPoint(Collection<Point> pPoints, Side pDirection) 
	{
		assert pPoints.size() > 0;
		assert pDirection!=null;
		if( pDirection == Side.TOP)
		{//Then return the point with the smallest Y-coordinate
			return pPoints.stream()
							.min((p1, p2)->Integer.compare(p1.y(), p2.y())).orElseGet(null);
		}
		else if( pDirection == Side.BOTTOM) 
		{//Then return the point with the the largest Y-coordinate
			return pPoints.stream()
						.max((p1, p2) -> Integer.compare(p1.y(), p2.y())).orElseGet(null);
		}
		else if(pDirection == Side.RIGHT)
		{//Then return the point with the largest X-coordinate
			return pPoints.stream()
				.max((p1, p2)-> Integer.compare(p1.x() , p2.x())).orElseGet(null);
		}
		else
		{ //Then return the point with the smallest X-coordinate
			return pPoints.stream()
					.min((p1, p2)-> Integer.compare(p1.x(), p2.x())).orElseGet(null);
		}
	}

	/**
	 * Checks whether the start labels of pEdge1 and pEdge2 are equal, if they both have start labels. 
	 * @param pEdge1 an edge of interest
	 * @param pEdge2 another edge of interest
	 * @return false if the edges are both ThreeLabelEdge edges with different start labels. True otherwise. 
	 * @pre pEdge1 != null && pEdge2 != null
	 */
	private static boolean noConflictingStartLabels(Edge pEdge1, Edge pEdge2)
	{
		assert pEdge1 !=null && pEdge2 !=null;
		if(pEdge1 instanceof ThreeLabelEdge edge1 && pEdge2 instanceof ThreeLabelEdge edge2 &&
				priorityOf(pEdge1) == priorityOf(pEdge2))
		{
			return edge1.getStartLabel().equals(edge2.getStartLabel());
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * Checks whether the end labels of pEdge1 and pEdge2 are equal, if they both have end labels. 
	 * @param pEdge1 an edge of interest
	 * @param pEdge2 another edge of interest
	 * @return false if the edges are both ThreeLabelEdge edges with different end labels. True otherwise. 
	 * @pre pEdge1 !=null && pEdge2 !=null
	 */
	private static boolean noConflictingEndLabels(Edge pEdge1, Edge pEdge2)
	{
		assert pEdge1 !=null && pEdge2 !=null;
		if(pEdge1 instanceof ThreeLabelEdge edge1 && pEdge2 instanceof ThreeLabelEdge edge2 &&
				priorityOf(pEdge1) == priorityOf(pEdge2))
		{
			return edge1.getEndLabel().equals(edge2.getEndLabel());
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * Returns whether there are any edges connected to pNode in between pEdge1 and pEdge2.
	 * @param pEdge1 an edge of interest
	 * @param pEdge2 another edge of interest
	 * @param pNode the node on which pEdge1 and pEdge2 are attached
	 * @return true if there are no stored edges on pNode which are attached in between pEdge1 and pEdge2, false otherwise
	 * @pre pEdge1.getStart() == pNode || pEdge1.getEnd() == pNode
	 * @pre pEdge2.getStart() == pNode || pEdge2.getEnd() == pNode
	 * @pre attachedSide(pEdge1, pNode) == attachedSide(pEdge2, pNode)
	 */
	private boolean noOtherEdgesBetween(Edge pEdge1, Edge pEdge2, Node pNode)
	{
		assert pEdge1.start() == pNode || pEdge1.end() == pNode;
		assert pEdge2.start() == pNode || pEdge2.end() == pNode;
		assert attachedSide(pEdge1, pNode) == attachedSide(pEdge2, pNode);
		if(pEdge1.equals(pEdge2))
		{
			return true;
		}
		//Return true if there are no other stored edges connected to the same side of pNode as pEdge1 and pEdge2
		if(aEdgeStorage.edgesConnectedTo(pNode).stream()
					.filter(edge -> attachedSide(edge, pNode) == attachedSide(pEdge1, pNode))
					.collect(toList()).isEmpty()) 
		{
			return true;
		}
		else
		{	//Compare the center point of pNode to the center points of the other nodes of pEdge1 and pEdge2:
			return nodesOnSameSideOfCommonNode(getOtherNode(pEdge1, pNode), getOtherNode(pEdge2, pNode), 
					pNode, attachedSide(pEdge1, pNode));
		}
	}
	
	/**
	 * Returns whether the center points of pNode1 and pNode2 are both on the same side relative to the center point of pCommonNode.
	 * @param pNode1 a node in the diagram
	 * @param pNode2 another node in the diagram
	 * @param pCommonNode aNode which has edges which connect to both pNode1 and pNode2
	 * @param pAttachedSide the side of pCommonNode on which the edges from pNode1 and pNode2 connect
	 * @return true if the center points of pNode1 and pNode2 are both above, below, to the left, or to the right 
	 *     of the center point of pCommonNode. False otherwise.
	 * @pre pAttachedSide.isCardinal()
	 */
	private boolean nodesOnSameSideOfCommonNode(Node pNode1, Node pNode2, Node pCommonNode, Side pAttachedSide)
	{
		//Compare positions of pNode1 and pNode2 to the position of pCommonNode
		Point node1Center = getBounds(pNode1).center();
		Point node2Center = getBounds(pNode2).center();
		Point commonNodeCenter = getBounds(pCommonNode).center();
		if(pAttachedSide.isHorizontal())//then compare X-coordinates
		{
			return node1Center.x() <= commonNodeCenter.x() && node2Center.x() <= commonNodeCenter.x() ||
					node1Center.x() >= commonNodeCenter.x() && node2Center.x() >= commonNodeCenter.x();
		}
		else//compare y-coordinates
		{
			return node1Center.y() <= commonNodeCenter.y() && node2Center.y() <= commonNodeCenter.y() ||
					node1Center.y() >= commonNodeCenter.y() && node2Center.y() >= commonNodeCenter.y();
		}
	}
	
	/**
	 * Gets the connection point for pEdge on the pAttachmentSide of pNode. 
	 * Checks with the diagram's EdgeStorage to find the closest available 
	 * connection point to the center point (NodeIndex ZERO). 
	 * @param pNode the node of interest
	 * @param pEdge the edge of interest
	 * @return the Point where pEdge connects to pNode
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode	
	 */
	private Point getConnectionPoint(Node pNode, Edge pEdge, Side pAttachmentSide)
	{
		assert pEdge.start() == pNode || pEdge.end() == pNode;	
		Line faceOfNode = getFace(pNode, pAttachmentSide);
		//North and South node sides have connection points: -4 ...0... +4
		//East and West node sides have connection points: -2 ...0... +2
		int maxIndex = 4; 
		if( pAttachmentSide.isVertical() )
		{
			maxIndex = 2; 
		}
		//Get the index sign (either -1 or +1)
		int indexSign = getIndexSign(pEdge, pNode, pAttachmentSide);
		//Get the first available connection point, starting at NodeIndex ZERO and moving outwards 
		for(int offset = 0; offset <= maxIndex; offset++) 
		{
			int ordinal = 4 + (indexSign * offset);
			NodeIndex index = NodeIndex.values()[ordinal];
			Point connectionPoint = index.toPoint(faceOfNode, pAttachmentSide); 
			if(aEdgeStorage.connectionPointIsAvailable(connectionPoint))
			{
				return connectionPoint;
			}
		}
		//If no connection point was available, return the point at NodeIndex MINUS_FOUR or PLUS_FOUR	
		int maxOrdinal = 4 + ( maxIndex * indexSign );
		return NodeIndex.values()[maxOrdinal].toPoint(faceOfNode, pAttachmentSide); 
	}
	
	/**
	 * Returns the node connected to pEdge which is not pNode.
	 * @param pEdge the edge of interest
	 * @param pNode a node attached to pEdge
	 * @return the other node attached to pEdge
	 * @pre pEdge!=null
	 * @pre pNode!=null
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode
	 */
	private static Node getOtherNode(Edge pEdge, Node pNode)
	{
		assert pEdge!=null;
		assert pNode!=null;
		assert pEdge.start() == pNode || pEdge.end() == pNode;
		if(pEdge.start() == pNode)
		{
			return pEdge.end();
		}
		else
		{
			return pEdge.start();
		}
	}
	
	
	/**
	 * Gets the index sign (-1 or 1) describing the attachment of pNode on pEdge.
	 * 
	 * Edges which share both their start and end nodes with another edge (shared-node edges) should have the same index sign, regardless
	 * of whether they are outgoing or incoming on pNode. 
	 * 
	 * For all other edges, the index sign can be obtained by 
	 * comparing the relative positions of pNode and the other Node attached to pEdge. 
	 * 
	 * @param pEdge the edge of interest
	 * @param pNode the node for which we want the index sign
	 * @param pSideOfNode the cardinal direction describing the side of pNode where pEdge attaches
	 * @return the index sign (-1 or 1) describing the attachment of pEdge onto pNode.
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode
	 */
	private int getIndexSign(Edge pEdge, Node pNode, Side pSideOfNode)
	{
		assert pEdge.start() == pNode || pEdge.end() == pNode;
		//Check whether there are any stored edges which are connected to both pEdge.getStart() and pEdge.getEnd()
		List<Edge> edgesWithSameNodes = aEdgeStorage.getEdgesWithSameNodes(pEdge);
		if(!edgesWithSameNodes.isEmpty()) 
		{	//For shared-nod edge: index sign on start node should always be same as end node index sign
			return indexSignOnNode(pEdge, pEdge.end(), pEdge.start(), attachedSide(pEdge, pEdge.end()));
		}
		else
		{
			return indexSignOnNode(pEdge, pNode, getOtherNode(pEdge, pNode), pSideOfNode);
		}
	}
	
	/**
	 * Gets the index sign (-1 or 1) describing the attachment of pNode on pEdge.
	 * Compares the relative positions of pNode and pOtherNode to determine if the index sign should be -1 or +1.
	 * @param pEdge the edge of interest
	 * @param pNode the node for which we want to compute the index sign
	 * @param pOtherNode the node attached to pEdge which is not pNode
	 * @param pSideOfNode the cardinal direction describing the side of pNode where pEdge could attach
	 * @return the index sign (-1 or 1) describing the attachment of pEdge onto pNode.
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode
	 * @pre getOtherNode(pEdge, pNode).equals(pOtherNode)
	 */
	private int indexSignOnNode(Edge pEdge, Node pNode, Node pOtherNode, Side pSideOfNode)
	{
		assert pEdge.start() == pNode || pEdge.end() == pNode;
		assert getOtherNode(pEdge, pNode).equals(pOtherNode);
		if( pSideOfNode.isHorizontal() ) //then compare X-coordinates
		{
			if(getBounds(pNode).center().x() <=  getBounds(pOtherNode).center().x())
			{
				return 1;
			}
			else 
			{
				return -1;
			}	
		}
		else //Side of node is East/West, so we need to compare Y-coordinates
		{
			if(getBounds(pNode).center().y() <=  getBounds(pOtherNode).center().y())
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}
	
	/**
	 * Returns whether pEdge is an outgoing edge from pNode.
	 * @param pEdge the edge of interest
	 * @param pNode the node of interest
	 * @return true if pEdge is outgoing from pNode, false otherwise
	 * @pre pNode!=null
	 * @pre pEdge!=null
	 */
	private static boolean isOutgoingEdge(Edge pEdge, Node pNode)
	{
		assert pEdge!=null && pNode!=null;
		return pEdge.start() == pNode;
	}
	
	/**
	 * Gets the side of pNode that pEdge should be attached to.
	 * @param pEdge the edge of interest
	 * @param pNode the node of interest
	 * @return The side of pNode where pEdge should be attached.
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode 
	 */
	private Side attachedSide(Edge pEdge, Node pNode)
	{
		assert pEdge.start() == pNode || pEdge.end() == pNode;
		Side startAttachedSide;
		/* If there is an Edge in storage which is also attached to pEdge.getStart() and pEdge.getEnd()
		  then the attachment side of pEdge onto pNode must be the same as the attachment side of the 
		  stored node onto pNode. (These are referred to as "shared-node edges").
		*/
		List<Edge> edgesWithSameNodes = aEdgeStorage.getEdgesWithSameNodes(pEdge);
		if(!edgesWithSameNodes.isEmpty())
		{
			return attachedSideFromStorage(edgesWithSameNodes.get(0), pNode);
		}
		//AggregationEdges prefer to attach on East/West sides, unless nodes are directly above/below each other
		if(pEdge instanceof AggregationEdge)
		{
			startAttachedSide = attachedSidePreferringEastWest(pEdge);	
		}
		//Other edges prefer to attach on North/South sides, unless nodes are directly beside each other
		else
		{
			startAttachedSide = attachedSidePreferringNorthSouth(pEdge);
		}
		//The attached side of pEdge to its end node is always opposite of the attachment side to its start node
		if(isOutgoingEdge(pEdge, pNode))
		{
			return startAttachedSide;
		}
		else
		{
			return startAttachedSide.mirrored();
		}
	}
	

	/**
	 * Gets the cardinal direction describing the side of pEdge's start node on which pEdge should attach.
	 * pEdge should connect to the East or West sides unless its nodes are directly above or below each other. 
	 * @param pEdge the edge of interest
	 * @return the side of pNode that pEdge should be attached to
	 * @pre pEdge != null
	 */
	private Side attachedSidePreferringEastWest(Edge pEdge)
	{
		assert pEdge != null;
		Rectangle startNodeBounds = getBounds(pEdge.start());
		Rectangle endNodeBounds = getBounds(pEdge.end());
		//if the start node is above or below the end node (+- 20 px) then determine whether it belongs on the N or S side
		if(startNodeBounds.maxX() > endNodeBounds.x() - (2 * TEN_PIXELS) &&
				startNodeBounds.x() < endNodeBounds.maxX() + (2 * TEN_PIXELS))
		{
			return northSouthSideUnlessTooClose(pEdge);		
		}
		else
		{
			return eastWestSideUnlessTooClose(pEdge);
		}
		
	}
	
	/**
	 * Gets the side of pEdge's start node that pEdge should attach to.  
	 * @param pEdge the edge of interest
	 * @return the North/South side of pEdge's start node which pEdge should attach to, unless pEdge's nodes are directly beside each other.
	 */
	private Side attachedSidePreferringNorthSouth(Edge pEdge)
	{
		assert pEdge!= null;
		Rectangle startNodeBounds = getBounds(pEdge.start());
		Rectangle endNodeBounds = getBounds(pEdge.end());
		//if the start node is beside the end node (+- 20 px) then compute whether it belongs on the E or W side
		if(startNodeBounds.maxY() > endNodeBounds.y() - (2 * TEN_PIXELS) && 
				startNodeBounds.y() < endNodeBounds.maxY() + (2 * TEN_PIXELS))
		{
			return eastWestSideUnlessTooClose(pEdge);
		}
		else //compute whether it belongs on the N or S side
		{
			return northSouthSideUnlessTooClose(pEdge);
		}
	}
	
	/**
	 * Returns the side of pEdge's start node that pEdge should attach to. This side will be either North or South, unless
	 * pEdge's nodes are closer to each other than they are to pEdge's middle segment. In that case, it returns the East/West attachment side.
	 * @param pEdge the edge of interest
	 * @return the attachment side of pEdge 
	 */
	private Side northSouthSideUnlessTooClose(Edge pEdge)
	{
		Side preferredSide = northOrSouthSide(getBounds(pEdge.start()), 
				getBounds(pEdge.end()));
		if(nodeIsCloserThanSegment(pEdge, pEdge.end(), preferredSide) || 
				nodeIsCloserThanSegment(pEdge, pEdge.start(), preferredSide.mirrored()))
		{
			return eastOrWestSide(getBounds(pEdge.start()), getBounds(pEdge.end()));
		}
		else
		{
			return preferredSide;
		}
	}
	
	/**
	 * Returns whether pEdge should attach on the East or West side of its start node, unless
	 * pEdge's nodes are too close together, in which case it returns the North/South attachment side.
	 * @param pEdge the edge of interest
	 * @return the attachment side of pEdge 
	 */
	private Side eastWestSideUnlessTooClose(Edge pEdge)
	{
		Side preferredSide = eastOrWestSide(getBounds(pEdge.start()), 
				getBounds(pEdge.end()));
		if(nodeIsCloserThanSegment(pEdge, pEdge.end(), preferredSide) || 
				nodeIsCloserThanSegment(pEdge, pEdge.start(), preferredSide.mirrored()))
		{
			return northOrSouthSide(getBounds(pEdge.start()), getBounds(pEdge.end()));
		}
		else
		{
			return preferredSide;
		}
	}
	
	/**
	 * Compares the relative positions of pBounds and pOtherBounds to determine if an edge connecting
	 * pBounds and pOtherBounds should connect on the North or South side of pBounds. 
	 * @param pBounds the rectangle of interest
	 * @param pOtherBounds another rectangle to compare with
	 * @return the side of pBounds (either North or South) where an edge connecting pBounds and pOtherBounds should attach.
	 * @pre pBounds != null && pOtherBounds != null
	 */
	private static Side northOrSouthSide(Rectangle pBounds, Rectangle pOtherBounds)
	{
		assert pBounds != null && pOtherBounds != null;
		if(pOtherBounds.center().y() < pBounds.center().y())
		{
			return Side.TOP;
		}
		else
		{
			return Side.BOTTOM;
		}
	}
	
	/**
	 * Compares the relative positions of pBounds and pOtherBounds to determine if an edge connecting
	 * pBounds and pOtherBounds should connect on the East or West side of pBounds. 
	 * @param pBounds the rectangle of interest
	 * @param pOtherBounds another rectangle to compare with
	 * @return the side of pBounds (either East or West) where an edge connecting pBounds and pOtherBounds should attach.
	 * @pre pBounds !=null && pOtherBounds !=null
	 */
	private static Side eastOrWestSide(Rectangle pBounds, Rectangle pOtherBounds)
	{
		assert pBounds !=null && pOtherBounds !=null;
		if(pOtherBounds.center().x() < pBounds.center().x() )
		{
			return Side.LEFT;
		}
		else
		{
			return Side.RIGHT;
					
		}
	}
	
	/**
	 * Returns whether the other node attached to pEdge is closer to pNode than pEdge's middle segment. 
	 * @param pEdge the edge of interest
	 * @param pNode a node attached to pEdge 
	 * @param pAttachedSide the side of pNode where pEdge is attached
	 * @return true if pEdge's other node is closer to pNode than pEdge's middle segment is, false otherwise.
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode
	 */
	private boolean nodeIsCloserThanSegment(Edge pEdge, Node pNode, Side pAttachedSide)
	{
		assert pEdge.start() == pNode || pEdge.end() == pNode;
		Rectangle otherNodeBounds = getBounds(getOtherNode(pEdge, pNode));
		if(pAttachedSide == Side.TOP)
		{ //Consider the middle segments of edges attached to pNode
			return !storedConflictingEdges(pAttachedSide.mirrored(), pNode, pEdge).stream()
					.filter(edge -> otherNodeBounds.y() < getEdgePath(edge).getPointByIndex(1).y() - TEN_PIXELS)
					.collect(toList())
					.isEmpty();
		}
		else if(pAttachedSide == Side.BOTTOM)
		{//Consider the middle segments of edges attached to pNode
			return !storedConflictingEdges(pAttachedSide.mirrored(), pNode, pEdge).stream()
					.filter(edge -> otherNodeBounds.maxY() > getEdgePath(edge).getPointByIndex(1).y() + TEN_PIXELS)
					.collect(toList())
					.isEmpty();
		}
		else if(pAttachedSide == Side.RIGHT)
		{//Consider the middle segments of edges attached to pNode
			return !storedConflictingEdges(pAttachedSide.mirrored(), pNode, pEdge).stream()
					.filter(edge -> otherNodeBounds.maxX() > getEdgePath(edge).getPointByIndex(1).x() - TEN_PIXELS)
					.collect(toList())
					.isEmpty();
		}
		else //Direction is LEFT
		{//Consider the middle segments of edges attached to pNode
			return !storedConflictingEdges(pAttachedSide.mirrored(), pNode, pEdge).stream()
					.filter(edge -> otherNodeBounds.x() < getEdgePath(edge).getPointByIndex(1).x() + TEN_PIXELS)
					.collect(toList())
					.isEmpty();
		}
	}
	
	/**
	 * Gets the EdgePath of pEdge from storage.
	 * @param pEdge the edge of interest
	 * @return the stored EdgePath of pEdge
	 * @pre the diagram's EdgeStorage contains pEdge
	 */
	private EdgePath getEdgePath(Edge pEdge)
	{
		assert aEdgeStorage.contains(pEdge);
		return aEdgeStorage.getEdgePath(pEdge);
	}
	
	/**
	 * @param pEdge The edge to check for a path.
	 * @return The path for the edge, or empty if there is none.
	 */
	public Optional<EdgePath> getStoredEdgePath(Edge pEdge)
	{
		if( aEdgeStorage.contains(pEdge) )
		{
			return Optional.of(aEdgeStorage.getEdgePath(pEdge));
		}
		else
		{
			return Optional.empty();
		}
	}
	
	/**
	 * Returns an array of [startPoint, endPoint] where a self-edge attached to the pCorner of pNode would connect.
	 * @param pCorner the NodeCorner of interest
	 * @param pNode the node of interest 
	 * @return an array containing the start point and end point for a self edge attached to the pCorner corner of pNode.
	 */
	public Point[] toPoints(NodeCorner pCorner, Node pNode)
	{ 
		Point startPoint;
		Point endPoint;		
		if(pCorner == NodeCorner.TOP_RIGHT)
		{
			Line topFace = getFace(pNode, Side.TOP);
			startPoint = new Point(topFace.x2() - TWENTY_PIXELS, topFace.y2());
			Line rightFace = getFace(pNode, Side.RIGHT);
			endPoint = new Point(rightFace.x1(), rightFace.y1() + TWENTY_PIXELS);
		}
		else if(pCorner == NodeCorner.TOP_LEFT)
		{
			Rectangle nodeBounds = getBounds(pNode);
			startPoint = new Point(nodeBounds.x() + TWENTY_PIXELS, nodeBounds.y());
			endPoint = new Point(nodeBounds.x(), nodeBounds.y() + TWENTY_PIXELS);
		}
		else if(pCorner == NodeCorner.BOTTOM_LEFT)
		{
			Rectangle nodeBounds = getBounds(pNode);
			startPoint = new Point(nodeBounds.x() + TWENTY_PIXELS, nodeBounds.maxY());
			endPoint = new Point(nodeBounds.x(), nodeBounds.maxY() - TWENTY_PIXELS);
		}
		else //BOTTOM_RIGHT
		{
			Rectangle nodeBounds = getBounds(pNode);
			startPoint = new Point(nodeBounds.maxX() - TWENTY_PIXELS, nodeBounds.maxY());
			endPoint = new Point(nodeBounds.maxX(), nodeBounds.maxY() - TWENTY_PIXELS);
		}
		return new Point[] {startPoint, endPoint};
	}
}

