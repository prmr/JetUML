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

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class ResourceFactory
{
   public ResourceFactory(ResourceBundle bundle)
   {
      this.bundle = bundle;
   }

   public ResourceBundle getBundle()
   {
      return bundle;
   }

   public JMenuItem createMenuItem(String prefix, 
      Object target, String methodName)
   {
      return createMenuItem(prefix,
         (ActionListener) EventHandler.create(
            ActionListener.class, target, methodName));
   }

   public JMenuItem createMenuItem(String prefix, 
      ActionListener listener)
   {
      String text = bundle.getString(prefix + ".text");
      JMenuItem menuItem = new JMenuItem(text);
      return configure(menuItem, prefix, listener);
   }

   public JMenuItem createCheckBoxMenuItem(String prefix, 
      ActionListener listener)
   {
      String text = bundle.getString(prefix + ".text");
      JMenuItem menuItem = new JCheckBoxMenuItem(text);
      return configure(menuItem, prefix, listener);
   }

   public JMenuItem configure(JMenuItem menuItem, 
      String prefix, ActionListener listener)
   {      
      menuItem.addActionListener(listener);
      try
      {
         String mnemonic = bundle.getString(prefix + ".mnemonic");
         menuItem.setMnemonic(mnemonic.charAt(0));
      }
      catch (MissingResourceException exception)
      {
         // ok not to set mnemonic
      }

      try
      {
         String accelerator = bundle.getString(prefix + ".accelerator");
         menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator));
      }
      catch (MissingResourceException exception)
      {
         // ok not to set accelerator
      }

      try
      {
         String tooltip = bundle.getString(prefix + ".tooltip");
         menuItem.setToolTipText(tooltip);         
      }
      catch (MissingResourceException exception)
      {
         // ok not to set tooltip
      }
      return menuItem;
   }
   
   public JMenu createMenu(String prefix)
   {
      String text = bundle.getString(prefix + ".text");
      JMenu menu = new JMenu(text);
      try
      {
         String mnemonic = bundle.getString(prefix + ".mnemonic");
         menu.setMnemonic(mnemonic.charAt(0));
      }
      catch (MissingResourceException exception)
      {
         // ok not to set mnemonic
      }

      try
      {
         String tooltip = bundle.getString(prefix + ".tooltip");
         menu.setToolTipText(tooltip);         
      }
      catch (MissingResourceException exception)
      {
         // ok not to set tooltip
      }
      return menu;
   }
      
   public JButton createButton(String prefix)
   {
      String text = bundle.getString(prefix + ".text");
      JButton button = new JButton(text);
      try
      {
         String mnemonic = bundle.getString(prefix + ".mnemonic");
         button.setMnemonic(mnemonic.charAt(0));
      }
      catch (MissingResourceException exception)
      {
         // ok not to set mnemonic
      }

      try
      {
         String tooltip = bundle.getString(prefix + ".tooltip");
         button.setToolTipText(tooltip);         
      }
      catch (MissingResourceException exception)
      {
         // ok not to set tooltip
      }
      return button;
   }
   
   
   public Action configureAction(String prefix, Action action)
   {
      try
      {
         String text = bundle.getString(prefix + ".text");
         action.putValue(Action.NAME, text);
      }
      catch (MissingResourceException exception)
      {
         // ok not to set name
      }

      try
      {
         String mnemonic = bundle.getString(prefix + ".mnemonic");
         action.putValue(Action.MNEMONIC_KEY, new Integer(mnemonic.charAt(0)));
      }
      catch (MissingResourceException exception)
      {
         // ok not to set mnemonic
      }

      try
      {
         String accelerator = bundle.getString(prefix + ".accelerator");
         action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
      }
      catch (MissingResourceException exception)
      {
         // ok not to set accelerator
      }

      try
      {
         String tooltip = bundle.getString(prefix + ".tooltip");
         action.putValue(Action.SHORT_DESCRIPTION, tooltip);         
      }
      catch (MissingResourceException exception)
      {
         // ok not to set tooltip
      }
      return action;
   }

   private ResourceBundle bundle;
}
