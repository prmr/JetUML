/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
 * 
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.rendering;

import java.util.Collections;
import java.util.EnumSet;

import org.jetuml.annotations.Immutable;
import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;

import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * A class to render strings with various decorations: underline, bold,
 * with different alignments.
 */
@Immutable
public final class StringRenderer
{
	private static final int PADDING_HORIZONTAL = 7;
	private static final int PADDING_VERTICAL = 6;

	/**
	 * How to position this string within its bounding box.
	 */
	public enum Alignment
	{
		TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;

		private boolean isTop()
		{
			return this == TOP_LEFT || this == TOP_CENTER || this == TOP_RIGHT;
		}

		private boolean isVerticallyCentered()
		{
			return this == CENTER_LEFT || this == CENTER_CENTER || this == CENTER_RIGHT;
		}

		private boolean isBottom()
		{
			return this == BOTTOM_LEFT || this == BOTTOM_CENTER || this == BOTTOM_RIGHT;
		}

		private boolean isLeft()
		{
			return this == TOP_LEFT || this == CENTER_LEFT || this == BOTTOM_LEFT;
		}

		private boolean isHorizontallyCentered()
		{
			return this == TOP_CENTER || this == CENTER_CENTER || this == BOTTOM_CENTER;
		}
	}

	/**
	 * Various text decorations.
	 */
	public enum Decoration
	{
		BOLD, ITALIC, UNDERLINED, PADDED
	}

	private final Alignment aAlign;
	private final EnumSet<Decoration> aDecorations;

	/**
	 * Creates a new String Renderer.
	 * 
	 * @param pAlign The desired alignment.
	 * @param pDecorations The desired decorations.
	 */
	public StringRenderer(Alignment pAlign, Decoration... pDecorations)
	{
		aAlign = pAlign;
		aDecorations = EnumSet.noneOf(Decoration.class);
		Collections.addAll(aDecorations, pDecorations);
	}
	
	private int verticalPadding()
	{
		if (aDecorations.contains(Decoration.PADDED))
		{
			return PADDING_VERTICAL;
		}
		else
		{
			return 0;
		}
	}
	
	private int horizontalPadding()
	{
		if (aDecorations.contains(Decoration.PADDED))
		{
			return PADDING_HORIZONTAL;
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * Draws the string inside a given bounding box.
	 * 
	 * @param pString The string to draw.
	 * @param pBoundingBox the rectangle into which to place the string.
	 * @param pContext The rendering context on which to draw the text.
	 */
	public void draw(String pString, Rectangle pBoundingBox, RenderingContext pContext)
	{
		int textX = 0;
		int textY = 0;
		if( aAlign.isHorizontallyCentered() )
		{
			textX = pBoundingBox.width() / 2;
		}
		else
		{
			textX = horizontalPadding();
		}

		if( aAlign.isVerticallyCentered() )
		{
			textY = pBoundingBox.height() / 2;
		}

		pContext.drawText(pString, pBoundingBox, textX, textY, getTextAlignment(), getTextBaseline(), 
				ColorScheme.get().stroke(),
				getFont());

		if( aDecorations.contains(Decoration.UNDERLINED) && pString.trim().length() > 0 )
		{
			int xOffset = 0;
			int yOffset = 0;
			Dimension dimension = FontMetrics.getDimension(pString, getFont());
			int baselineOffset = FontMetrics.getBaselineOffset(getFont());
			if( aAlign.isHorizontallyCentered() )
			{
				xOffset = dimension.width() / 2;
			}

			if( aAlign.isTop() )
			{
				yOffset = baselineOffset + 2;
			}
			else if( aAlign.isVerticallyCentered() )
			{
				yOffset = baselineOffset / 2 + 1;
			}
			pContext.strokeLine(pBoundingBox.x() + textX - xOffset, pBoundingBox.y() + textY + yOffset,
					pBoundingBox.x()+ textX - xOffset + dimension.width(), pBoundingBox.y() + textY + yOffset, 
					ColorScheme.get().stroke(),
					LineStyle.SOLID);
		}
	}

	/**
	 * Gets the width and height required to show pString, including padding
	 * around the string.
	 * 
	 * @param pString The input string.
	 * @return The dimension pString will use on the screen.
	 * @pre pString != null.
	 */
	public Dimension getDimension(String pString)
	{
		assert pString != null;
		if( pString.length() == 0 )
		{
			return Dimension.NULL;
		}
		Dimension dimension = FontMetrics.getDimension(pString, getFont());
		return new Dimension(dimension.width() + horizontalPadding() * 2, dimension.height() + verticalPadding() * 2);
	}

	/**
	 * Returns the distance between the top and bottom of a single lined text.
	 * 
	 * @param pString The string.
	 * @return The height of the string.
	 * @pre pString != null
	 */
	public int getHeight()
	{
		return FontMetrics.getHeight(getFont());
	}

	private TextAlignment getTextAlignment()
	{
		if( aAlign.isLeft() )
		{
			return TextAlignment.LEFT;
		}
		else if( aAlign.isHorizontallyCentered() )
		{
			return TextAlignment.CENTER;
		}
		return TextAlignment.RIGHT;
	}

	private VPos getTextBaseline()
	{
		if( aAlign.isBottom() )
		{
			return VPos.BASELINE;
		}
		else if( aAlign.isTop() )
		{
			return VPos.TOP;
		}
		return VPos.CENTER;
	}

	private Font getFont()
	{
		if( aDecorations.contains(Decoration.BOLD) && aDecorations.contains(Decoration.ITALIC) )
		{
			return Font.font(UserPreferences.instance().getString(StringPreference.fontName), FontWeight.BOLD,
					FontPosture.ITALIC, UserPreferences.instance().getInteger(IntegerPreference.fontSize));
		}
		else if( aDecorations.contains(Decoration.BOLD) )
		{
			return Font.font(UserPreferences.instance().getString(StringPreference.fontName), FontWeight.BOLD,
					UserPreferences.instance().getInteger(IntegerPreference.fontSize));
		}
		else if( aDecorations.contains(Decoration.ITALIC) )
		{
			return Font.font(UserPreferences.instance().getString(StringPreference.fontName), FontPosture.ITALIC,
					UserPreferences.instance().getInteger(IntegerPreference.fontSize));
		}
		return Font.font(UserPreferences.instance().getString(StringPreference.fontName),
				UserPreferences.instance().getInteger(IntegerPreference.fontSize));
	}
}
