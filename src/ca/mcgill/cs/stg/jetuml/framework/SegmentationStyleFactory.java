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
	
	private SegmentationStyleFactory(){}
	
	/*
     * Creates the cascading structure between strategies. 1. Always check for a self-edge.
     * Otherwise, implement the main strategy. If it does not work (returns null),
     * check the alternate strategy. If that fails too, return a straight line
     * (this should never return null).
	 */
	private static SegmentationStyle genericCreateStrategy( final SegmentationStyle pMain, final SegmentationStyle pAlternate)
	{
		return new SegmentationStyle()
		{
			@Override
			public Point2D[] getPath(Node pStart, Node pEnd, Graph pGraph)
			{
				if( pStart == pEnd )
				{
					return createSelfPath(pStart);
				}
				Point2D[] path = pMain.getPath(pStart, pEnd, pGraph);
				if( path == null && pAlternate != null )
				{
					path = pAlternate.getPath(pStart, pEnd, pGraph);
				}
				if( path != null )
				{
					return path;
				}
				else
				{
					path = new Straight().getPath(pStart, pEnd, pGraph);
					assert path != null;
					return path;
				}
			}
		};
	}
	
	/**
	 * Creates a strategy to draw straight (unsegmented) 
	 * lines by choosing the connection points that induce the 
	 * shortest path between two nodes (except in the case of self-paths). 
	 * @return A strategy for creating straight lines.
	 */
	public static SegmentationStyle createStraightStrategy()
	{
		return genericCreateStrategy(new Straight(), null);
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
		return genericCreateStrategy(new HVH(), new VHV());
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
		return genericCreateStrategy(new VHV(), new HVH());
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
		public Point2D[] getPath(Node pStart, Node pEnd, Graph pGraph)
		{
		    return new Point2D[] {pStart.getConnectionPoint(computeDirection(pStart, pEnd)), 
		    		pEnd.getConnectionPoint(computeDirection(pEnd, pStart))};
		}		
	}
	
	private static class HVH implements SegmentationStyle
	{
		@Override
		public Point2D[] getPath(Node pStart, Node pEnd, Graph pGraph)
		{
			Point2D start = pStart.getConnectionPoint(Direction.EAST);
			Point2D end = pEnd.getConnectionPoint(Direction.WEST);
			
			if( start.getX() + 2* MIN_SEGMENT <= end.getX() )
			{ 	// There is enough space to create the segment, we keep this order
			}
			else if( pEnd.getConnectionPoint(Direction.EAST).getX() + 2* MIN_SEGMENT <= pStart.getConnectionPoint(Direction.WEST).getX() )
			{ 	// The segment goes in the other direction
				start = pStart.getConnectionPoint(Direction.WEST);
				end = pEnd.getConnectionPoint(Direction.EAST);
			}
			else
			{	// There is not enough space for either direction, return null.
				return null;
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
		public Point2D[] getPath(Node pStart, Node pEnd, Graph pGraph)
		{
			Point2D start = pStart.getConnectionPoint(Direction.SOUTH);
			Point2D end = pEnd.getConnectionPoint(Direction.NORTH);
			
			if( start.getY() + 2* MIN_SEGMENT <= end.getY() )
			{ 	// There is enough space to create the segment, we keep this order
			}
			else if( pEnd.getConnectionPoint(Direction.SOUTH).getY() + 2* MIN_SEGMENT <= pStart.getConnectionPoint(Direction.NORTH).getY() )
			{ 	// The segment goes in the other direction
				start = pStart.getConnectionPoint(Direction.NORTH);
				end = pEnd.getConnectionPoint(Direction.SOUTH);
			}
			else
			{	// There is not enough space for either direction, return null.
				return null;
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
	 * point given a link to pEnd. 
	 * 
	 * @param pStart The start node
	 * @param pEnd The end node
	 * @return The direction pointing to the side of pStart that
	 * should be connected.
	 */
	private static Direction computeDirection(Node pStart, Node pEnd)
	{
		Direction[] allDirections = {Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH};
		Direction bestDirection = Direction.WEST; // Placeholder
		double shortestDistance = Double.MAX_VALUE;
		for( Direction direction : allDirections )
		{
			Point2D start = pStart.getConnectionPoint(direction);
			for( Direction inner : allDirections )
			{
				Point2D end = pEnd.getConnectionPoint(inner);
				double distance = start.distance(end);
				if( distance < shortestDistance )
				{
					shortestDistance = distance;
					bestDirection = direction;
				}
			}
		}
		return bestDirection;
	}
}

