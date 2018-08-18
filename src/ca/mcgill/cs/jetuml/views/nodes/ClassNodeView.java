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

import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an interface in a class diagram.
 */
public final class ClassNodeView extends InterfaceNodeView
{
	private static final StringViewer STRING_VIEWER = new StringViewer(StringViewer.Align.LEFT, false, false);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public ClassNodeView(ClassNode pNode)
	{
		super(pNode);
	}
	
	private String attributes()
	{
		return ((ClassNode)node()).getAttributes();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		super.draw(pGraphics); 
		int bottomHeight = computeBottom().getHeight();
		Rectangle bounds = getBounds();
		Rectangle top = new Rectangle(bounds.getX(), bounds.getY(), 
				bounds.getWidth(), bounds.getHeight() - middleHeight() - bottomHeight);
		Rectangle mid = new Rectangle(top.getX(), top.getMaxY(), top.getWidth(), middleHeight());
		STRING_VIEWER.draw(attributes(), pGraphics, mid);
	}
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	@Override
	protected boolean needsMiddleCompartment()
	{
		return attributes().length() > 0;
	}
	
	@Override
	protected int middleWidth()
	{
		if( !needsMiddleCompartment() )
		{
			return 0;
		}
		else
		{
			return Math.max(STRING_VIEWER.getBounds(attributes()).getWidth(), DEFAULT_WIDTH);
		}
	}
	
	@Override
	protected int middleHeight()
	{
		if( !needsMiddleCompartment() )
		{
			return 0;
		}
		else
		{
			return Math.max(STRING_VIEWER.getBounds(attributes()).getHeight(), DEFAULT_COMPARTMENT_HEIGHT);
		}
	}
}
