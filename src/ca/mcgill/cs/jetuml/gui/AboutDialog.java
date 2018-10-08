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

import ca.mcgill.cs.jetuml.UMLEditor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A modal dialog that provides information about JetUML.
 */
public class AboutDialog
{
	private final Stage aStage = new Stage();
	
	/**
	 * Creates a new dialog.
	 * 
	 * @param pOwner The stage that owns this stage.
	 */
	public AboutDialog( Stage pOwner )
	{
		prepareStage(pOwner);
		aStage.setScene(createScene());
	}
	
	private void prepareStage(Stage pOwner) 
	{
		aStage.setResizable(false);
		aStage.initModality(Modality.WINDOW_MODAL);
		aStage.initOwner(pOwner);
		aStage.setTitle(String.format("%s %s", RESOURCES.getString("dialog.about.title"),
				RESOURCES.getString("application.name")));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
	}
	
	private Scene createScene() 
	{
		final int verticalSpacing = 5;
		
		VBox info = new VBox(verticalSpacing);
		Text name = new Text(RESOURCES.getString("application.name"));
		name.setStyle("-fx-font-size: 18pt;");
		
		Text version = new Text(String.format("%s %s (%s)", RESOURCES.getString("dialog.about.version"), 
				RESOURCES.getString("application.version.number"), 
				RESOURCES.getString("application.version.date")));
		
		Text copyright = new Text(RESOURCES.getString("application.copyright"));
		
		Text license = new Text(RESOURCES.getString("dialog.about.license"));
		
		Hyperlink link = new Hyperlink(RESOURCES.getString("dialog.about.link"));
		link.setBorder(Border.EMPTY);
		link.setPadding(new Insets(0));
		link.setOnMouseClicked(e -> UMLEditor.openBrowser(RESOURCES.getString("dialog.about.url")));
		link.setUnderline(true);
		link.setFocusTraversable(false);
		
		info.getChildren().addAll(name, version, copyright, license, link);
		
		final int padding = 15;
		HBox layout = new HBox(padding);
		layout.setStyle("-fx-background-color: gainsboro;");
		layout.setPadding(new Insets(padding));
		layout.setAlignment(Pos.CENTER_LEFT);
		
		ImageView logo = new ImageView(RESOURCES.getString("application.icon"));
		logo.setEffect(new BoxBlur());
		layout.getChildren().addAll(logo, info);
		
		aStage.requestFocus();
		aStage.addEventHandler(KeyEvent.KEY_PRESSED, pEvent -> 
		{
			if (pEvent.getCode() == KeyCode.ENTER) 
			{
				aStage.close();
			}
		});
		
		return new Scene(layout);
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