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
package org.jetuml.gui;

import static org.jetuml.application.ApplicationResources.RESOURCES;

import org.jetuml.JetUML;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * A modal dialog that provides information about JetUML.
 */
public class AboutDialog
{
	private final Stage aStage;
	
	/**
	 * Creates a new dialog.
	 * 
	 * @param pOwner The stage that owns this stage.
	 */
	public AboutDialog( Stage pDialogStage )
	{
		aStage = pDialogStage;
		prepareStage();
		aStage.getScene().setRoot(createRoot());
	}
	
	private void prepareStage() 
	{
		aStage.setTitle(String.format("%s %s", RESOURCES.getString("dialog.about.title"),
				RESOURCES.getString("application.name")));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
	}
	
	private Pane createRoot() 
	{
		final int verticalSpacing = 5;
		
		VBox info = new VBox(verticalSpacing);
		Label name = new Label(RESOURCES.getString("application.name"));
		name.setStyle("-fx-font-size: 18pt;");
		
		Label version = new Label(String.format("%s %s", RESOURCES.getString("dialog.about.version"), 
				JetUML.VERSION));
		
		Label copyright = new Label(RESOURCES.getString("application.copyright"));
		
		Label license = new Label(RESOURCES.getString("dialog.about.license"));
		
		Label quotes = new Label(RESOURCES.getString("quotes.copyright"));
		
		Hyperlink link = new Hyperlink(RESOURCES.getString("dialog.about.link"));
		link.setBorder(Border.EMPTY);
		link.setPadding(new Insets(0));
		link.setOnMouseClicked(e -> JetUML.openBrowser(RESOURCES.getString("dialog.about.url")));
		link.setUnderline(true);
		link.setFocusTraversable(false);
		
		info.getChildren().addAll(name, version, copyright, license, link, quotes);
		
		final int padding = 15;
		HBox layout = new HBox(padding);
		layout.setPadding(new Insets(padding));
		layout.setAlignment(Pos.CENTER_LEFT);
		
		ImageView logo = new ImageView(RESOURCES.getString("application.icon"));
		logo.setEffect(new BoxBlur());
		layout.getChildren().addAll(logo, info);
		layout.setAlignment(Pos.TOP_CENTER);
		
		aStage.requestFocus();
		aStage.addEventHandler(KeyEvent.KEY_PRESSED, pEvent -> 
		{
			if(pEvent.getCode() == KeyCode.ENTER) 
			{
				aStage.close();
			}
		});
		
		return layout;
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