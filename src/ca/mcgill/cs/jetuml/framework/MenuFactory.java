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

package ca.mcgill.cs.jetuml.framework;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * A class for creating menus from strings in a 
 * resource bundle.
 */
class MenuFactory
{
	private ResourceBundle aBundle;
	private final String aSystem; 
	
	/**
	 * @param pBundle The bundle to use to fetch
	 * resources.
	 */
	MenuFactory(ResourceBundle pBundle)
	{
		aBundle = pBundle;
		aSystem = System.getProperty("os.name").toLowerCase();
	}

	/**
	 * Creates a menu item that calls a method in response to the action event.
	 * @param pPrefix A string such as "file.open" that indicates the menu->submenu path
	 * @param pTarget The object on which pMethodName will be invoked when the menu is selected.
	 * @param pMethodName The method to invoke when the menu is selected.
	 * @return A menu item for the action described.
	 */
	public JMenuItem createMenuItem(String pPrefix, Object pTarget, String pMethodName)
	{
		return createMenuItem(pPrefix, EventHandler.create(ActionListener.class, pTarget, pMethodName));
	}

	/**
	 * Creates a menu item where pListener is triggered when the menu item is selected.
	 * @param pPrefix A string such as "file.open" that indicates the menu->submenu path
	 * @param pListener The callback to execute when the menu item is selected.
	 * @return A menu item for the action described.
	 */
	public JMenuItem createMenuItem(String pPrefix, ActionListener pListener)
	{
		String text = aBundle.getString(pPrefix + ".text");
		JMenuItem menuItem = new JMenuItem(text);
		return configure(menuItem, pPrefix, pListener);
	}

	/**
	 * Create a checkbox menu.
	 * @param pPrefix A string such as "file.open" that indicates the menu->submenu path
	 * @param pListener The callback to execute when the menu item is selected.
	 * @return A menu item for the action described.
	 */
	public JMenuItem createCheckBoxMenuItem(String pPrefix, ActionListener pListener)
	{
		String text = aBundle.getString(pPrefix + ".text");
		JMenuItem menuItem = new JCheckBoxMenuItem(text);
		return configure(menuItem, pPrefix, pListener);
	}	

	/*
	 * Configures the menu with text, mnemonic, accelerator, etc
	 */
	private JMenuItem configure(JMenuItem pMenuItem, String pPrefix, ActionListener pListener)
	{
		pMenuItem.addActionListener(pListener);
		if( aBundle.containsKey(pPrefix + ".mnemonic"))
		{
			pMenuItem.setMnemonic(aBundle.getString(pPrefix + ".mnemonic").charAt(0));
		}
		if( aBundle.containsKey(pPrefix + ".accelerator.mac"))
		{
			if(aSystem.indexOf("mac") >= 0)
			{
				pMenuItem.setAccelerator(KeyStroke.getKeyStroke(aBundle.getString(pPrefix + ".accelerator.mac")));	
			}
			else
			{
				pMenuItem.setAccelerator(KeyStroke.getKeyStroke(aBundle.getString(pPrefix + ".accelerator.win")));
			}
			
		}
		if( aBundle.containsKey(pPrefix + ".tooltip"))
		{
			pMenuItem.setToolTipText(aBundle.getString(pPrefix + ".tooltip"));         
		}
		if( aBundle.containsKey(pPrefix + ".icon"))
		{
			pMenuItem.setIcon(new ImageIcon(getClass().getClassLoader().getResource(aBundle.getString(pPrefix + ".icon"))));
		}
		return pMenuItem;
	}
	
	/**
	 * Create a menu that corresponds to the resource for key pPrefix.
	 * @param pPrefix A string such as "file" that indicates the menu->submenu path
	 * @return A configured menu
	 */
	public JMenu createMenu(String pPrefix)
	{
		String text = aBundle.getString(pPrefix + ".text");
		JMenu menu = new JMenu(text);
		if( aBundle.containsKey(pPrefix + ".mnemonic"))
		{
			menu.setMnemonic(aBundle.getString(pPrefix + ".mnemonic").charAt(0));
		}
		if( aBundle.containsKey(pPrefix + ".tooltip"))
      	{
      		menu.setToolTipText(aBundle.getString(pPrefix + ".tooltip"));         
      	}
		if( aBundle.containsKey(pPrefix + ".accelerator.mac"))
		{
			if(aSystem.indexOf("mac") >= 0)
			{
				menu.setAccelerator(KeyStroke.getKeyStroke(aBundle.getString(pPrefix + ".accelerator.mac")));	
			}
			else
			{
				menu.setAccelerator(KeyStroke.getKeyStroke(aBundle.getString(pPrefix + ".accelerator.win")));
			}
		}
		if( aBundle.containsKey(pPrefix + ".icon"))
		{
			menu.setIcon(new ImageIcon(getClass().getClassLoader().getResource(aBundle.getString(pPrefix + ".icon"))));
		}

		return menu;
	}
}
