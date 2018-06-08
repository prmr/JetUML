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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.prefs.Preferences;

import ca.mcgill.cs.jetuml.UMLEditor;

/**
 * A Singleton that manages all user preferences global to
 * the application.
 */
public final class UserPreferences
{
	/**
	 * A boolean preference.
	 */
	public enum BooleanPreference
	{	
		showGrid(true), showToolHints(false);
		
		private boolean aDefault;
		
		BooleanPreference( boolean pDefault )
		{ 
			aDefault = pDefault;
		}
		
		String getDefault()
		{
			return Boolean.toString(aDefault);
		}
	}
	
	/**
	 * An integer preference.
	 */
	public enum IntegerPreference
	{
		diagramWidth(0), diagramHeight(0);
		
		private int aDefault;
		
		IntegerPreference( int pDefault )
		{
			aDefault = pDefault;
		}
		
		String getDefault()
		{
			return Integer.toString(aDefault);
		}
	}
	
	/**
	 * An object that can react to a change in user preference.
	 */
	public interface BooleanPreferenceChangeHandler
	{
		/**
		 * Callback for change in preference values.
		 * 
		 * @param pPreference The preference that just changed.
		 */
		void preferenceChanged(BooleanPreference pPreference);
	}
	
	private static final UserPreferences INSTANCE = new UserPreferences();
	
	private EnumMap<BooleanPreference, Boolean> aBooleanPreferences = new EnumMap<>(BooleanPreference.class);
	private final List<BooleanPreferenceChangeHandler> aBooleanPreferenceChangeHandlers = new ArrayList<>();
	private EnumMap<IntegerPreference, Integer> aIntegerPreferences = new EnumMap<>(IntegerPreference.class);
	
	private UserPreferences()
	{
		for( BooleanPreference preference : BooleanPreference.values() )
		{
			aBooleanPreferences.put(preference, 
					Boolean.valueOf(Preferences.userNodeForPackage(UMLEditor.class).get(preference.name(), preference.getDefault())));
		}
		for( IntegerPreference preference : IntegerPreference.values() )
		{
			aIntegerPreferences.put( preference, 
					Integer.valueOf(Preferences.userNodeForPackage(UMLEditor.class).get(preference.name(), preference.getDefault())));
		}
	}
	
	public static UserPreferences instance() 
	{ return INSTANCE; }
	
	/**
	 * @param pPreference The property whose value to obtain.
	 * @return The value of the property.
	 */
	public boolean getBoolean(BooleanPreference pPreference)
	{
		return aBooleanPreferences.get(pPreference);
	}
	
	/**
	 * @param pPreference The property whose value to obtain.
	 * @return The value of the property.
	 */
	public int getInteger(IntegerPreference pPreference)
	{
		return aIntegerPreferences.get(pPreference);
	}
	
	/**
	 * Sets and persists the value of a preference.
	 * 
	 * @param pPreference The property to set.
	 * @param pValue The value to set.
	 */
	public void setBoolean(BooleanPreference pPreference, boolean pValue)
	{
		aBooleanPreferences.put(pPreference, pValue);
		Preferences.userNodeForPackage(UMLEditor.class).put(pPreference.name(), Boolean.toString(pValue));
		aBooleanPreferenceChangeHandlers.forEach(handler -> handler.preferenceChanged(pPreference));
	}
	
	/**
	 * Sets and persists the value of a preference.
	 * 
	 * @param pPreference The property to set.
	 * @param pValue The value to set.
	 */
	public void setInteger(IntegerPreference pPreference, int pValue)
	{
		aIntegerPreferences.put(pPreference, pValue);
		Preferences.userNodeForPackage(UMLEditor.class).put(pPreference.name(), Integer.toString(pValue));
	}
	
	/**
	 * Adds a handler for a property change. Don't forget to remove handers if 
	 * objects are removed, e.g., diagram Tabs.
	 * 
	 * @param pHandler A handler for a change in boolean preferences.
	 */
	public void addBooleanPreferenceChangeHandler(BooleanPreferenceChangeHandler pHandler)
	{
		aBooleanPreferenceChangeHandlers.add(pHandler);
	}
	
	/**
	 * Removes a handler.
	 * 
	 * @param pHandler The handler to remove.
	 */
	public void removeBooleanPreferenceChangeHandler(BooleanPreferenceChangeHandler pHandler)
	{
		aBooleanPreferenceChangeHandlers.remove(pHandler);
	}
}
