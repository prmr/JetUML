/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ca.mcgill.cs.jetuml.application.MultiLineString;
import ca.mcgill.cs.jetuml.graph.GraphElement;
import ca.mcgill.cs.jetuml.graph.Properties;

/**
 *  A GUI component that can present the properties of a GraphElement
 *  and allow editing them.
 *  
 *  @author Martin P. Robillard
 */
@SuppressWarnings("serial")
public class PropertySheet extends JPanel
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
	private static Set<AWTKeyStroke> tab = new HashSet<>(1);
	private static Set<AWTKeyStroke> shiftTab = new HashSet<>(1);
	private static ResourceBundle aPropertyNames = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.graph.GraphElementProperties");

	private final PropertyChangeListener aListener;
	
	static
	{  
		tab.add(KeyStroke.getKeyStroke("TAB" ));
		shiftTab.add(KeyStroke.getKeyStroke( "shift TAB" ));
	}
	
	/**
	 * Constructs a PropertySheet to show and support editing all the properties 
	 * for pElement.
	 * 
	 * @param pElement The element whose properties we wish to edit.
	 * @param pListener An object that responds to property change events.
	 * @pre pElement != null
	 */
	public PropertySheet(GraphElement pElement, PropertyChangeListener pListener)
	{
		assert pElement != null;
		aListener = pListener;
		setLayout(new FormLayout());
		Properties properties = pElement.properties();
		for( String property : properties )
		{
			Component editor = getEditorComponent(properties, property);
			if(properties.isVisible(property) && editor != null )
			{
				add(new JLabel(getPropertyName(pElement.getClass(), property)));
				add(editor);
			}
		}
	}
	
	/**
	 * @return aEmpty whether this PropertySheet has fields to edit or not.
	 */
	public boolean isEmpty()
	{
		return getComponentCount() == 0;
	}
	
	private Component getEditorComponent(Properties pProperties, String pProperty)   
	{      
		if( pProperties.get(pProperty) instanceof String )
		{
			return createStringEditor(pProperties, pProperty);
		}
		else if( pProperties.get(pProperty) instanceof MultiLineString )
		{
			return createMultiLineStringEditor(pProperties, pProperty);
		}
		else if(  pProperties.get(pProperty) instanceof Enum )
		{
			return createEnumEditor(pProperties, pProperty);
		}
		else if( pProperties.get(pProperty) instanceof Boolean)
		{
			return createBooleanEditor(pProperties, pProperty);
		}
		return new JTextField();
	}
	
	private Component createMultiLineStringEditor(Properties pProperties, String pProperty)
	{
		final MultiLineString value = (MultiLineString) pProperties.get(pProperty);
		final int rows = 5;
		final int columns = 30;
		final JTextArea textArea = new JTextArea(rows, columns);

		textArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, tab);
		textArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, shiftTab);

		textArea.setText(value.getText());
		textArea.getDocument().addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent pEvent) 
			{
				value.setText(textArea.getText());
				aListener.propertyChanged();
			}
			public void removeUpdate(DocumentEvent pEvent) 
			{
				value.setText(textArea.getText());
				aListener.propertyChanged();
			}
			public void changedUpdate(DocumentEvent pEvent) 
			{}
		});
		return new JScrollPane(textArea);
	}
	
	private Component createStringEditor(Properties pProperties, String pProperty)
	{
		JTextField textField = new JTextField((String) pProperties.get(pProperty), TEXT_FIELD_WIDTH);
		textField.getDocument().addDocumentListener(new DocumentListener()
        	{
				public void insertUpdate(DocumentEvent pEvent) 
				{
					pProperties.set(pProperty, textField.getText());
					aListener.propertyChanged();
				}
				public void removeUpdate(DocumentEvent pEvent) 
				{
					pProperties.set(pProperty, textField.getText());
					aListener.propertyChanged();
				}
				public void changedUpdate(DocumentEvent pEvent) 
				{}
        	});
		return textField;
	}
	
	private Component createEnumEditor(Properties pProperties, String pProperty)
	{
		Enum<?> value = (Enum<?>)pProperties.get(pProperty);
		try 
		{
			final JComboBox<Enum<?>> comboBox = new JComboBox<Enum<?>>((Enum<?>[])value.getClass().getMethod("values").invoke(null));
			comboBox.setSelectedItem(value);
			comboBox.addItemListener(new ItemListener()
			{
					@Override
					public void itemStateChanged(ItemEvent pEvent)
					{
						if(pEvent.getStateChange() == ItemEvent.SELECTED)
						{
							pProperties.set(pProperty, comboBox.getSelectedItem().toString());
							aListener.propertyChanged();
						}
					}
	        	});
			return comboBox;
		}
		catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException e) 
		{ 
			return null; 
		}
	}
	
	private Component createBooleanEditor(Properties pProperties, String pProperty)
	{
		JCheckBox checkBox = new JCheckBox();
		checkBox.setSelected((boolean)pProperties.get(pProperty));
		checkBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
				pProperties.set(pProperty, checkBox.isSelected());
				aListener.propertyChanged();
			}
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
		if( !aPropertyNames.containsKey(key) )
		{
			return getPropertyName(pClass.getSuperclass(), pProperty);
		}
		else
		{
			return aPropertyNames.getString(key);
		}
	}
}

