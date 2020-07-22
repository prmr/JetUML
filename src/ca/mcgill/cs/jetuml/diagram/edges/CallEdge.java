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

package ca.mcgill.cs.jetuml.diagram.edges;

import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;

/**
 * An edge that joins two call nodes.
 */
public class CallEdge extends SingleLabelEdge
{
	private boolean aSignal;
	
	/**
	 * Creates a non-signal edge.
	 */
	public CallEdge()
	{
		setSignal(false);
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("signal", () -> aSignal, pSignal -> aSignal = (boolean) pSignal);
	}
	
	/**
     * Gets the signal property.
     * @return true if this is a signal edge
	 */
	public boolean isSignal() 
	{ return aSignal; }

	/**
     * Sets the signal property.
     * @param pNewValue true if this is a signal edge
     */      
	public void setSignal(boolean pNewValue) 
	{ 
		aSignal = pNewValue; 
	}
	
	/**
	 * Determines if this edge is a self-edge. A self-edge has its start and 
	 * end call nodes on the same implicit parameter node.
	 * 
	 * @return True if this a self-edge.
	 */
	public boolean isSelfEdge()
	{
		return getEnd() instanceof CallNode && 
				getEnd().getParent() == getStart().getParent();
	}
}
