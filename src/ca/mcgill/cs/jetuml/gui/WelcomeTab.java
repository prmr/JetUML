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

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A tab that allow users to open new diagrams of the different types
 * or open recently saved diagrams.
 */
public class WelcomeTab extends Tab
{
	/* CSS classes of the different GUI elements in the welcome tab. See UMLEditorStyle.css for the styling */
	private static final String CLASS_WELCOME_TAB_PANEL = "welcome-tab-panel"; 	// One column in the welcome tab
	private static final String CLASS_PANEL_TITLE = "panel-title"; 				// The title/header above each of the two columns
	private static final String CLASS_FOOTER = "welcome-tab-footer"; 						// The footer with the copyright information
    
	/**
	 * @param pNewDiagramHandlers A list of named handlers for opening new diagrams. The name 
	 * is expected to be the simple name of a concrete diagram, all in lower case.
	 */
	public WelcomeTab(List<NewDiagramHandler> pNewDiagramHandlers)
	{
		super(RESOURCES.getString("welcome.title"));
		setClosable(false);
		
		BorderPane layout = new BorderPane();
		
		HBox shortcutPanel = new HBox();
		shortcutPanel.setAlignment(Pos.CENTER);
		shortcutPanel.getChildren().addAll(createDiagramPanel(pNewDiagramHandlers), createFilePanel());
		layout.setCenter(shortcutPanel);
		layout.setBottom(createFootTextPanel());
	    
	    setContent(layout);
	}
		
	private VBox createDiagramPanel(List<NewDiagramHandler> pNewDiagramHandlers)
	{
		HBox titleBox = new HBox();
		titleBox.getStyleClass().add(CLASS_PANEL_TITLE);
		titleBox.getChildren().addAll(new Label(RESOURCES.getString("welcome.create.text")));

		VBox diagramBox = new VBox();
		diagramBox.getStyleClass().add(CLASS_WELCOME_TAB_PANEL);
		diagramBox.getChildren().add(titleBox);
		for(NewDiagramHandler handler : pNewDiagramHandlers)
		{
			Button newDiagramShortcut = new Button(RESOURCES.getString(handler.getDiagramType().getName() + ".text"));
			newDiagramShortcut.setOnAction(handler);
			diagramBox.getChildren().add(newDiagramShortcut);
		}
		return diagramBox;
	}
	
	/**
	 * Loads the links to recent files into the panel.
	 * 
	 * @param pFileOpenHanders The file handlers.
	 */
	public void loadRecentFileLinks(List<NamedHandler> pFileOpenHanders)
	{
		VBox filesNode = (VBox) ((HBox)((BorderPane) getContent()).getCenter()).getChildren().get(1);
		filesNode.getChildren().remove(1, filesNode.getChildren().size());
		for(NamedHandler handler : pFileOpenHanders)
		{
			Button fileShortcut = new Button(handler.getName());
			fileShortcut.setOnAction(handler);
			filesNode.getChildren().add(fileShortcut);
		}
	}
	
	private VBox createFilePanel()
	{
		HBox titleBox = new HBox();
		titleBox.getStyleClass().add(CLASS_PANEL_TITLE);
		titleBox.getChildren().add(new Label(RESOURCES.getString("welcome.open.text")));

		VBox fileBox = new VBox();
		fileBox.getStyleClass().add(CLASS_WELCOME_TAB_PANEL);
		fileBox.getChildren().add(titleBox);

		return fileBox;
	}


	private HBox createFootTextPanel()
	{
		HBox footTextPanel = new HBox();
		footTextPanel.getStyleClass().add(CLASS_FOOTER);
		footTextPanel.getChildren().add(new Label(RESOURCES.getString("application.copyright")));
		return footTextPanel;
	}
}	
