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

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.IntegerPreferenceChangeHandler;
import org.jetuml.application.UserPreferences.StringPreference;
import org.jetuml.application.UserPreferences.StringPreferenceChangeHandler;
import org.jetuml.geom.Dimension;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * A utility class for StringRenderer that is in sync with  
 * the user font size setting and manages font metrics calculations.
 */
public final class CanvasFont implements IntegerPreferenceChangeHandler, StringPreferenceChangeHandler
{	
	private Font aFont;
	private Font aFontBold;
	private Font aFontItalic;
	private Font aFontBoldItalic;
	private FontMetrics aFontMetrics;
	private FontMetrics aFontBoldMetrics;
	private FontMetrics aFontItalicMetrics;
	private FontMetrics aFontBoldItalicMetrics;

	/**
	 * Initializes its attributes based on the user's preferences.
	 */
	public CanvasFont()
	{
		refreshAttributes();
		UserPreferences.instance().addIntegerPreferenceChangeHandler(this);
		UserPreferences.instance().addStringPreferenceChangeHandler(this);
	}
	
	
	/**
	 * @param pBold Whether the font is bold.
	 * @param pItalic Whether the font is italic.
	 * @return The font.
	 */
	public Font getFont(boolean pBold, boolean pItalic)
	{
		if( pBold && pItalic )
		{
			return aFontBoldItalic;
		}
		else if( pBold )
		{
			return aFontBold;
		}
		else if( pItalic )
		{
			return aFontItalic;
		}
		return aFont;
	}

	private FontMetrics getFontMetrics(boolean pBold, boolean pItalic)
	{
		if( pBold && pItalic )
		{
			return aFontBoldItalicMetrics;
		}
		else if( pBold )
		{
			return aFontBoldMetrics;
		}
		else if( pItalic )
		{
			return aFontItalicMetrics;
		}
		return aFontMetrics;
	}

	/**
	 * Returns the dimension of a given string.
	 * @param pString The string to which the bounds pertain.
	 * @param pBold Whether the text is in bold.
	 * @param pItalic Whether the text is in italic.
	 * @return The dimension of the string.
	 */
	public Dimension getDimension(String pString, boolean pBold, boolean pItalic)
	{
		return getFontMetrics(pBold, pItalic).getDimension(pString);
	}
	
	/**
	 * Returns the height of a string including the leading space.
	 * 
	 * @param pString The string.
	 * @param pBold Whether the text is in bold.
	 * @param pItalic Whether the text is in italic.
	 * @return The height of the string.
	 */
	public int getHeight(String pString, boolean pBold, boolean pItalic)
	{
		return getFontMetrics(pBold, pItalic).getHeight(pString);
	}
	
	public int getBaselineOffset(boolean pBold, boolean pItalic)
	{
		return getFontMetrics(pBold, pItalic).getBaselineOffset();
	}

	@Override
	public void integerPreferenceChanged(IntegerPreference pPreference) 
	{
		if ( pPreference == IntegerPreference.fontSize && aFont.getSize() != UserPreferences.instance().getInteger(pPreference) )
		{
			refreshAttributes();
		}

	}
	
	@Override
	public void stringPreferenceChanged(StringPreference pPreference) 
	{
		if ( pPreference == StringPreference.fontName && !aFont.getFamily().equals(UserPreferences.instance().getString(pPreference)) )
		{
			refreshAttributes();
		}
	}

	private void refreshAttributes()
	{
		aFont = Font.font(UserPreferences.instance().getString(StringPreference.fontName), 
				UserPreferences.instance().getInteger(IntegerPreference.fontSize));
		aFontBold = Font.font(aFont.getFamily(), FontWeight.BOLD, aFont.getSize());
		aFontItalic = Font.font(aFont.getFamily(), FontPosture.ITALIC, aFont.getSize());
		aFontBoldItalic = Font.font(aFont.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, aFont.getSize());
		aFontMetrics = new FontMetrics(aFont);
		aFontBoldMetrics = new FontMetrics(aFontBold);
		aFontItalicMetrics = new FontMetrics(aFontItalic);
		aFontBoldItalicMetrics = new FontMetrics(aFontBoldItalic);
	}

}
