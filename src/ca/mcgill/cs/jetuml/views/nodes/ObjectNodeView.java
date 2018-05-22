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

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.Grid;
import ca.mcgill.cs.jetuml.views.StringViewer;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an object in an object diagram.
 */
public class ObjectNodeView extends RectangleBoundedNodeView
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	private static final int XGAP = 5;
	private static final int YGAP = 5;
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, true, true);
	
	private int aTopHeight;
	
	/**
	 * @param pNode The node to wrap.
	 */
	public ObjectNodeView(ObjectNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
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
		super.draw(pGraphics);
		Rectangle top = getTopRectangle();
		if (top.getHeight() < getBounds().getHeight()) 
		{
			pGraphics.strokeLine(top.getX(), top.getMaxY(), top.getX() + top.getWidth(), top.getMaxY());
		}
		NAME_VIEWER.draw(name(), pGraphics, top);
	}
	
	@Override
	public void layout(Diagram pGraph)
	{
		Rectangle bounds = NAME_VIEWER.getBounds(name()); 
		bounds = bounds.add(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - YGAP));
		int leftWidth = 0;
		int rightWidth = 0;
		int height = 0;
		if( children().size() != 0 )
		{
			height = YGAP;
		}
		for(ChildNode field : children())
		{
			field.view().layout(pGraph);
			Rectangle b2 = field.view().getBounds();
			height += b2.getHeight() + YGAP;   
			int axis = ((FieldNode)field).obtainAxis();
			leftWidth = Math.max(leftWidth, axis);
			rightWidth = Math.max(rightWidth, b2.getWidth() - axis);
		}
		int width = (int) (2 * Math.max(leftWidth, rightWidth) + 2 * XGAP);
		width = Math.max(width, bounds.getWidth());
		width = Math.max(width, DEFAULT_WIDTH);
		bounds = new Rectangle(getBounds().getX(), getBounds().getY(), width, bounds.getHeight() + height);
		Rectangle snappedBounds = Grid.snapped(bounds);
		setBounds(snappedBounds);
		bounds = snappedBounds;
		aTopHeight = bounds.getHeight() - height;
		int ytop = (int)(bounds.getY() + aTopHeight + YGAP);
		int xmid = bounds.getCenter().getX();
		for(ChildNode field : children())
		{
			Rectangle b2 = field.view().getBounds();
			((FieldNode)field).setBounds(new Rectangle((int)(xmid - ((FieldNode)field).obtainAxis()), 
					ytop, ((FieldNode)field).obtainAxis() + rightWidth, b2.getHeight()));
			ytop += field.view().getBounds().getHeight() + YGAP;
		}
	}
	
	/**
	 * Returns the rectangle at the top of the object node.
	 * @return the top rectangle
	 */
	private Rectangle getTopRectangle()
	{
		return new Rectangle(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), aTopHeight);
	}
}
