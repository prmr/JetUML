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

import ca.mcgill.cs.jetuml.diagram.nodes.StateNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a StateNode.
 */
public final class StateNodeView extends AbstractNodeView
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, false);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public StateNodeView(StateNode pNode)
	{
		super(pNode);
	}
	
	private String name()
	{
		return ((StateNode)node()).getName();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		ViewUtils.drawRoundedRectangle(pGraphics, getBounds());
		NAME_VIEWER.draw(name(), pGraphics, getBounds());
	}
	
	@Override
	public Rectangle getBounds()
	{
		Rectangle bounds = NAME_VIEWER.getBounds(name());
		return new Rectangle(node().position().getX(), node().position().getY(), 
				Math.max(bounds.getWidth(), DEFAULT_WIDTH), Math.max(bounds.getHeight(), DEFAULT_HEIGHT));
	}
}
