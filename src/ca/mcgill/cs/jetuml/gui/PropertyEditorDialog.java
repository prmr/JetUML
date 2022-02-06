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
package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.util.EnumMap;

import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Properties;
import ca.mcgill.cs.jetuml.diagram.Property;
import ca.mcgill.cs.jetuml.diagram.PropertyName;
import ca.mcgill.cs.jetuml.diagram.builder.CompoundOperation;
import ca.mcgill.cs.jetuml.diagram.builder.SimpleOperation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A modal dialog that allows the user to edit the 
 * properties of a DiagramElement.
 */
public class PropertyEditorDialog
{
	private static final int LAYOUT_PADDING = 20;
	
	private final Stage aStage = new Stage();
	private final DiagramElement aElement;
	
	/**
	 * Creates a new dialog.
	 * 
	 * @param pOwner The stage that owns this stage.
	 * @param pElement The element to edit with this dialog.
	 * @param pPropertyChangeListener A callback to run whenever a property changes.
	 */
	public PropertyEditorDialog( Stage pOwner, DiagramElement pElement, 
			PropertySheet.PropertyChangeListener pPropertyChangeListener )
	{
		aElement = pElement;
		prepareStage(pOwner);
		aStage.setScene(createScene(pPropertyChangeListener));
	}
	
	private void prepareStage(Stage pOwner) 
	{
		aStage.setResizable(false);
		aStage.initModality(Modality.APPLICATION_MODAL);
		aStage.initOwner(pOwner);
		aStage.setTitle(RESOURCES.getString("dialog.properties"));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
	}
	
	private Scene createScene(PropertySheet.PropertyChangeListener pPropertyChangeListener) 
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
		
		return new Scene(layout);
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