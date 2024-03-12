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
import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_SIZE;

import java.util.ArrayList;
import java.util.Arrays;

import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_NAME;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.application.UserPreferences.StringPreference;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
	private static final int HSPACE = 50;
	private static final int VSPACE = 20;
	private static final int MIN_SIZE = 8;
	private static final int MAX_SIZE = 24;
	private static final int FONT_LIST_HEIGHT = 200;
	private static final Insets LEFT_MARGIN = new Insets(0, 0, 0, 30);
	private static final ArrayList<Integer> FONT_SIZES = new ArrayList<>(
			Arrays.asList(8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
	private static final String PREVIEW_MESSAGE = "The quick brown fox jumps over the lazy dog.";
	private static final String FAMILY_COMBOBOX_STYLE = "family-combobox";
	
	private final Stage aStage = new Stage();
	private final GridPane aLayout = new GridPane();
	private final TextField aSizeField = new TextField();
	private final ListView<String> aFonts = new ListView<>();
	
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
//		BorderPane layout = new BorderPane();
//		layout.setPadding( new Insets(SPACING));
//		
//		layout.setLeft(createForm());
//		layout.setRight(createFontChooser());
//		layout.setBottom(createButtons());
//		
//		BorderPane.setMargin(layout.getLeft(), new Insets(SPACING));
//		BorderPane.setMargin(layout.getRight(), new Insets(SPACING));
		aLayout.setHgap(HSPACE);
		aLayout.setVgap(VSPACE);
		aLayout.setPadding(new Insets(SPACING));
		//aLayout.setGridLinesVisible(true);
		
		createFamily();
		createSize();
		createPreview();
		createButton();
		return new Scene(aLayout);
	}
	
	private void createFamily()
	{
		Label family = new Label("Family");
		GridPane.setConstraints(family, 0, 0);
		GridPane.setMargin(family, LEFT_MARGIN);
		
		ComboBox<String> fonts = new ComboBox<>(FXCollections.observableArrayList(Font.getFamilies()));
		fonts.setPrefWidth(150);
		GridPane.setConstraints(fonts, 1, 0);
		
		aLayout.getChildren().addAll(family, fonts);
	}
	
	private void createSize()
	{
		Label size = new Label("Size");
		GridPane.setConstraints(size, 0, 1);
		GridPane.setMargin(size, LEFT_MARGIN);
		
		ComboBox<Integer> sizes = new ComboBox<>(FXCollections.observableArrayList(FONT_SIZES));
		sizes.setPrefWidth(150);
		GridPane.setConstraints(sizes, 1, 1);
		GridPane.setHalignment(sizes, HPos.RIGHT);
		
		aLayout.getChildren().addAll(size, sizes);
	}
	
	private void createPreview()
	{
		Label preview = new Label(PREVIEW_MESSAGE);
		GridPane.setConstraints(preview, 0, 2);
		GridPane.setMargin(preview, new Insets(20, 0, 20, 0));
		GridPane.setColumnSpan(preview, 2);
		GridPane.setHalignment(preview, HPos.CENTER);
		
		aLayout.getChildren().add(preview);
	}
	
	private void createButton()
	{	
		Button ok = new Button("OK");
		Button cancel = new Button("Cancel");
		HBox layout = new HBox(SPACING);
		layout.setAlignment(Pos.CENTER_RIGHT);
		layout.getChildren().addAll(ok, cancel);
		GridPane.setConstraints(layout, 1, 3);
		
		Button restore = new Button("Restore Defaults");
		GridPane.setConstraints(restore, 0, 3);

		aLayout.getChildren().addAll(restore, layout);
	}
	
	private Pane createForm()
	{
		VBox fontSizeChooser = new VBox();
		fontSizeChooser.setAlignment(Pos.CENTER);
		fontSizeChooser.setSpacing(SPACING);
		
		String message = RESOURCES.getString("dialog.font_size.message");
		message = message.replace("#1", Integer.toString(MIN_SIZE));
		message = message.replace("#2", Integer.toString(MAX_SIZE));
		HBox restriction = new HBox(new Text(message));
		restriction.setAlignment(Pos.CENTER);
		
		aSizeField.setPrefColumnCount((int)Math.log10(MAX_SIZE)+1);
		aSizeField.setText(Integer.toString(getFontSize()));
		HBox size = new HBox(new Label(RESOURCES.getString("dialog.font_size.size")), aSizeField);
		size.setAlignment(Pos.CENTER);
		size.setSpacing(2);
		aSizeField.setOnAction(event -> onInput());		
		
		fontSizeChooser.getChildren().addAll(restriction, size);
		return fontSizeChooser;
	}
	
	private ListView<String> createFontChooser()
	{
		ObservableList<String> fonts = FXCollections.observableArrayList(Font.getFamilies());
		aFonts.setItems(fonts);
		aFonts.setMaxHeight(FONT_LIST_HEIGHT);
		aFonts.getSelectionModel().select(getFontName());
		aFonts.scrollTo(aFonts.getSelectionModel().getSelectedIndex());
		return aFonts;
	}
	
	private static boolean isValid(String pSize) 
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
	
	private static String getFontName()
	{
		return UserPreferences.instance().getString(StringPreference.fontName);
	}
	
	private Pane createButtons()
	{
		Button defaultButton = new Button(RESOURCES.getString("dialog.font_size.default"));
		defaultButton.setOnAction( pEvent -> 
		{
			aSizeField.setText(Integer.toString(DEFAULT_FONT_SIZE));
			aFonts.getSelectionModel().select(DEFAULT_FONT_NAME);
			aFonts.scrollTo(aFonts.getSelectionModel().getSelectedIndex());
		});
		
		Button ok = new Button(RESOURCES.getString("dialog.font_size.ok"));
		Button cancel = new Button(RESOURCES.getString("dialog.font_size.cancel"));
		ok.setOnAction(event -> onInput());
		cancel.setOnAction( pEvent -> aStage.close() );

		HBox box = new HBox(defaultButton, ok, cancel);
		box.setSpacing(SPACING);
		box.setAlignment(Pos.CENTER_RIGHT);
		box.setPadding(new Insets(VSPACE, 0, 0, 0));
		return box;
	}
	
	private void onInput()
	{
		if( isValid(aSizeField.getText()) )
		{
			UserPreferences.instance().setString(StringPreference.fontName, aFonts.getSelectionModel().getSelectedItem());
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
