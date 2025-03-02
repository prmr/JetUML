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

import java.util.Optional;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.TypeNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.StringRenderer;
import org.jetuml.rendering.StringRenderer.Alignment;
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
	private static final StringRenderer NAME_VIEWER = StringRenderer.get(Alignment.CENTER_CENTER, Decoration.BOLD, Decoration.PADDED);
	private static final StringRenderer ITALIC_NAME_VIEWER = StringRenderer.get(
			Alignment.CENTER_CENTER, Decoration.BOLD, Decoration.ITALIC, Decoration.PADDED);
	private static final StringRenderer STRING_VIEWER = StringRenderer.get(Alignment.TOP_LEFT, Decoration.PADDED);
	private static final StringRenderer UNDERLINING_STRING_VIEWER = StringRenderer.get(
			Alignment.TOP_LEFT, Decoration.PADDED, Decoration.UNDERLINED);
	private static final StringRenderer ITALIC_STRING_VIEWER = StringRenderer.get(
			Alignment.TOP_LEFT, Decoration.PADDED, Decoration.ITALIC);
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
	}
	
	/*
	 * @param pName The possibly multi-line text to draw in the name box.
	 * @param pBounds The bounds of the name box.
	 * @param pContext The rendering context
	 */
	private static void drawName(String pName, Rectangle pBounds, RenderingContext pContext)
	{
		String[] nameByLine = pName.trim().split("\n");
		int numLines = nameByLine.length;
		
		for( int i = 0; i < numLines; i++ )
		{
			boolean italic = false;
			String paddedName = "";
			if( containsMarkup(nameByLine[i], ITALIC_MARKUP) )
			{
				nameByLine[i] = removeMarkup(nameByLine[i]);
				italic = true;
			}
			// We pad each line to maintain centering of entire name in the node. This allows us to render the name line by line.
			for( int j = 0; j < numLines; j++ )
			{
				if( i == j )
				{
					paddedName += nameByLine[i];
				}
				else
				{
					paddedName += "\n";
				}
			}
			if( italic )
			{
				ITALIC_NAME_VIEWER.draw(paddedName, pContext, pBounds);
			}
			else
			{
				NAME_VIEWER.draw(paddedName, pContext, pBounds);
			}
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
				UNDERLINING_STRING_VIEWER.draw(removeMarkup(attribute), pContext, 
						new Rectangle(pBounds.x(), pBounds.y() + lineSpacing, pBounds.width(), STRING_VIEWER.getHeight()));
			}
			else
			{
				STRING_VIEWER.draw(attribute, pContext, 
						new Rectangle(pBounds.x(), pBounds.y() + lineSpacing, pBounds.width(), STRING_VIEWER.getHeight()));
			}
			lineSpacing += STRING_VIEWER.getHeight();
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
				UNDERLINING_STRING_VIEWER.draw(removeMarkup(method), pContext, 
						new Rectangle(pBounds.x(), pBounds.y() + lineSpacing, pBounds.width(), STRING_VIEWER.getHeight()));
			}
			else if( containsMarkup(method, ITALIC_MARKUP) )
			{
				ITALIC_STRING_VIEWER.draw(removeMarkup(method), pContext, 
						new Rectangle(pBounds.x(), pBounds.y() + lineSpacing, pBounds.width(), STRING_VIEWER.getHeight()));
			}
			else
			{
				STRING_VIEWER.draw(method, pContext, 
						new Rectangle(pBounds.x(), pBounds.y() + lineSpacing, pBounds.width(), STRING_VIEWER.getHeight()));
			}
			lineSpacing += STRING_VIEWER.getHeight();
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
