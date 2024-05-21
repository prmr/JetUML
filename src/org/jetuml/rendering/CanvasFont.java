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

import org.jetuml.annotations.Singleton;
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
 * A utility class for StringRenderer objects.
 * CanvasFont is in sync with user font settings, and 
 * it manages font metrics calculations through FontMetrics.
 */
@Singleton
public final class CanvasFont implements IntegerPreferenceChangeHandler, StringPreferenceChangeHandler
{	
	private Font aFont;
	private Font aFontBold;
	private Font aFontItalic;
	private Font aFontBoldItalic;
	private FontMetrics aFontMetrics = new FontMetrics();

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

	/**
	 * Returns the dimension of a given string.
	 * 
	 * @param pString The string to which the bounds pertain.
	 * @param pBold Whether the text is in bold.
	 * @param pItalic Whether the text is in italic.
	 * @return The dimension of the string.
	 * @pre pString != null
	 */
	public Dimension getDimension(String pString, boolean pBold, boolean pItalic)
	{
		assert pString != null;
		
		return aFontMetrics.getDimension(pString, getFont(pBold, pItalic));
	}
	
	/**
	 * Returns the distance between the top and bottom of a single lined text.
	 * 
	 * @param pBold Whether the text is in bold.
	 * @param pItalic Whether the text is in italic.
	 * @return The height of the string.
	 * @pre pString != null
	 */
	public int getHeight(boolean pBold, boolean pItalic)
	{
		return aFontMetrics.getHeight(getFont(pBold, pItalic));
	}
	
	/**
	 * Returns the distance between the top and baseline of a single lined text.
	 * 
	 * @param pBold Whether the font is bold.
	 * @param pItalic whether the font is italic.
	 * @return the distance above the baseline for a single lined text.
	 */
	public int getBaselineOffset(boolean pBold, boolean pItalic)
	{
		return aFontMetrics.getBaselineOffset(getFont(pBold, pItalic));
	}

	@Override
	public void integerPreferenceChanged(IntegerPreference pPreference) 
	{
		if( pPreference == IntegerPreference.fontSize && aFont.getSize() != UserPreferences.instance().getInteger(pPreference) )
		{
			refreshAttributes();
		}

	}
	
	@Override
	public void stringPreferenceChanged(StringPreference pPreference)
	{
		if( pPreference == StringPreference.fontName && !aFont.getFamily().equals(UserPreferences.instance().getString(pPreference)) )
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
	}

}
