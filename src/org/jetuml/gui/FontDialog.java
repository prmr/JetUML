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
 * A modal dialog that allows users to change font
 * size of diagrams.
 */
public class FontDialog 
{
	private static final int SPACING = 10;
	private static final int HSPACE = 50;
	private static final int VSPACE = 20;
	private static final Insets EXTRA_MARGIN = new Insets(0, 0, 0, 10);
	
	private static final ArrayList<String> FONT_FAMILIES = new ArrayList<>(
			Arrays.asList("System", "SansSerif", "Serif", "Monospaced"));
	private static final ArrayList<Integer> FONT_SIZES = new ArrayList<>(
			Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
	private static final String PREVIEW_MESSAGE = "Preview";
	private static final int PREVIEW_HEIGHT = 35;
	private static final int PREVIEW_WIDTH = 110;
	
	private final Stage aStage = new Stage();
	private final GridPane aLayout = new GridPane();
	private final ComboBox<String> aFonts = new ComboBox<>(FXCollections.observableArrayList(FONT_FAMILIES));
	private final ComboBox<Integer> aSizes = new ComboBox<>(FXCollections.observableArrayList(FONT_SIZES));
	private final Label aPreview = new Label(PREVIEW_MESSAGE);
	private String aSelectedFont = getCurrentFont();
	private int aSelectedSize = getCurrentFontSize();
	
	/**
	 * Creates a new font dialog.
	 * 
	 * @param pOwner The stage that owns this stage.
	 */
	public FontDialog( Stage pOwner )
	{
		prepareStage(pOwner);
		aStage.setScene(createScene());
		aStage.getScene().getStylesheets().add("FontDialog.css");
	}
	
	private void prepareStage(Stage pOwner) 
	{
		aStage.setResizable(false);
		aStage.initModality(Modality.WINDOW_MODAL);
		aStage.initOwner(pOwner);
		aStage.setTitle(RESOURCES.getString("dialog.font_size.title"));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
		aStage.addEventHandler(KeyEvent.KEY_PRESSED, pEvent -> 
		{
			if( pEvent.getCode() == KeyCode.ESCAPE ) 
			{
				aStage.close();
			}
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
		Label family = new Label("Font");
		GridPane.setConstraints(family, 0, 0);
		GridPane.setMargin(family, EXTRA_MARGIN);
		
		aFonts.getSelectionModel().select(getCurrentFont());
		aFonts.setOnAction(pEvent -> updatePreview());
		GridPane.setConstraints(aFonts, 1, 0);
		GridPane.setHalignment(aFonts, HPos.RIGHT);
		
		aLayout.getChildren().addAll(family, aFonts);
	}
	
	private void createSize()
	{
		Label size = new Label("Size");
		GridPane.setConstraints(size, 0, 1);
		GridPane.setMargin(size, EXTRA_MARGIN);
		
		aSizes.getSelectionModel().select((Integer) getCurrentFontSize());
		aSizes.setOnAction(pEvent -> updatePreview());
		GridPane.setConstraints(aSizes, 1, 1);
		GridPane.setHalignment(aSizes, HPos.RIGHT);
		
		aLayout.getChildren().addAll(size, aSizes);
	}
	
	private void createPreview()
	{
		aPreview.setFont(Font.font(getCurrentFont(), getCurrentFontSize()));
		aPreview.setPrefWidth(PREVIEW_WIDTH);
		aPreview.setPrefHeight(PREVIEW_HEIGHT);
		aPreview.setWrapText(true);
		aPreview.setAlignment(Pos.CENTER);
		GridPane.setConstraints(aPreview, 0, 2, 2, 1, HPos.CENTER, VPos.CENTER);
		
		aLayout.getChildren().add(aPreview);
	}
	
	private void createButton()
	{	
		Button ok = new Button("OK");
		ok.setOnAction(pEvent -> onInput());
		
		Button cancel = new Button("Cancel");
		cancel.setOnAction(pEvent -> aStage.close());
		
		HBox layout = new HBox(SPACING);
		layout.setAlignment(Pos.CENTER_RIGHT);
		layout.getChildren().addAll(ok, cancel);
		GridPane.setConstraints(layout, 1, 3);
		
		Button restoreDefaults = new Button("Restore Defaults");
		restoreDefaults.setOnAction(pEvent -> 
		{
			aFonts.getSelectionModel().select(DEFAULT_FONT_NAME);
			aSizes.getSelectionModel().select((Integer) DEFAULT_FONT_SIZE);
		});
		GridPane.setConstraints(restoreDefaults, 0, 3);
		
		aLayout.getChildren().addAll(restoreDefaults, layout);
	}

	private static int getCurrentFontSize()
	{
		return UserPreferences.instance().getInteger(IntegerPreference.fontSize);
	}
	
	private static String getCurrentFont()
	{
		return UserPreferences.instance().getString(StringPreference.fontName);
	}
	
	private void updatePreview()
	{
		aSelectedFont = aFonts.getSelectionModel().getSelectedItem();
		aSelectedSize = aSizes.getSelectionModel().getSelectedItem();
		aPreview.setFont(Font.font(aSelectedFont, aSelectedSize));
	}
	
	private void onInput()
	{
		if( !aSelectedFont.equals(getCurrentFont()) )
		{
			UserPreferences.instance().setString(StringPreference.fontName, aSelectedFont);
		}
		
		if( aSelectedSize != getCurrentFontSize() )
		{
			UserPreferences.instance().setInteger(IntegerPreference.fontSize, aSelectedSize);
		}
		aStage.close();
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
