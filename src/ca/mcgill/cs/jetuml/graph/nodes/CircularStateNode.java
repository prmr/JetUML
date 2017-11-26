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

package ca.mcgill.cs.jetuml.graph.nodes;

import ca.mcgill.cs.jetuml.views.nodes.CircularStateNodeView;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;

/**
 * An initial or final node (bull's eye) in a state or activity diagram.
 */
public class CircularStateNode extends AbstractNode
{
	
	private boolean aFinalState; 
	
	@Override
	protected NodeView generateView()
	{
		return new CircularStateNodeView(this);
	}
	   
	/**
	 * @return true if this represents a final state.
	 */
	public boolean isFinal()
	{
		return aFinalState; 
	}
   
	/**
	 * @param pFinalState true to set this object to represent a final state.
	 */
	public void setFinal(boolean pFinalState)
	{
		aFinalState = pFinalState;
	}
}

