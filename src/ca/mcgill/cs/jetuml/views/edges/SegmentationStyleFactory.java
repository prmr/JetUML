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

package ca.mcgill.cs.jetuml.views.edges;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ClassRelationshipEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge.Type;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.views.edges.SegmentationStyle.Side;
import ca.mcgill.cs.jetuml.views.nodes.PackageNodeView;
import javafx.geometry.Point2D;

/**
 * A class for creating line segmentation strategies.
 */
public final class SegmentationStyleFactory
{
	private static final int MARGIN = 20;
	private static final int MIN_SEGMENT = 10;
	private static final int MAX_NUDGE = 11;

	private SegmentationStyleFactory(){}
	
	/**
	 * Creates a strategy to draw straight (unsegmented) 
	 * lines by choosing the connection points that induce the 
	 * shortest path between two nodes (except in the case of self-paths). 
	 * @return A strategy for creating straight lines.
	 */
	public static SegmentationStyle createStraightStrategy()
	{
		return new Straight();
	}
	
	/**
	 * Creates a strategy that attempts to create horizontal links between
	 * nodes (except in the case of self-edges). If the node geometry
	 * does not permit it, attempts to use the VHV style and, if that
	 * still does not work, resorts to the straight style.
	 * @return A strategy for creating lines according to the HVH style.
	 */
	public static SegmentationStyle createHVHStrategy()
	{
		return new HVH();
	}
	
	/**
	 * Creates a strategy that attempts to create vertical links between
	 * nodes (except in the case of self-edges). If the node geometry
	 * does not permit it, attempts to use the HVH style and, if that
	 * still does not work, resorts to the straight style.
	 * @return A strategy for creating lines according to the VHV style.
	 */
	public static SegmentationStyle createVHVStrategy()
	{
		return new VHV();
	}
	
	/*
	 * The idea for creating a self path is to find the top left corner of 
	 * the actual figure and walk back N pixels away from it.
	 * Assumes that pNode is composed of rectangles with sides at least
	 * N wide.
	 */
	private static Point2D[] createSelfPath(Node pNode)
	{
		Point2D topRight = findTopRightCorner(pNode);
		double x1 = topRight.getX() - MARGIN;
		double y1 = topRight.getY();
		double x2 = x1;
		double y2 = y1 - MARGIN;
		double x3 = x2 + MARGIN * 2;
		double y3 = y2;
		double x4 = x3;
		double y4 = topRight.getY() + MARGIN;
		double x5 = topRight.getX();
		double y5 = y4;
		
		return new Point2D[] {new Point2D(x1, y1), new Point2D(x2, y2),
							  new Point2D(x3, y3), new Point2D(x4, y4), new Point2D(x5, y5)};
	}
	
	/*
	 * This solution is very complex if we can't assume any knowledge
	 * of Node types and only rely on getConnectionPoints, but it can
	 * be made quite optimal in exchange for an unpretty dependency to
	 * specific node types.
	 */
	private static Point2D findTopRightCorner(Node pNode)
	{
		if( pNode instanceof PackageNode )
		{
			return Conversions.toPoint2D(((PackageNodeView)((PackageNode)pNode).view()).getTopRightCorner());
		}
		else
		{
			return new Point2D(pNode.view().getBounds().getMaxX(), pNode.view().getBounds().getY());
		}
	}
	
	private static class Straight implements SegmentationStyle
	{
		@Override
		public Side getAttachedSide(Edge pEdge, Node pNode)
		{
			Side bestSide = Side.WEST; // Placeholder
			double shortestDistance = Double.MAX_VALUE;
			for( Side side : Side.values() )
			{
				Point start = pNode.view().getConnectionPoint(side.getDirection());
				for( Side inner : Side.values() )
				{
					Point end = otherNode(pEdge, pNode).view().getConnectionPoint(inner.getDirection());
					double distance = start.distance(end);
					if( distance < shortestDistance )
					{
						shortestDistance = distance;
						bestSide = side;
					}
				}
			}
			return bestSide;
		}
		
		@Override
		public boolean isPossible(Edge pEdge) 
		{
			return true;
		}
		
		@Override
		public Point2D[] getPath(Edge pEdge, Diagram pGraph)
		{
			if( pEdge.getStart() == pEdge.getEnd() )
			{
				return createSelfPath(pEdge.getStart());
			}
			
			Side startSide = getAttachedSide(pEdge, pEdge.getStart());
			Point start = pEdge.getStart().view().getConnectionPoint(startSide.getDirection());
			if( pGraph != null )
			{
				start = computePointPosition(pEdge.getStart(), startSide, computePosition(pEdge, startSide, pGraph, true), pGraph);
			}
			
			Side endSide = getAttachedSide(pEdge, pEdge.getEnd());
			Point end = pEdge.getEnd().view().getConnectionPoint(endSide.getDirection());
			if( pGraph != null )
			{
				end = computePointPosition(pEdge.getEnd(), endSide, computePosition(pEdge, endSide, pGraph, false), pGraph);
			}
			
		    return new Point2D[] {Conversions.toPoint2D(start), Conversions.toPoint2D(end) };
		}		
	}
	
	/*
	 * Compute the point where to attach an edge in position pPosition on side pSide of node pNode
	 */
	private static Point computePointPosition(Node pNode, Side pSide, Position pPosition, Diagram pGraph)
	{
		assert pNode != null && pSide != null && pPosition != null && pGraph != null;
		Point start = pNode.view().getConnectionPoint(pSide.getDirection());
		if( pSide.isEastWest() )
		{
			double yPosition = start.getY()+ pPosition.computeNudge(pNode.view().getBounds().getHeight()); // Default
			if( hasSelfEdge(pNode, pGraph) && pSide == Side.EAST )
			{
				double increment = (pNode.view().getBounds().getHeight() - MARGIN) / (pPosition.aTotal+1);
				yPosition = pNode.view().getBounds().getY() + MARGIN + pPosition.getIndex() * increment;
			}
			return new Point( start.getX(), (int) Math.round(yPosition));	
		}
		else
		{
			double xPosition = start.getX()+ pPosition.computeNudge(pNode.view().getBounds().getWidth());
			if( hasSelfEdge(pNode, pGraph) && pSide == Side.NORTH )
			{
				double increment = (pNode.view().getBounds().getWidth() - MARGIN) / (pPosition.aTotal+1);
				xPosition = pNode.view().getBounds().getX() + pPosition.getIndex() * increment;
			}
			return new Point( (int) Math.round(xPosition), start.getY());
		}
	}
	
	private static boolean hasSelfEdge(Node pNode, Diagram pGraph)
	{
		for( Edge edge : pGraph.edgesConnectedTo(pNode))
		{
			if( edge.getStart() == edge.getEnd())
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Computes the relative attachment position for an edge's node endpoint:
	 * either the start node (pForward == true) or the end node (pForward == false).
	 * The position is given in terms of top-bottom for sides, and left-to-right
	 * for top and bottom. 
	 * @param pEdge The edge containing the node for which a connection is being calculated
	 * @param pStartSide The side of the node for which a connection is being calculated
	 * @param pGraph The graph storing the node.
	 * @param pForward true if this is the calculation for the start node of the edge
	 * @return The position on the side of the node where the edge should be connected.
	 */
	private static Position computePosition(Edge pEdge, Side pStartSide, Diagram pGraph, boolean pForward)
	{
		assert pEdge != null && pStartSide != null && pGraph != null;
		Node tempTarget = pEdge.getStart();
		if( !pForward )
		{
			tempTarget = pEdge.getEnd();
		}
		final Node target = tempTarget;
		List<Edge> edgesOnSelectedSide = getAllEdgesForSide(pGraph, target, pStartSide);
		sortPositions(edgesOnSelectedSide, target, pStartSide);
		
		// Group identical edge ends
		List<Edge> finalPositions = new ArrayList<>();
		int index = -1;
		for( Edge edge : edgesOnSelectedSide )
		{
			int aggregated = -1;
			for( Edge classifiedEdge : finalPositions )
			{
				if( canAggregate(edge, classifiedEdge, target))
				{
					aggregated = finalPositions.indexOf(classifiedEdge);
					break;
				}
			}
			if( aggregated < 0 )
			{
				finalPositions.add(edge);
				aggregated = finalPositions.size() - 1;
			}
			if( edge == pEdge )
			{
				index = aggregated;
			}
		}
		return new Position(index + 1, finalPositions.size());
	}
	
	// CSOFF:
	private static boolean canAggregate(Edge pEdge1, Edge pEdge2, Node pTarget)
	{
		if( pEdge1.getEnd() == pTarget && pEdge2.getEnd() == pTarget &&
				pEdge1 instanceof GeneralizationEdge && pEdge2 instanceof GeneralizationEdge &&
				((GeneralizationEdge)pEdge1).getType() == ((GeneralizationEdge)pEdge2).getType())
		{
			return true;
		}
		else if( pEdge1.getStart() == pTarget && pEdge2.getStart() == pTarget && 
				pEdge1 instanceof AggregationEdge && pEdge2 instanceof AggregationEdge &&
				((AggregationEdge)pEdge1).getType() == Type.Aggregation &&
				((AggregationEdge)pEdge2).getType() == Type.Aggregation)
		{
			return true;
		}
		else if( pEdge1.getStart() == pTarget && pEdge2.getStart() == pTarget && 
				pEdge1 instanceof AggregationEdge && pEdge2 instanceof AggregationEdge &&
				((AggregationEdge)pEdge1).getType() == Type.Composition &&
				((AggregationEdge)pEdge2).getType() == Type.Composition)
		{
			return true;
		}
		else
		{
			return false;
		}
	} // CSON:
	
	// TODO Fix this
	private static List<Edge> getAllEdgesForSide(Diagram pGraph, Node pTarget, Side pSide)
	{
		List<Edge> edgesOnSelectedSide = new ArrayList<>();
		for( Edge edge : pGraph.edgesConnectedTo(pTarget))
		{
			if( otherNode(edge, pTarget) == pTarget)
			{
				continue; // Do not count self-edges
			}
			if( !(edge instanceof ClassRelationshipEdge))
			{
				continue;
			}
			if( edge instanceof ClassRelationshipEdge && 
					((ClassRelationshipEdge)edge).obtainSegmentationStyle().getAttachedSide(edge, pTarget) == pSide )
			{
				edgesOnSelectedSide.add(edge);
			}
		}
		return edgesOnSelectedSide;
	}
	
	// Sort in terms of the position of the other node
	private static void sortPositions(List<Edge> pEdges, Node pTarget, Side pSide)
	{
		Collections.sort(pEdges, (pEdge1, pEdge2) ->
		{
			Node otherNode1 = otherNode(pEdge1, pTarget);
			Node otherNode2 = otherNode(pEdge2, pTarget);
			
			if( otherNode1 == otherNode2)
			{
				// Sort by type
				int direction = pEdge1.getClass().getSimpleName().compareTo(pEdge2.getClass().getSimpleName());
				return direction;
			}
						
			if( pSide.isEastWest() )
			{		
				return (int)(otherNode1.view().getBounds().getCenter().getY() - otherNode2.view().getBounds().getCenter().getY());
			}
			else
			{
				return (int)(otherNode1.view().getBounds().getCenter().getX() - otherNode2.view().getBounds().getCenter().getX());
			}
		});
	}
	
	private static Node otherNode(Edge pEdge, Node pNode)
	{
		if( pEdge.getStart() == pNode)
		{
			return pEdge.getEnd();
		}
		else
		{
			return pEdge.getStart();
		}
	}
	
	private static class HVH implements SegmentationStyle
	{
		@Override
		public Side getAttachedSide(Edge pEdge, Node pNode)
		{
			Side lReturn = Side.WEST; // Placeholder
			if( pEdge.getStart() == pEdge.getEnd() )
			{
				if( pNode == pEdge.getStart() )
				{
					return Side.NORTH;
				}
				else
				{
					return Side.EAST;
				}
			}
			if( goingEast(pEdge) )
			{
				if( pNode == pEdge.getStart() )
				{
					lReturn = Side.EAST;
				}
				else
				{
					lReturn = Side.WEST;
				}
			}
			else if( goingWest(pEdge) )
			{
				if( pNode == pEdge.getStart() )
				{
					lReturn = Side.WEST;
				}
				else
				{
					lReturn = Side.EAST;
				}
			}
			else
			{
				SegmentationStyle vhv = new VHV();
				if( vhv.isPossible(pEdge) )
				{
					lReturn = vhv.getAttachedSide(pEdge, pNode);
				}
				else
				{
					lReturn = new Straight().getAttachedSide(pEdge, pNode);
				}
			}
			return lReturn;
		}
		
		/*
		 * There is room for at least two segments going right from the start node
		 * to the end node.
		 */
		private static boolean goingEast(Edge pEdge)
		{
			return pEdge.getStart().view().getConnectionPoint(Direction.EAST).getX() + 2 * MIN_SEGMENT <= 
					pEdge.getEnd().view().getConnectionPoint(Direction.WEST).getX();
		}
		
		/*
		 * There is room for at least two segments going left from the start node
		 * to the end node.
		 */
		private static boolean goingWest(Edge pEdge)
		{
			return pEdge.getEnd().view().getConnectionPoint(Direction.EAST).getX() + 2 * MIN_SEGMENT <= 
					pEdge.getStart().view().getConnectionPoint(Direction.WEST).getX();
		}
		
		@Override
		public boolean isPossible(Edge pEdge) 
		{
			return goingEast(pEdge) || goingWest(pEdge);
		}
		
		@Override
		public Point2D[] getPath(Edge pEdge, Diagram pGraph)
		{
			assert pEdge != null;
			
			if( pEdge.getStart() == pEdge.getEnd() )
			{
				return createSelfPath(pEdge.getStart());
			}
			if( !isPossible(pEdge) )
			{
				SegmentationStyle alternate = new VHV();
				if( alternate.isPossible(pEdge))
				{
					return alternate.getPath(pEdge, pGraph);
				}
				else
				{
					return new Straight().getPath(pEdge, pGraph);
				}
			}
			
			Point start = pEdge.getStart().view().getConnectionPoint(Direction.EAST);
			Point end = pEdge.getEnd().view().getConnectionPoint(Direction.WEST);
			Side startSide = Side.EAST;
			
			if( goingEast(pEdge) )
			{ 	// There is enough space to create the segment, we keep this order
			}
			else if( goingWest(pEdge) )
			{ 	// The segment goes in the other direction
				startSide = Side.WEST;	
				start = pEdge.getStart().view().getConnectionPoint(Direction.WEST);
				end = pEdge.getEnd().view().getConnectionPoint(Direction.EAST);
			}
						
			if( pGraph != null )
			{
				start = computePointPosition(pEdge.getStart(), startSide, computePosition(pEdge, startSide, pGraph, true), pGraph);
				end = computePointPosition(pEdge.getEnd(), startSide.flip(), computePosition(pEdge, startSide.flip(), pGraph, false), pGraph);
			}
			
	  		if(Math.abs(start.getY() - end.getY()) <= MIN_SEGMENT)
	  		{
	  			return new Point2D[] {new Point2D(start.getX(), end.getY()), new Point2D(end.getX(), end.getY()) };
	  		}
	  		else
	  		{
	  			return new Point2D[] { new Point2D(start.getX(), start.getY()), 
	  								   new Point2D((start.getX() + end.getX()) / 2, start.getY()),
	  								   new Point2D((start.getX() + end.getX()) / 2, end.getY()), 
	  								   new Point2D(end.getX(), end.getY())};
	  		}
		}
	}
	
	private static class VHV implements SegmentationStyle
	{
		@Override
		public Side getAttachedSide(Edge pEdge, Node pNode)
		{
			Side lReturn = Side.SOUTH; // Placeholder
			if( pEdge.getStart() == pEdge.getEnd() )
			{
				if( pNode == pEdge.getStart() )
				{
					return Side.NORTH;
				}
				else
				{
					return Side.EAST;
				}
			}
			if( goingSouth(pEdge) )
			{
				if( pNode == pEdge.getStart() )
				{
					lReturn = Side.SOUTH;
				}
				else
				{
					lReturn = Side.NORTH;
				}
			}
			else if( goingNorth(pEdge) )
			{
				if( pNode == pEdge.getStart() )
				{
					lReturn = Side.NORTH;
				}
				else
				{
					lReturn = Side.SOUTH;
				}
			}
			else
			{
				SegmentationStyle hvh = new HVH();
				if( hvh.isPossible(pEdge) )
				{
					lReturn = hvh.getAttachedSide(pEdge, pNode);
				}
				else
				{
					lReturn = new Straight().getAttachedSide(pEdge, pNode);
				}
			}
			return lReturn;
		}
		
		/*
		 * There is room for at least two segments going down from the start node
		 * to the end node.
		 */
		private static boolean goingSouth(Edge pEdge)
		{
			return pEdge.getStart().view().getConnectionPoint(Direction.SOUTH).getY() + 2 * MIN_SEGMENT <= 
					pEdge.getEnd().view().getConnectionPoint(Direction.NORTH).getY();
		}
		
		/*
		 * There is room for at least two segments going up from the start node
		 * to the end node.
		 */
		private static boolean goingNorth(Edge pEdge)
		{
			return pEdge.getEnd().view().getConnectionPoint(Direction.SOUTH).getY() + 2 * MIN_SEGMENT <= 
					pEdge.getStart().view().getConnectionPoint(Direction.NORTH).getY();
		}
		
		@Override
		public boolean isPossible(Edge pEdge)
		{	
			return goingSouth(pEdge) || goingNorth(pEdge);
		}
		
		@Override
		public Point2D[] getPath(Edge pEdge, Diagram pGraph)
		{
			assert pEdge != null;
			
			if( pEdge.getStart() == pEdge.getEnd() )
			{
				return createSelfPath(pEdge.getStart());
			}
			if( !isPossible(pEdge) )
			{
				SegmentationStyle alternate = new HVH();
				if( alternate.isPossible(pEdge))
				{
					return alternate.getPath(pEdge, pGraph);
				}
				else
				{
					return new Straight().getPath(pEdge, pGraph);
				}
			}
			
			Point start = pEdge.getStart().view().getConnectionPoint(Direction.SOUTH);
			Point end = pEdge.getEnd().view().getConnectionPoint(Direction.NORTH);
			Side startSide = Side.SOUTH;
			
			if( start.getY() + 2* MIN_SEGMENT <= end.getY() )
			{ 	// There is enough space to create the segment, we keep this order
			}
			else if( pEdge.getEnd().view().getConnectionPoint(Direction.SOUTH).getY() + 
					2 * MIN_SEGMENT <= pEdge.getStart().view().getConnectionPoint(Direction.NORTH).getY() )
			{ 	// The segment goes in the other direction
				startSide = Side.NORTH;
				start = pEdge.getStart().view().getConnectionPoint(Direction.NORTH);
				end = pEdge.getEnd().view().getConnectionPoint(Direction.SOUTH);
			}
			
			if( pGraph != null )
			{
				start = computePointPosition(pEdge.getStart(), startSide, computePosition(pEdge, startSide, pGraph, true), pGraph);
				end = computePointPosition(pEdge.getEnd(), startSide.flip(), computePosition(pEdge, startSide.flip(), pGraph, false), pGraph);
			}
			
	  		if(Math.abs(start.getX() - end.getX()) <= MIN_SEGMENT)
	  		{
	  			return new Point2D[] {new Point2D(end.getX(), start.getY()), new Point2D(end.getX(), end.getY())};
	  		}
	  		else
	  		{
	  			return new Point2D[] {new Point2D(start.getX(), start.getY()), 
	  								  new Point2D(start.getX(), (start.getY() + end.getY()) / 2), 
	  								  new Point2D(end.getX(), (start.getY() + end.getY()) / 2), 
	  								  new Point2D(end.getX(), end.getY())};
	  		}
		}
	}
	
	/** 
	 * Indicates the total number of connection points
	 * on the side of a rectangular node, and the index
	 * of a node. Immutable. The index starts at 1.
	 */
	private static class Position
	{
		private int aIndex;
		private int aTotal;
		
		Position( int pIndex, int pTotal)
		{
			aIndex = pIndex;
			aTotal = pTotal;
		}
		
		int getIndex()
		{
			return aIndex;
		}
		
		/* Returns the index in the middle of the series */
		private double getMiddle()
		{
			return ((double)aTotal +1 )/2.0;
		}
		
		/* Returns the nudge value for a position */
		double computeNudge(double pMaxWidth)
		{
			double increment = MAX_NUDGE;
			double availableSpace = pMaxWidth - (2 * MAX_NUDGE  );
			if( (aTotal - 2) * MAX_NUDGE > availableSpace )
			{
				increment = availableSpace / (aTotal - 1);
			}
			return -(getMiddle()-getIndex()) * increment;
		}
		
		public String toString()
		{
			return aIndex + " of " + aTotal;
		}
	}
}

