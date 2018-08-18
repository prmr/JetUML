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

import ca.mcgill.cs.jetuml.diagram.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * An object to render a UseCaseNode.
 */
public final class UseCaseNodeView extends AbstractNodeView
{
	private static final int DEFAULT_WIDTH = 110;
	private static final int DEFAULT_HEIGHT = 40;
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, false);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public UseCaseNodeView(UseCaseNode pNode)
	{
		super(pNode);
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		Rectangle bounds = getBounds();
		ViewUtils.drawOval(pGraphics, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), Color.WHITE, true);
		NAME_VIEWER.draw(name(), pGraphics, getBounds());
	}
	
	private String name()
	{
		return ((UseCaseNode)node()).getName();
	}
	
	@Override
	public Rectangle getBounds()
	{
		return new Rectangle(node().position().getX(), node().position().getY(), 
				Math.max(DEFAULT_WIDTH,  NAME_VIEWER.getBounds(name()).getWidth()), 
				Math.max(DEFAULT_HEIGHT, NAME_VIEWER.getBounds(name()).getHeight()));
	}
}