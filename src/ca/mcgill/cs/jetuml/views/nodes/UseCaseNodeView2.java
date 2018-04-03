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

import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.views.Grid2;
import ca.mcgill.cs.jetuml.views.StringViewer2;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a UseCaseNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class UseCaseNodeView2 extends RectangleBoundedNodeView2
{
	private static final int DEFAULT_WIDTH = 110;
	private static final int DEFAULT_HEIGHT = 40;
	private static final StringViewer2 NAME_VIEWER = new StringViewer2(StringViewer2.Align.CENTER, false, false);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public UseCaseNodeView2(UseCaseNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		super.draw(pGraphics);  
		NAME_VIEWER.draw(name(), pGraphics, getBounds());
	}
	
	@Override
	public void fillShape(GraphicsContext pGraphics, boolean pShadow)
	{
		if (pShadow) 
		{
			pGraphics.setFill(SHADOW_COLOR);
			pGraphics.fillOval(node().position().getX(), node().position().getY(), 
					getBounds().getWidth(), getBounds().getHeight());
		}
		else 
		{
			pGraphics.setFill(BACKGROUND_COLOR);
			pGraphics.fillOval(node().position().getX(), node().position().getY(), 
					getBounds().getWidth(), getBounds().getHeight());
			pGraphics.strokeOval(node().position().getX(), node().position().getY(), 
					getBounds().getWidth(), getBounds().getHeight());
		}	
	}
	
	
	private String name()
	{
		return ((UseCaseNode)node()).getName();
	}
	
	@Override	
	public void layout(Graph2 pGraph)
	{
		Rectangle bounds = NAME_VIEWER.getBounds(name());
		bounds = new Rectangle(getBounds().getX(), getBounds().getY(), 
				Math.max(bounds.getWidth(), DEFAULT_WIDTH), Math.max(bounds.getHeight(), DEFAULT_HEIGHT));
		setBounds(Grid2.snapped(bounds));
	}
}