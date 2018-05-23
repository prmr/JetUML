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

import ca.mcgill.cs.jetuml.application.ApplicationResources;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;

/**
 * A utility class for creating menus from strings in a resource bundle. The class does not 
 * depend on the Singleton ApplicationResource instance to improve testability.
 * 
 * An instance of this class is intended to be initialized with a resource bundle,
 * used to create any number of menus, then discarded.
 */
class MenuFactory
{
	private static final boolean IS_MAC = isMacOS();
	
	private final ApplicationResources aResources;
	
	MenuFactory(ApplicationResources pResources)
	{
		aResources = pResources;
	}
	
	private static boolean isMacOS()
	{
		boolean result = false;
		try
		{
			result = System.getProperty("os.name", "unknown").toLowerCase().startsWith("mac");
		}
		catch( SecurityException pException )
		{ /* Result stays false */ }
		return result;
	}
	
	/**
	 * Creates a normal menu item with pHandler as the event handler.
	 * @param pPrefix A string such as "file.open" that indicates the menu->submenu path
	 * @param pHandler The callback to execute when the menu item is selected.
	 * @return A menu item for the action described.
	 */
	public MenuItem createMenuItem(String pPrefix, EventHandler<ActionEvent> pHandler)
	{
		return initialize(new MenuItem(), pPrefix, pHandler);
	}

	/**
	 * Create a checkbox menu.
	 * @param pPrefix A string such as "file.open" that indicates the menu->submenu path
	 * @param pHandler The callback to execute when the menu item is selected.
	 * @return A menu item for the action described.
	 */
	public MenuItem createCheckMenuItem(String pPrefix, EventHandler<ActionEvent> pHandler) 
	{
		return initialize(new CheckMenuItem(), pPrefix, pHandler);
	}	
	
	/*
	 * Initializes pMenuItem with text, mnemonic, accelerator, etc., and returns it.
	 */
	private MenuItem initialize(MenuItem pMenuItem, String pPrefix, EventHandler<ActionEvent> pHandler)
	{
		pMenuItem.setOnAction(pHandler);
		String text = aResources.getString(pPrefix + ".text");
		
		if( aResources.containsKey(pPrefix + ".mnemonic"))
		{
			// get index of character to properly insert mnemonic symbol "_"
			int index = text.indexOf(aResources.getString(pPrefix + ".mnemonic").charAt(0));
			if(index < 0) 
			{
				index = text.indexOf(aResources.getString(pPrefix + ".mnemonic").toLowerCase().charAt(0));
			}
			
			if (index >= 0) 
			{
				text = text.substring(0, index) + "_" + text.substring(index);
			}
			else 
			{
				pMenuItem.setAccelerator(KeyCombination.keyCombination("ALT+"+ aResources.getString(pPrefix + ".mnemonic")));
			}
		}
		pMenuItem.setText(text);
		
		if( aResources.containsKey(pPrefix + ".accelerator.mac"))
		{
			if(IS_MAC)
			{
				pMenuItem.setAccelerator(KeyCombination.keyCombination(aResources.getString(pPrefix + ".accelerator.mac")));	
			}
			else
			{
				pMenuItem.setAccelerator(KeyCombination.keyCombination(aResources.getString(pPrefix + ".accelerator.win")));
			}	
		}
		
		if( aResources.containsKey(pPrefix + ".icon"))
		{
			pMenuItem.setGraphic(new ImageView(aResources.getString(pPrefix + ".icon").toString()));
		}
		return pMenuItem;
	}
	
	/**
	 * Create a menu that corresponds to the resource for key pPrefix.
	 * @param pPrefix A string such as "file" that indicates the menu->submenu path
	 * @return A configured menu
	 */
	public Menu createMenu(String pPrefix)
	{
		String text = aResources.getString(pPrefix + ".text");
		Menu menu = new Menu();
		if( aResources.containsKey(pPrefix + ".mnemonic"))
		{
			int index = text.indexOf(aResources.getString(pPrefix + ".mnemonic").charAt(0));
			assert index >= 0;
			text = text.substring(0, index) + "_" + text.substring(index);
		}
		menu.setText(text);
		
		if( aResources.containsKey(pPrefix + ".accelerator.mac"))
		{
			if(IS_MAC)
			{
				menu.setAccelerator(KeyCombination.keyCombination(aResources.getString(pPrefix + ".accelerator.mac")));	
			}
			else
			{
				menu.setAccelerator(KeyCombination.keyCombination(aResources.getString(pPrefix + ".accelerator.win")));
			}
		}

		if( aResources.containsKey(pPrefix + ".icon"))
		{
			menu.setGraphic(new ImageView(aResources.getString(pPrefix + ".icon").toString()));
		}

		return menu;
	}

}
