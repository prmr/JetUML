package org.jetuml.gui;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 
 */
public class AbstractDialog extends Stage
{		
	/**
	 * s.
	 * @param pOwner d
	 */
	public AbstractDialog(Stage pOwner)
	{
		Scene scenex = new Scene(new GridPane());
		setScene(scenex);
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
		getScene().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
	}
}
