/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.framework.SegmentationStyle.Side;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.PackageNode;

/**
 * A class for creating line segmentation strategies.
 * 
 * @author Martin P. Robillard
 *
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
		
		return new Point2D[] {new Point2D.Double(x1, y1), new Point2D.Double(x2, y2),
							  new Point2D.Double(x3, y3), new Point2D.Double(x4, y4), new Point2D.Double(x5, y5)};
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
			return ((PackageNode)pNode).getTopRightCorner();
		}
		else
		{
			return new Point2D.Double(pNode.getBounds().getMaxX(), pNode.getBounds().getMinY());
		}
	}
	
	static Point2D[] connectionPoints(Node pNode)
	{
		return new Point2D[] { pNode.getConnectionPoint(Direction.WEST) ,
							   pNode.getConnectionPoint(Direction.NORTH),
							   pNode.getConnectionPoint(Direction.EAST),
							   pNode.getConnectionPoint(Direction.SOUTH)};
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
				Point2D start = pNode.getConnectionPoint(side.getDirection());
				for( Side inner : Side.values() )
				{
					Point2D end = otherNode(pEdge, pNode).getConnectionPoint(inner.getDirection());
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
		public Point2D[] getPath(Edge pEdge, Graph pGraph)
		{
			if( pEdge.getStart() == pEdge.getEnd() )
			{
				return createSelfPath(pEdge.getStart());
			}
			
//			Side startSide = computeSide(pEdge.getStart(), pEdge.getEnd());
			Side startSide = getAttachedSide(pEdge, pEdge.getStart());
			Point2D start = pEdge.getStart().getConnectionPoint(startSide.getDirection());
			if( pGraph != null )
			{
				Position position = computePosition(pEdge, startSide, pGraph, true);
				
				if( startSide.isEastWest() )
				{
					start = new Point2D.Double( start.getX(), start.getY()+ position.computeNudge(pEdge.getStart().getBounds().getHeight()));
				}
				else
				{
					start = new Point2D.Double( start.getX()+ position.computeNudge(pEdge.getStart().getBounds().getWidth()), start.getY());
				}
			}
			
//			Side endSide = computeSide(pEdge.getEnd(), pEdge.getStart());
			Side endSide = getAttachedSide(pEdge, pEdge.getEnd());
			Point2D end = pEdge.getEnd().getConnectionPoint(endSide.getDirection());
			if( pGraph != null )
			{
				Position position = computePosition(pEdge, endSide, pGraph, false);
				
				if( endSide.isEastWest() )
				{
					end = new Point2D.Double( end.getX(), end.getY()+ position.computeNudge(pEdge.getEnd().getBounds().getHeight()));
				}
				else
				{
					end = new Point2D.Double( end.getX()+ position.computeNudge(pEdge.getEnd().getBounds().getWidth()), end.getY());
				}
			}
			
		    return new Point2D[] {start, end };
		}		
	}
	
	/**
	 * Computes the relative attachment position for an edge's node endpoint:
	 * either the start node (pForward == true) or the end node (pForward == false).
	 * The position is given in terms of top-bottom for sides, and left-to-right
	 * for top and bottom. 
	 * @param pEdge The edge containing the node for which a connection is being calculated
	 * @param pSide The side of the node for which a connection is being calculated
	 * @param pGraph The graph storing the node.
	 * @param pForward true if this is the calculation for the start node of the edge
	 * @return The position on the side of the node where the edge should be connected.
	 */
	private static Position computePosition(Edge pEdge, Side pSide, Graph pGraph, boolean pForward)
	{
		Node tempTarget = pEdge.getStart();
		if( !pForward )
		{
			tempTarget = pEdge.getEnd();
		}
		final Node target = tempTarget;
		
		// Get all edges for this side of the node
		List<Edge> edgesOnSelectedSide = new ArrayList<>();
		for( Edge edge : pGraph.getEdges(target))
		{
			SegmentationStyle style = new Straight(); // Default
			if( edge instanceof ClassRelationshipEdge )
			{
				style = ((ClassRelationshipEdge)edge).obtainSegmentationStyle();
			}
			if( style.getAttachedSide(edge, target) == pSide )
			{
				edgesOnSelectedSide.add(edge);
			}
		}
		
		// Sort in terms of the position of the other node
		Collections.sort(edgesOnSelectedSide, (pEdge1, pEdge2) ->
		{
			Node otherNode1 = otherNode(pEdge1, target);
			Node otherNode2 = otherNode(pEdge2, target);
			
			if( otherNode1 == otherNode2)
			{
				// Sort by type
				int direction = pEdge1.getClass().getSimpleName().compareTo(pEdge2.getClass().getSimpleName());
				return direction;
			}
						
			if( pSide.isEastWest() )
			{		
				return (int)(otherNode1.getBounds().getCenterY() - otherNode2.getBounds().getCenterY());
			}
			else
			{
				return (int)(otherNode1.getBounds().getCenterX() - otherNode2.getBounds().getCenterX());
			}
		});
		
		return new Position(edgesOnSelectedSide.indexOf(pEdge) + 1, edgesOnSelectedSide.size());
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
			return pEdge.getStart().getConnectionPoint(Direction.EAST).getX() + 2 * MIN_SEGMENT <= 
					pEdge.getEnd().getConnectionPoint(Direction.WEST).getX();
		}
		
		/*
		 * There is room for at least two segments going left from the start node
		 * to the end node.
		 */
		private static boolean goingWest(Edge pEdge)
		{
			return pEdge.getEnd().getConnectionPoint(Direction.EAST).getX() + 2 * MIN_SEGMENT <= 
					pEdge.getStart().getConnectionPoint(Direction.WEST).getX();
		}
		
		@Override
		public boolean isPossible(Edge pEdge) 
		{
			return goingEast(pEdge) || goingWest(pEdge);
		}
		
		@Override
		public Point2D[] getPath(Edge pEdge, Graph pGraph)
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
			
			Point2D start = pEdge.getStart().getConnectionPoint(Direction.EAST);
			Point2D end = pEdge.getEnd().getConnectionPoint(Direction.WEST);
			Side startSide = Side.EAST;
			
			if( goingEast(pEdge) )
			{ 	// There is enough space to create the segment, we keep this order
			}
			else if( goingWest(pEdge) )
			{ 	// The segment goes in the other direction
				startSide = Side.WEST;	
				start = pEdge.getStart().getConnectionPoint(Direction.WEST);
				end = pEdge.getEnd().getConnectionPoint(Direction.EAST);
			}
						
			if( pGraph != null )
			{
				Position position = computePosition(pEdge, startSide, pGraph, true);
				start = new Point2D.Double( start.getX(), start.getY()+ position.computeNudge(pEdge.getStart().getBounds().getHeight()));
				position = computePosition(pEdge, startSide.flip(), pGraph, false);
				end = new Point2D.Double( end.getX(), end.getY()+ position.computeNudge(pEdge.getEnd().getBounds().getHeight()));
			}
			
	  		if(Math.abs(start.getY() - end.getY()) <= MIN_SEGMENT)
	  		{
	  			return new Point2D[] {new Point2D.Double(start.getX(), end.getY()), new Point2D.Double(end.getX(), end.getY()) };
	  		}
	  		else
	  		{
	  			return new Point2D[] { new Point2D.Double(start.getX(), start.getY()), 
	  								   new Point2D.Double((start.getX() + end.getX()) / 2, start.getY()),
	  								   new Point2D.Double((start.getX() + end.getX()) / 2, end.getY()), 
	  								   new Point2D.Double(end.getX(), end.getY())};
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
			return pEdge.getStart().getConnectionPoint(Direction.SOUTH).getY() + 2 * MIN_SEGMENT <= 
					pEdge.getEnd().getConnectionPoint(Direction.NORTH).getY();
		}
		
		/*
		 * There is room for at least two segments going up from the start node
		 * to the end node.
		 */
		private static boolean goingNorth(Edge pEdge)
		{
			return pEdge.getEnd().getConnectionPoint(Direction.SOUTH).getY() + 2 * MIN_SEGMENT <= 
					pEdge.getStart().getConnectionPoint(Direction.NORTH).getY();
		}
		
		@Override
		public boolean isPossible(Edge pEdge)
		{	
			return goingSouth(pEdge) || goingNorth(pEdge);
		}
		
		@Override
		public Point2D[] getPath(Edge pEdge, Graph pGraph)
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
			
			Point2D start = pEdge.getStart().getConnectionPoint(Direction.SOUTH);
			Point2D end = pEdge.getEnd().getConnectionPoint(Direction.NORTH);
			Side startSide = Side.SOUTH;
			
			if( start.getY() + 2* MIN_SEGMENT <= end.getY() )
			{ 	// There is enough space to create the segment, we keep this order
			}
			else if( pEdge.getEnd().getConnectionPoint(Direction.SOUTH).getY() + 
					2 * MIN_SEGMENT <= pEdge.getStart().getConnectionPoint(Direction.NORTH).getY() )
			{ 	// The segment goes in the other direction
				startSide = Side.NORTH;
				start = pEdge.getStart().getConnectionPoint(Direction.NORTH);
				end = pEdge.getEnd().getConnectionPoint(Direction.SOUTH);
			}
			
			if( pGraph != null )
			{
				Position position = computePosition(pEdge, startSide, pGraph, true);
				start = new Point2D.Double( start.getX() + position.computeNudge(pEdge.getStart().getBounds().getWidth()), start.getY());
				position = computePosition(pEdge, startSide.flip(), pGraph, false);
				end = new Point2D.Double( end.getX()+ position.computeNudge(pEdge.getEnd().getBounds().getWidth()), end.getY());
			}
			
	  		if(Math.abs(start.getX() - end.getX()) <= MIN_SEGMENT)
	  		{
	  			return new Point2D[] {new Point2D.Double(end.getX(), start.getY()), new Point2D.Double(end.getX(), end.getY())};
	  		}
	  		else
	  		{
	  			return new Point2D[] {new Point2D.Double(start.getX(), start.getY()), 
	  								  new Point2D.Double(start.getX(), (start.getY() + end.getY()) / 2), 
	  								  new Point2D.Double(end.getX(), (start.getY() + end.getY()) / 2), 
	  								  new Point2D.Double(end.getX(), end.getY())};
	  		}
		}
	}
	
	/*
	 * Computes which side of pStart should contain the connection
	 * point given a link to pEnd. This method works independently
	 * of the actual directionality of the edge, so it does not matter
	 * if pStart is actually the start or end node of the edge, or 
	 * vice versa.
	 * 
	 * @param pTarget The target node
	 * @param pOther The other connected node
	 * @return The side of pTarget that should be connected.
	 */
//	private static Side computeSide(Node pTarget, Node pOther)
//	{
//		Side bestSide = Side.WEST; // Placeholder
//		double shortestDistance = Double.MAX_VALUE;
//		for( Side side : Side.values() )
//		{
//			Point2D start = pTarget.getConnectionPoint(side.getDirection());
//			for( Side inner : Side.values() )
//			{
//				Point2D end = pOther.getConnectionPoint(inner.getDirection());
//				double distance = start.distance(end);
//				if( distance < shortestDistance )
//				{
//					shortestDistance = distance;
//					bestSide = side;
//				}
//			}
//		}
//		return bestSide;
//	}
	
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

