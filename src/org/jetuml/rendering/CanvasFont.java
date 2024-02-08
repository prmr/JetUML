package org.jetuml.rendering;

import org.jetuml.annotations.Singleton;
import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.IntegerPreferenceChangeHandler;
import org.jetuml.geom.Dimension;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Responsible for performing more rudimentary operations involving font,
 * as well as being synchronized with the user's current font.
 */
@Singleton
public final class CanvasFont implements IntegerPreferenceChangeHandler
{
	public static final CanvasFont INSTANCE = new CanvasFont();
	
	private Font aFont;
	private Font aFontBold;
	private Font aFontItalic;
	private Font aFontBoldItalic;
	private FontMetrics aFontMetrics;
	private FontMetrics aFontBoldMetrics;
	private FontMetrics aFontItalicMetrics;
	private FontMetrics aFontBoldItalicMetrics;

	private CanvasFont()
	{
		refreshAttributes();
		UserPreferences.instance().addIntegerPreferenceChangeHandler(this);
	}
	
	/**
	 * @return The CanvasFont singleton instance.
	 */
	public static CanvasFont instance()
	{
		return INSTANCE;
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

	@Override
	public void integerPreferenceChanged(IntegerPreference pPreference) 
	{
		if ( pPreference == IntegerPreference.fontSize && aFont.getSize() != UserPreferences.instance().getInteger(pPreference) )
		{
			refreshAttributes();
		}

	}

	private void refreshAttributes()
	{
		aFont = Font.font("System", UserPreferences.instance().getInteger(IntegerPreference.fontSize));
		aFontBold = Font.font(aFont.getFamily(), FontWeight.BOLD, aFont.getSize());
		aFontItalic = Font.font(aFont.getFamily(), FontPosture.ITALIC, aFont.getSize());
		aFontBoldItalic = Font.font(aFont.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, aFont.getSize());
		aFontMetrics = new FontMetrics(aFont);
		aFontBoldMetrics = new FontMetrics(aFontBold);
		aFontItalicMetrics = new FontMetrics(aFontItalic);
		aFontBoldItalicMetrics = new FontMetrics(aFontBoldItalic);
	}

}
