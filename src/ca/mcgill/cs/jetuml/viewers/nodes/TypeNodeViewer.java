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
import ca.mcgill.cs.jetuml.diagram.nodes.TypeNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ViewUtils;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a class or interface in a class diagram.
 * 
 * The top box, which shows the title, has a minimum height of 20 pixels,
 * minus 20 if attributes are present, minus another 20 if methods are present.
 */
public final class TypeNodeViewer extends AbstractNodeViewer
{
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_HEIGHT = 60;
	protected static final int TOP_INCREMENT = 20;
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.CENTER, true, false);
	private static final StringViewer STRING_VIEWER = new StringViewer(StringViewer.Align.LEFT, false, false);
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{	
		assert pNode instanceof TypeNode;
		TypeNode node = (TypeNode) pNode;
		final Rectangle bounds = getBounds(pNode);
		final int nameHeight = max(topBoxMinHeight(node), textDimensions(node.getName()).getHeight());
		final int attributeHeight = textDimensions(node.getAttributes()).getHeight();
		final int methodHeight = textDimensions(node.getMethods()).getHeight();
		
		ViewUtils.drawRectangle(pGraphics, bounds);	
		NAME_VIEWER.draw(node.getName(), pGraphics, new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), nameHeight));
		
		if( attributeHeight > 0 )
		{
			final int splitY = bounds.getY() + nameHeight;
			ViewUtils.drawLine(pGraphics, bounds.getX(), splitY, bounds.getMaxX(), splitY, LineStyle.SOLID);
			STRING_VIEWER.draw(node.getAttributes(), pGraphics, new Rectangle(bounds.getX(), splitY, bounds.getWidth(), attributeHeight));
			if( methodHeight > 0 )
			{
				final int splitY2 = splitY + attributeHeight;
				ViewUtils.drawLine(pGraphics, bounds.getX(), splitY2, bounds.getMaxX(), splitY2, LineStyle.SOLID);
				STRING_VIEWER.draw(node.getMethods(), pGraphics, 
						new Rectangle(bounds.getX(), splitY2, bounds.getWidth(), methodHeight));
			}
		}
		else
		{
			final int splitY = bounds.getY() + nameHeight;
			ViewUtils.drawLine(pGraphics, bounds.getX(), splitY, bounds.getMaxX(), splitY, LineStyle.SOLID);
			STRING_VIEWER.draw(node.getMethods(), pGraphics, new Rectangle(bounds.getX(), splitY, bounds.getWidth(), methodHeight));
		}	
	}
	
	private static boolean hasAttributes(TypeNode pNode)
	{
		return pNode.getAttributes().trim().length() > 0;
	}
	
	private static boolean hasMethods(TypeNode pNode)
	{
		return pNode.getAttributes().trim().length() > 0;
	}
	
	private static int topBoxMinHeight(TypeNode pNode)
	{
		int result = DEFAULT_HEIGHT;
		if( hasAttributes(pNode))
		{
			result -= TOP_INCREMENT;
		}
		if( hasMethods(pNode))
		{
			result -= TOP_INCREMENT;
		}
		return result;
	}
	
	private static Dimension textDimensions(String pString)
	{
		Dimension result = Dimension.NULL;
		if( pString.length() > 0 )
		{
			result = STRING_VIEWER.getDimension(pString);
		}
		return result;
	}
	
	@Override
	public Rectangle getBounds(Node pNode)
	{
		assert pNode instanceof TypeNode;
		TypeNode node = (TypeNode) pNode;
		Dimension nameDimension = textDimensions(node.getName());
		Dimension attributeDimension = textDimensions(node.getAttributes());
		Dimension methodDimension = textDimensions(node.getMethods());
		int width = max(DEFAULT_WIDTH, nameDimension.getWidth(), attributeDimension.getWidth(), methodDimension.getWidth());
		int height = max(nameDimension.getHeight(), max(topBoxMinHeight(node))) + 
				attributeDimension.getHeight() + methodDimension.getHeight();
		height = max(DEFAULT_HEIGHT, height);
		return new Rectangle(node.position().getX(), node.position().getY(), width, height);
	}
}
