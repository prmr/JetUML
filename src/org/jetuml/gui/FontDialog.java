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
import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_NAME;
import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_SIZE;

import java.util.ArrayList;
import java.util.Arrays;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;

import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A modal dialog that allows users to change the font
 * and font size of text in diagrams.
 */
public class FontDialog 
{
	private static final int SPACING = 10;
	private static final int HSPACE = 50;
	private static final int VSPACE = 20;
	private static final Insets EXTRA_MARGIN = new Insets(0, 0, 0, 10);
	
	// The Serif and Monospaced fonts position the underscore character below the underline.
	private static final ArrayList<String> FONT_FAMILIES = new ArrayList<>(Arrays.asList("System", "SansSerif", "Serif", "Monospaced"));
	private static final ArrayList<Integer> FONT_SIZES = new ArrayList<>(
			Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
	private static final int PREVIEW_HEIGHT = 35;
	private static final int PREVIEW_WIDTH = 110;
	
	private final Stage aStage = new Stage();
	private final GridPane aLayout = new GridPane();
	private final ComboBox<String> aFonts = new ComboBox<>(FXCollections.observableArrayList(FONT_FAMILIES));
	private final ComboBox<Integer> aSizes = new ComboBox<>(FXCollections.observableArrayList(FONT_SIZES));
	private final Label aPreview = new Label(RESOURCES.getString("dialog.font.preview"));
	private final String aUserFamily = getCurrentFont();
	private final int aUserSize = getCurrentFontSize();
	
	/**
	 * Creates a new font dialog.
	 * 
	 * @param pOwner The stage that owns this stage.
	 */
	public FontDialog( Stage pOwner )
	{
		prepareStage(pOwner);
		aStage.setScene(createScene());
		aStage.getScene().getStylesheets().add(getClass().getResource("FontDialog.css").toExternalForm());
	}
	
	private void prepareStage(Stage pOwner) 
	{
		aStage.setResizable(false);
		aStage.initModality(Modality.WINDOW_MODAL);
		aStage.initOwner(pOwner);
		aStage.setTitle(RESOURCES.getString("dialog.font.title"));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
		aStage.addEventHandler(KeyEvent.KEY_PRESSED, pEvent -> 
		{
			if( pEvent.getCode() == KeyCode.ESCAPE ) 
			{
				restoreUserSettings();
				aStage.close();
			}
		});
		aStage.setOnCloseRequest(pEvent ->
		{
			restoreUserSettings();
		});
	}
	
	private Scene createScene() 
	{
		aLayout.setHgap(HSPACE);
		aLayout.setVgap(VSPACE);
		aLayout.setPadding(new Insets(SPACING));
		createFont();
		createSize();
		createPreview();
		createButton();
		return new Scene(aLayout);
	}
	
	private void createFont()
	{
		Label family = new Label(RESOURCES.getString("dialog.font.family"));
		GridPane.setConstraints(family, 0, 0);
		GridPane.setMargin(family, EXTRA_MARGIN);
		
		aFonts.getStyleClass().add("font-combobox");
		aFonts.getSelectionModel().select(getCurrentFont());
		aFonts.setOnAction(pEvent -> onInput());
		GridPane.setConstraints(aFonts, 1, 0);
		GridPane.setHalignment(aFonts, HPos.RIGHT);
		
		aLayout.getChildren().addAll(family, aFonts);
	}
	
	private void createSize()
	{
		Label size = new Label(RESOURCES.getString("dialog.font.size"));
		GridPane.setConstraints(size, 0, 1);
		GridPane.setMargin(size, EXTRA_MARGIN);
		
		aSizes.getStyleClass().add("font-combobox");
		aSizes.getSelectionModel().select((Integer) getCurrentFontSize());
		aSizes.setOnAction(pEvent -> onInput());
		GridPane.setConstraints(aSizes, 1, 1);
		GridPane.setHalignment(aSizes, HPos.RIGHT);
		
		aLayout.getChildren().addAll(size, aSizes);
	}
	
	private void createPreview()
	{
		aPreview.setFont(Font.font(getCurrentFont(), getCurrentFontSize()));
		aPreview.setPrefWidth(PREVIEW_WIDTH);
		aPreview.setPrefHeight(PREVIEW_HEIGHT);
		aPreview.setAlignment(Pos.CENTER);
		
		HBox previewContainer = new HBox(aPreview);
		previewContainer.getStyleClass().add("font-preview");
		previewContainer.setAlignment(Pos.CENTER);
		GridPane.setConstraints(previewContainer, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
		
		aLayout.getChildren().add(previewContainer);
	}
	
	private void createButton()
	{	
		Button ok = new Button(RESOURCES.getString("dialog.font.ok"));
		ok.setOnAction(pEvent -> aStage.close());
		ok.getStyleClass().add("font-button");
		
		Button cancel = new Button(RESOURCES.getString("dialog.font.cancel"));
		cancel.setOnAction(pEvent -> 
		{
			restoreUserSettings();
			aStage.close();
		});
		cancel.getStyleClass().add("font-button");
		
		HBox layout = new HBox(SPACING);
		layout.setAlignment(Pos.CENTER_RIGHT);
		layout.getChildren().addAll(ok, cancel);
		GridPane.setConstraints(layout, 1, 3);
		
		Button restoreDefault = new Button(RESOURCES.getString("dialog.font.default"));
		restoreDefault.setOnAction(pEvent -> 
		{
			aFonts.getSelectionModel().select(DEFAULT_FONT_NAME);
			aSizes.getSelectionModel().select((Integer) DEFAULT_FONT_SIZE);
		});
		restoreDefault.getStyleClass().add("font-button");
		GridPane.setConstraints(restoreDefault, 0, 3);
		
		aLayout.getChildren().addAll(restoreDefault, layout);
	}

	private static int getCurrentFontSize()
	{
		return UserPreferences.instance().getInteger(IntegerPreference.fontSize);
	}
	
	private static String getCurrentFont()
	{
		return UserPreferences.instance().getString(StringPreference.fontName);
	}
	
	private void restoreUserSettings()
	{
		aFonts.getSelectionModel().select(aUserFamily);
		aSizes.getSelectionModel().select((Integer) aUserSize);
	}
	
	private void onInput()
	{
		if( !aFonts.getSelectionModel().getSelectedItem().equals(getCurrentFont()) )
		{
			UserPreferences.instance().setString(StringPreference.fontName, aFonts.getSelectionModel().getSelectedItem());
		}
		
		if( aSizes.getSelectionModel().getSelectedItem() != getCurrentFontSize() )
		{
			UserPreferences.instance().setInteger(IntegerPreference.fontSize, aSizes.getSelectionModel().getSelectedItem());
		}
		aPreview.setFont(Font.font(getCurrentFont(), getCurrentFontSize()));
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
