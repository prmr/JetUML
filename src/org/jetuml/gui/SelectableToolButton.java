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
package org.jetuml.gui;

import java.util.Optional;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.BooleanPreference;
import org.jetuml.application.UserPreferences.BooleanPreferenceChangeHandler;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Prototypes;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;

/**
 * A selectable button that wraps a creation tool represented by an 
 * object prototype.
 */
public class SelectableToolButton extends ToggleButton implements BooleanPreferenceChangeHandler
{
	private static final String BUTTON_STYLE_CSS = "-fx-background-radius: 0";
	private static final int TOOLTIP_WIDTH = 250;
	
	private Optional<DiagramElement> aPrototype = Optional.empty(); // If empty, indicates the "Select" tool
	
	/**
	 * Creates a button with an empty tool prototype.
	 *
	 * @param pIcon The button's icon.
	 * @param pToolTip A short sentence describing the button.
	 * @param pToggleGroup The toggle group this button is part of.
	 * @pre pImage != null && pToolTip != null && pToggleGroup != null.
	 */
	public SelectableToolButton(Canvas pIcon, String pToolTip, ToggleGroup pToggleGroup)
	{
		assert pIcon != null && pToolTip != null && pToggleGroup != null;
		setStyle(BUTTON_STYLE_CSS);
		setGraphic(pIcon);
		setToggleGroup(pToggleGroup);
		setSelected(true);
		setTooltip(new Tooltip(pToolTip));
		getTooltip().setStyle("-fx-font-size: 100%"); // Override JavaFX defaults
		getTooltip().setWrapText(true);
		getTooltip().setMaxWidth(TOOLTIP_WIDTH);
		setAlignment(Pos.BASELINE_LEFT);
		setOnAction( pEvent -> setSelected(true) );
	}
	
	/**
	 * Creates a button to represent a node or edge creation tool.
	 *
	 * @param pToolTip A short sentence describing the tool.
	 * @param pToggleGroup The toggle group this button is part of.
	 * @param pPrototype The object prototype for the creation.
	 * @pre pImage != null && pToolTip != null && pToggleGroup != null.
	 */
	public SelectableToolButton(Canvas pIcon, String pToolTip, ToggleGroup pToggleGroup, DiagramElement pPrototype)
	{
		this(pIcon, pToolTip, pToggleGroup);
		aPrototype = Optional.of(pPrototype);
		setSelected(false);
	}
	
	/**
	 * @return The prototype graph element to create new ones. isPresent() == false indicates
	 *     that the Selection Tool is selected.
	 */
	public Optional<DiagramElement> getPrototype()
	{
		return aPrototype;
	}

	@Override
	public void booleanPreferenceChanged(BooleanPreference pPreference)
	{
		if( pPreference == BooleanPreference.verboseToolTips && aPrototype.isPresent())
		{
			getTooltip().setText(Prototypes.instance().tooltip(aPrototype.get(), 
					UserPreferences.instance().getBoolean(BooleanPreference.verboseToolTips)));
		}
	}
}
