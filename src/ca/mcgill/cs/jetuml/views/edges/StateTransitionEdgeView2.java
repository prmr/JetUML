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
package ca.mcgill.cs.jetuml.views.edges;

import ca.mcgill.cs.jetuml.graph.Edge;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;


//TODO: TO BE COMPLETED


/**
 * An edge view specialized for state transitions.
 * 
 * @author Martin P. Robillard
 */
public class StateTransitionEdgeView2 extends AbstractEdgeView2
{
	/**
	 * @param pEdge an edge
	 */
	public StateTransitionEdgeView2(Edge pEdge) 
	{
		super(pEdge);
	}

	@Override
	public void draw(GraphicsContext pGraphics) {}

	@Override
	protected Shape getShape() 
	{
		return null;
	}
}
