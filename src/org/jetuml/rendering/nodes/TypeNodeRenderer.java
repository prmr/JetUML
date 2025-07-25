/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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

import java.util.Optional;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.TypeNode;
import org.jetuml.geom.Alignment;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.StringRenderer;
import org.jetuml.rendering.StringRenderer.Decoration;

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
	private static final int TOP_MARGIN = 5;
	private static final int HORIZONTAL_PADDING = 7;
	private static final int VERTICAL_PADDING = 6;
	private static final StringRenderer TYPE_NAME_RENDERER = new StringRenderer(Alignment.CENTER, Decoration.BOLD);
	private static final StringRenderer ITALIC_NAME_RENDERER = new StringRenderer(
			Alignment.CENTER, Decoration.BOLD, Decoration.ITALIC);
	private static final StringRenderer STRING_RENDERER = new StringRenderer(Alignment.LEFT);
	private static final StringRenderer UNDERLINING_STRING_RENDERER = new StringRenderer(
			Alignment.LEFT, Decoration.UNDERLINED);
	private static final StringRenderer ITALIC_STRING_RENDERER = new StringRenderer(
			Alignment.LEFT, Decoration.ITALIC);
	private static final String ITALIC_MARKUP = "/";
	private static final String UNDERLINE_MARKUP = "_";
	
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
	
	private static int lineHeight()
	{
		return STRING_RENDERER.getDimension("|").height();
	}
	
	@Override
	public void draw(DiagramElement pElement, RenderingContext pContext)
	{	
		assert pElement instanceof TypeNode;
		TypeNode node = (TypeNode) pElement;
		final Rectangle bounds = getBounds(pElement);
		final int attributeBoxHeight = attributeBoxHeight(node);
		final int methodBoxHeight = methodBoxHeight(node);
		final int nameBoxHeight = nameBoxHeight(node, attributeBoxHeight, methodBoxHeight);

		pContext.drawRectangle(bounds, ColorScheme.get().fill(), ColorScheme.get().stroke(),
				Optional.of(ColorScheme.get().dropShadow()));	
		drawName(getNameText(node), new Rectangle(bounds.x(), bounds.y(), bounds.width(), nameBoxHeight), pContext);
		
		if( attributeBoxHeight > 0 )
		{
			final int splitY = bounds.y() + nameBoxHeight;
			pContext.strokeLine(bounds.x(), splitY, bounds.maxX(), splitY, 
					ColorScheme.get().stroke(),
					LineStyle.SOLID);
			drawAttribute(node.getAttributes(), new Rectangle(bounds.x(), splitY, bounds.width(), attributeBoxHeight), pContext);
			if( methodBoxHeight > 0 )
			{
				final int splitY2 = splitY + attributeBoxHeight;
				pContext.strokeLine(bounds.x(), splitY2, bounds.maxX(), splitY2, 
						ColorScheme.get().stroke(),
						LineStyle.SOLID);
				drawMethod(node.getMethods(), new Rectangle(bounds.x(), splitY2, bounds.width(), methodBoxHeight), pContext);
			}
		}
		else if( methodBoxHeight > 0 )
		{
			final int splitY = bounds.y() + nameBoxHeight;
			pContext.strokeLine(bounds.x(), splitY, bounds.maxX(), splitY, 
					ColorScheme.get().stroke(),
					LineStyle.SOLID);
			drawMethod(node.getMethods(), new Rectangle(bounds.x(), splitY, bounds.width(), methodBoxHeight), pContext);
		}
		lineHeight();
	}
	
	/*
	 * @param pName The possibly multi-line text to draw in the name box.
	 * @param pBounds The bounds of the name box.
	 * @param pContext The rendering context
	 */
	private static void drawName(String pName, Rectangle pBounds, RenderingContext pContext)
	{
		String[] nameByLine = pName.trim().split("\n");
		int startY = pBounds.center().y() - lineHeight() * nameByLine.length / 2;
		
		for (String line : nameByLine)
		{
			Rectangle bounds = new Rectangle(pBounds.x(), startY, pBounds.width(), lineHeight());
			if( containsMarkup(line, ITALIC_MARKUP) )
			{
				ITALIC_NAME_RENDERER.draw(removeMarkup(line), bounds, pContext);
			}
			else
			{
				TYPE_NAME_RENDERER.draw(line, bounds, pContext);
			}
			startY += lineHeight();
		}
	}
	
	/*
	 * @param pText The possibly multi-line text to draw in the attribute box.
	 * @param pBounds The bounds of the attribute box.
	 * @param pContext The rendering context
	 */
	private static void drawAttribute(String pText, Rectangle pBounds, RenderingContext pContext)
	{
		String[] attributesByLine = pText.trim().split("\n");
		int lineSpacing = TOP_MARGIN;
		
		for( String attribute : attributesByLine )
		{
			if( containsMarkup(attribute, UNDERLINE_MARKUP) )
			{
				UNDERLINING_STRING_RENDERER.draw(removeMarkup(attribute), 
						new Rectangle(pBounds.x() + HORIZONTAL_PADDING, pBounds.y() + lineSpacing, 
								pBounds.width(), 
								lineHeight()), pContext);
			}
			else
			{
				STRING_RENDERER.draw(attribute, 
						new Rectangle(pBounds.x() + HORIZONTAL_PADDING, pBounds.y() + lineSpacing, 
								pBounds.width(), 
								lineHeight()), pContext);
			}
			lineSpacing += lineHeight();
		}	
	}
	
	/*
	 * @param pText The possibly multi-line text to draw in the method box.
	 * @param pBounds The bounds of the method box.
	 * @param pContext The rendering context
	 */
	private static void drawMethod(String pText, Rectangle pBounds, RenderingContext pContext)
	{
		String[] methodsByLine = pText.trim().split("\n");
		int lineSpacing = TOP_MARGIN;
		
		for( String method : methodsByLine )
		{
			if( containsMarkup(method, UNDERLINE_MARKUP) )
			{
				UNDERLINING_STRING_RENDERER.draw(removeMarkup(method), 
						new Rectangle(pBounds.x() + HORIZONTAL_PADDING, pBounds.y() + lineSpacing, 
								pBounds.width(), 
								lineHeight()), pContext);
			}
			else if( containsMarkup(method, ITALIC_MARKUP) )
			{
				ITALIC_STRING_RENDERER.draw(removeMarkup(method), 
						new Rectangle(pBounds.x() + HORIZONTAL_PADDING, pBounds.y() + lineSpacing, 
								pBounds.width(), 
								lineHeight()), pContext);
			}
			else
			{
				STRING_RENDERER.draw(method, 
						new Rectangle(pBounds.x() + HORIZONTAL_PADDING, pBounds.y() + lineSpacing, 
								pBounds.width(), 
								lineHeight()), pContext);
			}
			lineSpacing += lineHeight();
		}	
	}
	
	private static boolean containsMarkup(String pText, String pMarkup)
	{
		return pText.length() > 2 && pText.startsWith(pMarkup) && pText.endsWith(pMarkup);
	}
	
	private static String removeMarkup(String pString)
	{
		StringBuilder result = new StringBuilder(pString);
		result.deleteCharAt(0);
		result.deleteCharAt(result.length() - 1);
		return result.toString();
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
			Dimension dimension = STRING_RENDERER.getDimension(pString);
			result = new Dimension(dimension.width() + 2 * HORIZONTAL_PADDING, dimension.height() + 2 * VERTICAL_PADDING);
		}
		return result;
	}
	
	private static Dimension textDimensionsBold(String pString)
	{
		Dimension result = Dimension.NULL;
		if( pString.length() > 0 )
		{
			Dimension dimension = TYPE_NAME_RENDERER.getDimension(pString);
			result = new Dimension(dimension.width() + 2 * HORIZONTAL_PADDING, dimension.height());
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
		return new Rectangle(node.position().x(), node.position().y(), width, height);
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
