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
package org.jetuml.rendering;

import org.jetuml.geom.Dimension;
import org.jetuml.geom.GeomUtils;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A utility class to determine various font metrics
 * for the particular text and font.
 * 
 * A visual diagram for why the bounds values are what they are
 * (with word "Thy"):   ____________________
 * (ascent)            |*****  *           |
 *                     |  *    *           |
 *                     |  *    *****   *  *|
 *                     |  *    *   *   *  *|
 *                     |  *    *   *   ****|
 * (baseline)          |------------------*|
 *                     |                  *|
 *                     |                  *| 
 * (descent)           |                ***|
 *                     |                   |
 *                     |                   |
 * (leading)           |-------------------|
 */
public final class FontMetrics 
{
	public static final int DEFAULT_FONT_SIZE = 12;
	public static final String DEFAULT_FONT_NAME = "System";
	public static final Font DEFAULT_FONT = Font.font(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE);
	private static final String SINGLE_LINED_TEXT = "One";
	private static final String TWO_LINED_TEXT = "One\nTwo";
	private static Text aTextNode = new Text();

	private FontMetrics() {}

	/**
	 * Returns the dimension of a given string.
	 * For the fonts supported in JetUML, the dimension includes the leading space in the height.
	 * However, this behavior is not consistent across all fonts.
	 * 
	 * @param pString The string to which the bounds pertain.
	 * @return The dimension of the string
	 */
	public static Dimension getDimension(String pString, Font pFont)
	{
		assert pString != null;

		aTextNode.setFont(pFont);
		aTextNode.setText(pString);
		Bounds bounds = aTextNode.getLayoutBounds();
		return new Dimension(GeomUtils.round(bounds.getWidth()), GeomUtils.round(bounds.getHeight()));
	}
	
	/**
	 * Returns the distance between the top and bottom of a single lined text.
	 * Text#getLayoutBounds().getHeight() varies in its inclusion of the leading space depending on the font,
	 * hence the subtraction approach was taken to ensure inclusion of the leading space.
	 * 
	 * @param pString The string. 
	 * @return The height of a single lined text.
	 * @pre pString != null
	 */
	public static int getHeight(Font pFont)
	{
		aTextNode.setFont(pFont);
		aTextNode.setText(TWO_LINED_TEXT);
		double twoLineHeight = aTextNode.getLayoutBounds().getHeight();
		aTextNode.setText(SINGLE_LINED_TEXT);
		double singleLineHeight = aTextNode.getLayoutBounds().getHeight();
		return GeomUtils.round(twoLineHeight - singleLineHeight);
	}
	
	/**
	 * Returns the distance between the top and baseline of a single lined text.
	 * 
	 * @param pBold Whether the font is bold.
	 * @param pItalic whether the font is italic.
	 * @return the distance above the baseline for a single lined text.
	 */
	public static int getBaselineOffset(Font pFont)
	{
		aTextNode.setFont(pFont);
		aTextNode.setText(SINGLE_LINED_TEXT);
		return GeomUtils.round(aTextNode.getBaselineOffset());
	}
} 