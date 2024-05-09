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

package org.jetuml.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.builder.CompoundOperation;
import org.jetuml.diagram.builder.DiagramBuilder;
import org.jetuml.geom.Rectangle;

/**
 * Helper class for the DiagramCanvas that computes the difference in position
 * of diagram nodes being moved.
 */
public final class MoveTracker
{
	private final List<Node> aTrackedNodes = new ArrayList<>();
	private final List<Rectangle> aOriginalBounds = new ArrayList<>();
	private final Function<Node, Rectangle> aBoundsCalculator;
	
	/**
	 * Creates a new move tracker.
	 * 
	 * @param pBoundsCalculator A function to return the bounds of a diagram element.
	 */
	public MoveTracker(Function<Node, Rectangle> pBoundsCalculator)
	{
		aBoundsCalculator = pBoundsCalculator;
	}
	
	/**
	 * Records the elements in pElements and their current position.
	 * 
	 * @param pElements The elements that are being moved. 
	 * @pre pElements != null
	 */
	public void start(Iterable<DiagramElement> pElements)
	{
		assert pElements != null;
		
		aTrackedNodes.clear();
		aOriginalBounds.clear();
		
		for(DiagramElement element : pElements)
		{
			assert element != null;
			if(element instanceof Node node)
			{
				aTrackedNodes.add(node);
				aOriginalBounds.add(aBoundsCalculator.apply(node));
			}
		}
	}

	/**
	 * Creates and returns a CompoundOperation that represents the movement
	 * of all tracked nodes between the time where startTrackingMove was 
	 * called and the time endTrackingMove was called.
	 * 
	 * @return A CompoundCommand describing the move.
	 */
	public CompoundOperation stop()
	{
		CompoundOperation operation = new CompoundOperation();
		Rectangle[] selectionBounds2 = new Rectangle[aOriginalBounds.size()];
		int i = 0;
		for(Node node : aTrackedNodes)
		{
			selectionBounds2[i] = aBoundsCalculator.apply(node);
			i++;
		}
		for(i = 0; i < aOriginalBounds.size(); i++)
		{
			int dY = selectionBounds2[i].y() - aOriginalBounds.get(i).y();
			int dX = selectionBounds2[i].x() - aOriginalBounds.get(i).x();
			if(dX != 0 || dY != 0)
			{
				operation.add(DiagramBuilder.createMoveNodeOperation(aTrackedNodes.get(i), dX, dY));
			}
		}
		return operation;
	}
}