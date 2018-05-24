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
 * A class for creating menus from strings in a resource bundle. The class does not 
 * depend on the Singleton ApplicationResource instance to improve testability.
 * 
 * An instance of this class is intended to be initialized with a resource bundle,
 * used to create any number of menus, then discarded.
 * 
 * The user data for a menu item is a boolean flag that indicates whether the
 * menu is only relevant to a current diagram (true) or generally relevant (false).
 * 
 * To create a menu item, the creation methods will look for the following resources
 * in the specified ApplicationResources object, with a key followed by a prefix P.
 * 
 * - P.text (required):        The text of the menu
 * - P.mnemonic (optional):    The single letter mnemonic for opening the menu with the keyboard 
 * - P.icon (optional):	 	   The path to the menu icon
 * - P.accelerator (optional): The shortcut key combination.  Add ".mac" for macs.
 */
class MenuFactory
{
	private static final boolean IS_MAC = isMacOS();
	private static final String KEY_TEXT = ".text";
	private static final String KEY_MNEMONIC = ".mnemonic";
	private static final String KEY_ICON = ".icon";
	private static final String KEY_ACCELERATOR_MAC = ".accelerator.mac";
	private static final String KEY_ACCELERATOR = ".accelerator";
	
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
	public MenuItem createMenuItem(String pPrefix,  boolean pDiagramSpecific, EventHandler<ActionEvent> pHandler)
	{
		MenuItem item = new MenuItem();
		item.setUserData(pDiagramSpecific);
		initialize(item, pPrefix);
		item.setOnAction(pHandler);
		return item;
	}

	/**
	 * Create a checkbox menu.
	 * @param pPrefix A string such as "file.open" that indicates the menu->submenu path
	 * @param pHandler The callback to execute when the menu item is selected.
	 * @return A menu item for the action described.
	 */
	public MenuItem createCheckMenuItem(String pPrefix, boolean pDiagramSpecific, boolean pInitialState, EventHandler<ActionEvent> pHandler) 
	{
		CheckMenuItem item = new CheckMenuItem();
		item.setUserData(pDiagramSpecific);
		initialize(item, pPrefix);
		item.setOnAction(pHandler);
		item.setSelected(pInitialState);
		return item;
	}	
	
	/*
	 * Initializes pMenuItem with text, mnemonic, accelerator, etc., and returns it.
	 */
	private void initialize(MenuItem pMenuItem, String pPrefix)
	{
		assert aResources.containsKey(pPrefix + KEY_TEXT);
		
		String text = aResources.getString(pPrefix + KEY_TEXT);
		if( aResources.containsKey(pPrefix + KEY_MNEMONIC))
		{
			text = installMnemonic(text, aResources.getString(pPrefix + KEY_MNEMONIC));
		}
		
		pMenuItem.setText(text);
		if( aResources.containsKey(pPrefix + KEY_ICON))
		{
			pMenuItem.setGraphic(new ImageView(aResources.getString(pPrefix + KEY_ICON)));
		}
		
		if( IS_MAC && aResources.containsKey(pPrefix + KEY_ACCELERATOR_MAC))
		{
			pMenuItem.setAccelerator(KeyCombination.keyCombination(aResources.getString(pPrefix + KEY_ACCELERATOR_MAC)));	
		}
		else if( aResources.containsKey(pPrefix + KEY_ACCELERATOR))
		{
			pMenuItem.setAccelerator(KeyCombination.keyCombination(aResources.getString(pPrefix + KEY_ACCELERATOR)));
		}
	}
	
	/*
	 * @param pText The text to install the mnemonic on
	 * @param pMnemonic The letter that is the mnemonic
	 * @return A new text string with the character '_' before the letter
	 * that is to be the mnemonic for the menu item. The resulting
	 * string is intended to be set as the text of the menu. 
	 * If pMnemonic is not a single letter that is part of pText, it
	 * is simply ignored.
	 */
	private String installMnemonic(String pText, String pMnemonic)
	{
		if( pMnemonic.length() != 1 || !pText.contains(pMnemonic))
		{
			return pText;
		}
		int index = pText.indexOf(pMnemonic.charAt(0));
		assert index >=0 && index < pText.length();
		return pText.substring(0, index) + "_" + pText.substring(index);
	}
	
	/**
	 * Create a menu with the resources for key pPrefix.
	 * @param pPrefix A string such as "file" that indicates where to find the related
	 * resources in the resource bundle.
	 * @return A configured menu.
	 * @pre The .text and .mnemonic properties exist in the resource bundle
	 */
	public Menu createMenu(String pPrefix, boolean pDiagramSpecific)
	{
		Menu menu = new Menu();
		menu.setUserData(pDiagramSpecific);
		initialize(menu, pPrefix);
		return menu;
	}
	
	/**
	 * Create a menu with the resources for key pPrefix, with submenus that 
	 * correspond to the NamedHandlers.
	 * @param pPrefix A string such as "file" that indicates where to find the related
	 * resources in the resource bundle.
	 * @param pMenuItems The items to add to the menu
	 * @return A configured menu.
	 * @pre The .text and .mnemonic properties exist in the resource bundle
	 */
	public Menu createMenu(String pPrefix, boolean pDiagramSpecific, MenuItem... pMenuItems)
	{
		Menu menu = new Menu();
		menu.setUserData(pDiagramSpecific);
		initialize(menu, pPrefix);
		for( MenuItem item : pMenuItems )
		{
			menu.getItems().add(item);
		}
		return menu;
	}
}
