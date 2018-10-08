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

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A selectable button in the DiagramTabToolBar. 
 */
public class SelectableToolButton extends ToggleButton
{
	// If empty, indicates the "Select" tool
	private final Optional<DiagramElement> aPrototype;
	
	/**
	 * Creates a button to represent the "select" tool.
	 *
	 * @param pImage The image representing the tool.
	 * @param pToolTip A short sentence describing the tool.
	 * @param pToggleGroup The toggle group this button is part of.
	 * @pre pImage != null && pToolTip != null && pToggleGroup != null.
	 */
	public SelectableToolButton(Image pImage, String pToolTip, ToggleGroup pToggleGroup)
	{
		assert pImage != null && pToolTip != null && pToggleGroup != null;
		setStyle("-fx-background-radius: 0");
		aPrototype = Optional.empty();
		setGraphic(new ImageView(pImage));
		setToggleGroup(pToggleGroup);
		setSelected(true);
		setTooltip(new Tooltip(pToolTip));
		setAlignment(Pos.BASELINE_LEFT);
		setOnAction( pEvent -> setSelected(true) );
	}
	
	/**
	 * Creates a button to represent a node or edge creation tool.
	 *
	 * @param pImage The image representing the tool.
	 * @param pToolTip A short sentence describing the tool.
	 * @param pToggleGroup The toggle group this button is part of.
	 * @param pPrototype The object prototype for the creation.
	 * @pre pImage != null && pToolTip != null && pToggleGroup != null.
	 */
	public SelectableToolButton(Image pImage, String pToolTip, ToggleGroup pToggleGroup, DiagramElement pPrototype)
	{
		// Note: we don't use a this(...) constructor call here to be able to set aPrototype to final.
		assert pImage != null && pToolTip != null && pToggleGroup != null;
		setStyle("-fx-background-radius: 0");
		aPrototype = Optional.of(pPrototype);
		setGraphic(new ImageView(pImage));
		setToggleGroup(pToggleGroup);
		setSelected(false);
		setTooltip(new Tooltip(pToolTip));
		setAlignment(Pos.BASELINE_LEFT);
		setOnAction( pEvent -> setSelected(true) );
	}
	
	/**
	 * @return The prototype graph element to create new ones. isPresent() == false indicates
	 * that the Selection Tool is selected.
	 */
	public Optional<DiagramElement> getPrototype()
	{
		return aPrototype;
	}
}
