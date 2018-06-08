/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.gui;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.BooleanPreference;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.stage.Screen;

/**
 * A collection of utility methods to deal with general
 * GUI concerns.
 */
public final class GuiUtils
{
	private static final int MARGIN_SCREEN = 8; // Fraction of the screen to leave around the sides
	private static final int WIDTH_PADDING = 32; // Number of pixels of differences between stage and canvas width. Accounts for frame border.
	private static final int HEIGHT_PADDING = 128; // Number of pixels of difference between stage and canvas height. Accounts for borders and menus.
	private static final int TOOLBAR_WIDTH_COLLAPSED = 60; // Placeholder for the toolbar in collapsed mode
	private static final int TOOLBAR_WIDTH_EXPANDED = 150; // Placeholder for the toolbar in expanded mode
	
	private GuiUtils()
	{}
	
	/**
	 * @return a bounding box that represents the default
	 * bounds of the main stage. Finds the visual bounds 
	 * of the primary screen and removes a marging around it.
	 */
	public static Rectangle defaultStageBounds()
	{
		int screenWidth = (int) Screen.getPrimary().getVisualBounds().getWidth();
		int screenHeight = (int) Screen.getPrimary().getVisualBounds().getHeight();
		return new Rectangle( screenWidth / (MARGIN_SCREEN * 2), 
							  screenHeight / (MARGIN_SCREEN * 2),
							  (screenWidth * (MARGIN_SCREEN -1 )) / MARGIN_SCREEN,
							  (screenHeight * (MARGIN_SCREEN -1 ))/ MARGIN_SCREEN);
	}
	
	/**
	 * @return A preferred value for the width of new diagrams.
	 */
	public static int defaultDiagramWidth()
	{
		if( UserPreferences.instance().getBoolean(BooleanPreference.showToolHints))
		{
			return defaultStageBounds().getWidth() - WIDTH_PADDING - TOOLBAR_WIDTH_EXPANDED;
		}
		else
		{
			return defaultStageBounds().getWidth() - WIDTH_PADDING - TOOLBAR_WIDTH_COLLAPSED;
		}
	}
	
	/**
	 * @return A preferred value for the height of new diagrams.
	 */
	public static int defaultDiagramHeight()
	{
		return defaultStageBounds().getHeight() - HEIGHT_PADDING;
	}
}
