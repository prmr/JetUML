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

import static ca.mcgill.cs.jetuml.geom.Util.max;

import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an interface in a class diagram.
 */
public class InterfaceNodeView extends AbstractNodeView
{
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_HEIGHT = 60;
	protected static final int DEFAULT_COMPARTMENT_HEIGHT = 20;
	private static final StringViewer METHOD_VIEWER = new StringViewer(StringViewer.Align.LEFT, false, false);
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, true, false);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public InterfaceNodeView(InterfaceNode pNode)
	{
		super(pNode);
	}
	
	private String name()
	{
		return ((InterfaceNode)node()).getName();
	}
	
	private String methods()
	{
		return ((InterfaceNode)node()).getMethods();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		Rectangle bounds = getBounds();
		ViewUtils.drawRectangle(pGraphics, bounds);	

		int bottomHeight = computeBottom().getHeight();
		Rectangle top = new Rectangle(bounds.getX(), bounds.getY(), 
				bounds.getWidth(), bounds.getHeight() - middleHeight() - bottomHeight);
		NAME_VIEWER.draw(name(), pGraphics, top);		
		Rectangle mid = new Rectangle((int) top.getX(), (int) top.getMaxY(), (int) top.getWidth(), middleHeight());
		if(middleHeight() > 0) 
		{
			ViewUtils.drawLine(pGraphics, top.getX(), top.getMaxY(), top.getX()+top.getWidth(), top.getMaxY(), LineStyle.SOLID);
		}
		Rectangle bot = new Rectangle(top.getX(), mid.getMaxY(), top.getWidth(), bottomHeight);
		if(bottomHeight > 0)
		{
			ViewUtils.drawLine(pGraphics, top.getX(), mid.getMaxY(), top.getX()+top.getWidth(), mid.getMaxY(), LineStyle.SOLID);
		}
		METHOD_VIEWER.draw(methods(), pGraphics, bot);
	}
	
	/**
	 * @return The width of the middle compartment.
	 */
	protected int middleWidth()
	{
		return 0;
	}
	
	/**
	 * @return The width of the middle compartment.
	 */
	protected int middleHeight()
	{
		return 0;
	}
	
	/**
	 * @return The area of the bottom compartment. The x and y values
	 * are meaningless.
	 */
	protected Rectangle computeBottom()
	{
		if( !needsBottomCompartment() )
		{
			return new Rectangle(0, 0, 0, 0);
		}
			
		Rectangle bottom = METHOD_VIEWER.getBounds(methods());
		bottom = bottom.add(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT));
		return bottom;
	}
	
	/**
	 * The top is computed to be at least the default
	 * node size.
	 * @return The area of the top compartment
	 */
	protected Rectangle computeTop()
	{
		Rectangle top = NAME_VIEWER.getBounds(name()); 
		
		int minHeight = DEFAULT_COMPARTMENT_HEIGHT;
		if(!needsMiddleCompartment() && !needsBottomCompartment() )
		{
			minHeight = DEFAULT_HEIGHT;
		}
		else if( needsMiddleCompartment() ^ needsBottomCompartment() )
		{
			minHeight = 2 * DEFAULT_COMPARTMENT_HEIGHT;
		}
		top = top.add(new Rectangle(0, 0, DEFAULT_WIDTH, minHeight));

		return top;
	}
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	protected boolean needsMiddleCompartment()
	{
		return false;
	}
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	protected boolean needsBottomCompartment()
	{
		return methods().length() > 0;
	}

	@Override
	public Rectangle getBounds()
	{
		Rectangle top = computeTop();
		Rectangle bottom = computeBottom();
		final int width = max(top.getWidth(), middleWidth(), bottom.getWidth(), DEFAULT_WIDTH);
		final int height = max( top.getHeight() + middleHeight() + bottom.getHeight(), DEFAULT_HEIGHT);
		Rectangle bounds = new Rectangle(node().position().getX(), node().position().getY(), width, height);
//		return Grid.snapped(bounds); TODO improve snapping
		return bounds;
	}
}
