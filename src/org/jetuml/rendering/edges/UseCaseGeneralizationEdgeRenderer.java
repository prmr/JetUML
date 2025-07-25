/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
package org.jetuml.rendering.edges;

import org.jetuml.rendering.ArrowHead;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;

/**
 * A straight solid line with a triangle end decoration.
 */
public final class UseCaseGeneralizationEdgeRenderer extends StraightEdgeRenderer
{	
	/**
	 * Creates a viewer for UseCaseGeneralizationEdge instances.
	 */
	public UseCaseGeneralizationEdgeRenderer(DiagramRenderer pParent)
	{
		super(pParent, LineStyle.SOLID, ArrowHead.TRIANGLE);
	}
}