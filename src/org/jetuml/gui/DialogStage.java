package org.jetuml.gui;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A unique stage for dialogs.
 * Different dialogs sharing a common stage simplifies 
 * applying the dark mode theme to the dialogs.
 */
public class DialogStage extends Stage
{		
	/**
	 * Creates the stage with settings consistent for all dialogs.
	 * Dialogs that use this stage should not change the scene - doing
	 * so will stop applying the dark theme to dialogs. Instead,
	 * dialogs can customize the layout by reassigning the root of the scene.
	 * Making modifications to the stage should be done with caution, since 
	 * changes may persist across different dialogs.
	 * 
	 * @param pOwner The JetUML application stage.
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
