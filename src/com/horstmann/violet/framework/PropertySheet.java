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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;


/**
   A component filled with editors for all editable properties 
   of an object.
*/
public class PropertySheet extends JPanel
{
   /**
      Constructs a property sheet that shows the editable
      properties of a given object.
      @param object the object whose properties are being edited
      @param parent the parent component
   */
   public PropertySheet(Object bean, Component parent)
   {
      this.parent = parent;
      try
      {
         BeanInfo info 
            = Introspector.getBeanInfo(bean.getClass());
         PropertyDescriptor[] descriptors 
            = (PropertyDescriptor[])info.getPropertyDescriptors().clone();      
         Arrays.sort(descriptors, new
            Comparator()
            {
               public int compare(Object o1, Object o2)
               {
                  PropertyDescriptor d1 = (PropertyDescriptor)o1;
                  PropertyDescriptor d2 = (PropertyDescriptor)o2;
                  Integer p1 = (Integer)d1.getValue("priority");
                  Integer p2 = (Integer)d2.getValue("priority");
                  if (p1 == null && p2 == null) return 0;
                  if (p1 == null) return 1;
                  if (p2 == null) return -1;
                  return p1.intValue() - p2.intValue();
               }
            });
         setLayout(new FormLayout());
         for (int i = 0; i < descriptors.length; i++)
         {
            PropertyEditor editor 
               = getEditor(bean, descriptors[i]);
            if (editor != null)
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
         /*
         
         // Make a button that pops up the custom editor
         final JButton button = new JButton();
         // if the editor is paintable, have it paint an icon
         if (editor.isPaintable())
         {
            button.setIcon(new 
               Icon()
               {
                  public int getIconWidth() { return WIDTH - 8; }
                  public int getIconHeight() { return HEIGHT - 8; }

                  public void paintIcon(Component c, Graphics g, 
                     int x, int y)
                  {
                     g.translate(x, y);
                     Rectangle r = new Rectangle(0, 0, 
                        getIconWidth(), getIconHeight());
                     Color oldColor = g.getColor();
                     g.setColor(Color.BLACK);
                     editor.paintValue(g, r);
                     g.setColor(oldColor);
                     g.translate(-x, -y);
                  }
               });
         } 
         else 
            button.setText(buttonText(text));
         // pop up custom editor when button is clicked
         button.addActionListener(new
            ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  final Component customEditor = 
                     editor.getCustomEditor();
                   
                  JOptionPane.showMessageDialog(parent,
                     customEditor);
                  
                  // This should really be showInternalMessageDialog,
                  // but then you get awful focus behavior with JDK 5.0
                  // (i.e. the property sheet retains focus). In 
                  // particular, the color dialog never works.
                  
                  if (editor.isPaintable())
                     button.repaint();
                  else 
                     button.setText(buttonText(editor.getAsText()));
               }
            });
         return button;
         */         
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
      changeListeners.add(listener);
   }

   /**
      Notifies all listeners of a state change.
      @param event the event to propagate
   */
   private void fireStateChanged(ChangeEvent event)
   {
      for (int i = 0; i < changeListeners.size(); i++)
      {
         ChangeListener listener = (ChangeListener)changeListeners.get(i);
         listener.stateChanged(event);
      }
   }
   
   private ArrayList changeListeners = new ArrayList();
   private Component parent;
   
   private static Map editors;
   
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
   
   private static final int WIDTH = 100;
   private static final int HEIGHT = 25;
   private static final int MAX_TEXT_LENGTH = 15;
}

