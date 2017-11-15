package ca.mcgill.cs.stg.jetuml.graph.edges.views;

import java.awt.Graphics2D;

import ca.mcgill.cs.stg.jetuml.geom.Line;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.edges.Edge;

/**
 * An object capable of computing the actual geometry
 * of an edge and drawing it on a graphics context.
 * 
 * @author Martin P. Robillard
 *
 */
public interface EdgeView extends Cloneable
{
	/**
     * Gets the smallest rectangle that bounds this edge.
     * The bounding rectangle contains all labels.
     * @return the bounding rectangle
   	 */
	Rectangle getBounds();
   	
	/**
     * Draw the edge.
     * @param pGraphics2D the graphics context
	 */
   	void draw(Graphics2D pGraphics2D);
   	
   	/**
     * Tests whether the edge contains a point.
     * @param pPoint the point to test
     * @return true if this edge contains aPoint
     */
   	boolean contains(Point pPoint);

   	/**
     * Gets the points at which this edge is connected to
     * its nodes.
     * @return a line joining the two connection points
     */
   	Line getConnectionPoints();
   	
   	/**
   	 * @param pEdge The edge to wrap.
   	 * @return A copy of this EdgeView that wraps pEdge.
   	 */
   	EdgeView copy(Edge pEdge);
}
