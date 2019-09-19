/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018, 2019 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.viewers.nodes;

import static ca.mcgill.cs.jetuml.geom.Util.max;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an interface in a class diagram.
 */
public class InterfaceNodeViewer extends AbstractNodeViewer
{
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_HEIGHT = 60;
	protected static final int DEFAULT_COMPARTMENT_HEIGHT = 20;
	private static final StringViewer METHOD_VIEWER = new StringViewer(StringViewer.Align.LEFT, false, false);
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, true, false);
	
//	private String name()
//	{
//		return ((InterfaceNode)node()).getName();
//	}
//	
//	private String methods()
//	{
//		return ((InterfaceNode)node()).getMethods();
//	}
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		Rectangle bounds = getBounds(pNode);
		ViewUtils.drawRectangle(pGraphics, bounds);	

		int bottomHeight = computeBottom(pNode).getHeight();
		Rectangle top = new Rectangle(bounds.getX(), bounds.getY(), 
				bounds.getWidth(), bounds.getHeight() - middleHeight(pNode) - bottomHeight);
		NAME_VIEWER.draw(((InterfaceNode)pNode).getName(), pGraphics, top);		
		Rectangle mid = new Rectangle(top.getX(), top.getMaxY(), top.getWidth(), middleHeight(pNode));
		if(middleHeight(pNode) > 0) 
		{
			ViewUtils.drawLine(pGraphics, top.getX(), top.getMaxY(), top.getX()+top.getWidth(), top.getMaxY(), LineStyle.SOLID);
		}
		Rectangle bot = new Rectangle(top.getX(), mid.getMaxY(), top.getWidth(), bottomHeight);
		if(bottomHeight > 0)
		{
			ViewUtils.drawLine(pGraphics, top.getX(), mid.getMaxY(), top.getX()+top.getWidth(), mid.getMaxY(), LineStyle.SOLID);
		}
		METHOD_VIEWER.draw(((InterfaceNode)pNode).getMethods(), pGraphics, bot);
	}
	
	/**
	 * @param pNode The interface node to draw
	 * @return The width of the middle compartment.
	 */
	protected int middleWidth(Node pNode)
	{
		return 0;
	}
	
	/**
	 * @param pNode The interface node to draw
	 * @return The width of the middle compartment.
	 */
	protected int middleHeight(Node pNode)
	{
		return 0;
	}
	
	/**
	 * @param pNode The interface node to draw
	 * @return The area of the bottom compartment.
	 */
	protected Dimension computeBottom(Node pNode)
	{
		if( !needsBottomCompartment(pNode) )
		{
			return Dimension.NULL;
		}
			
		Dimension bottom = METHOD_VIEWER.getDimension(((InterfaceNode)pNode).getMethods());
		return bottom.include(DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT);
	}
	
	/**
	 * The top is computed to be at least the default
	 * node size.
	 * @param pNode The interface node to draw
	 * @return The area of the top compartment
	 */
	protected Dimension computeTop(Node pNode)
	{
		Dimension top = NAME_VIEWER.getDimension(((InterfaceNode)pNode).getName()); 
		
		int minHeight = DEFAULT_COMPARTMENT_HEIGHT;
		if(!needsMiddleCompartment(pNode) && !needsBottomCompartment(pNode) )
		{
			minHeight = DEFAULT_HEIGHT;
		}
		else if( needsMiddleCompartment(pNode) || needsBottomCompartment(pNode) )
		{
			minHeight = 2 * DEFAULT_COMPARTMENT_HEIGHT;
		}
		return top.include(DEFAULT_WIDTH, minHeight);
	}
	
	/**
	 * @param pNode the interface node to draw.
	 * @return True if the node requires a bottom compartment.
	 */
	protected boolean needsMiddleCompartment(Node pNode)
	{
		return false;
	}
	
	/**
	 * @param pNode The interface node to draw.
	 * @return True if the node requires a bottom compartment.
	 */
	protected boolean needsBottomCompartment(Node pNode)
	{
		return ((InterfaceNode)pNode).getMethods().length() > 0;
	}

	@Override
	public Rectangle getBounds(Node pNode)
	{
		Dimension top = computeTop(pNode);
		Dimension bottom = computeBottom(pNode);
		final int width = max(top.getWidth(), middleWidth(pNode), bottom.getWidth(), DEFAULT_WIDTH);
		final int height = max( top.getHeight() + middleHeight(pNode) + bottom.getHeight(), DEFAULT_HEIGHT);
		Rectangle bounds = new Rectangle(pNode.position().getX(), pNode.position().getY(), width, height);
		return bounds;
	}
}
