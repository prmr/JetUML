/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.application;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ca.mcgill.cs.jetuml.UMLEditor;

/**
 * A single access point for all application resources.
 * 
 * Although ResourceBundles are cached internally, this class defines
 * a singleton to make its nature as a single access point more 
 * explicit.
 * 
 * The instance is exported as a public field instead of a static method
 * to makes its more convenient and clear when statically imported.
 *
 */
public final class ApplicationResources
{
	public static final ApplicationResources RESOURCES = new ApplicationResources();
	
	private static final String ERROR_MESSAGE = "[Resource cannot be found]";
	
	private ResourceBundle aResouceBundle = ResourceBundle.getBundle(UMLEditor.class.getName());
	
	private ApplicationResources() {}
	
	/**
	 * Returns a resource string for the key. If the key cannot be found or is not a string,
	 * returns a string that indicates an error.
	 * 
	 * @param pKey The key to look up. 
	 * @return The corresponding resource string.
	 * @pre pKey != null
	 */
	public String getString(String pKey)
	{
		assert pKey != null;
		try
		{
			return aResouceBundle.getString(pKey);
		}
		catch( MissingResourceException | ClassCastException pException )
		{
			return ERROR_MESSAGE;
		}
	}
	
	/**
	 * @param pKey The key to check. Cannot be null.
	 * @return True if the key is found in the resources.
	 */
	public boolean containsKey(String pKey)
	{
		assert pKey != null;
		return aResouceBundle.containsKey(pKey);
	}
}
