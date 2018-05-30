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

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.util.Optional;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.BooleanPreference;
import ca.mcgill.cs.jetuml.application.UserPreferences.BooleanPreferenceChangeHandler;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.views.ImageCreator;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

/**
 *  A tool bar than contains various tools and command shortcut buttons. 
 *  Only one tool can be selected at the time. The tool bar also controls a pop-up 
 *  menu with the same tools as the tool bar. Labels can optionally be shown next 
 *  to tools.
 */
public class DiagramTabToolBar extends ToolBar implements BooleanPreferenceChangeHandler
{
	private ContextMenu aPopupMenu = new ContextMenu();

	/**
     * Constructs the tool bar.
     * 
     * @param pDiagram The diagram associated with this tool bar.
	 */
	public DiagramTabToolBar(Diagram pDiagram)
	{
		setOrientation(Orientation.VERTICAL);
		setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;"); 
		ToggleGroup toggleGroup = new ToggleGroup();
		installSelectionTool(toggleGroup);
		installNodesAndEdgesTools(pDiagram, toggleGroup);
		installCopyToClipboard();
    	showButtonLabels( UserPreferences.instance().getBoolean(BooleanPreference.showToolHints ));
	}
	
	private void installSelectionTool(ToggleGroup pToggleGroup)
	{
		add(new SelectableToolButton(ImageCreator.createSelectionImage(), 
									 RESOURCES.getString("toolbar.select.tooltip"), 
									 pToggleGroup));
	}
	
	private void installNodesAndEdgesTools(Diagram pGraph, ToggleGroup pToggleGroup)
	{
		Node[] nodeTypes = pGraph.getNodePrototypes();
		for(int i = 0; i < nodeTypes.length; i++)
		{
			add(new SelectableToolButton(ImageCreator.createImage(nodeTypes[i]), 
					RESOURCES.getString(pGraph.getClass().getSimpleName().toLowerCase() + ".node" + (i + 1) + ".tooltip"), 
					pToggleGroup, nodeTypes[i]));
		}
		Edge[] edgeTypes = pGraph.getEdgePrototypes();
		for(int i = 0; i < edgeTypes.length; i++)
		{
			add(new SelectableToolButton(ImageCreator.createImage(edgeTypes[i]), 
					RESOURCES.getString(pGraph.getClass().getSimpleName().toLowerCase() + ".edge" + (i + 1) + ".tooltip"), 
					pToggleGroup, edgeTypes[i]));
		}
	}
	
	private void installCopyToClipboard()
	{
		final Button button = new Button();
		button.setGraphic(new ImageView(RESOURCES.getString("toolbar.toclipboard.icon")));
		button.setTooltip( new Tooltip(RESOURCES.getString("toolbar.toclipboard.tooltip")));
		button.setOnAction(pEvent-> 
		{
			copyToClipboard();
			getSelectedTool().requestFocus();
		});
		button.setStyle("-fx-background-radius: 0");
		button.setAlignment(Pos.BASELINE_LEFT);
		assert getItems().size() > 0; // We copy size information from the top button
		button.prefWidthProperty().bind(((ToggleButton)getItems().get(0)).widthProperty());
		button.prefHeightProperty().bind(((ToggleButton)getItems().get(0)).heightProperty());
		add(button);
	}
	
	/**
	 * Adds the button to this toolbar and the corresponding context menu.
	 * 
	 * @param pButton The button to add.
	 */
	private void add(ButtonBase pButton)
	{
		assert pButton != null;
		getItems().add( pButton );
		MenuItem item = new MenuItem(pButton.getTooltip().getText());
		item.setGraphic(new ImageView(((ImageView)pButton.getGraphic()).getImage()));
		item.setOnAction(pButton.getOnAction());
		aPopupMenu.getItems().add(item);
	}
	
	private ToggleButton getSelectedTool()
	{
		assert getItems().size() > 0;
		return (ToggleButton) ((ToggleButton)getItems().get(0)).getToggleGroup().getSelectedToggle();
	}
	
	/**
     * Gets the node or edge prototype that is associated with
     * the currently selected button, if available. A tool is unavailable if 
     * the select tool is currently selected.
     * @return a Node or Edge prototype if present.
	 */
	public Optional<DiagramElement> getCreationPrototype()
	{
		assert getItems().size() > 0;
		return ((SelectableToolButton)((ToggleButton) getItems().get(0)).
				getToggleGroup().getSelectedToggle()).getPrototype();
	}
	
	private void copyToClipboard()
	{
		Parent parent = getParent();
		while( parent.getClass() != EditorFrame.class )
		{
			parent = parent.getParent();
		}
		((EditorFrame)parent).copyToClipboard();	
	}
	
	/**
	 * Show the pop-up menu corresponding to this toolbar.
	 * @param pScreenXCoordinate The X-coordinate where to position the menu, in screen coordinates.
	 * @param pScreenYCoordinate The Y-coordinate where to position the menu, in screen coordinates.
	 */
	public void showPopup(double pScreenXCoordinate, double pScreenYCoordinate) 
	{
		aPopupMenu.show(this, pScreenXCoordinate, pScreenYCoordinate);
	}
	
	/**
	 * Overrides the currently selected tool to be the grabber tool instead.
	 */
	public void setToolToBeSelect()
	{
		assert getItems().size() > 0;
		((ToggleButton)getItems().get(0)).setSelected(true);
	}
	
	/**
	 * Shows or hides the textual description of the tools and commands.
	 * @param pShow True if the labels should be shown
	 */
	private void showButtonLabels(boolean pShow)
	{
		for( javafx.scene.Node item : getItems() )
		{
			ButtonBase button = (ButtonBase) item;
			if( pShow )
			{
				button.setText(button.getTooltip().getText());
				button.setMaxWidth(Double.MAX_VALUE);
			}
			else
			{
				button.setText("");
				button.autosize();
			}
		}
	}

	@Override
	public void preferenceChanged(BooleanPreference pPreference)
	{
		if( pPreference == BooleanPreference.showToolHints )
		{
			showButtonLabels(UserPreferences.instance().getBoolean(BooleanPreference.showToolHints));
		}
	}
}