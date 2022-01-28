/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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

import java.lang.reflect.InvocationTargetException;

import ca.mcgill.cs.jetuml.application.ApplicationResources;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Property;
import ca.mcgill.cs.jetuml.diagram.PropertyName;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageDescriptionNode;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 *  A layout that presents the properties of a DiagramElement
 *  and allow editing them.
 */
public class PropertySheet extends GridPane
{
    private static final KeyCombination STEREOTYPE_DELIMITER_TRIGGER = 
    		new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);

	/**
	 * A handler for whenever a property is being detected
	 * as being edited. This allows a more responsive UI,
	 * where properties can be shown as they are typed, as
	 * opposed to only when the value is entered.
	 */
	interface PropertyChangeListener
	{
		void propertyChanged();
	}
	
	private static final int TEXT_FIELD_WIDTH = 10;
	private static final int LAYOUT_SPACING = 10;
	private static final int LAYOUT_PADDING = 20;

	private final PropertyChangeListener aListener;
	private final DiagramElement aElement;

	/**
	 * Constructs a PropertySheet to show and support editing all the properties 
	 * for pElement.
	 * 
	 * @param pElement The element whose properties we wish to edit.
	 * @param pListener An object that responds to property change events.
	 * @pre pElement != null
	 */
	public PropertySheet(DiagramElement pElement, PropertyChangeListener pListener)
	{
		assert pElement != null;
		aListener = pListener;
		aElement = pElement;
		int row = 0;
		for( Property property : aElement.properties() )
		{
			Control editor = getEditorControl(property);
			if( editor != null )
			{
				add(new Label(labelName(pElement, property.name())), 0, row);
				add(editor, 1, row);
				row++;
			}
		}
		setVgap(LAYOUT_SPACING);
		setHgap(LAYOUT_SPACING);
		setPadding(new Insets(LAYOUT_PADDING));
	}
	
	/*
	 * Special case due to the poor decision in the past to declare NodeNodes as a 
	 * subclass of Name nodes, which means that its control would use the NAME
	 * property and thus be labeled with "Name" instead of "Text". Ideally, the 
	 * property for NodeNodes should be a new enum called TEXT, and NoteNode should 
	 * not be children of NamedNodes. However, changing this would break backward
	 * compatibility.
	 * TODO: Next time we do a backward-incompatible release, we'll fix this.
	 */
	private static String labelName(DiagramElement pElement, PropertyName pPropertyName)
	{
		if( pElement.getClass() == NoteNode.class && pPropertyName == PropertyName.NAME )
		{
			return ApplicationResources.RESOURCES.getString("NoteNode.name");
		}
		return pPropertyName.visible();
	}
	
	/**
	 * @return aEmpty whether this PropertySheet has fields to edit or not.
	 */
	public boolean isEmpty()
	{
		return getChildren().isEmpty();
	}
	
	/**
	 * @return The element being edited.
	 */
	public DiagramElement getElement()
	{
		return aElement;
	}

	private Control getEditorControl(Property pProperty)   
	{      
		if( pProperty.get() instanceof String )
		{
			if( extended(pProperty.name()))
			{
				return createExtendedStringEditor(pProperty);
			}
			else
			{
				return createStringEditor(pProperty);
			}
		}
		else if( pProperty.get() instanceof Enum )
		{
			return createEnumEditor(pProperty);
		}
		else if( pProperty.get() instanceof Boolean)
		{
			return createBooleanEditor(pProperty);
		}
		return null;
	}
	
	/*
	 * Not the greatest but avoids over-engineering the rest of the properties API. CSOFF:
	 */
	private boolean extended(PropertyName pProperty)
	{
		return 	aElement.getClass() == ClassNode.class ||
				aElement.getClass() == InterfaceNode.class ||
				aElement.getClass() == PackageDescriptionNode.class && pProperty == PropertyName.CONTENTS ||
				aElement.getClass() == NoteNode.class;
	} // CSON:
	
	private Control createExtendedStringEditor(Property pProperty)
	{
		final int rows = 5;
		final int columns = 30;
		final TextArea textArea = new TextArea();
		textArea.setPrefRowCount(rows);
		textArea.setPrefColumnCount(columns);

		addTabbingFeature(textArea);
		addStereotypeDelimiterFeature(textArea);

		textArea.setText((String) pProperty.get());
		textArea.textProperty().addListener((pObservable, pOldValue, pNewValue) -> 
		{
		   pProperty.set(textArea.getText());
		   aListener.propertyChanged();
		});
		
		return new ScrollPane(textArea);
	}
	
	/*
	 * Add a feature to the control that adds the stereotype delimiters to the input control
	 * if Ctrl-Q is typed, and position the caret between the delimiters.
	 */
	private static void addStereotypeDelimiterFeature(TextInputControl pTextInput)
	{
		pTextInput.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() 
		{
			@Override
		    public void handle(KeyEvent pKeyEvent) 
		    {
		    	if(STEREOTYPE_DELIMITER_TRIGGER.match(pKeyEvent)) 
		    	{
		    		pTextInput.setText(pTextInput.getText() + "\u00AB\u00BB");
		    		pTextInput.end();
		    		pTextInput.backward();
		    		pKeyEvent.consume();
		    	}
		    }
		});
	}
	
	/*
	 * Make it possible to insert tab characters in a text area using Ctrl-Tab.
	 * The tab character is otherwise used to switch between fields.
	 */
	private static void addTabbingFeature(TextArea pTextArea)
	{
		pTextArea.addEventFilter(KeyEvent.KEY_PRESSED, pKeyEvent ->
		{
			final String aFocusEventText = "TAB_TO_FOCUS_EVENT";
			
			if (!KeyCode.TAB.equals(pKeyEvent.getCode()))
	        {
	            return;
	        }
	        if (pKeyEvent.isAltDown() || pKeyEvent.isMetaDown() || pKeyEvent.isShiftDown() || !(pKeyEvent.getSource() instanceof TextArea))
	        {
	            return;
	        }
	        final TextArea textAreaSource = (TextArea) pKeyEvent.getSource();
	        if (pKeyEvent.isControlDown())
	        {
	            if (!aFocusEventText.equalsIgnoreCase(pKeyEvent.getText()))
	            {
	            	pKeyEvent.consume();
	                textAreaSource.replaceSelection("\t");
	            }
	        }
	        else
	        {
	        	pKeyEvent.consume();
	            final KeyEvent tabControlEvent = new KeyEvent(pKeyEvent.getSource(), pKeyEvent.getTarget(), pKeyEvent.getEventType(), 
	            		pKeyEvent.getCharacter(), aFocusEventText, pKeyEvent.getCode(), pKeyEvent.isShiftDown(), true, pKeyEvent.isAltDown(),
	            		pKeyEvent.isMetaDown());
	            textAreaSource.fireEvent(tabControlEvent);
	        }
	    });
	}
	
	private Control createStringEditor(Property pProperty)
	{
		TextField textField = new TextField((String) pProperty.get());
		textField.setPrefColumnCount(TEXT_FIELD_WIDTH);
		addStereotypeDelimiterFeature(textField);
		
		textField.textProperty().addListener((pObservable, pOldValue, pNewValue) -> 
		{
			pProperty.set(textField.getText());
			aListener.propertyChanged();
		});

		return textField;
	}
	
	private Control createEnumEditor(Property pProperty)
	{
		Enum<?> value = (Enum<?>)pProperty.get();
		try 
		{
			Enum<?>[] enumValues = (Enum<?>[])value.getClass().getMethod("values").invoke(null);
			final ComboBox<Enum<?>> comboBox = new ComboBox<>(FXCollections.observableArrayList(enumValues));
			
			comboBox.getSelectionModel().select(value);
			comboBox.valueProperty().addListener((pObservable, pOldValue, pNewValue) -> 
			{
				pProperty.set(pNewValue.toString());
				aListener.propertyChanged();
			});
		
			return comboBox;
		}
		catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException e) 
		{ 
			return null; 
		}
	}
	
	private Control createBooleanEditor(Property pProperty)
	{
		CheckBox checkBox = new CheckBox();
		checkBox.setSelected((boolean)pProperty.get());
		checkBox.selectedProperty().addListener((pObservable, pOldValue, pNewValue) -> 
		{
			pProperty.set(pNewValue);
			aListener.propertyChanged();
		});

		return checkBox;
	}
}

