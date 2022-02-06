/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.viewers.nodes;

import static ca.mcgill.cs.jetuml.geom.GeomUtils.max;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.TypeNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.LineStyle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.ViewerUtils;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a class or interface in a class diagram.
 * 
 * The top box, which shows the title, has a minimum height of 20 pixels,
 * minus 20 if attributes are present, minus another 20 if methods are present.
 */
public class TypeNodeViewer extends AbstractNodeViewer
{
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_HEIGHT = 60;
	protected static final int TOP_INCREMENT = 20;
	private static final StringViewer NAME_VIEWER = StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.BOLD, TextDecoration.PADDED);
	private static final StringViewer STRING_VIEWER = StringViewer.get(Alignment.TOP_LEFT, TextDecoration.PADDED);
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{	
		assert pNode instanceof TypeNode;
		TypeNode node = (TypeNode) pNode;
		final Rectangle bounds = getBounds(pNode);
		final int attributeHeight = attributeBoxHeight(node);
		final int methodHeight = methodBoxHeight(node);
		final int nameHeight = nameBoxHeight(node, attributeHeight, methodHeight);

		ViewerUtils.drawRectangle(pGraphics, bounds);	
		NAME_VIEWER.draw(getNameText(node), pGraphics, new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), nameHeight));
		
		if( attributeHeight > 0 )
		{
			final int splitY = bounds.getY() + nameHeight;
			ViewerUtils.drawLine(pGraphics, bounds.getX(), splitY, bounds.getMaxX(), splitY, LineStyle.SOLID);
			STRING_VIEWER.draw(node.getAttributes(), pGraphics, new Rectangle(bounds.getX(), splitY, bounds.getWidth(), attributeHeight));
			if( methodHeight > 0 )
			{
				final int splitY2 = splitY + attributeHeight;
				ViewerUtils.drawLine(pGraphics, bounds.getX(), splitY2, bounds.getMaxX(), splitY2, LineStyle.SOLID);
				STRING_VIEWER.draw(node.getMethods(), pGraphics, 
						new Rectangle(bounds.getX(), splitY2, bounds.getWidth(), methodHeight));
			}
		}
		else if( methodHeight > 0 )
		{
			final int splitY = bounds.getY() + nameHeight;
			ViewerUtils.drawLine(pGraphics, bounds.getX(), splitY, bounds.getMaxX(), splitY, LineStyle.SOLID);
			STRING_VIEWER.draw(node.getMethods(), pGraphics, new Rectangle(bounds.getX(), splitY, bounds.getWidth(), methodHeight));
		}	
	}
	
	private static int attributeBoxHeight(TypeNode pNode)
	{
		return textDimensions(pNode.getAttributes()).height();
	}
	
	private static int methodBoxHeight(TypeNode pNode)
	{
		return textDimensions(pNode.getMethods()).height();
	}
	
	private int nameBoxHeight(TypeNode pNode, int pAttributeBoxHeight, int pMethodBoxHeight)
	{
		final int textHeight = max(textDimensions(getNameText(pNode)).height(), TOP_INCREMENT);
		final int freeSpaceInTopBox = DEFAULT_HEIGHT - textHeight;
		if( freeSpaceInTopBox < 0 )
		{
			return textHeight; // There's no free space so we return the height we need.
		}
		if( pAttributeBoxHeight + pMethodBoxHeight > freeSpaceInTopBox )
		{
			return textHeight; // We use all the free space so we return the height we need.
		}
		// We expand the name box to use the unclaimed free space
		return textHeight + freeSpaceInTopBox - (pAttributeBoxHeight + pMethodBoxHeight);
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
	
	private static Dimension textDimensionsBold(String pString)
	{
		Dimension result = Dimension.NULL;
		if( pString.length() > 0 )
		{
			result = NAME_VIEWER.getDimension(pString);
		}
		return result;
	}
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		assert pNode instanceof TypeNode;
		TypeNode node = (TypeNode) pNode;
		final int attributeHeight = attributeBoxHeight(node);
		final int methodHeight = methodBoxHeight(node);
		final int nameHeight = nameBoxHeight(node, attributeHeight, methodHeight);
		Dimension nameDimension = textDimensionsBold(getNameText(node));
		Dimension attributeDimension = textDimensions(node.getAttributes());
		Dimension methodDimension = textDimensions(node.getMethods());
		int width = max(DEFAULT_WIDTH, nameDimension.width(), attributeDimension.width(), methodDimension.width());
		int height = attributeHeight + methodHeight + nameHeight;
		return new Rectangle(node.position().getX(), node.position().getY(), width, height);
	}
	
	/**
	 * By default the name text is the name of the node.
	 * 
	 * @param pNode The Node.
	 * @return The text to show as the name of the node.
	 * @pre pNode != null
	 */
	protected String getNameText(TypeNode pNode)
	{
		assert pNode != null;
		return pNode.getName();
	}
}
