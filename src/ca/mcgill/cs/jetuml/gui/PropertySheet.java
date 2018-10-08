/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.lang.reflect.InvocationTargetException;

import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Property;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 *  A layout that presents the properties of a DiagramElement
 *  and allow editing them.
 */
public class PropertySheet extends GridPane
{
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
			if(property.isVisible() && editor != null )
			{
				add(new Label(getPropertyName(pElement.getClass(), property.getName())), 0, row);
				add(editor, 1, row);
				row++;
			}
		}
		setVgap(LAYOUT_SPACING);
		setHgap(LAYOUT_SPACING);
		setPadding(new Insets(LAYOUT_PADDING));
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
			if( extended(pProperty.getName()))
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
	private boolean extended(String pProperty)
	{
		return 	aElement.getClass() == ClassNode.class ||
				aElement.getClass() == InterfaceNode.class ||
				aElement.getClass() == PackageNode.class && pProperty.equals("contents") ||
				aElement.getClass() == NoteNode.class;
	} // CSON:
	
	private Control createExtendedStringEditor(Property pProperty)
	{
		final int rows = 5;
		final int columns = 30;
		final TextArea textArea = new TextArea();
		textArea.setPrefRowCount(rows);
		textArea.setPrefColumnCount(columns);

		textArea.addEventFilter(KeyEvent.KEY_PRESSED, pKeyEvent ->
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

		textArea.setText((String) pProperty.get());
		textArea.textProperty().addListener((pObservable, pOldValue, pNewValue) -> 
		{
		   pProperty.set(textArea.getText());
		   aListener.propertyChanged();
		});
		
		return new ScrollPane(textArea);
	}
	
	private Control createStringEditor(Property pProperty)
	{
		TextField textField = new TextField((String) pProperty.get());
		textField.setPrefColumnCount(TEXT_FIELD_WIDTH);
		
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
			final ComboBox<Enum<?>> comboBox = new ComboBox<Enum<?>>(FXCollections.observableArrayList(enumValues));
			
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

	/*
	 * Obtains the externalized name of a property and takes account
	 * of property inheritance: if a property is not found on a class,
	 * looks for the property name is superclasses. We do not use the actual
	 * property names to decouple visual representation (which can eventually
	 * be translated) from names in the design space.
	 */
	private static String getPropertyName(Class<?> pClass, String pProperty)
	{
		assert pProperty != null;
		if( pClass == null )
		{
			return pProperty;
		}
		String key = pClass.getSimpleName() + "." + pProperty;
		if( !RESOURCES.containsKey(key) )
		{
			return getPropertyName(pClass.getSuperclass(), pProperty);
		}
		else
		{
			return RESOURCES.getString(key);
		}
	}
}

