package ca.mcgill.cs.jetuml.viewers;

import static ca.mcgill.cs.jetuml.viewers.EdgePriority.priorityOf;
import static ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry.getBounds;
import static java.util.stream.Collectors.toList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ThreeLabelEdge;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.EdgePath;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.edges.NodeIndex;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

/**
 * Plans and stores the paths for edges based on the position of stored edges and nodes 
 * to make the diagram more clean and readable.
*/
public class Layouter 
{
	private static final int TEN_PIXELS = 10;
	
	/**
	 * Uses positional information of nodes and stored edges to layout and store 
	 * the EdgePaths of edges in pDiagram.
	 * @param pDiagram the diagram of interest
	 * @pre pDiagram.getType() == DiagramType.CLASS
	 */
	public void layout(Diagram pDiagram)
	{
		assert pDiagram.getType() == DiagramType.CLASS;
		layoutSegmentedEdges(pDiagram, EdgePriority.INHERITANCE);	
		layoutSegmentedEdges(pDiagram, EdgePriority.IMPLEMENTATION);
		layoutSegmentedEdges(pDiagram, EdgePriority.AGGREGATION);
		layoutSegmentedEdges(pDiagram, EdgePriority.COMPOSITION);
		layoutSegmentedEdges(pDiagram, EdgePriority.ASSOCIATION);
		layoutDependencyEdges(pDiagram);
		layoutSelfEdges(pDiagram);
	}
	


	/**
	 * Plans the EdgePaths for all segmented edges in pDiagram with EdgePriority pEdgePriority.
	 * @param pDiagram the diagram to layout
	 * @param pEdgePriority the edge priority level 
	 * @pre pDiagram.getType() == DiagramType.CLASS
	 * @pre EdgePriority.isSegmented(pEdgePriority)
	 */
	private void layoutSegmentedEdges(Diagram pDiagram, EdgePriority pEdgePriority)
	{
		assert pDiagram.getType() == DiagramType.CLASS;
		assert EdgePriority.isSegmented(pEdgePriority);
		List<Edge> edgesToProcess = pDiagram.edges().stream()
				.filter(edge -> priorityOf(edge) == pEdgePriority)
				.sorted(Comparator.comparing(edge -> edge.getStart().position().getX()))
				.collect(toList());
				
		while (!edgesToProcess.isEmpty())
		{
			Edge currentEdge = edgesToProcess.get(0);
			Direction edgeDirection = attachedSide(currentEdge, currentEdge.getStart());
			//Get all the edges which will merge with the start or end of currentEdge
			List<Edge> edgesToMergeStart = getEdgesToMergeStart(currentEdge, edgesToProcess);
			List<Edge> edgesToMergeEnd = getEdgesToMergeEnd(currentEdge, edgesToProcess);	
			//Determine if currendEdge should merge with other edges at its start node or end node
			if (!edgesToMergeStart.isEmpty())
			{ 	
				edgesToMergeStart.add(currentEdge);
				edgesToProcess.removeAll(edgesToMergeStart);
				storeMergedStartEdges(edgeDirection, edgesToMergeStart, pDiagram);
			}
			else
			{
				edgesToMergeEnd.add(currentEdge);
				edgesToProcess.removeAll(edgesToMergeEnd);
				storeMergedEndEdges(edgeDirection, edgesToMergeEnd, pDiagram);
			}
		}
	}
	
	
	/**
	 * Plans the EdgePaths for Dependency Edges.
	 * @param pDiagram the diagram of interest
	 * @pre pDiagram.getType() == DiagramType.CLASS;
	 */
	private void layoutDependencyEdges(Diagram pDiagram)
	{
		assert pDiagram.getType() == DiagramType.CLASS;
		for (Edge edge : pDiagram.edges())
		{
			if (priorityOf(edge)==EdgePriority.DEPENDENCY)
			{   //Determine the start and end connection points
				Direction attachedEndSide = attachedSide(edge, edge.getEnd());
				Point startPoint = getConnectionPoint(edge.getStart(), edge, attachedEndSide.mirrored());
				Point endPoint = getConnectionPoint(edge.getEnd(), edge, attachedEndSide);
				//Store an EdgePath from startPoint to endPoint
				classDiagramViewerFor(edge).store(edge, new EdgePath(startPoint, endPoint));
			}
		}	
	}
	
	/**
	 * Plans the EdgePaths for self-edges in pDiagram.
	 * @param pDiagram the diagram of interest
	 * @pre pDiagram.getType() == DiagramType.CLASS;
	 */
	private void layoutSelfEdges(Diagram pDiagram)
	{
		assert pDiagram.getType() == DiagramType.CLASS;
		List<Edge> selfEdges = pDiagram.edges().stream()
			.filter(edge -> priorityOf(edge) == EdgePriority.SELF_EDGE)
			.collect(toList());
		for (Edge edge : selfEdges)
		{
			//Determine the corner where the self-edge should be placed
			NodeCorner corner = getSelfEdgeCorner(edge);
			//Build a self-edge EdgePath at the corner and store it
			EdgePath path = buildSelfEdge(edge, corner);
			classDiagramViewerFor(edge).store(edge, path);
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
		for (NodeCorner corner : NodeCorner.values())
		{	//Get a 2D array of [startPoint, endPoint] for a self edge at the corner
			Point[] points = NodeCorner.toPoints(corner, pEdge.getEnd());
			//Return the first corner with available start and end points
			if (classDiagramViewerFor(pEdge).connectionPointAvailableInStorage(points[0]) && 
					classDiagramViewerFor(pEdge).connectionPointAvailableInStorage(points[1]))
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
		Point[] connectionPoints = NodeCorner.toPoints(pCorner, pEdge.getEnd());
		Point startPoint = connectionPoints[0];
		Point endPoint = connectionPoints[1];
		Point firstBend;
		Point middleBend;
		Point lastBend;
		//determine location of first bend: either 20px above or 20px below the start point
		if (NodeCorner.horizontalSide(pCorner) == Direction.NORTH)
		{
			firstBend = new Point(startPoint.getX(), startPoint.getY() - (2 * TEN_PIXELS)); 
		}
		else
		{
			firstBend = new Point(startPoint.getX(), startPoint.getY() + (2 * TEN_PIXELS)); 
		}
		//determine location of middle and last bends
		if (NodeCorner.verticalSide(pCorner) == Direction.EAST)
		{
			middleBend = new Point(firstBend.getX() + (4 * TEN_PIXELS), firstBend.getY());
			lastBend = new Point(endPoint.getX() + (2 * TEN_PIXELS), endPoint.getY());
		}
		else
		{
			middleBend = new Point(firstBend.getX() - (4 * TEN_PIXELS), firstBend.getY());
			lastBend = new Point(endPoint.getX() - (2 * TEN_PIXELS), endPoint.getY());
		}
		return new EdgePath(startPoint, firstBend, middleBend, lastBend, endPoint);
	}

	
	/**
	 * Builds and stores the EdgePaths for edges in pEdgesToMergeEnd, so they merge at a common end point.
	 * @param pDirection the trajectory of the edges in pEdgesToMergeEnd (the direction of the first segment of the edges)
	 * @param pEdgesToMergeEnd a list of edges whose ends should be merged
	 * @param pDiagram the diagram
	 * @pre pDirection.isCardinal()
	 * @pre pEdgesToMergeEnd.size() > 0
	 * @pre pDiagram.getType() == DiagramType.CLASS
	 */
	private void storeMergedEndEdges(Direction pDirection, List<Edge> pEdgesToMergeEnd, Diagram pDiagram)
	{
		assert pDirection.isCardinal();
		assert pEdgesToMergeEnd.size() > 0;
		assert pDiagram.getType() == DiagramType.CLASS;
		//Merged edges will share a common end point
		Point sharedEndPoint = getConnectionPoint(pEdgesToMergeEnd.get(0).getEnd(), pEdgesToMergeEnd.get(0), pDirection.mirrored());
		//get the individual start points for each edge
		Map<Edge, Point> startPoints = new HashMap<Edge, Point>();
		for (Edge e : pEdgesToMergeEnd)
		{
			startPoints.put(e, getConnectionPoint(e.getStart(), e, pDirection));
		}
		//Determine the position of the shared middle segment
		Point closestStartPoint = getClosestPoint(startPoints.values(), pDirection);
		int midLineCoordinate;
		if (pDirection == Direction.NORTH || pDirection == Direction.SOUTH)
		{
			midLineCoordinate = getHorizontalMidLine(closestStartPoint, sharedEndPoint, pDirection, pEdgesToMergeEnd.get(0));
		}
		else
		{
			midLineCoordinate = getVerticalMidLine(closestStartPoint, sharedEndPoint, pDirection, pEdgesToMergeEnd.get(0));
		}
		//Build and store each edge's EdgePath
		for (Edge edge : pEdgesToMergeEnd)
		{
			EdgePath path = buildSegmentedEdgePath(pDirection, startPoints.get(edge), midLineCoordinate, sharedEndPoint);
			classDiagramViewerFor(pEdgesToMergeEnd.get(0)).store(edge, path);
		}
	}
	
	
	/**
	 * Builds and stores the EdgePaths for edges in pEdgesToMergeStart so that they share a common start point.
	 * @param pDirection the trajectory of the edges in pEdgesToMmergeStart (the direction of the first segment of the edges)
	 * @param pEdgesToMergeStart a list of edges which should me merged at their start points
	 * @param pDiagram the class diagram 
	 * @pre pDirection.isCardinal()
	 * @pre pEdgesToMergeStart.size() > 0
	 * @pre pDiagram.getType() == DiagramType.CLASS
	 */
	private void storeMergedStartEdges(Direction pDirection, List<Edge> pEdgesToMergeStart, Diagram pDiagram)
	{
		assert pDirection.isCardinal();
		assert pEdgesToMergeStart.size() > 0;
		//Get the shared start point for all pEdgesToMerge
		Point startPoint = getConnectionPoint(pEdgesToMergeStart.get(0).getStart(), pEdgesToMergeStart.get(0), pDirection);
		//Get the individual end points for each edge
		Map<Edge, Point> endPoints = new HashMap<Edge, Point>();
		for (Edge edge : pEdgesToMergeStart)
		{
			endPoints.put(edge, getConnectionPoint(edge.getEnd(), edge, pDirection.mirrored()));
		}
		//Determine the X or Y coordinate of the middle segment for the edges:
		//The default position of the merged middle segment is halfway between startPoint and closestEndPoint:
		Point closestEndPoint = getClosestPoint(endPoints.values(), pDirection.mirrored());
		int midLineCoordinate;
		if (pDirection == Direction.NORTH || pDirection == Direction.SOUTH)
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
		for (Edge edge : pEdgesToMergeStart)
		{
			EdgePath path = buildSegmentedEdgePath(pDirection, startPoint, midLineCoordinate, endPoints.get(edge));
			classDiagramViewerFor(pEdgesToMergeStart.get(0)).store(edge, path);
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
	 * @pre pEdgeDirection.isCardinal()
	 * @pre pStart != null && pEnd != null
	 * @pre pMidLine >= 0
	 */
	private EdgePath buildSegmentedEdgePath(Direction pEdgeDirection, Point pStart, int pMidLine, Point pEnd)
	{
		assert pEdgeDirection.isCardinal();
		assert pStart != null && pEnd != null;
		assert pMidLine >= 0;
		Point firstMiddlePoint;
		Point secondMiddlePoint;
		if (pEdgeDirection == Direction.NORTH || pEdgeDirection == Direction.SOUTH)
		{
			//Then the mid-point coordinate is a Y-coordinate
			firstMiddlePoint = new Point(pStart.getX(), pMidLine);
			secondMiddlePoint = new Point(pEnd.getX(), pMidLine);
		}
		else //East or West
		{	//Then the mid-point coordinate is a X-coordinate
			firstMiddlePoint = new Point(pMidLine, pStart.getY());
			secondMiddlePoint = new Point(pMidLine, pEnd.getY());
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
				.filter(edge -> edge.getEnd() == pEdge.getEnd())
				.filter(edge -> priorityOf(edge) == priorityOf(pEdge))
				.filter(edge -> attachedSide(edge, edge.getEnd()) == attachedSide(pEdge, pEdge.getEnd())) 
				.filter(edge -> noConflictingEndLabels(edge, pEdge))
				.filter(edge -> noOtherEdgesBetween(edge, pEdge, pEdge.getEnd()))
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
			.filter(edge -> edge.getStart().equals(pEdge.getStart()))
			.filter(edge -> priorityOf(edge) == priorityOf(pEdge))
			.filter(edge -> attachedSide(edge, edge.getStart()) == attachedSide(pEdge, pEdge.getStart()))
			.filter(edge -> noOtherEdgesBetween(edge, pEdge, pEdge.getStart()))
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
	private int getHorizontalMidLine(Point pStart, Point pEnd, Direction pEdgeDirection, Edge pEdge)
	{
		assert pEdgeDirection == Direction.NORTH || pEdgeDirection == Direction.SOUTH;
		assert EdgePriority.isSegmented(pEdge);
		assert pStart != null && pEnd != null;
		//Check for any edge in storage which is attached to pEdge's start and end nodes
		// "Shared-node edges" require a different layout strategy:
		List<Edge> storedEdgesWithSameNodes = classDiagramViewerFor(pEdge).storedEdgesWithSameNodes(pEdge);
		if (!storedEdgesWithSameNodes.isEmpty())
		{
			return horizontalMidlineForSharedNodeEdges(storedEdgesWithSameNodes.get(0), pEdge, pEdgeDirection);
		}
		//Otherwise, find the closest edge which conflicts with pEdge
		Optional<Edge> closestStoredEdge = closestConflictingHorizontalSegment(pEdgeDirection, pEdge);
		if (closestStoredEdge.isEmpty())
		{
			//If there are no conflicting segments, return the y-coordinate equidistant between the start and end points
			return pEnd.getY() + ((pStart.getY() - pEnd.getY()) / 2);
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
	private int getVerticalMidLine(Point pStart, Point pEnd, Direction pEdgeDirection, Edge pEdge)
	{
		assert pEdgeDirection == Direction.EAST || pEdgeDirection == Direction.WEST;
		assert EdgePriority.isSegmented(pEdge);
		assert pStart != null && pEnd != null;
		//Check for any edge in storage which shares the same 2 nodes as pEdge: 
		List<Edge> storedEdgesWithSameNodes = classDiagramViewerFor(pEdge).storedEdgesWithSameNodes(pEdge);
		if (!storedEdgesWithSameNodes.isEmpty())
		{
			//"Shared-node edges" require a different layout strategy
			return verticalMidlineForSharedNodeEdges(storedEdgesWithSameNodes.get(0), pEdge, pEdgeDirection);
		}
		//Otherwise, find the closest edge which conflicts with pEdge
		Optional<Edge> closestStoredEdge = closestConflictingVerticalSegment(pEdgeDirection, pEdge);
		if (closestStoredEdge.isEmpty())
		{
			//If no stored edges conflict with pEdge then return the x-coordinate in between the start and end points
			return pEnd.getX() + ((pStart.getX() - pEnd.getX()) / 2);
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
	private int horizontalMidlineForSharedNodeEdges(Edge pEdgeWithSameNodes, Edge pNewEdge, Direction pEdgeDirection)
	{
		assert classDiagramViewerFor(pEdgeWithSameNodes).storageContains(pEdgeWithSameNodes);
		assert pEdgeWithSameNodes.getStart() == pNewEdge.getStart() || pEdgeWithSameNodes.getStart() == pNewEdge.getEnd();
		assert pEdgeWithSameNodes.getEnd() == pNewEdge.getStart() || pEdgeWithSameNodes.getEnd() == pNewEdge.getEnd();
		assert pEdgeDirection == Direction.NORTH || pEdgeDirection == Direction.SOUTH;
		if (pEdgeDirection == Direction.NORTH)
		{	
			return getStoredEdgePath(pEdgeWithSameNodes).getPointByIndex(1).getY() - TEN_PIXELS;
		}
		else //pEdgeDirection == Direction.SOUTH
		{
			return getStoredEdgePath(pEdgeWithSameNodes).getPointByIndex(1).getY() + TEN_PIXELS;
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
	private int verticalMidlineForSharedNodeEdges(Edge pEdgeWithSameNodes, Edge pNewEdge, Direction pEdgeDirection) 
	{
		assert classDiagramViewerFor(pEdgeWithSameNodes).storageContains(pEdgeWithSameNodes);
		assert pEdgeWithSameNodes.getStart() == pNewEdge.getStart() || pEdgeWithSameNodes.getStart() == pNewEdge.getEnd();
		assert pEdgeWithSameNodes.getEnd() == pNewEdge.getStart() || pEdgeWithSameNodes.getEnd() == pNewEdge.getEnd();
		assert pEdgeDirection == Direction.WEST || pEdgeDirection == Direction.EAST;
		if (pEdgeDirection == Direction.WEST)
		{
			return getStoredEdgePath(pEdgeWithSameNodes).getPointByIndex(1).getX() - TEN_PIXELS;
		}
		else
		{
			return getStoredEdgePath(pEdgeWithSameNodes).getPointByIndex(1).getX() + TEN_PIXELS;
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
	private Optional<Edge> closestConflictingHorizontalSegment(Direction pEdgeDirection, Edge pEdge)
	{
		assert pEdgeDirection == Direction.NORTH || pEdgeDirection == Direction.SOUTH;
		//Consider all edges connected to pEdge.getEnd() which are in the way of pEdge
		List<Edge> conflictingEdges = storedConflictingEdges(pEdgeDirection.mirrored(), pEdge.getEnd(), pEdge);	
		//also consider edges which are connected to pEdge.getStart() which are in the way of pEdge
		conflictingEdges.addAll(storedConflictingEdges(pEdgeDirection, pEdge.getStart(), pEdge));
		if (conflictingEdges.isEmpty())
		{
			return Optional.empty();
		}
		else 
		{	//For Aggregation/Composition edges: return the Edge with the middle segment which is closest to pEdge's start node
			if (pEdge instanceof AggregationEdge)
			{
				return conflictingEdges.stream()
						.min(Comparator.comparing(edge -> verticalDistanceToNode(pEdge.getStart(), edge, pEdgeDirection)));
			}
			else
			{  //For all other segmented edges: return the Edge with the middle segment which is closest to pEdge's end node
				return conflictingEdges.stream()
						.min(Comparator.comparing(edge -> verticalDistanceToNode(pEdge.getEnd(), edge, pEdgeDirection)));
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
	private Optional<Edge> closestConflictingVerticalSegment(Direction pEdgeDirection, Edge pEdge) 
	{
		assert pEdgeDirection == Direction.EAST || pEdgeDirection == Direction.WEST;
		assert EdgePriority.isSegmented(pEdge);
		//Get all edges connected to pEdge's end node which could conflict with pEdge's middle segment position
		List<Edge> conflictingEdges = storedConflictingEdges(pEdgeDirection.mirrored(), pEdge.getEnd(), pEdge);	
		//Also consider edges connected to pEdge's start node which could conflict with pEdge's middle segment
		conflictingEdges.addAll(storedConflictingEdges(pEdgeDirection, pEdge.getStart(), pEdge));
		if (conflictingEdges.isEmpty())
		{
			return Optional.empty();
		}
		else 
		{	//for AggregationEdges: return the Edge with the middle segment which is closest to pEdge.getStart()
			if (pEdge instanceof AggregationEdge)
			{
				return conflictingEdges.stream()
						.min(Comparator.comparing(edge -> horizontalDistanceToNode(pEdge.getStart(), edge, pEdgeDirection)));
			}
			else
			{	//For all other edges: return the Edge with the middle segment which is closest to pEdge.getEnd()
				return conflictingEdges.stream()
						.min(Comparator.comparing(edge -> horizontalDistanceToNode(pEdge.getEnd(), edge, pEdgeDirection)));
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
	private int adjacentHorizontalMidLine(Edge pClosestStoredEdge, Edge pEdge, Direction pEdgeDirection)
	{
		assert classDiagramViewerFor(pEdge).storageContains(pClosestStoredEdge);
		assert EdgePriority.isSegmented(pEdge);
		assert pEdgeDirection == Direction.NORTH || pEdgeDirection == Direction.SOUTH;
		Node commonNode = getSharedNode(pClosestStoredEdge, pEdge);
		if (pEdgeDirection == Direction.NORTH)
		{
			if (isOutgoingEdge(pEdge, commonNode))
			{
				return getStoredEdgePath(pClosestStoredEdge).getPointByIndex(1).getY() + TEN_PIXELS;
			}
			else
			{
				return getStoredEdgePath(pClosestStoredEdge).getPointByIndex(1).getY() - TEN_PIXELS;
			}
		}
		else //Direction is SOUTH
		{
			if (isOutgoingEdge(pEdge, commonNode))
			{
				return getStoredEdgePath(pClosestStoredEdge).getPointByIndex(1).getY() - TEN_PIXELS;
			}
			else
			{
				return getStoredEdgePath(pClosestStoredEdge).getPointByIndex(1).getY() + TEN_PIXELS;
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
	private int adjacentVerticalMidLine(Edge pClosestStoredEdge, Edge pEdge, Direction pEdgeDirection) 
	{
		assert classDiagramViewerFor(pEdge).storageContains(pClosestStoredEdge);
		assert EdgePriority.isSegmented(pEdge);
		assert pEdgeDirection == Direction.EAST || pEdgeDirection == Direction.WEST;
		Node commonNode = getSharedNode(pClosestStoredEdge, pEdge);
		if (pEdgeDirection == Direction.WEST)
		{
			if (isOutgoingEdge(pEdge, commonNode))
			{
				return getStoredEdgePath(pClosestStoredEdge).getPointByIndex(1).getX() + TEN_PIXELS;
			}
			else
			{
				return getStoredEdgePath(pClosestStoredEdge).getPointByIndex(1).getX() - TEN_PIXELS;
			}
		}
		else //Direction is EAST
		{
			if (isOutgoingEdge(pEdge, commonNode))
			{
				return getStoredEdgePath(pClosestStoredEdge).getPointByIndex(1).getX() - TEN_PIXELS;
			}
			else
			{
				return getStoredEdgePath(pClosestStoredEdge).getPointByIndex(1).getX() + TEN_PIXELS;
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
	private Node getSharedNode(Edge pEdgeA, Edge pEdgeB)
	{
		assert pEdgeA.getStart() == pEdgeB.getStart() || pEdgeA.getStart() == pEdgeB.getEnd() ||
				pEdgeA.getEnd() == pEdgeB.getStart() || pEdgeA.getEnd() == pEdgeB.getEnd();
		if (pEdgeA.getStart().equals(pEdgeB.getStart()) || pEdgeA.getStart().equals(pEdgeB.getEnd()))
		{
			return pEdgeA.getStart();
		}
		else
		{			
			return pEdgeA.getEnd();
		}
	}
	
	/**
	 * Gets all edges currently in storage which are connected to the pNodeFace side of pNode
	 * with the same index sign (-1 or +1) as pEdge. These edges may conflict with the default placement 
	 * of pEdge, so their EdgePaths need to be considered when planning the EdgePath of pEdge. 
	 * @param pNodeFace the face of pNode on which pEdge is attached
	 * @param pNode the node of interest which pEdge is attached to
	 * @param pEdge the edge of interest
	 * @return a list of edges which could conflict with the default EdgePath of pEdge.
	 * @pre pNodeFace.isCardinal() 
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode
	 */
	private List<Edge> storedConflictingEdges(Direction pNodeFace, Node pNode, Edge pEdge)
	{
		assert pNodeFace.isCardinal();
		assert pEdge.getStart() == pNode || pEdge.getEnd() == pNode;
		return classDiagramViewerFor(pEdge).storedEdgesConnectedTo(pNode).stream()
			.filter(edge -> attachedSideFromStorage(edge, pNode) == pNodeFace)
			.filter(edge -> EdgePriority.isSegmented(edge))
			.filter(edge -> getIndexSign(edge, pNode, pNodeFace) == getIndexSign(pEdge, pNode, pNodeFace))
			.filter(edge -> !edge.equals(pEdge))
			.collect(toList());
	}
	
	/**
	 * Uses EdgeStorage to get the cardinal Direction describing the face of pNode on which a stored edge pEdge is attached.
	 * @param pEdge the edge of interest
	 * @param pNode the node of interest which PEdge is attached to
	 * @return the face of pNode on which pEdge is attached
	 * @pre storageContains(pEdge)
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode
	 */
	private Direction attachedSideFromStorage(Edge pEdge, Node pNode)
	{
		assert storageContains(pEdge);
		assert pEdge.getStart() == pNode || pEdge.getEnd() == pNode;
		//Get the connection point of pEdge onto pNode
		Point connectionPoint = getStoredEdgePath(pEdge).getStartPoint();
		if (!isOutgoingEdge(pEdge, pNode))
		{
			connectionPoint = getStoredEdgePath(pEdge).getEndPoint();
		}
		//Iterate over each side of pNode. Return the side which contains connectionPoint
		Direction[] sides = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
		for (Direction side : sides)
		{
			if (getNodeFace(getBounds(pNode), side).spanning().contains(connectionPoint))
			{
				return side;
			}
		}
		return Direction.NORTH;
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
	private int verticalDistanceToNode(Node pEndNode, Edge pEdge, Direction pEdgeDirection) 
	{
		assert pEdgeDirection == Direction.NORTH || pEdgeDirection == Direction.SOUTH;	
		assert EdgePriority.isSegmented(priorityOf(pEdge));
		assert storageContains(pEdge);
		return Math.abs(getStoredEdgePath(pEdge).getPointByIndex(1).getY() - pEndNode.position().getY());
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
	private int horizontalDistanceToNode(Node pEndNode, Edge pEdge, Direction pEdgeDirection) 
	{
		assert pEdgeDirection == Direction.EAST || pEdgeDirection == Direction.WEST;
		assert EdgePriority.isSegmented(priorityOf(pEdge));
		assert storageContains(pEdge);
		return Math.abs(getStoredEdgePath(pEdge).getPointByIndex(1).getX() - pEndNode.position().getX());
	}
	
	/**
	 * Gets the point from pPoints which is farthest in the direction pEdgeDirection.
	 * For example, if pDirection is North, it will return the Northern-most point from pPoints.
	 * @param pPoints the list of edge start points
	 * @param pDirection the direction used to compare pPoints
	 * @return the Point which which maximizes pDirection
	 * @pre pPoints.size() > 0
	 * @pre pEdgeDirection.iscardinal()
	 */
	private Point getClosestPoint(Collection<Point> pPoints, Direction pDirection) 
	{
		assert pPoints.size() > 0;
		assert pDirection!=null && pDirection.isCardinal();
		if (pDirection == Direction.NORTH)
		{//Then return the point with the smallest Y-coordinate
			return pPoints.stream()
							.min((p1, p2)->Integer.compare(p1.getY(), p2.getY())).orElseGet(null);
		}
		else if (pDirection == Direction.SOUTH) 
		{//Then return the point with the the largest Y-coordinate
			return pPoints.stream()
						.max((p1, p2) -> Integer.compare(p1.getY(), p2.getY())).orElseGet(null);
		}
		else if (pDirection == Direction.EAST)
		{//Then return the point with the largest X-coordinate
			return pPoints.stream()
				.max((p1, p2)-> Integer.compare(p1.getX() , p2.getX())).orElseGet(null);
		}
		else
		{ //Then return the point with the smallest X-coordinate
			return pPoints.stream()
					.min((p1, p2)-> Integer.compare(p1.getX(), p2.getX())).orElseGet(null);
		}
	}

	/**
	 * Checks whether the start labels of pEdge1 and pEdge2 are equal, if they both have start labels. 
	 * @param pEdge1 an edge of interest
	 * @param pEdge2 another edge of interest
	 * @return false if the edges are both ThreeLabelEdge edges with different start labels. True otherwise. 
	 * @pre pEdge1 != null && pEdge2 != null
	 */
	private boolean noConflictingStartLabels(Edge pEdge1, Edge pEdge2)
	{
		assert pEdge1 !=null && pEdge2 !=null;
		if (pEdge1 instanceof ThreeLabelEdge && pEdge2 instanceof ThreeLabelEdge &&
				priorityOf(pEdge1) == priorityOf(pEdge2))
		{
			ThreeLabelEdge labelEdge1 = (ThreeLabelEdge) pEdge1;
			ThreeLabelEdge labelEdge2 = (ThreeLabelEdge) pEdge2;
			return labelEdge1.getStartLabel().equals(labelEdge2.getStartLabel());
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
	private boolean noConflictingEndLabels(Edge pEdge1, Edge pEdge2)
	{
		assert pEdge1 !=null && pEdge2 !=null;
		if (pEdge1 instanceof ThreeLabelEdge && pEdge2 instanceof ThreeLabelEdge &&
				priorityOf(pEdge1) == priorityOf(pEdge2))
		{
			ThreeLabelEdge labelEdge1 = (ThreeLabelEdge) pEdge1;
			ThreeLabelEdge labelEdge2 = (ThreeLabelEdge) pEdge2;
			return labelEdge1.getEndLabel().equals(labelEdge2.getEndLabel());
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
		assert pEdge1.getStart() == pNode || pEdge1.getEnd() == pNode;
		assert pEdge2.getStart() == pNode || pEdge2.getEnd() == pNode;
		assert attachedSide(pEdge1, pNode) == attachedSide(pEdge2, pNode);
		if (pEdge1.equals(pEdge2))
		{
			return true;
		}
		//Return true if there are no other stored edges connected to the same side of pNode as pEdge1 and pEdge2
		if(classDiagramViewerFor(pEdge1).storedEdgesConnectedTo(pNode).stream()
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
	private boolean nodesOnSameSideOfCommonNode(Node pNode1, Node pNode2, Node pCommonNode, Direction pAttachedSide)
	{
		assert pAttachedSide.isCardinal();
		//Compare positions of pNode1 and pNode2 to the position of pCommonNode
		Point node1Center = getBounds(pNode1).getCenter();
		Point node2Center = getBounds(pNode2).getCenter();
		Point commonNodeCenter = getBounds(pCommonNode).getCenter();
		if (pAttachedSide == Direction.NORTH || pAttachedSide == Direction.SOUTH)//then compare X-coordinates
		{
			return node1Center.getX() <= commonNodeCenter.getX() && node2Center.getX() <= commonNodeCenter.getX() ||
					node1Center.getX() >= commonNodeCenter.getX() && node2Center.getX() >= commonNodeCenter.getX();
		}
		else//compare y-coordinates
		{
			return node1Center.getY() <= commonNodeCenter.getY() && node2Center.getY() <= commonNodeCenter.getY() ||
					node1Center.getY() >= commonNodeCenter.getY() && node2Center.getY() >= commonNodeCenter.getY();
		}
	}
	
	/**
	 * Gets the connection point for pEdge on the pAttachmentSide of pNode. Checks with the diagram's EdgeStorage to find the
	 * closest available connection point to the center point (NodeIndex ZERO). 
	 * @param pNode the node of interest
	 * @param pEdge the edge of interest
	 * @return the Point where pEdge connects to pNode
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode	
	 * @pre pAttachmentSide.isCardinal()
	 */
	private Point getConnectionPoint(Node pNode, Edge pEdge, Direction pAttachmentSide)
	{
		assert pEdge.getStart() == pNode || pEdge.getEnd() == pNode;	
		assert pAttachmentSide.isCardinal();
		Line faceOfNode = getNodeFace(getBounds(pNode), pAttachmentSide);
		//North and South node sides have connection points: -4 ...0... +4
		//East and West node sides have connection points: -2 ...0... +2
		int maxIndex = 4; 
		if (pAttachmentSide == Direction.EAST || pAttachmentSide == Direction.WEST)
		{
			maxIndex = 2; 
		}
		//Get the index sign (either -1 or +1)
		int indexSign = getIndexSign(pEdge, pNode, pAttachmentSide);
		//Get the first available connection point, starting at NodeIndex ZERO and moving outwards 
		for (int offset = 0; offset <= maxIndex; offset++) 
		{
			int ordinal = 4 + (indexSign * offset);
			NodeIndex index = NodeIndex.values()[ordinal];
			Point indexPoint = index.toPoint(faceOfNode, pAttachmentSide.toNodeSide()); // TODO Convert pAttachmentSide to NodeSide
			if (classDiagramViewerFor(pEdge).connectionPointAvailableInStorage(indexPoint))
			{
				return indexPoint;
			}
		}
		//If no connection point was available, return the point at NodeIndex MINUS_FOUR or PLUS_FOUR	
		int maxOrdinal = 4 + ( maxIndex * indexSign );
		return NodeIndex.values()[maxOrdinal].toPoint(faceOfNode, pAttachmentSide.toNodeSide()); // TODO Convert pAttachmentSide to NodeSide
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
	private Node getOtherNode(Edge pEdge, Node pNode)
	{
		assert pEdge!=null;
		assert pNode!=null;
		assert pEdge.getStart() == pNode || pEdge.getEnd() == pNode;
		if (pEdge.getStart() == pNode)
		{
			return pEdge.getEnd();
		}
		else
		{
			return pEdge.getStart();
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
	 * @pre pSideOfNode.isCardinal()
	 */
	private int getIndexSign(Edge pEdge, Node pNode, Direction pSideOfNode)
	{
		assert pEdge.getStart() == pNode || pEdge.getEnd() == pNode;
		//Check whether there are any stored edges which are connected to both pEdge.getStart() and pEdge.getEnd()
		List<Edge> edgesWithSameNodes = classDiagramViewerFor(pEdge).storedEdgesWithSameNodes(pEdge);
		if (!edgesWithSameNodes.isEmpty()) 
		{	//For shared-nod edge: index sign on start node should always be same as end node index sign
			return indexSignOnNode(pEdge, pEdge.getEnd(), pEdge.getStart(), attachedSide(pEdge, pEdge.getEnd()));
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
	 * @pre pSideOfNode.isCardinal()
	 */
	private int indexSignOnNode(Edge pEdge, Node pNode, Node pOtherNode, Direction pSideOfNode)
	{
		assert pEdge.getStart() == pNode || pEdge.getEnd() == pNode;
		assert getOtherNode(pEdge, pNode).equals(pOtherNode);
		assert pSideOfNode.isCardinal();
		if (pSideOfNode == Direction.NORTH || pSideOfNode == Direction.SOUTH) //then compare X-coordinates
		{
			if (NodeViewerRegistry.getBounds(pNode).getCenter().getX() <=  NodeViewerRegistry.getBounds(pOtherNode).getCenter().getX())
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
			if (NodeViewerRegistry.getBounds(pNode).getCenter().getY() <=  NodeViewerRegistry.getBounds(pOtherNode).getCenter().getY())
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
	 * Gets a line representing the pSideOfNode side of pNode. 
	 * @param pNode the node of interest
	 * @param pSideOfNode the desired side of pNode
	 * @return a line spanning the pSideOfNode side of pNode
	 * @pre pNodeBounds != null
	 * @pre pSideOfNode.isCardinal()
	 */
	private Line getNodeFace(Rectangle pNodeBounds, Direction pSideOfNode)
	{
		assert pNodeBounds !=null && pSideOfNode.isCardinal();
		Point topLeft = pNodeBounds.getOrigin();
		Point topRight = new Point(pNodeBounds.getMaxX(), pNodeBounds.getY());
		Point bottomLeft = new Point(pNodeBounds.getX(), pNodeBounds.getMaxY());
		Point bottomRight = new Point(pNodeBounds.getMaxX(), pNodeBounds.getMaxY());
		if (pSideOfNode == Direction.SOUTH)
		{
			return new Line(bottomLeft, bottomRight);
		}
		else if (pSideOfNode == Direction.NORTH)
		{
			return new Line(topLeft, topRight);
		}
		else if (pSideOfNode == Direction.WEST)
		{
			return new Line(topLeft, bottomLeft);
		}
		else 
		{
			return new Line(topRight, bottomRight);
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
	private boolean isOutgoingEdge(Edge pEdge, Node pNode)
	{
		assert pEdge!=null && pNode!=null;
		return pEdge.getStart() == pNode;
	}
	
	/**
	 * Gets the Direction describing the side of pNode that pEdge should be attached to.
	 * @param pEdge the edge of interest
	 * @param pNode the node of interest
	 * @return the cardinal side of pNode where pEdge should be attached.
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode 
	 */
	private Direction attachedSide(Edge pEdge, Node pNode)
	{
		assert pEdge.getStart() == pNode || pEdge.getEnd() == pNode;
		Direction startAttachedSide;
		/* If there is an Edge in storage which is also attached to pEdge.getStart() and pEdge.getEnd()
		  then the attachment side of pEdge onto pNode must be the same as the attachment side of the 
		  stored node onto pNode. (These are referred to as "shared-node edges").
		*/
		List<Edge> edgesWithSameNodes = classDiagramViewerFor(pEdge).storedEdgesWithSameNodes(pEdge);
		if (!edgesWithSameNodes.isEmpty())
		{
			return attachedSideFromStorage(edgesWithSameNodes.get(0), pNode);
		}
		//AggregationEdges prefer to attach on East/West sides, unless nodes are directly above/below each other
		if (pEdge instanceof AggregationEdge)
		{
			startAttachedSide = attachedSidePreferringEastWest(pEdge);	
		}
		//Other edges prefer to attach on North/South sides, unless nodes are directly beside each other
		else
		{
			startAttachedSide = attachedSidePreferringNorthSouth(pEdge);
		}
		//The attached side of pEdge to its end node is always opposite of the attachment side to its start node
		if (isOutgoingEdge(pEdge, pNode))
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
	 * @return the cardinal side of pNode that pEdge should be attached to
	 * @pre pEdge != null
	 */
	private Direction attachedSidePreferringEastWest(Edge pEdge)
	{
		assert pEdge != null;
		Rectangle startNodeBounds = NodeViewerRegistry.getBounds(pEdge.getStart());
		Rectangle endNodeBounds = NodeViewerRegistry.getBounds(pEdge.getEnd());
		//if the start node is above or below the end node (+- 20 px) then determine whether it belongs on the N or S side
		if (startNodeBounds.getMaxX() > endNodeBounds.getX() - (2 * TEN_PIXELS) &&
				startNodeBounds.getX() < endNodeBounds.getMaxX() + (2 * TEN_PIXELS))
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
	private Direction attachedSidePreferringNorthSouth(Edge pEdge)
	{
		assert pEdge!= null;
		Rectangle startNodeBounds = NodeViewerRegistry.getBounds(pEdge.getStart());
		Rectangle endNodeBounds = NodeViewerRegistry.getBounds(pEdge.getEnd());
		//if the start node is beside the end node (+- 20 px) then compute whether it belongs on the E or W side
		if (startNodeBounds.getMaxY() > endNodeBounds.getY() - (2 * TEN_PIXELS) && 
				startNodeBounds.getY() < endNodeBounds.getMaxY() + (2 * TEN_PIXELS))
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
	private Direction northSouthSideUnlessTooClose(Edge pEdge)
	{
		Direction preferredSide = northOrSouthSide(NodeViewerRegistry.getBounds(pEdge.getStart()), 
				NodeViewerRegistry.getBounds(pEdge.getEnd()));
		if (nodeIsCloserThanSegment(pEdge, pEdge.getEnd(), preferredSide) || 
				nodeIsCloserThanSegment(pEdge, pEdge.getStart(), preferredSide.mirrored()))
		{
			return eastOrWestSide(NodeViewerRegistry.getBounds(pEdge.getStart()), NodeViewerRegistry.getBounds(pEdge.getEnd()));
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
	private Direction eastWestSideUnlessTooClose(Edge pEdge)
	{
		Direction preferredSide = eastOrWestSide(NodeViewerRegistry.getBounds(pEdge.getStart()), 
				NodeViewerRegistry.getBounds(pEdge.getEnd()));
		if (nodeIsCloserThanSegment(pEdge, pEdge.getEnd(), preferredSide) || 
				nodeIsCloserThanSegment(pEdge, pEdge.getStart(), preferredSide.mirrored()))
		{
			return northOrSouthSide(NodeViewerRegistry.getBounds(pEdge.getStart()), NodeViewerRegistry.getBounds(pEdge.getEnd()));
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
	private Direction northOrSouthSide(Rectangle pBounds, Rectangle pOtherBounds)
	{
		assert pBounds != null && pOtherBounds != null;
		if (pOtherBounds.getCenter().getY() < pBounds.getCenter().getY())
		{
			return Direction.NORTH;
		}
		else
		{
			return Direction.SOUTH;
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
	private Direction eastOrWestSide(Rectangle pBounds, Rectangle pOtherBounds)
	{
		assert pBounds !=null && pOtherBounds !=null;
		if (pOtherBounds.getCenter().getX() < pBounds.getCenter().getX() )
		{
			return Direction.WEST;
		}
		else
		{
			return Direction.EAST;
					
		}
	}
	
	/**
	 * Returns whether the other node attached to pEdge is closer to pNode than pEdge's middle segment. 
	 * @param pEdge the edge of interest
	 * @param pNode a node attached to pEdge 
	 * @param pAttachedSide the cardinal direction describing the side of pNode where pEdge is attached
	 * @return true if pEdge's other node is closer to pNode than pEdge's middle segment is, false otherwise.
	 * @pre pEdge.getStart() == pNode || pEdge.getEnd() == pNode
	 * @pre pAttachedSide.isCardinal()
	 */
	private boolean nodeIsCloserThanSegment(Edge pEdge, Node pNode, Direction pAttachedSide)
	{
		assert pEdge.getStart() == pNode || pEdge.getEnd() == pNode;
		assert pAttachedSide.isCardinal();
		Rectangle otherNodeBounds = NodeViewerRegistry.getBounds(getOtherNode(pEdge, pNode));
		if (pAttachedSide == Direction.NORTH)
		{ //Consider the middle segments of edges attached to pNode
			return !storedConflictingEdges(pAttachedSide.mirrored(), pNode, pEdge).stream()
					.filter(edge -> otherNodeBounds.getY() < getStoredEdgePath(edge).getPointByIndex(1).getY() - TEN_PIXELS)
					.collect(toList())
					.isEmpty();
		}
		else if (pAttachedSide == Direction.SOUTH)
		{//Consider the middle segments of edges attached to pNode
			return !storedConflictingEdges(pAttachedSide.mirrored(), pNode, pEdge).stream()
					.filter(edge -> otherNodeBounds.getMaxY() > getStoredEdgePath(edge).getPointByIndex(1).getY() + TEN_PIXELS)
					.collect(toList())
					.isEmpty();
		}
		else if (pAttachedSide == Direction.EAST)
		{//Consider the middle segments of edges attached to pNode
			return !storedConflictingEdges(pAttachedSide.mirrored(), pNode, pEdge).stream()
					.filter(edge -> otherNodeBounds.getMaxX() > getStoredEdgePath(edge).getPointByIndex(1).getX() - TEN_PIXELS)
					.collect(toList())
					.isEmpty();
		}
		else //Direction is WEST
		{//Consider the middle segments of edges attached to pNode
			return !storedConflictingEdges(pAttachedSide.mirrored(), pNode, pEdge).stream()
					.filter(edge -> otherNodeBounds.getX() < getStoredEdgePath(edge).getPointByIndex(1).getX() + TEN_PIXELS)
					.collect(toList())
					.isEmpty();
		}
	}
	
	/**
	 * Returns the ClassDiagramViewer associated with pEdge's Diagram.
	 * @param pEdge the Edge of interest
	 * @return the ClassDiagramViewer for the Diagram which pEdge is part of.
	 * @pre DiagramType.viewerFor(pEdge.getDiagram()) instanceof ClassDiagramViewer
	 */
	private ClassDiagramViewer classDiagramViewerFor(Edge pEdge)
	{
		assert DiagramType.viewerFor(pEdge.getDiagram()) instanceof ClassDiagramViewer;
		return (ClassDiagramViewer) DiagramType.viewerFor(pEdge.getDiagram());
	}

	/**
	 * Gets the EdgePath of pEdge from storage.
	 * @param pEdge the edge of interest
	 * @return the stored EdgePAth of pEdge
	 * @pre the diagram's EdgeStorage contains pEdge
	 */
	private EdgePath getStoredEdgePath(Edge pEdge)
	{
		ClassDiagramViewer classDiagramViewer = (ClassDiagramViewer) DiagramType.viewerFor(pEdge.getDiagram());
		assert classDiagramViewer.storageContains(pEdge);
		return classDiagramViewer.storedEdgePath(pEdge);
	}

	/**
	 * Returns whether pEdge is present in the EdgeStorage associated with its Diagram.
	 * @param pEdge the Edge of interest
	 * @return true if EdgeStorage contains pEdge, false otherwise.
	 */
	private boolean storageContains(Edge pEdge)
	{
		ClassDiagramViewer classDiagramViewer = (ClassDiagramViewer) DiagramType.viewerFor(pEdge.getDiagram());
		return classDiagramViewer.storageContains(pEdge);
	}
}
