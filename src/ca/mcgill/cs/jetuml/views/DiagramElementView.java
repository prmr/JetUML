/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.views;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object capable of computing the actual geometry
 * of a diagram element and draw it on a graphics context.
 */
public interface DiagramElementView
{
	/**
     * Gets the smallest rectangle that bounds this element.
     * The bounding rectangle contains all labels.
     * @return the bounding rectangle
   	 */
	Rectangle getBounds();
   	
	/**
     * Draw the element.
     * @param pGraphics the graphics context
	 */
   	void draw(GraphicsContext pGraphics);
   	
   	/**
     * Draw selection handles around the element.
     * @param pGraphics the graphics context
	 */
   	void drawSelectionHandles(GraphicsContext pGraphics);
   	
   	/**
     * Tests whether the element contains a point.
     * @param pPoint the point to test
     * @return true if this element contains aPoint
     */
   	boolean contains(Point pPoint);
}
