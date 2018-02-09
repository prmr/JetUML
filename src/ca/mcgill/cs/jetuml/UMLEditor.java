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

import java.util.List;
import java.util.ResourceBundle;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.jetuml.diagrams.ObjectDiagramGraph;
import ca.mcgill.cs.jetuml.diagrams.SequenceDiagramGraph;
import ca.mcgill.cs.jetuml.diagrams.StateDiagramGraph;
import ca.mcgill.cs.jetuml.diagrams.UseCaseDiagramGraph;
import ca.mcgill.cs.jetuml.gui.EditorFrame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;


/**
 * A program for editing UML diagrams.
 * @author Kaylee I. Kutschera - Migration to JavaFX
 */
public final class UMLEditor extends Application
{
	private static final int JAVA_MAJOR_VERSION = 7;
	private static final int JAVA_MINOR_VERSION = 0;
	
	private static final int MARGIN_SCREEN = 8; // Fraction of the screen to leave around the sides
	
	/**
	 * @param pArgs Each argument is a file to open upon launch.
	 * Can be empty.
	 */
	public static void main(String[] pArgs)
	{
		// checkVersion(); 
		try
		{
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		catch (SecurityException ex)
		{
			// well, we tried...
		}
		launch(pArgs);
   }
	@Override
	public void start(Stage pStage) throws Exception 
	{
		//set Stage boundaries 
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		int screenWidth = (int) primaryScreenBounds.getWidth();
		int screenHeight = (int) primaryScreenBounds.getHeight();
		pStage.setX(screenWidth / (MARGIN_SCREEN*2));
		pStage.setY(screenHeight / (MARGIN_SCREEN*2));
		pStage.setWidth((screenWidth * (MARGIN_SCREEN-1)) / MARGIN_SCREEN);
		pStage.setHeight((screenHeight * (MARGIN_SCREEN-1))/ MARGIN_SCREEN);
		
		EditorFrame frame = initializeEditorFrame(pStage);
		BorderPane pane = new BorderPane();
		pane.setCenter(frame);
			
		ResourceBundle aAppResources = ResourceBundle.getBundle(this.getClass().getName() + "Strings");
		pStage.setTitle(aAppResources.getString("app.name"));
		String imagePath = aAppResources.getString("app.icon");
		pStage.getIcons().add(new Image(imagePath));
		
		pStage.setScene(new Scene(pane));
		pStage.setOnCloseRequest(pWindowEvent -> 
		{
			pWindowEvent.consume();
			frame.exit();
	    });
        pStage.show();
	}

	private EditorFrame initializeEditorFrame(Stage pStage) 
	{
		EditorFrame frame = new EditorFrame(UMLEditor.class, pStage);
		List<String> argsList = getParameters().getRaw();
		String[] arguments = argsList.toArray(new String[argsList.size()]);
		setLookAndFeel();
		frame.addGraphType("class_diagram", ClassDiagramGraph.class);
		frame.addGraphType("sequence_diagram", SequenceDiagramGraph.class);
		frame.addGraphType("state_diagram", StateDiagramGraph.class);
	    frame.addGraphType("object_diagram", ObjectDiagramGraph.class);
	    frame.addGraphType("usecase_diagram", UseCaseDiagramGraph.class);
		frame.readArgs(arguments);
		frame.addWelcomeTab();
		return frame;
	}
	
	private static void setLookAndFeel()
	{
		try
		{
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
			{
				if("Nimbus".equals(info.getName())) 
				{
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} 
		catch(UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) 
		{
		    // Nothing: We revert to the default LAF
		}
	}
	
	/**
	 *  Checks if the current VM has at least the given
	 *  version, and exits the program with an error dialog if not.
	 */
	@SuppressWarnings("unused")
	private static void checkVersion()
	{
		String version = obtainJavaVersion();
		if( version == null || !isOKJVMVersion(version))
		{
			ResourceBundle resources = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings");
			String minor = "";
			int minorVersion = JAVA_MINOR_VERSION;
			if( minorVersion > 0 )
			{
				minor = "." + JAVA_MINOR_VERSION;
			}
			final String minorValue = minor;
			Platform.runLater(() -> 
			{
				Alert alert = new Alert(AlertType.ERROR, resources.getString("error.version") +	"1." + JAVA_MAJOR_VERSION + minorValue);
				alert.showAndWait();
			});
			System.exit(1);
		}
	}
	
	static String obtainJavaVersion()
	{
		String version = System.getProperty("java.version");
		if( version == null )
		{
			version = System.getProperty("java.runtime.version");
		}
		return version;
	}
	
	/**
	 * @return True is the JVM version is higher than the 
	 * versions specified as constants.
	 */
	static boolean isOKJVMVersion(String pVersion)
	{
		assert pVersion != null;
		String[] components = pVersion.split("\\.|_");
		boolean lReturn = true;
		
		try
		{
			int systemMajor = Integer.parseInt(String.valueOf(components[1]));
			int systemMinor = Integer.parseInt(String.valueOf(components[2]));
			if( systemMajor > JAVA_MAJOR_VERSION )
			{
				lReturn = true;
			}
			else if( systemMajor < JAVA_MAJOR_VERSION )
			{
				lReturn = false;
			}
			else // major Equals
			{
				if( systemMinor > JAVA_MINOR_VERSION )
				{
					lReturn = true;
				}
				else if( systemMinor < JAVA_MINOR_VERSION )
				{
					lReturn = false;
				}
				else // minor equals
				{
					lReturn = true;
				}
			}
        }
		catch( NumberFormatException e)
		{
			lReturn = false;
		}
		return lReturn;
    }
}