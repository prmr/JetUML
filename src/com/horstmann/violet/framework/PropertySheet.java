/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet.framework;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *   A component filled with editors for all editable properties 
 *  of an object.
 */
@SuppressWarnings("serial")
public class PropertySheet extends JPanel
{
	private static final int MAX_TEXT_LENGTH = 15;
	
	private static Map editors;

	private ArrayList aChangeListeners = new ArrayList();
	private Component aParent;
	
	/**
     * Constructs a property sheet that shows the editable
     * properties of a given object.
     * @param pBean the object whose properties are being edited
     * @param pParent the parent component
	 */
	public PropertySheet(Object pBean, Component pParent)
	{
		aParent = pParent;
		try
		{
			BeanInfo info = Introspector.getBeanInfo(pBean.getClass());
			PropertyDescriptor[] descriptors = (PropertyDescriptor[])info.getPropertyDescriptors().clone();      
			Arrays.sort(descriptors, new Comparator<PropertyDescriptor>()
					{
						public int compare(PropertyDescriptor pDescriptor1, PropertyDescriptor pDescriptor2)
						{
							Integer p1 = (Integer)pDescriptor1.getValue("priority");
							Integer p2 = (Integer)pDescriptor2.getValue("priority");
							if(p1 == null && p2 == null)
							{
								return 0;
							}
							if(p1 == null)
							{
								return 1;
							}
							if(p2 == null)
							{
								return -1;
							}
							return p1.intValue() - p2.intValue();
						}
					});
			setLayout(new FormLayout());
			for(int i = 0; i < descriptors.length; i++)
			{
				PropertyEditor editor = getEditor(pBean, descriptors[i]);
				if(editor != null)
				{
					add(new JLabel(descriptors[i].getName()));
					add(getEditorComponent(editor));
				}
			}		
		}
		catch (IntrospectionException exception)
		{
			exception.printStackTrace();
		}
	}

   /**
      Gets the property editor for a given property,
      and wires it so that it updates the given object.
      @param bean the object whose properties are being edited
      @param descriptor the descriptor of the property to
      be edited
      @return a property editor that edits the property
      with the given descriptor and updates the given object
   */
   public PropertyEditor getEditor(final Object bean,
      PropertyDescriptor descriptor)
   {
      try
      {
         Method getter = descriptor.getReadMethod();
         if (getter == null) return null;
         final Method setter = descriptor.getWriteMethod();
         if (setter == null) return null;
         Class type = descriptor.getPropertyType();
         final PropertyEditor editor;
         Class editorClass = descriptor.getPropertyEditorClass();
         if (editorClass == null && editors.containsKey(type))
            editorClass = (Class) editors.get(type);
         if (editorClass != null)            
            editor = (PropertyEditor) editorClass.newInstance();
         else
            editor = PropertyEditorManager.findEditor(type);
         if (editor == null) return null;

         Object value = getter.invoke(bean, new Object[] {});
         editor.setValue(value);
         editor.addPropertyChangeListener(new
            PropertyChangeListener()
            {
               public void propertyChange(PropertyChangeEvent event)
               {
                  try
                  {
                     setter.invoke(bean, 
                        new Object[] { editor.getValue() });
                     fireStateChanged(null);
                  }
                  catch (IllegalAccessException exception)
                  {
                     exception.printStackTrace();
                  }
                  catch (InvocationTargetException exception)
                  {
                     exception.printStackTrace();
                  }
               }
            });
         return editor;
      }
      catch (InstantiationException exception)
      {
         exception.printStackTrace();
         return null;
      }
      catch (IllegalAccessException exception)
      {
         exception.printStackTrace();
         return null;
      }
      catch (InvocationTargetException exception)
      {
         exception.printStackTrace();
         return null;
      }
   }

   /**
      Wraps a property editor into a component.
      @param editor the editor to wrap
      @return a button (if there is a custom editor), 
      combo box (if the editor has tags), or text field (otherwise)
   */      
   public Component getEditorComponent(final PropertyEditor editor)   
   {      
      String[] tags = editor.getTags();
      String text = editor.getAsText();
      if (editor.supportsCustomEditor())
      {
         return editor.getCustomEditor();         
         
      }
      else if (tags != null)
      {
         // make a combo box that shows all tags
         final JComboBox comboBox = new JComboBox(tags);
         comboBox.setSelectedItem(text);
         comboBox.addItemListener(new
            ItemListener()
            {
               public void itemStateChanged(ItemEvent event)
               {
                  if (event.getStateChange() == ItemEvent.SELECTED)
                     editor.setAsText(
                        (String)comboBox.getSelectedItem());
               }
            });
         return comboBox;
      }
      else 
      {
         final JTextField textField = new JTextField(text, 10);
         textField.getDocument().addDocumentListener(new
            DocumentListener()
            {
               public void insertUpdate(DocumentEvent e) 
               {
                  try
                  {
                     editor.setAsText(textField.getText());
                  }
                  catch (IllegalArgumentException exception)
                  {
                  }
               }
               public void removeUpdate(DocumentEvent e) 
               {
                  try
                  {
                     editor.setAsText(textField.getText());
                  }
                  catch (IllegalArgumentException exception)
                  {
                  }
               }
               public void changedUpdate(DocumentEvent e) 
               {
               }
            });
         return textField;
      }
   }

   /**
      Formats text for the button that pops up a
      custom editor.
      @param text the property value as text
      @return the text to put on the button
   */
   private static String buttonText(String text)
   {
      if (text == null || text.equals("")) 
         return " ";
      if (text.length() > MAX_TEXT_LENGTH)
         return text.substring(0, MAX_TEXT_LENGTH) + "...";
      return text;
   }

   /**
      Adds a change listener to the list of listeners.
      @param listener the listener to add
   */
   public void addChangeListener(ChangeListener listener)
   {
      aChangeListeners.add(listener);
   }

   /**
      Notifies all listeners of a state change.
      @param event the event to propagate
   */
   private void fireStateChanged(ChangeEvent event)
   {
      for (int i = 0; i < aChangeListeners.size(); i++)
      {
         ChangeListener listener = (ChangeListener)aChangeListeners.get(i);
         listener.stateChanged(event);
      }
   }
   
   
   
   // workaround for Web Start bug
   public static class StringEditor extends PropertyEditorSupport
   {
      public String getAsText() { return (String) getValue(); }
      public void setAsText(String s) { setValue(s); }      
   }
   
   static
   {  
      editors = new HashMap();
      editors.put(String.class, StringEditor.class);
      editors.put(java.awt.Color.class, ColorEditor.class);
   }
   

}

