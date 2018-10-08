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
import static ca.mcgill.cs.jetuml.application.DiagramSizeUtils.MIN_SIZE;

import ca.mcgill.cs.jetuml.application.DiagramSizeUtils;

import static ca.mcgill.cs.jetuml.application.DiagramSizeUtils.MAX_SIZE;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A modal dialog that allows users to set the size of
 * the future diagrams to be created.
 */
public class DiagramSizeDialog
{
	private static final int SPACING = 10;
	private static final int VSPACE = 20;
	
	private final Stage aStage = new Stage();
	private final TextField aWidthField = new TextField();
	private final TextField aHeightField = new TextField();
	
	/**
	 * Creates a new dialog.
	 * 
	 * @param pOwner The stage that owns this stage.
	 */
	public DiagramSizeDialog( Stage pOwner )
	{
		prepareStage(pOwner);
		aStage.setScene(createScene());
	}
	
	private void prepareStage(Stage pOwner) 
	{
		aStage.setResizable(false);
		aStage.initModality(Modality.WINDOW_MODAL);
		aStage.initOwner(pOwner);
		aStage.setTitle(RESOURCES.getString("dialog.diagram_size.title"));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
	}
	
	private Scene createScene() 
	{
		BorderPane layout = new BorderPane();
		layout.setPadding( new Insets(SPACING));
		
		String message = RESOURCES.getString("dialog.diagram_size.message");
		message = message.replace("#1", Integer.toString(MIN_SIZE));
		message = message.replace("#2", Integer.toString(MAX_SIZE));

		HBox top = new HBox(new Text(message));
		top.setAlignment(Pos.CENTER);
		layout.setTop(top);
		layout.setCenter(createForm());
		layout.setBottom(createButtons());
		
		return new Scene(layout);
	}
	
	private Pane createForm()
	{
		HBox pane = new HBox();
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(SPACING));
		pane.setSpacing(SPACING);
				
		aWidthField.setPrefColumnCount((int)Math.log10(MAX_SIZE)+1);
		aWidthField.setText(Integer.toString(getDiagramWidth()));
		HBox width = new HBox(new Label(RESOURCES.getString("dialog.diagram_size.width")), aWidthField);
		width.setAlignment(Pos.CENTER);
		width.setSpacing(2);
		aWidthField.setOnAction( pEvent -> 
		{
			if( !DiagramSizeUtils.isValid(aWidthField.getText()) )
			{
				aWidthField.setText(Integer.toString(getDiagramWidth()));
				showInvalidSizeAlert();
			}
		});
		
		aHeightField.setPrefColumnCount((int)Math.log10(MAX_SIZE)+1);
		aHeightField.setText(Integer.toString(getDiagramHeight()));
		HBox height = new HBox(new Label(RESOURCES.getString("dialog.diagram_size.height")), aHeightField);
		height.setAlignment(Pos.CENTER);
		height.setSpacing(2);
		aHeightField.setOnAction( pEvent -> 
		{
			if( !DiagramSizeUtils.isValid(aHeightField.getText()) )
			{
				aHeightField.setText(Integer.toString(getDiagramHeight()));
				showInvalidSizeAlert();
			}
		});
		
		Button defaultButton = new Button(RESOURCES.getString("dialog.diagram_size.defaults"));
		defaultButton.setOnAction( pEvent -> 
		{
			aWidthField.setText(Integer.toString(GuiUtils.defaultDiagramWidth()));
			aHeightField.setText(Integer.toString(GuiUtils.defaultDiagramHeight()));
		});
		
		
		pane.getChildren().addAll(width, height, defaultButton);
				
		return pane;
	}
	
	private static int getDiagramWidth()
	{
		int preferredWidth = UserPreferences.instance().getInteger(IntegerPreference.diagramWidth);
		if( preferredWidth == 0 )
		{
			int width = GuiUtils.defaultDiagramWidth();
			UserPreferences.instance().setInteger(IntegerPreference.diagramWidth, width);
			return width;
		}
		else
		{
			return preferredWidth;
		}
	}
	
	private static int getDiagramHeight()
	{
		int preferredHeight = UserPreferences.instance().getInteger(IntegerPreference.diagramHeight);
		if( preferredHeight == 0 )
		{
			int height = GuiUtils.defaultDiagramHeight();
			UserPreferences.instance().setInteger(IntegerPreference.diagramHeight, height);
			return height;
		}
		else
		{
			return preferredHeight;
		}
	}
	
	private void showInvalidSizeAlert()
	{
		String content = RESOURCES.getString("dialog.diagram_size.error_content");
		content = content.replace("#1", Integer.toString(MIN_SIZE));
		content = content.replace("#2", Integer.toString(MAX_SIZE));
		Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
		alert.setTitle(RESOURCES.getString("alert.error.title"));
		alert.setHeaderText(RESOURCES.getString("dialog.diagram_size.error_header"));
		alert.initOwner(aStage);
		alert.showAndWait();
	}
	
	private Pane createButtons()
	{
		Button ok = new Button(RESOURCES.getString("dialog.diagram_size.ok"));
		Button cancel = new Button(RESOURCES.getString("dialog.diagram_size.cancel"));
		ok.setOnAction(pEvent -> 
		{
			if( DiagramSizeUtils.isValid(aWidthField.getText()) && DiagramSizeUtils.isValid(aHeightField.getText()))
			{
				UserPreferences.instance().setInteger(IntegerPreference.diagramWidth, Integer.parseInt(aWidthField.getText()));
				UserPreferences.instance().setInteger(IntegerPreference.diagramHeight, Integer.parseInt(aHeightField.getText()));
				aStage.close();
			}
			else
			{
				showInvalidSizeAlert();
			}
		});
		cancel.setOnAction( pEvent -> aStage.close() );

		HBox box = new HBox(ok, cancel);
		box.setSpacing(SPACING);
		box.setAlignment(Pos.CENTER_RIGHT);
		box.setPadding(new Insets(VSPACE, 0, 0, 0));
		return box;
	}
	
	/**
	 * Shows the dialog and blocks the remainder of the UI
	 * until it is closed.
	 */
	public void show() 
	{
        aStage.showAndWait();
    }
}