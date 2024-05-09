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
package org.jetuml.rendering.edges;

import org.jetuml.geom.GeomUtils;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.rendering.Grid;
import org.jetuml.rendering.Side;

/**
 * Represents indexed positions on the faces of nodes where edges can attach.
 * North-facing and South-facing sides of nodes have indices in range -4 to +4.
 * East and West-facing sides of nodes have indices in range -2 to +2.
 */
public enum NodeIndex 
{
	MINUS_FOUR, MINUS_THREE, MINUS_TWO, MINUS_ONE, ZERO,
	PLUS_ONE, PLUS_TWO, PLUS_THREE, PLUS_FOUR;
	
	private static final int NUM_SPACES_NS = 9; // Number of attachments spaces for north and south sides
	private static final int NUM_SPACES_EW = 5; // Number of attachments spaces for east and west sides
	
	// Space between attachment points are increments of this number
	private static final int SPACE_INCREMENT = 10;
	
	// We leave an extra marging at the end of the face when spacing out nodes
	// so the general look is a bit concentrated in the middle: purely esthetic
	private static final int MARGIN = 10;
	
	/**
	 * Returns a point on pNodeFace at the position represented by this index.
	 * @param pNodeFace a Line representing the side of pNode where the point
	 * is needed.
	 * @param pAttachmentSide the side of the node of interest
	 * @return a point on pNodeFace at the pNodeIndex position
	 * @pre pNodeFace != null
	 */
	public Point toPoint(Line pNodeFace, Side pAttachmentSide)
	{
		//determine the offset from the center point. 
		float spacing = spaceBetweenConnectionPoints(pNodeFace, pAttachmentSide);
		int offset = (int) ((ordinal() - 4) * spacing);
		
		//Determine center point and add the offset to the center point
		if(pAttachmentSide.isHorizontal())
		{
			Point center = Grid.snappedHorizontally(
					new Point(((pNodeFace.x2() - pNodeFace.x1())/2) + pNodeFace.x1(), pNodeFace.y1()));
			return new Point(center.x() + offset, center.y());
		}
		else 
		{
			Point center = Grid.snappedVertically( 
					new Point(pNodeFace.x1(), ((pNodeFace.y2() - pNodeFace.y1())/2) + pNodeFace.y1()));
			return new Point(center.x(), center.y() + offset);
		}
	}
	
	/*
	 * Determines the number of pixels in between edge connection points on 
	 * pNode. This allows the space between NodeIndex connection points
	 * to increase proportionally with the width or height of the node. 
	 * 
	 * Algorithm: at least 10, then any greater multiple of 5 if possible. 
	 * 
	 * @param pNodeFace a line representing the pAttachmentSide of a node
	 * @param pAttachmentSide A side of a node. 
	 * @return the spacing in between connection points on pNodeFace. 
	 * @pre pNodeFace != null && pAttachmentSide != null
	 */
	private static int spaceBetweenConnectionPoints(Line pNodeFace, Side pAttachmentSide)
	{
		assert pNodeFace != null && pAttachmentSide != null;
		
		// Default for horizontal
		int lengthOfSide = Math.abs(pNodeFace.x2() - pNodeFace.x1());
		int numberOfSpaces = NUM_SPACES_NS;
		
		// Adjust if vertical
		if(pAttachmentSide.isVertical())
		{
			lengthOfSide = Math.abs(pNodeFace.y2() - pNodeFace.y1());
			numberOfSpaces = NUM_SPACES_EW;
		}
		
		int unadjustedSpace = GeomUtils.round((lengthOfSide - MARGIN * 2) / (float) numberOfSpaces);
		// Closest further multiple of 10 at least 10
		int result = Math.max(SPACE_INCREMENT, unadjustedSpace / SPACE_INCREMENT * SPACE_INCREMENT);
		return result;
	}
}
