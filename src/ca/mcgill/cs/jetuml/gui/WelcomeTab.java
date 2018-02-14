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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This class instantiates the Welcome Tab that is the default Tab in JetUML.
 * 
 * @author JoelChev - Original Code
 * @author Kaylee I. Kutschera - Migration to JavaFX
 */
public class WelcomeTab extends Tab
{
	private static final int BORDER_MARGIN = 45;
	private static final int ALTERNATIVE_BORDER_MARGIN = 30;
	private static final int FOOT_BORDER_MARGIN = 10;
	private static final int FONT_SIZE = 25;
	private static final int PADDING = 10;
	private ResourceBundle aWelcomeResources;
    private HBox aFootTextPanel;
    private HBox aRightTitlePanel;
    private HBox aLeftTitlePanel;
    private VBox aLeftPanel;
    private VBox aRightPanel;
    private ImageView aLeftPanelIcon;
    private ImageView aRightPanelIcon;
    private String aFootText;
    
	/**
	 * @param pNewDiagramMap a map containing the name and handler corresponding to the creation of a new diagram.
	 * @param pRecentFilesMap a map containing the name and handler corresponding to opening a recent file.
	 */
	public WelcomeTab(Map<String, EventHandler<ActionEvent>> pNewDiagramMap, Map<String, EventHandler<ActionEvent>> pRecentFilesMap)
	{
		super("Welcome");
		setClosable(false);
		aWelcomeResources = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings");
		aLeftPanelIcon = new ImageView(aWelcomeResources.getString("welcome.create.icon"));
		aRightPanelIcon = new ImageView(aWelcomeResources.getString("welcome.open.icon")); 
		
		BorderPane layout = new BorderPane();
		layout.getStyleClass().add("welcome-tab");
		HBox shortcutPanel = new HBox();
		shortcutPanel.setAlignment(Pos.TOP_CENTER);
		VBox newPanel = new VBox();
		newPanel.setAlignment(Pos.CENTER_RIGHT);
		newPanel.getChildren().addAll(getLeftTitlePanel(), getLeftPanel(pNewDiagramMap));
		VBox recentPanel = new VBox();
		recentPanel.setAlignment(Pos.CENTER_LEFT);
		recentPanel.getChildren().addAll(getRightTitlePanel(), getRightPanel(pRecentFilesMap));

		shortcutPanel.getChildren().addAll(newPanel, recentPanel);
		layout.setCenter(shortcutPanel);
		layout.setBottom(getFootTextPanel());
	    
	    setContent(layout);
	}
		
	private VBox getLeftPanel(Map<String, EventHandler<ActionEvent>> pNewDiagramMap)
	{
		if(aLeftPanel == null)
		{
			aLeftPanel = new VBox();
			for(Map.Entry<String, EventHandler<ActionEvent>> entry : pNewDiagramMap.entrySet())
			{
				String label = entry.getKey();
				Button newDiagramShortcut = new Button(label.toLowerCase());
				newDiagramShortcut.setOnAction(entry.getValue());
				aLeftPanel.getChildren().add(newDiagramShortcut);
			}
		}
		return aLeftPanel;
	}
	
	private VBox getRightPanel(Map<String, EventHandler<ActionEvent>> pRecentFilesMap)
	{
		if(aRightPanel == null)
		{
			aRightPanel = new VBox();
//			aRightPanel.setOpaque(false);
//			aRightPanel.setLayout(new BoxLayout(aRightPanel, BoxLayout.Y_AXIS));
//			aRightPanel.setBorder(new EmptyBorder(0, BORDER_MARGIN, 0, BORDER_MARGIN));

			for(Map.Entry<String, EventHandler<ActionEvent>> entry : pRecentFilesMap.entrySet())
			{
				String label = entry.getKey();
				Button fileShortcut = new Button(label.toLowerCase());
				fileShortcut.setOnAction(entry.getValue());
				aRightPanel.getChildren().add(fileShortcut);
			}

		}
		return this.aRightPanel;
	}

	private HBox getLeftTitlePanel()
	{
		if(aLeftTitlePanel == null)
		{
			Label icon = new Label();
			icon.setGraphic(this.aLeftPanelIcon);

			Label title = new Label(aWelcomeResources.getString("file.new.text").toLowerCase());
//			title.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
//			title.setForeground(Color.DARK_GRAY);
//			title.setBorder(new EmptyBorder(0, ALTERNATIVE_BORDER_MARGIN, 0, 0));

			aLeftTitlePanel = new HBox();
			aLeftTitlePanel.getChildren().addAll(icon, title);
			
//			aLeftTitlePanel.add(panel, BorderLayout.EAST);
//			aLeftTitlePanel.setBorder(new EmptyBorder(0, 0, ALTERNATIVE_BORDER_MARGIN, BORDER_MARGIN));
		
		}
		return aLeftTitlePanel;
	}

	private HBox getRightTitlePanel()
	{
		if(aRightTitlePanel == null)
		{
			Label icon = new Label();
			icon.setGraphic(this.aRightPanelIcon);
//			icon.setAlignmentX(Component.LEFT_ALIGNMENT);

			Label title = new Label(aWelcomeResources.getString("file.recent.text").toLowerCase());
//			title.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
//			title.setForeground(Color.DARK_GRAY);
//			title.setBorder(new EmptyBorder(0, 0, 0, ALTERNATIVE_BORDER_MARGIN));

			aRightTitlePanel = new HBox();
			aRightTitlePanel.getChildren().addAll(title, icon);

//			aRightTitlePanel.setBorder(new EmptyBorder(0, BORDER_MARGIN, ALTERNATIVE_BORDER_MARGIN, 0));
		}
		return aRightTitlePanel;
	}

	private HBox getFootTextPanel()
	{
		if(aFootTextPanel == null)
		{
			aFootText = aWelcomeResources.getString("welcome.copyright");
			aFootTextPanel = new HBox();
			aFootTextPanel.setAlignment(Pos.BASELINE_CENTER);
//			aFootTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

			Label text = new Label(this.aFootText);
//			text.setAlignmentX(Component.CENTER_ALIGNMENT);

			aFootTextPanel.getChildren().add(text);
		}

		return aFootTextPanel;
	}

}	
