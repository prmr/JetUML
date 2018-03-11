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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;

/**
 * Basic services for drawing nodes.
 * 
 * @author Martin P. Robillard
 *
 */
public abstract class AbstractNodeView implements NodeView
{
	public static final int SHADOW_GAP = 4;
	private static final Color SHADOW_COLOR = Color.LIGHT_GRAY;
	
	private Node aNode;
	
	/**
	 * @param pNode The node to wrap.
	 */
	protected AbstractNodeView(Node pNode)
	{
		aNode = pNode;
	}
	
	/**
	 * @return The wrapped node.
	 */
	public Node node() // change to protected
	{
		return aNode;
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		Shape shape = getShape();
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.translate(SHADOW_GAP, SHADOW_GAP);      
		pGraphics2D.setColor(SHADOW_COLOR);
		pGraphics2D.fill(shape);
		pGraphics2D.translate(-SHADOW_GAP, -SHADOW_GAP);
		pGraphics2D.setColor(pGraphics2D.getBackground());
		pGraphics2D.fill(shape);      
		pGraphics2D.setColor(oldColor);
	}
	
	/**
     *  @return the shape to be used for computing the drop shadow
    */
	protected abstract Shape getShape();

	@Override
	public void layout(Graph pGraph)
	{}
}
