package org.jetuml.rendering;

import org.jetuml.annotations.Singleton;
import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.IntegerPreferenceChangeHandler;
import org.jetuml.geom.Dimension;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Responsible for performing more rudimentary operations involving font,
 * as well as being synchronized with the user's current font.
 */
@Singleton
public final class CanvasFont implements IntegerPreferenceChangeHandler
{
	private static final CanvasFont INSTANCE = new CanvasFont();
	
	private Font aFont;
	private Font aFontBold;
	private FontMetrics aFontMetrics;
	private FontMetrics aFontBoldMetrics;
	
	private CanvasFont()
	{
		refreshAttributes();
		UserPreferences.instance().addIntegerPreferenceChangeHandler(this);
	}
	
	/**
	 * @return The singleton CanvasFont instance.
	 */
	public static CanvasFont instance()
	{
		return INSTANCE;
	}

	Font getFont(boolean pBold)
	{
		if ( pBold )
		{
			return aFontBold;
		}
		return aFont;
	}

	private FontMetrics getFontMetrics(boolean pBold)
	{
		if ( pBold )
		{
			return aFontBoldMetrics;
		}
		return aFontMetrics;
	}

	/**
	 * Returns the dimension of a given string.
	 * @param pString The string to which the bounds pertain.
	 * @return The dimension of the string
	 */
	public Dimension getDimension(String pString, boolean pBold)
	{
		return getFontMetrics(pBold).getDimension(pString);
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
		aFontMetrics = new FontMetrics(aFont);
		aFontBoldMetrics = new FontMetrics(aFontBold);
	}
}
