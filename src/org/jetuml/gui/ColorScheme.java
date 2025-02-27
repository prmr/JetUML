package org.jetuml.gui;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.BooleanPreference;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * Defines the color schemes that can be applied to the diagram.
 */
public enum ColorScheme 
{
	LIGHT(Color.WHITE, Color.rgb(220, 220, 220), Color.WHITE, Color.BLACK, Color.color(0.9f, 0.9f, 0.6f), Color.LIGHTGRAY), 
	DARK(Color.rgb(7, 7, 7), Color.rgb(40, 40, 40), Color.rgb(31, 31, 31), Color.WHITE, Color.rgb(30, 63, 102), Color.TRANSPARENT);

	private final Color aCanvas;
	private final Color aGrid;
	private final Color aFill;
	private final Color aStroke;
	private final Color aNote;
	private final DropShadow aShadow;
	
	ColorScheme(Color pCanvas, Color pGrid, Color pFill, Color pStroke, Color pNote, Color pShadow)
	{
		aCanvas = pCanvas;
		aGrid = pGrid;
		aFill = pFill;
		aStroke = pStroke;
		aNote = pNote;
		aShadow = new DropShadow(3, 3, 3, pShadow);
	}
	
	/**
	 * @return The canvas color.
	 */
	public Color background()
	{
		return aCanvas;
	}
	
	/**
	 * @return The canvas color.
	 */
	public Color grid()
	{
		return aGrid;
	}
	
	/**
	 * @return The fill color.
	 */
	public Color fill()
	{
		return aFill;
	}
	
	/**
	 * @return The stroke color.
	 */
	public Color stroke()
	{
		return aStroke;
	}
	
	/**
	 * @return The note color.
	 */
	public Color note()
	{
		return aNote;
	}
	
	/**
	 * @return The drop shadow.
	 */
	public DropShadow dropShadow()
	{
		return aShadow;
	}
	
	/**
	 * @return The color scheme for the user UI theme.
	 */
	public static ColorScheme get()
	{
		if (UserPreferences.instance().getBoolean(BooleanPreference.darkMode))
		{
			return DARK;
		}
		return LIGHT;
	}
}