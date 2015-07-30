package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.geom.Rectangle2D;

/**
 * A type that allows us to tread nodes and edges uniformly.
 * 
 * @author Martin P. Robillard
 */
public interface GraphElement extends Cloneable
{
	/**
     * Gets the smallest rectangle that bounds this graph element.
     * The bounding rectangle contains all labels.
     * @return the bounding rectangle
   	 */
   	Rectangle2D getBounds();
}
