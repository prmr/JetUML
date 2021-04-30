/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static ca.mcgill.cs.jetuml.views.FontMetrics.DEFAULT_FONT_SIZE;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import javafx.event.ActionEvent;
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
 * A modal dialog that allows users to change font
 * size of diagrams.
 */
public class FontSizeDialog 
{
	private static final int SPACING = 10;
	private static final int VSPACE = 20;
	private static final int MIN_SIZE = 8;
	private static final int MAX_SIZE = 24;
	
	private final Stage aStage = new Stage();
	private final TextField aSizeField = new TextField();
	
	/**
	 * Creates a new font dialog.
	 * 
	 * @param pOwner The stage that owns this stage.
	 */
	public FontSizeDialog( Stage pOwner )
	{
		prepareStage(pOwner);
		aStage.setScene(createScene());
	}
	
	private void prepareStage(Stage pOwner) 
	{
		aStage.setResizable(false);
		aStage.initModality(Modality.WINDOW_MODAL);
		aStage.initOwner(pOwner);
		aStage.setTitle(RESOURCES.getString("dialog.font_size.title"));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
	}
	
	private Scene createScene() 
	{
		BorderPane layout = new BorderPane();
		layout.setPadding( new Insets(SPACING));
		
		String message = RESOURCES.getString("dialog.font_size.message");
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
				
		aSizeField.setPrefColumnCount((int)Math.log10(MAX_SIZE)+1);
		aSizeField.setText(Integer.toString(getFontSize()));
		HBox size = new HBox(new Label(RESOURCES.getString("dialog.font_size.size")), aSizeField);
		size.setAlignment(Pos.CENTER);
		size.setSpacing(2);
		aSizeField.setOnAction(this::onInput);
		
		Button defaultButton = new Button(RESOURCES.getString("dialog.font_size.default"));
		defaultButton.setOnAction( pEvent -> 
		{
			aSizeField.setText(Integer.toString(DEFAULT_FONT_SIZE));
		});
		
		
		pane.getChildren().addAll(size, defaultButton);
				
		return pane;
	}
	
	private boolean isValid(String pSize) 
	{
		try
		{
			int parsedSize = Integer.parseInt(pSize);
			return MIN_SIZE <= parsedSize && parsedSize <= MAX_SIZE;
		}
		catch ( NumberFormatException exception )
		{
			return false;
		}
	}

	private static int getFontSize()
	{
		return UserPreferences.instance().getInteger(IntegerPreference.fontSize);
	}
	
	private void showInvalidSizeAlert()
	{
		String content = RESOURCES.getString("dialog.font_size.error_content");
		content = content.replace("#1", Integer.toString(MIN_SIZE));
		content = content.replace("#2", Integer.toString(MAX_SIZE));
		Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
		alert.setTitle(RESOURCES.getString("alert.error.title"));
		alert.setHeaderText(RESOURCES.getString("dialog.font_size.error_header"));
		alert.initOwner(aStage);
		alert.showAndWait();
	}
	
	private Pane createButtons()
	{
		Button ok = new Button(RESOURCES.getString("dialog.font_size.ok"));
		Button cancel = new Button(RESOURCES.getString("dialog.font_size.cancel"));
		ok.setOnAction(this::onInput);
		cancel.setOnAction( pEvent -> aStage.close() );

		HBox box = new HBox(ok, cancel);
		box.setSpacing(SPACING);
		box.setAlignment(Pos.CENTER_RIGHT);
		box.setPadding(new Insets(VSPACE, 0, 0, 0));
		return box;
	}
	
	private void onInput(ActionEvent pEvent)
	{
		if( isValid(aSizeField.getText()) )
		{
			UserPreferences.instance().setInteger(IntegerPreference.fontSize, Integer.parseInt(aSizeField.getText()));
			aStage.close();
		}
		else
		{
			aSizeField.setText(Integer.toString(getFontSize()));
			showInvalidSizeAlert();
		}
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
