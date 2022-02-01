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

package ca.mcgill.cs.jetuml;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.io.File;
import java.util.List;
import java.util.Optional;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.Version;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.gui.EditorFrame;
import ca.mcgill.cs.jetuml.gui.GuiUtils;
import ca.mcgill.cs.jetuml.gui.tips.TipDialog;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Entry point for launching JetUML.
 */
public final class JetUML extends Application
{
	@SuppressWarnings("exports")
	public static final Version VERSION = Version.create(3, 3);
	
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

		pStage.setScene(new Scene(new EditorFrame(pStage, openWith())));
		pStage.getScene().getStylesheets().add(getClass().getResource("JetUML.css").toExternalForm());

		pStage.setOnCloseRequest(pWindowEvent -> 
		{
			pWindowEvent.consume();
			((EditorFrame)((Stage)pWindowEvent.getSource()).getScene().getRoot()).exit();
		});
		pStage.show();
		
		if(UserPreferences.instance().getBoolean(UserPreferences.BooleanPreference.showTips))
		{
			new TipDialog(pStage).show();
		}
	}
	
	// If the first argument passed to the application is a valid file, open it.
	private Optional<File> openWith()
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
		pStage.setX(defaultStageBounds.getX());
		pStage.setY(defaultStageBounds.getY());
		pStage.setWidth(defaultStageBounds.getWidth());
		pStage.setHeight(defaultStageBounds.getHeight());
	}
}