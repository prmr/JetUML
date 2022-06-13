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
 *******************************************************************************/
package org.jetuml.viewers.edges;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;

import javafx.scene.canvas.Canvas;

/**
 * An object that can be used to render a diagram element.
 */
public interface DiagramElementRenderer
{
	/**
	 * Returns an icon that represents pElement.
	 * @param pElement The element for which we need an icon.
	 * @return A canvas object on which the icon is painted.
	 * @pre pElement != null
	 */
	Canvas createIcon(DiagramElement pElement);
	
	/**
     * Gets the smallest rectangle that bounds the element.
     * The bounding rectangle contains all labels.
     * @param pElement The element whose bounds we wish to compute.
     * @return the bounding rectangle
     * @pre pElement != null
   	 */
	Rectangle getBounds(DiagramElement pElement);
	
  	/**
     * Tests whether pElement contains a point.
     * @param pElement the element to test
     * @param pPoint the point to test
     * @return true if this element contains aPoint
     */
   	boolean contains(DiagramElement pElement, Point pPoint);
}