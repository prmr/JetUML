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
package ca.mcgill.cs.jetuml.views.nodes;

import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Node;
import javafx.scene.canvas.GraphicsContext;


//TODO: TO BE COMPLETED


/**
 * An object to render a CircularStateNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class CircularStateNodeView2 extends AbstractNodeView2
{
	/**
	 * @param pNode a node
	 * @param pBoolean a boolean
	 */
	public CircularStateNodeView2(Node pNode, boolean pBoolean) 
	{
		super(pNode);
	}

	@Override
	public Rectangle getBounds() 
	{
		return null;
	}

	@Override
	public boolean contains(Point pPoint) 
	{
		return false;
	}

	@Override
	public Point getConnectionPoint(Direction pDirection) 
	{
		return null;
	}

	@Override
	protected void fillShape(GraphicsContext pGraphics, boolean pShadow) {}
}
