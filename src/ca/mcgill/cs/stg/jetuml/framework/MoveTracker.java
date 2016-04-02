/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.commands.CompoundCommand;
import ca.mcgill.cs.stg.jetuml.commands.MoveCommand;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Tracks the movement of a set of selected graph elements.
 * 
 * @author Martin P. Robillard
 */
public class MoveTracker 
{
	private List<Node> aTrackedNodes = new ArrayList<>();
	private List<Rectangle2D> aOriginalBounds = new ArrayList<>();

	/**
	 * Records the elements in pSelectedElements and their position at the 
	 * time where the method is called.
	 * 
	 * @param pSelectedElements The elements that are being moved. Not null.
	 */
	public void startTrackingMove(SelectionList pSelectedElements)
	{
		assert pSelectedElements != null;
		
		aTrackedNodes.clear();
		aOriginalBounds.clear();
		
		for(GraphElement element : pSelectedElements)
		{
			assert element != null;
			if(element instanceof Node)
			{
				aTrackedNodes.add((Node) element);
				aOriginalBounds.add(element.getBounds());
			}
		}
	}

	/**
	 * Creates and returns a CompoundCommand that represents the movement
	 * of all tracked nodes between the time where startTrackingMove was 
	 * called and the time endTrackingMove was called.
	 * 
	 * @param pGraph The Graph containing the selected elements.
	 * @return A CompoundCommand describing the move.
	 */
	public CompoundCommand endTrackingMove(Graph pGraph)
	{
		CompoundCommand command = new CompoundCommand();
		Rectangle2D[] selectionBounds2 = new Rectangle2D[aOriginalBounds.size()];
		int i = 0;
		for(Node node : aTrackedNodes)
		{
			selectionBounds2[i] = node.getBounds();
			i++;
		}
		for(i = 0; i < aOriginalBounds.size(); i++)
		{
			double dY = selectionBounds2[i].getY() - aOriginalBounds.get(i).getY();
			double dX = selectionBounds2[i].getX() - aOriginalBounds.get(i).getX();
			if(dX != 0 || dY != 0)
			{
				command.add(new MoveCommand(pGraph, aTrackedNodes.get(i), dX, dY));
			}
		}
		return command;
	}
}
