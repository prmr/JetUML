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

package ca.mcgill.cs.jetuml;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.util.concurrent.CountDownLatch;

import ca.mcgill.cs.jetuml.application.JavaVersion;
import ca.mcgill.cs.jetuml.gui.EditorFrame;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Entry point for launching JetUML.
 */
public final class UMLEditor extends Application
{
	private static final JavaVersion MINIMAL_JAVA_VERSION = new JavaVersion(10, 0, 1);
	private static final int MARGIN_SCREEN = 8; // Fraction of the screen to leave around the sides
	
	private static HostServices aHostServices; // Required to open a browser page.
	
	/**
	 * @param pArgs Not used.
	 */
	public static void main(String[] pArgs)
	{
		checkVersion(); 
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
		pStage.getScene().getStylesheets().add(getClass().getResource("UMLEditorStyle.css").toExternalForm());

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
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		int screenWidth = (int) primaryScreenBounds.getWidth();
		int screenHeight = (int) primaryScreenBounds.getHeight();
		pStage.setX(screenWidth / (MARGIN_SCREEN * 2));
		pStage.setY(screenHeight / (MARGIN_SCREEN * 2));
		pStage.setWidth((screenWidth * (MARGIN_SCREEN -1 )) / MARGIN_SCREEN);
		pStage.setHeight((screenHeight * (MARGIN_SCREEN -1 ))/ MARGIN_SCREEN);
	}

	/*
	 * Verifies that the current version of Java is equal to or 
	 * higher than the required version, and exits with an error
	 * message if it is not. 
	 */
	private static void checkVersion()
	{
		final JavaVersion currentVersion = new JavaVersion();
		if( currentVersion.compareTo(MINIMAL_JAVA_VERSION) < 0 )
		{
			final CountDownLatch wait = new CountDownLatch(1);
			Platform.runLater(() -> 
			{
				createVersionNotSupportedDialog(currentVersion).showAndWait();
				wait.countDown();
			});
			try
			{
				wait.await();
			}
			catch(InterruptedException exception)
			{} // Nothing, we want to exit anyways
			System.exit(1);
		}
	}

	private static Alert createVersionNotSupportedDialog(JavaVersion pCurrentVersion)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(RESOURCES.getString("alert.error.title"));
		alert.setHeaderText(RESOURCES.getString("alert.version.header"));
		alert.setContentText(String.format("%s %s. %s %s.",
				RESOURCES.getString("alert.version.required"),
				MINIMAL_JAVA_VERSION,
				RESOURCES.getString("alert.version.detected"),
				pCurrentVersion));
		return alert;
	}
}