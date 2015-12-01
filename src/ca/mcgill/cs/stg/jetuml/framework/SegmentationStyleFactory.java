/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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
	
	private static SegmentationStyle genericCreateStrategy( final SegmentationStyle pMain, final SegmentationStyle pAlternate)
	{
		return new SegmentationStyle()
		{
			@Override
			public Point2D[] getPath(Node pStart, Node pEnd)
			{
				if( pStart == pEnd )
				{
					return createSelfPath(pStart);
				}
				Point2D[] path = pMain.getPath(pStart, pEnd);
				if( path == null && pAlternate != null )
				{
					path = pAlternate.getPath(pStart, pEnd);
				}
				if( path != null )
				{
					return path;
				}
				else
				{
					path = new Straight().getPath(pStart, pEnd);
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
		public Point2D[] getPath(Node pStart, Node pEnd)
		{
			if( pStart == pEnd )
			{
				return createSelfPath(pStart);
			}
			Point2D[] startConnectionPoints = connectionPoints(pStart);
		    Point2D[] endConnectionPoints = connectionPoints(pEnd);
		    Point2D start = startConnectionPoints[0];
		    Point2D end = endConnectionPoints[0];
		    double distance = start.distance(end);
		    
		    for( Point2D startPoint : startConnectionPoints)
		    {
		    	for( Point2D endPoint : endConnectionPoints )
		    	{
		    		double newDistance = startPoint.distance(endPoint);
		    		if( newDistance < distance )
		    		{
		    			start = startPoint;
		    			end = endPoint;
		    			distance = newDistance;
		    		}
		    	}
		    }
		    return new Point2D[] {start, end};
		}		
	}
	
	private static class HVH implements SegmentationStyle
	{
		@Override
		public Point2D[] getPath(Node pStart, Node pEnd)
		{
			if( pStart == pEnd )
			{
				return createSelfPath(pStart);
			}
			double x1;
	  		double x2;
	  		double y1 = pStart.getBounds().getCenterY();
	  		double y2 = pEnd.getBounds().getCenterY();
	  		if(pStart.getBounds().getMaxX() + 2 * MIN_SEGMENT <= pEnd.getBounds().getX())
	  		{
	  			x1 = pStart.getBounds().getMaxX();
	  			x2 = pEnd.getBounds().getX();
	  		}
	  		else if(pEnd.getBounds().getMaxX() + 2 * MIN_SEGMENT <= pStart.getBounds().getX())
	  		{
	  			x1 = pStart.getBounds().getX();
	  			x2 = pEnd.getBounds().getMaxX();
	  		}
	  		else 
	  		{
				return null;
			}
	  		if(Math.abs(y1 - y2) <= MIN_SEGMENT)
	  		{
	  			return new Point2D[] {new Point2D.Double(x1, y2), new Point2D.Double(x2, y2) };
	  		}
	  		else
	  		{
	  			return new Point2D[] { new Point2D.Double(x1, y1), new Point2D.Double((x1 + x2) / 2, y1),
	  				new Point2D.Double((x1 + x2) / 2, y2), new Point2D.Double(x2, y2)};
	  		}
		}
	}
	
	private static class VHV implements SegmentationStyle
	{
		@Override
		public Point2D[] getPath(Node pStart, Node pEnd)
		{
			if( pStart == pEnd )
			{
				return createSelfPath(pStart);
			}
			double x1 = pStart.getBounds().getCenterX();
	  		double x2 = pEnd.getBounds().getCenterX();
	  		double y1;
	  		double y2;
	  		if(pStart.getBounds().getMaxY() + 2 * MIN_SEGMENT <= pEnd.getBounds().getY())
	  		{
	  			y1 = pStart.getBounds().getMaxY();
	  			y2 = pEnd.getBounds().getY();
	  		}
	  		else if(pEnd.getBounds().getMaxY() + 2 * MIN_SEGMENT <= pStart.getBounds().getY())
	  		{
	  			y1 = pStart.getBounds().getY();
	  			y2 = pEnd.getBounds().getMaxY();
	  		}
	  		else 
	  		{
				return null;
			}
	  		if(Math.abs(x1 - x2) <= MIN_SEGMENT)
	  		{
	  			return new Point2D[] {new Point2D.Double(x2, y1), new Point2D.Double(x2, y2)};
	  		}
	  		else
	  		{
	  			return new Point2D[] {new Point2D.Double(x1, y1), new Point2D.Double(x1, (y1 + y2) / 2), 
	  					new Point2D.Double(x2, (y1 + y2) / 2), new Point2D.Double(x2, y2)};
	  		}
		}
	}
}

