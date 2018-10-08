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

import java.util.List;

import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an object in an object diagram.
 */
public final class ObjectNodeView extends AbstractNodeView
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	private static final int XGAP = 5;
	private static final int YGAP = 5;
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, true, true);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public ObjectNodeView(ObjectNode pNode)
	{
		super(pNode);
	}
	
	private String name()
	{
		return ((ObjectNode)node()).getName();
	}
	
	private List<ChildNode> children()
	{
		return ((ObjectNode)node()).getChildren();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		Rectangle bounds = getBounds();
		int dividerPosition = getTopRectangle().getMaxY();
		ViewUtils.drawRectangle(pGraphics, bounds);
		if( children().size() > 0 ) 
		{
			ViewUtils.drawLine(pGraphics, bounds.getX(), dividerPosition, bounds.getMaxX(), dividerPosition, LineStyle.SOLID);
		}
		NAME_VIEWER.draw(name(), pGraphics, new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), getTopRectangle().getHeight()));
	}
	
	private Rectangle getTopRectangle()
	{
		Rectangle bounds = NAME_VIEWER.getBounds(name()); 
		bounds = bounds.add(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		return bounds.translated(node().position().getX(), node().position().getY());
	}
	
	@Override
	public Rectangle getBounds()
	{
		Rectangle bounds = getTopRectangle();
		int leftWidth = 0;
		int rightWidth = 0;
		int height = 0;
		if( children().size() > 0 )
		{
			height = YGAP;
		}
		for(ChildNode field : children())
		{
			FieldNodeView view = (FieldNodeView) field.view();
			height += view.getHeight() + YGAP;   
			leftWidth = Math.max(leftWidth, view.leftWidth());
			rightWidth = Math.max(rightWidth, view.rightWidth());
		}
		int width = Math.max(bounds.getWidth(), 2 * Math.max(leftWidth, rightWidth) + 2 * XGAP);
		return new Rectangle(bounds.getX(), bounds.getY(), width, bounds.getHeight() + height);
	}
	
	/**
	 * @param pNode The node whose position to compute.
	 * @return The y position of a child node.
	 */
	public int getYPosition(FieldNode pNode)
	{
		assert children().contains(pNode);
		Rectangle bounds = getTopRectangle();
		int yPosition = bounds.getMaxY() + YGAP; 
		for( ChildNode field : children() )
		{
			yPosition += YGAP;
			if( field == pNode )
			{
				return yPosition;
			}
			yPosition += ((FieldNodeView)field.view()).getHeight();
		}
		return yPosition;
	}
}
