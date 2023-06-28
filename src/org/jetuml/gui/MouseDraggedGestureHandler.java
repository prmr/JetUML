/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020-2023 by McGill University.
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
package org.jetuml.gui;

import org.jetuml.geom.Direction;
import org.jetuml.geom.Rectangle;

/**
 * An object that can respond to the mouse being dragged.
 */
public interface MouseDraggedGestureHandler
{
	/**
	 * Indicates that a gesture moves to a point pTo.
	 * 
	 * @param pBounds Bounding box of the objects being dragged, before they are moved
	 * @param pDirection The direction of the drag gesture
	 */
	void interactionTo(Rectangle pBounds, Direction pDirection);
}
