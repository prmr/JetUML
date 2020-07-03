/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by the contributors of the JetUML project.
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

import ca.mcgill.cs.jetuml.application.Version;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.gui.EditorFrame;
import ca.mcgill.cs.jetuml.gui.GuiUtils;
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
	public static final Version VERSION = Version.create(3, 0);
	
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

		pStage.setScene(new Scene(new EditorFrame(pStage)));
		pStage.getScene().getStylesheets().add(getClass().getResource("JetUML.css").toExternalForm());

		pStage.setOnCloseRequest(pWindowEvent -> 
		{
			pWindowEvent.consume();
			((EditorFrame)((Stage)pWindowEvent.getSource()).getScene().getRoot()).exit();
		});
		pStage.show();
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

	private void setStageBoundaries(Stage pStage)
	{
		Rectangle defaultStageBounds = GuiUtils.defaultStageBounds();
		pStage.setX(defaultStageBounds.getX());
		pStage.setY(defaultStageBounds.getY());
		pStage.setWidth(defaultStageBounds.getWidth());
		pStage.setHeight(defaultStageBounds.getHeight());
	}
}