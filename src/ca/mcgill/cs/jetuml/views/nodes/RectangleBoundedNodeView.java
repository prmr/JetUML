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

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.Grid;

/**
 * A view for nodes that are bounded by a rectangle.
 */
public abstract class RectangleBoundedNodeView extends AbstractNodeView
{
	private int aWidth;
	private int aHeight;
	
	/**
	 * @param pNode The node to wrap.
	 * @param pMinWidth The minimum width for the node.
	 * @param pMinHeight The minimum height for the node.
	 */
	protected RectangleBoundedNodeView(Node pNode, int pMinWidth, int pMinHeight)
	{
		super(pNode);
		aWidth = pMinWidth;
		aHeight = pMinHeight;
	}
	
	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(node().position().getX(), node().position().getY(), aWidth, aHeight);
	}
	
	/**
	 * @param pNewBounds The new bounds for this node.
	 */
	protected void setBounds(Rectangle pNewBounds)
	{
		node().moveTo(pNewBounds.getOrigin());
		aWidth = pNewBounds.getWidth();
		aHeight = pNewBounds.getHeight();
	}
	
	@Override
	public void layout(Diagram pGraph)
	{
		Rectangle snapped = Grid.snapped(getBounds());
		node().moveTo(snapped.getOrigin());
		aWidth = snapped.getWidth();
		aHeight = snapped.getHeight();
	}
}
