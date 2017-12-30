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

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ca.mcgill.cs.jetuml.graph.GraphElement;
import ca.mcgill.cs.jetuml.graph.Properties;

/**
 *  A GUI component that can present the properties of a GraphElement
 *  and allow editing them.
 *  
 *  @author Martin P. Robillard
 */
@SuppressWarnings("serial")
public class PropertySheet2 extends JPanel
{
	private static final int TEXT_FIELD_WIDTH = 10;
	private static final String INVISIBLE_PROPERTY_MARKER = "**INVISIBLE**";
	private static Map<Class<?>, Class<?>> editors;
	private static ResourceBundle aPropertyNames = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.graph.GraphElementProperties");

	private ArrayList<ChangeListener> aChangeListeners = new ArrayList<>();
	
	static
	{  
	      editors = new HashMap<>();
	      editors.put(String.class, PropertyEditorSupport.class);
	}
	
	/**
	 * Constructs a PropertySheet to show and support editing all the properties 
	 * for pElement.
	 * 
	 * @param pElement The element whose properties we wish to edit.
	 * @pre pElement != null
	 */
	public PropertySheet2(GraphElement pElement)
	{
		assert pElement != null;
		setLayout(new FormLayout());
		Properties properties = pElement.properties();
		for( String property : properties )
		{
			add(new JLabel(getPropertyName(pElement.getClass(), property)));
			add(getEditorComponent(properties, property));
		}
//		try
//		{
//			PropertyDescriptor[] descriptors = Introspector.getBeanInfo(pElement.getClass()).getPropertyDescriptors().clone();
//			Arrays.sort(descriptors, new Comparator<PropertyDescriptor>()
//			{
//				public int compare(PropertyDescriptor pDescriptor1, PropertyDescriptor pDescriptor2)
//				{
//					int index1 = PropertyOrder.getInstance().getIndex(pElement.getClass(), pDescriptor1.getName());
//					int index2 = PropertyOrder.getInstance().getIndex(pElement.getClass(), pDescriptor2.getName());
//					if( index1 == index2 )
//					{
//						return pDescriptor1.getName().compareTo(pDescriptor2.getName());
//					}
//					else
//					{
//						return index1 - index2;
//					}
//				}
//			});
//			
//			for(PropertyDescriptor descriptor : descriptors)
//			{
//				PropertyEditor editor = getEditor(pElement, descriptor);
//				String propertyName = getPropertyName(pElement.getClass(), descriptor.getName());
//				if(editor != null && !propertyName.equals(INVISIBLE_PROPERTY_MARKER))
//				{
//					add(new JLabel(propertyName));
//					add(getEditorComponent(editor));
//				}
//			}		
//		}
//		catch (IntrospectionException exception)
//		{
//			// Do nothing
//		}
	}

	/**
     * Gets the property editor for a given property,
     * and wires it so that it updates the given object.
     * @param pBean the object whose properties are being edited
     * @param pDescriptor the descriptor of the property to be edited
     * @return a property editor that edits the property
     *  with the given descriptor and updates the given object
	 */
	public PropertyEditor getEditor(final Object pBean, PropertyDescriptor pDescriptor)
	{
		try
		{
			final Method getter = pDescriptor.getReadMethod();
			final Method setter = pDescriptor.getWriteMethod();
			if(getter == null || setter == null )
			{
				return null;
			}
			
			Class<?> type = pDescriptor.getPropertyType();
			final PropertyEditor editor;
			Class<?> editorClass = pDescriptor.getPropertyEditorClass();
			if(editorClass == null && editors.containsKey(type))
			{
				editorClass = editors.get(type);
			}
			if(editorClass != null)
			{
				editor = (PropertyEditor) editorClass.newInstance();
			}
			else
			{
				editor = PropertyEditorManager.findEditor(type);
			}
			if(editor == null)
			{
				return null;
			}

			Object value = getter.invoke(pBean, new Object[] {});
			editor.setValue(value);
			editor.addPropertyChangeListener(new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent pEvent)
				{
					try
					{	
						setter.invoke(pBean, new Object[] { editor.getValue() });
						fireStateChanged(null);
					}
					catch(IllegalAccessException | InvocationTargetException exception)
					{
						exception.printStackTrace();
					}
				}
			});
			return editor;
		}
		catch(InstantiationException | IllegalAccessException | InvocationTargetException exception)
		{
			return null;
		}
	}

	public Component getEditorComponent(Properties pProperties, String pProperty)   
	{      
		System.out.println(pProperties.get(pProperty).getClass());
		if( pProperties.get(pProperty) instanceof String )
		{
			return createStringEditor(pProperties, pProperty);
		}
		return new JTextField();
//		String[] tags = pEditor.getTags();
//		String text = pEditor.getAsText();
//		if(pEditor.supportsCustomEditor())
//		{
//			return pEditor.getCustomEditor();         
//         
//		}
//		else if(tags != null)
//		{
//			// make a combo box that shows all tags
//			final JComboBox<String> comboBox = new JComboBox<>(tags);
//			comboBox.setSelectedItem(text);
//			comboBox.addItemListener(new ItemListener()
//            	{
//					public void itemStateChanged(ItemEvent pEvent)
//					{
//						if(pEvent.getStateChange() == ItemEvent.SELECTED)
//						{
//							pEditor.setAsText((String)comboBox.getSelectedItem());
//						}
//					}
//            	});
//			return comboBox;
//		}
//		else 
//		{
//			final JTextField textField = new JTextField(text, 10);
//			textField.getDocument().addDocumentListener(new DocumentListener()
//            	{
//					public void insertUpdate(DocumentEvent pEvent) 
//					{
//						pEditor.setAsText(textField.getText());	
//					}
//					public void removeUpdate(DocumentEvent pEvent) 
//					{
//						pEditor.setAsText(textField.getText());
//					}
//					public void changedUpdate(DocumentEvent pEvent) 
//					{}
//            	});
//			return textField;
//		}
	}
	
	private Component createStringEditor(Properties pProperties, String pProperty)
	{
		JTextField textField = new JTextField((String) pProperties.get(pProperty), TEXT_FIELD_WIDTH);
		textField.getDocument().addDocumentListener(new DocumentListener()
        	{
				public void insertUpdate(DocumentEvent pEvent) 
				{
					pProperties.set(pProperty, textField.getText());
				}
				public void removeUpdate(DocumentEvent pEvent) 
				{
					pProperties.set(pProperty, textField.getText());
				}
				public void changedUpdate(DocumentEvent pEvent) 
				{}
        	});
		return textField;
	}

	/**
     * Adds a change listener to the list of listeners.
     * @param pListener the listener to add
	 */
	public void addChangeListener(ChangeListener pListener)
	{
		aChangeListeners.add(pListener);
	}

	/**
     * Notifies all listeners of a state change.
     * @param pEvent the event to propagate
	 */
	private void fireStateChanged(ChangeEvent pEvent)
	{
		for(ChangeListener listener : aChangeListeners)
		{
			listener.stateChanged(pEvent);
		}
	}

	/**
	 * @return aEmpty whether this PropertySheet has fields to edit or not.
	 */
	public boolean isEmpty()
	{
		return getComponentCount() == 0;
	}
	
	/*
	 * Obtains the externalized name of a property and takes account
	 * of property inheritance: if a property is not found on a class,
	 * looks for the property name is superclasses.
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

