/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
