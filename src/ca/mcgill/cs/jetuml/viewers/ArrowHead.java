/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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

package ca.mcgill.cs.jetuml.viewers;

/**
 * This class defines arrowheads of various shapes.
 */
public enum ArrowHead
{
	NONE, TRIANGLE, BLACK_TRIANGLE, V, HALF_V, DIAMOND, BLACK_DIAMOND;
	
	private final ArrowHeadViewer aView = new ArrowHeadViewer(this);
	
	/**
	 * @return An object that can draw this arrowhead.
	 */
	public ArrowHeadViewer view()
	{
		return aView;
	}
}
