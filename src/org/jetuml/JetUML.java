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

package org.jetuml;

import static org.jetuml.application.ApplicationResources.RESOURCES;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.Version;
import org.jetuml.diagram.Diagram;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.DeserializationErrorAlert;
import org.jetuml.gui.DialogStage;
import org.jetuml.gui.EditorFrame;
import org.jetuml.gui.GuiUtils;
import org.jetuml.gui.NotificationService;
import org.jetuml.gui.tips.TipDialog;
import org.jetuml.persistence.DeserializationException;
import org.jetuml.persistence.PersistenceService;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Entry point for launching JetUML.
 */
public final class JetUML extends Application
{
	@SuppressWarnings("exports")
	public static final Version VERSION = Version.create(3, 8);
	
	private static HostServices aHostServices; // Required to open a browser page.
	
	/**
	 * @param pArgs Not used.
	 */
	public static void main(String[] pArgs)
	{
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		launch(pArgs);
	}
	
	@Override
	public void start(Stage pStage) throws Exception 
	{
		aHostServices = getHostServices();
		setStageBoundaries(pStage);

		pStage.setTitle(RESOURCES.getString("application.name"));
		pStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));

		Optional<Diagram> diagramToOpen = Optional.empty();
		Optional<File> fileToOpen = getFileToOpenIfPresent();
		
		/* For simplicity, the code below does not set up a stage just to serve
		 * as a parent for the Alert dialog. The only consequence is that if
		 * this dialog appears, a default "Java" icon will be shown in the OS
		 * taskbar instead of the JetUML-branded icon. To show the JetUML
		 * icon, the code needs to be extended to create an empty stage
		 * and ensure the dialog shows up in the middle of the screen, before
		 * exiting.
		 */
		try
		{
			if( fileToOpen.isPresent())
			{
				diagramToOpen = Optional.of(PersistenceService.read(fileToOpen.get()));
			}
		}
		catch( IOException | DeserializationException exception )
		{
			Alert alert = new DeserializationErrorAlert(exception);
			alert.showAndWait();
			System.exit(0);
		}
		
		DialogStage dialogStage = new DialogStage(pStage);
		dialogStage.getScene().getStylesheets().add(getClass().getResource("JetUML.css").toExternalForm());
		
		EditorFrame editor = new EditorFrame(pStage, dialogStage);
		diagramToOpen.ifPresent(diagram -> editor.setOpenFileAsDiagram(fileToOpen.get(), diagram));
		pStage.setScene(new Scene(editor));

		NotificationService.instance().setMainStage(pStage);
		
		pStage.getScene().getStylesheets().add(getClass().getResource("JetUML.css").toExternalForm());
		editor.booleanPreferenceChanged(UserPreferences.BooleanPreference.darkMode);
		
		pStage.setOnCloseRequest(pWindowEvent -> 
		{
			pWindowEvent.consume();
			((EditorFrame)((Stage)pWindowEvent.getSource()).getScene().getRoot()).exit();
		});
		pStage.show();
		
		if(UserPreferences.instance().getBoolean(UserPreferences.BooleanPreference.showTips))
		{
			new TipDialog(dialogStage).show();
		}
	}
	
	// If the first argument passed to the application is a valid file
	private Optional<File> getFileToOpenIfPresent()
	{
		List<String> parameters = getParameters().getUnnamed();
		if( parameters.isEmpty() )
		{
			return Optional.empty();
		}
		File file = new File(parameters.get(0));
		if(file.exists() && !file.isDirectory())
		{
			return Optional.of(file);
		}
		else
		{
			return Optional.empty();
		}
	}
	
	/**
	 * Open pUrl in the default system browser.
	 * 
	 * @param pUrl The url to open.
	 * @pre pUrl != null
	 */
	public static void openBrowser(String pUrl)
	{
		assert pUrl != null;
		aHostServices.showDocument(pUrl);
	}

	private static void setStageBoundaries(Stage pStage)
	{
		Rectangle defaultStageBounds = GuiUtils.defaultStageBounds();
		pStage.setX(defaultStageBounds.x());
		pStage.setY(defaultStageBounds.y());
		pStage.setWidth(defaultStageBounds.width());
		pStage.setHeight(defaultStageBounds.height());
	}
}