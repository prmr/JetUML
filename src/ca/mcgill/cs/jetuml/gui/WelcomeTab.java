/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2017 by the contributors of the JetUML project.
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

import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A tab that allow users to open new diagrams of the different types
 * or open recently saved diagrams.
 * 
 * @author JoelChev - Original Code
 * @author Kaylee I. Kutschera - Migration to JavaFX
 * @author Martin P. Robillard - Refactoring and new layout
 */
public class WelcomeTab extends Tab
{
	private static final ResourceBundle WELCOME_RESOURCES = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings");
    
	/**
	 * @param pNewDiagramMap a map containing the name and handler corresponding to the creation of a new diagram.
	 * @param pRecentFilesMap a map containing the name and handler corresponding to opening a recent file.
	 */
	public WelcomeTab(Map<String, EventHandler<ActionEvent>> pNewDiagramMap, Map<String, EventHandler<ActionEvent>> pRecentFilesMap)
	{
		super(WELCOME_RESOURCES.getString("welcome.title"));
		setClosable(false);
		
		BorderPane layout = new BorderPane();
		layout.getStyleClass().add("welcome-tab");
		HBox shortcutPanel = new HBox();
		shortcutPanel.getStyleClass().add("panel");
		shortcutPanel.getChildren().addAll(createDiagramPanel(pNewDiagramMap), createFilePanel(pRecentFilesMap));
		layout.setCenter(shortcutPanel);
		layout.setBottom(createFootTextPanel());
	    
	    setContent(layout);
	}
		
	private VBox createDiagramPanel(Map<String, EventHandler<ActionEvent>> pNewDiagramMap)
	{
		HBox titleBox = new HBox();
		titleBox.getStyleClass().add("panel-title");
		titleBox.getChildren().addAll(new Label(WELCOME_RESOURCES.getString("welcome.create.text")));

		VBox diagramBox = new VBox();
		diagramBox.getStyleClass().add("panel-content");
		diagramBox.getChildren().add(titleBox);
		for(Map.Entry<String, EventHandler<ActionEvent>> entry : pNewDiagramMap.entrySet())
		{
			Button newDiagramShortcut = new Button(entry.getKey());
			newDiagramShortcut.setOnAction(entry.getValue());
			diagramBox.getChildren().add(newDiagramShortcut);
		}
		return diagramBox;
	}
	
	private VBox createFilePanel(Map<String, EventHandler<ActionEvent>> pRecentFilesMap)
	{
		HBox titleBox = new HBox();
		titleBox.getStyleClass().add("panel-title");
		titleBox.getChildren().add(new Label(WELCOME_RESOURCES.getString("welcome.open.text")));

		VBox fileBox = new VBox();
		fileBox.getStyleClass().add("panel-content");
		fileBox.getChildren().add(titleBox);

		for(Map.Entry<String, EventHandler<ActionEvent>> entry : pRecentFilesMap.entrySet())
		{
			Button fileShortcut = new Button(entry.getKey());
			fileShortcut.setOnAction(entry.getValue());
			fileBox.getChildren().add(fileShortcut);
		}
		return fileBox;
	}


	private HBox createFootTextPanel()
	{
		HBox footTextPanel = new HBox();
		footTextPanel.getStyleClass().add("footer");
		footTextPanel.getChildren().add(new Label(WELCOME_RESOURCES.getString("welcome.copyright")));
		return footTextPanel;
	}
}	
