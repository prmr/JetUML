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

import static org.jetuml.application.ApplicationResources.RESOURCES;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * A modal dialog that allows users to change the duration (in seconds) that
 * notifications remain visible.
 */
public class NotificationTimeDialog 
{
	private static final int SPACING = 10;
	private static final int VSPACE = 20;
	private static final int MIN_DURATION = 0;
	private static final int MAX_DURATION = 10;
	private final Stage aStage;
	private final TextField aDurationField = new TextField();
	
	/**
	 * Creates a new dialog.
	 * 
	 * @param pDialogStage The stage that owns this dialog.
	 */
	public NotificationTimeDialog( Stage pDialogStage )
	{
		aStage = pDialogStage;
		prepareStage();
		aStage.getScene().setRoot(createRoot());
	}
	
	private void prepareStage() 
	{
		aStage.setTitle(RESOURCES.getString("dialog.notifications.title"));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
	}
	
	private Pane createRoot() 
	{
		BorderPane layout = new BorderPane();
		layout.setPadding( new Insets(SPACING));
		
		String message = RESOURCES.getString("dialog.notifications.message");
		message = message.replace("#1", Integer.toString(MIN_DURATION));
		message = message.replace("#2", Integer.toString(MAX_DURATION));

		HBox top = new HBox(new Label(message));
		top.setAlignment(Pos.CENTER);
		layout.setTop(top);
		layout.setCenter(createForm());
		layout.setBottom(createButtons());
		
		return layout;
	}
	
	private Pane createForm()
	{
		HBox pane = new HBox();
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(SPACING));
		pane.setSpacing(SPACING);
				
		aDurationField.setPrefColumnCount((int)Math.log10(MAX_DURATION)+1);
		aDurationField.setText(durationAsString());
		HBox size = new HBox(new Label(RESOURCES.getString("dialog.notifications.value")), aDurationField);
		size.setAlignment(Pos.CENTER);
		size.setSpacing(2);
		aDurationField.setOnAction(event -> onInput());
		
		Button defaultButton = new Button(RESOURCES.getString("dialog.notifications.default"));
		defaultButton.setOnAction( pEvent -> 
		{
			aDurationField.setText(UserPreferences.IntegerPreference.notificationDuration.getDefault());
		});
				
		pane.getChildren().addAll(size, defaultButton);
				
		return pane;
	}
	
	private static boolean isValid(String pSize) 
	{
		try
		{
			int parsedSize = Integer.parseInt(pSize);
			return MIN_DURATION <= parsedSize && parsedSize <= MAX_DURATION;
		}
		catch ( NumberFormatException exception )
		{
			return false;
		}
	}

	private static String durationAsString()
	{
		return Integer.toString(UserPreferences.instance().
				getInteger(IntegerPreference.notificationDuration));
	}
	
	private void showInvalidDurationAlert()
	{
		String content = RESOURCES.getString("dialog.notifications.error_content");
		content = content.replace("#1", Integer.toString(MIN_DURATION));
		content = content.replace("#2", Integer.toString(MAX_DURATION));
		Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
		alert.setTitle(RESOURCES.getString("alert.error.title"));
		alert.setHeaderText(RESOURCES.getString("dialog.notifications.error_header"));
		alert.initOwner(aStage);
		alert.showAndWait();
	}
	
	private Pane createButtons()
	{
		Button ok = new Button(RESOURCES.getString("dialog.notifications.ok"));
		Button cancel = new Button(RESOURCES.getString("dialog.notifications.cancel"));
		ok.setOnAction(event -> onInput());
		cancel.setOnAction( pEvent -> aStage.close() );

		HBox box = new HBox(ok, cancel);
		box.setSpacing(SPACING);
		box.setAlignment(Pos.CENTER_RIGHT);
		box.setPadding(new Insets(VSPACE, 0, 0, 0));
		return box;
	}
	
	private void onInput()
	{
		if( isValid(aDurationField.getText()) )
		{
			UserPreferences.instance().setInteger(IntegerPreference.notificationDuration, Integer.parseInt(aDurationField.getText()));
			aStage.close();
		}
		else
		{
			aDurationField.setText(durationAsString());
			showInvalidDurationAlert();
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
