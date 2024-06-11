package org.jetuml.gui;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A Stage that is to be shared by different dialogs.
 * Different dialogs sharing a common stage simplifies applying 
 * the dark mode theme to the dialogs. There is only one DialogStage
 * in the program's life cycle.
 */
public class DialogStage extends Stage
{		
	/**
	 * Creates the stage for different dialogs.
	 * Dialogs that use this stage should not change the scene - doing
	 * so will stop applying the dark theme to dialogs. Instead,
	 * dialogs can customize the layout by reassigning the root of the scene.
	 * 
	 * @param pOwner The main JetUML stage.
	 */
	public DialogStage(Stage pOwner)
	{
		// The root is defined only for compilation.
		setScene(new Scene(new GridPane()));
		setResizable(false);
		initModality(Modality.WINDOW_MODAL);
		initOwner(pOwner);
		addEventHandler(KeyEvent.KEY_PRESSED, pEvent -> 
		{
			if( pEvent.getCode() == KeyCode.ESCAPE ) 
			{
				close();
			}
		});
	}
}
