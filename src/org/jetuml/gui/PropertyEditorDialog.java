/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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

import static org.jetuml.application.ApplicationResources.RESOURCES;

import java.util.EnumMap;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Properties;
import org.jetuml.diagram.Property;
import org.jetuml.diagram.PropertyName;
import org.jetuml.diagram.builder.CompoundOperation;
import org.jetuml.diagram.builder.SimpleOperation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * A modal dialog that allows the user to edit the 
 * properties of a DiagramElement.
 */
public class PropertyEditorDialog
{
	private static final int LAYOUT_PADDING = 20;
	
	private final Stage aStage;
	private final DiagramElement aElement;
	
	/**
	 * Creates a new dialog.
	 * 
	 * @param pDialogStage The stage that owns this dialog.
	 * @param pElement The element to edit with this dialog.
	 * @param pPropertyChangeListener A callback to run whenever a property changes.
	 */
	public PropertyEditorDialog( Stage pDialogStage, DiagramElement pElement, 
			PropertySheet.PropertyChangeListener pPropertyChangeListener )
	{
		aStage = pDialogStage;
		aElement = pElement;
		prepareStage();
		aStage.getScene().setRoot(createRoot(pPropertyChangeListener));
	}
	
	private void prepareStage() 
	{
		aStage.setTitle(RESOURCES.getString("dialog.properties"));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
	}
	
	private Pane createRoot(PropertySheet.PropertyChangeListener pPropertyChangeListener) 
	{
		PropertySheet sheet = new PropertySheet(aElement, pPropertyChangeListener);
				
		BorderPane layout = new BorderPane();
		Button button = new Button(RESOURCES.getString("dialog.diagram_size.ok"));
		
		// The line below allows to click the button by using the "Enter" key, 
		// by making it the default button, but only when it has the focus.
		button.defaultButtonProperty().bind(button.focusedProperty());
		button.setOnAction(pEvent -> aStage.close());
		BorderPane.setAlignment(button, Pos.CENTER_RIGHT);
		
		layout.setPadding(new Insets(LAYOUT_PADDING));
		layout.setCenter(sheet);
		layout.setBottom(button);
		
		return layout;
	}
	
	private PropertySheet getPropertySheet()
	{
		return (PropertySheet)((BorderPane) aStage.getScene().getRoot()).getCenter();
	}

	/**
	 * Shows the dialog and blocks the remainder of the UI
	 * until it is closed.
	 * 
	 * @return A compound operation that represents the modification of
	 *     each property modified through this dialog.
	 */
	public CompoundOperation show() 
	{
		if(!getPropertySheet().isEmpty())
		{
			PropertyChangeTracker tracker = new PropertyChangeTracker(getPropertySheet().getElement());
			tracker.startTracking();
			aStage.showAndWait();
			return tracker.stopTracking();
		}
		else
		{
			return new CompoundOperation();
		}
    }
	
	private static class PropertyChangeTracker 
	{
		private EnumMap<PropertyName, Object> aOldValues = new EnumMap<>(PropertyName.class);
		private Properties aProperties;
		
		/**
		 * Creates a new tracker for pEdited.
		 *  
		 * @param pEdited The element to track.
		 * @pre pEdited != null;
		 */
		PropertyChangeTracker(DiagramElement pEdited)
		{
			assert pEdited != null;
			aProperties = pEdited.properties();
		}

		/**
		 * Makes a snapshot of the properties values of the tracked element.
		 */
		public void startTracking()
		{
			for( Property property : aProperties )
			{
				aOldValues.put(property.name(), property.get());
			}
		}
		
		/**
		 * Creates and returns a CompoundOperation that represents any change
		 * in properties detected between the time startTracking
		 * and stopTracking were called.
		 * 
		 * @return A CompoundOperation describing the property changes.
		 */
		public CompoundOperation stopTracking()
		{
			CompoundOperation operation = new CompoundOperation();
			for( Property property : aProperties )
			{
				if( !aOldValues.get(property.name()).equals(property.get()))
				{
					final Object newValue = property.get();
					final Object oldValue = aOldValues.get(property.name());
					operation.add(new SimpleOperation(
							()-> property.set(newValue),
							()-> property.set(oldValue)));
				}
			}
			return operation;
		}
	}
}