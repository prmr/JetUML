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

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render an interface in a class diagram.
 */
public final class ClassNodeViewer extends InterfaceNodeViewer
{
	private static final StringViewer STRING_VIEWER = new StringViewer(StringViewer.Align.LEFT, false, false);
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		super.draw(pNode, pGraphics); 
		int bottomHeight = computeBottom(pNode).getHeight();
		Rectangle bounds = getBounds(pNode);
		Rectangle top = new Rectangle(bounds.getX(), bounds.getY(), 
				bounds.getWidth(), bounds.getHeight() - middleHeight(pNode) - bottomHeight);
		Rectangle mid = new Rectangle(top.getX(), top.getMaxY(), top.getWidth(), middleHeight(pNode));
		STRING_VIEWER.draw(((ClassNode)pNode).getAttributes(), pGraphics, mid);
	}
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	@Override
	protected boolean needsMiddleCompartment(Node pNode)
	{
		return ((ClassNode)pNode).getAttributes().length() > 0;
	}
	
	@Override
	protected int middleWidth(Node pNode)
	{
		if( !needsMiddleCompartment(pNode) )
		{
			return 0;
		}
		else
		{
			return Math.max(STRING_VIEWER.getDimension(((ClassNode)pNode).getAttributes()).getWidth(), DEFAULT_WIDTH);
		}
	}
	
	@Override
	protected int middleHeight(Node pNode)
	{
		if( !needsMiddleCompartment(pNode) )
		{
			return 0;
		}
		else
		{
			return Math.max(STRING_VIEWER.getDimension(((ClassNode)pNode).getAttributes()).getHeight(), DEFAULT_COMPARTMENT_HEIGHT);
		}
	}
}
