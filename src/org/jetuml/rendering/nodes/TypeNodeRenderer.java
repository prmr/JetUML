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
package org.jetuml.rendering.nodes;

import static org.jetuml.geom.GeomUtils.max;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.TypeNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingUtils;
import org.jetuml.rendering.StringRenderer;
import org.jetuml.rendering.StringRenderer.Alignment;
import org.jetuml.rendering.StringRenderer.TextDecoration;

import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a class or interface in a class diagram.
 * 
 * The top box, which shows the title, has a minimum height of 20 pixels,
 * minus 20 if attributes are present, minus another 20 if methods are present.
 */
public class TypeNodeRenderer extends AbstractNodeRenderer
{
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_HEIGHT = 60;
	protected static final int TOP_INCREMENT = 20;
	private static final StringRenderer NAME_VIEWER = StringRenderer.get(Alignment.CENTER_CENTER, TextDecoration.BOLD, TextDecoration.PADDED);
	private static final StringRenderer STRING_VIEWER = StringRenderer.get(Alignment.TOP_LEFT, TextDecoration.PADDED);
	private static final StringRenderer UNDERLINING_STRING_VIEWER = StringRenderer.get(
			Alignment.TOP_LEFT, TextDecoration.PADDED, TextDecoration.UNDERLINED);
	/**
	 * @param pParent The renderer for the parent diagram.
	 */
	public TypeNodeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	public Dimension getDefaultDimension(Node pNode)
	{
		return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void draw(DiagramElement pElement, GraphicsContext pGraphics)
	{	
		assert pElement instanceof TypeNode;
		TypeNode node = (TypeNode) pElement;
		final Rectangle bounds = getBounds(pElement);
		final int attributeHeight = attributeBoxHeight(node);
		final int methodHeight = methodBoxHeight(node);
		final int nameHeight = nameBoxHeight(node, attributeHeight, methodHeight);

		RenderingUtils.drawRectangle(pGraphics, bounds);	
		NAME_VIEWER.draw(getNameText(node), pGraphics, new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), nameHeight));
		
		if( attributeHeight > 0 )
		{
			final int splitY = bounds.getY() + nameHeight;
			RenderingUtils.drawLine(pGraphics, bounds.getX(), splitY, bounds.getMaxX(), splitY, LineStyle.SOLID);
			drawAttribute(node, bounds, splitY, attributeHeight, pGraphics);
			if( methodHeight > 0 )
			{
				final int splitY2 = splitY + attributeHeight;
				RenderingUtils.drawLine(pGraphics, bounds.getX(), splitY2, bounds.getMaxX(), splitY2, LineStyle.SOLID);
				drawMethod(node, bounds, splitY2, methodHeight, pGraphics);
			}
		}
		else if( methodHeight > 0 )
		{
			final int splitY = bounds.getY() + nameHeight;
			RenderingUtils.drawLine(pGraphics, bounds.getX(), splitY, bounds.getMaxX(), splitY, LineStyle.SOLID);
			drawMethod(node, bounds, splitY, methodHeight, pGraphics);
		}
	}
	
	private static void drawAttribute(TypeNode pNode, Rectangle pBounds, int pSplitY, int pAttributeBoxHeight, GraphicsContext pGraphics)
	{
		String attributes = pNode.getAttributes();
		String[] attributesByLine = attributes.split("\n");
		int lineSpacing = 0;
		
		for( String attribute : attributesByLine )
		{
			if( attribute.length() > 2 && attribute.startsWith("_") && attribute.endsWith("_") )
			{
				StringBuilder underline = new StringBuilder(attribute);
				underline.deleteCharAt(0);
				underline.deleteCharAt(underline.length() - 1);
				UNDERLINING_STRING_VIEWER.draw(underline.toString(), pGraphics, 
						new Rectangle(pBounds.getX(), pSplitY + lineSpacing, pBounds.getWidth(), pAttributeBoxHeight));
			}
			else
			{
				STRING_VIEWER.draw(attribute, pGraphics, 
						new Rectangle(pBounds.getX(), pSplitY + lineSpacing, pBounds.getWidth(), pAttributeBoxHeight));
			}
			lineSpacing += STRING_VIEWER.getHeight(attribute);
		}	
	}
	
	private static void drawMethod(TypeNode pNode, Rectangle pBounds, int pSplitY, int pMethodBoxHeight, GraphicsContext pGraphics)
	{
		String methods = pNode.getMethods();
		String[] methodsByLine = methods.split("\n");
		int lineSpacing = 0;
		
		for( String method : methodsByLine )
		{
			if( method.length() > 2 && method.startsWith("_") && method.endsWith("_") )
			{
				StringBuilder underline = new StringBuilder(method);
				underline.deleteCharAt(0);
				underline.deleteCharAt(underline.length() - 1);
				UNDERLINING_STRING_VIEWER.draw(underline.toString(), pGraphics, 
						new Rectangle(pBounds.getX(), pSplitY + lineSpacing, pBounds.getWidth(), pMethodBoxHeight));
			}
			else
			{
				STRING_VIEWER.draw(method, pGraphics, 
						new Rectangle(pBounds.getX(), pSplitY + lineSpacing, pBounds.getWidth(), pMethodBoxHeight));
			}
			lineSpacing += STRING_VIEWER.getHeight(method);
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
