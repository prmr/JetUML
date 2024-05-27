package org.jetuml.gui;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A Stage that is to be shared by different dialogs.
 * Different dialogs sharing a common stage simplifies 
 * applying the dark mode theme to the dialogs.
 */
public class DialogStage extends Stage
{		
	/**
	 * Creates the stage for different dialogs.
	 * 
	 * @param pOwner The main JetUML stage.
	 */
	public DialogStage(Stage pOwner)
	{
		Scene dialogScene = new Scene(new GridPane());
		setScene(dialogScene);
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
